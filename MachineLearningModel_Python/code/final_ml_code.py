from collections import defaultdict
import numpy
from sklearn.svm import SVC
from sklearn.model_selection import train_test_split
from sklearn.utils import column_or_1d
from sklearn import preprocessing
import csv
from decimal import Decimal

attributes = ['attributes_Price_Range','attributes_Accepts_Credit_Cards',
              'attributes_Take_out','attributes_Delivery','attributes_Wheelchair_Accessible',
              'attributes_Good_For_lunch','attributes_Good_For_dinner','attributes_Good_For_brunch',
              'attributes_Good_For_breakfast','attributes_Takes_Reservations',
              'latitude','longitude','business_stars', 'review_count', 'sentimental_rating',
              'review_avg_stars']

print('---- Reading OurUsers_OurBusinesses_OurFeatures.csv -------- ')
our_user_our_business_dict = defaultdict(dict)               
with open('OurUsers_OurBusinesses_OurFeatures.csv') as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        for attribute in attributes[:10]:
            if row[attribute].lower() == 'true':
                row[attribute] = 1
            elif row[attribute].lower() == 'false':
                row[attribute] = 0
            elif row[attribute].lower() == 'na':
                row[attribute] = 0
            else:
                row[attribute] = 0
        
        our_user_our_business_dict[row['user_id']].update({ 
                                                   row['business_id'] : 
                                                    [ 
                                                       int(row[attributes[0]]),
                                                       int(row[attributes[1]]), 
                                                       int(row[attributes[2]]), 
                                                       int(row[attributes[3]]), 
                                                       int(row[attributes[4]]), 
                                                       int(row[attributes[5]]), 
                                                       int(row[attributes[6]]), 
                                                       int(row[attributes[7]]), 
                                                       int(row[attributes[8]]), 
                                                       int(row[attributes[9]]), 
                                                       float(row[attributes[10]]), 
                                                       float(row[attributes[11]]),
                                                       float(row[attributes[12]]),
                                                       int(row[attributes[13]]),
                                                       int('{}'.format(round(Decimal(row[attributes[14]]),0))),
                                                       int(numpy.round(float(row[attributes[15]]))),
                                                     ]
                                                  })


print('------Model Making Started-------')
results = defaultdict(list)
users_models = defaultdict()
for user, business_dict in our_user_our_business_dict.items():
    final_features = []
    final_labels = []
    for business, features_list in business_dict.items():
        final_features.append(features_list[:-1]) #everything except the last item 
        final_labels.append(features_list[-1]) #last label

    final_features = numpy.array(final_features)
    final_labels = numpy.array(final_labels)
   
    X_train, X_test, y_train, y_test = train_test_split(final_features, final_labels, 
                                                        test_size=0.2, random_state=0)
    y_train = column_or_1d(y_train, warn=False)
    y_test = column_or_1d(y_test, warn=False)          
    scaler = preprocessing.StandardScaler().fit(X_train)
    X_train_transformed = scaler.transform(X_train)
    clf = SVC(C=1).fit(X_train_transformed, y_train)
    X_test_transformed = scaler.transform(X_test)
    train_accuracy = clf.score(X_train_transformed, y_train)
    test_accuracy = clf.score(X_test_transformed, y_test)
    
    if train_accuracy >=0.65 and test_accuracy>=0.60:
        users_models[user] = clf
        results[user].append(X_train.shape)
        results[user].append(X_test.shape)
        results[user].append(train_accuracy)
        results[user].append(test_accuracy)
    
print('------Write accuracy in file-------')
final_results = []
for u, r in results.items():
    train_rows = r[0][0]
    test_rows = r[1][0]
    final_results.append([u, train_rows, test_rows, r[2], r[3]])
resultFile = open('OurUsers_AllAccuracy_C1_FilteredUsers.csv','w')
wr = csv.writer(resultFile, delimiter=',', lineterminator='\n' )
wr.writerow(['user_id', 'X_train_rows' , 'X_test_rows', 'train_accuracy', 'test_accuracy'])
wr.writerows(final_results)
resultFile.close()

print("-----Generate list of Old Businesses for Users -------")
all_users_old_businesses = defaultdict(set)
for user, business_list in our_user_our_business_dict.items():
    if user in results.keys():
        for b in business_list.keys():
            all_users_old_businesses[user].add(b)
            
print("----- Generate set of all businesses -------")
all_business = []
all_business_features = defaultdict(list)
with open('business_foodData_sentiments.csv') as businessfile:
   b_reader = csv.DictReader(businessfile)
   for row in b_reader:
       business_id = row['business_id']
       all_business.append(business_id)  
       
       for attribute in attributes[:10]:
           if row[attribute].lower() == 'true':
               row[attribute] = 1
           elif row[attribute].lower() == 'false':
               row[attribute] = 0
           elif row[attribute].lower() == 'na':
               row[attribute] = 0
           else:
               row[attribute] = 0        
       
       all_business_features[row['business_id']] = [ 
                                                       int(row[attributes[0]]),
                                                       int(row[attributes[1]]), 
                                                       int(row[attributes[2]]), 
                                                       int(row[attributes[3]]), 
                                                       int(row[attributes[4]]), 
                                                       int(row[attributes[5]]), 
                                                       int(row[attributes[6]]), 
                                                       int(row[attributes[7]]), 
                                                       int(row[attributes[8]]), 
                                                       int(row[attributes[9]]), 
                                                       float(row[attributes[10]]), 
                                                       float(row[attributes[11]]),
                                                       float(row[attributes[12]]),
                                                       int(row[attributes[13]]),
                                                       int('{}'.format(round(Decimal(row[attributes[14]]),0)))
                                                     ]
                                                     
                                                     
print("------- Diff the businesses for users --------- ")                                                
all_business = set(all_business)
all_users_new_businesses = defaultdict(list)
for u, business_set in all_users_old_businesses.items():
   diff_business = all_business.symmetric_difference(business_set)
   all_users_new_businesses[u] = list(diff_business)
   
print("------GENERATING NEW BUSINESSES WITH FEATURES FOR USERS --------")
all_users_new_businesses_features = defaultdict(dict)
final_results =[]
for u, business_list in all_users_new_businesses.items():
   for b in business_list:
       final_results.append([u , b] +  all_business_features[b] )
       all_users_new_businesses_features[u].update({ b : all_business_features[b]})
       
test_user = list(all_users_new_businesses_features.keys())[0]
print(all_users_new_businesses_features[test_user])
       
# write results to a csv
resultFile = open("OurUsers_NewBusinesses_OurFeatures.csv",'w')
wr = csv.writer(resultFile, delimiter=',', lineterminator='\n' )
wr.writerow(['user_id', 'business_id', 'attributes_Price_Range','attributes_Accepts_Credit_Cards',
              'attributes_Take_out','attributes_Delivery','attributes_Wheelchair_Accessible',
              'attributes_Good_For_lunch','attributes_Good_For_dinner','attributes_Good_For_brunch',
              'attributes_Good_For_breakfast','attributes_Takes_Reservations',
              'latitude','longitude','business_stars', 'review_count', 'sentimental_rating' ])
wr.writerows(final_results)
resultFile.close()

print("----- Predicting new businesses and Writing --------")
resultFile = open("OurUsers_NewBusiness_Predictions.csv",'w')
wr = csv.writer(resultFile, delimiter=',', lineterminator='\n' )
wr.writerow(['user_id', 'business_id' , 'prediction'])
all_users_new_businesses_predictions = defaultdict(dict)
for user, business_list in all_users_new_businesses_features.items():
    for new_business, new_business_features in business_list.items():
        predicted_label = users_models[user].predict([new_business_features])
        wr.writerow([user, new_business, predicted_label[0]])
        all_users_new_businesses_predictions[user] = { new_business : predicted_label[0] }

resultFile.close()       
print("done")