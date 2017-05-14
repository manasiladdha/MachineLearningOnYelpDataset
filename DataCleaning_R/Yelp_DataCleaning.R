######## Loading relevant libraries

library(magrittr)
library(dplyr)
library(data.table)
install.packages("sqldf")
library(sqldf)



############################### Load files ############################### 
##### Sentiment
sentiment <- read.csv("D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV2/AllBusinessesSentimentalRatings.csv")
str(sentiment)

data_df <- data.frame(data)
head(data_df)

data_table <- data.table(data_df)

aa <- as.data.table(data_table)[, toString(movieId), by = list(userId)]
head(aa)

##### CHECKIN
checkin <- read.csv("D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV/yelp_academic_dataset_checkin.csv")
head(checkin)
str(checkin)
summary(checkin)

##### USER
user <- read.csv("D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV/yelp_academic_dataset_user.csv")
head(user)
str(user)

#### BUSINESS
business <- read.csv("D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV/yelp_academic_dataset_business.csv")
head(business)
str(business)

###### TIPS
tips <- read.csv("D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV/yelp_academic_dataset_tip.csv", quote)
head(tips,10)

####### REVIEW
file.path <- "D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV/yelp_academic_dataset_review.csv"
review <- fread(file.path)
nrow(review)
head(review,1)
str(review)


# Find food related businesses.
business_foodData <- sqldf('select distinct * from business where categories like \'%Food%\' 
                           or  categories  like \'%Restaurants%\' 
                           or categories  like \'%Lounges%\' 
                           or categories like \'%Nightlife%\' 
                           or categories like \'%Bars%\'')
str(business_foodData)

business_foodData_sentiment <-
  sqldf('select a.business_id,
              a.latitude,
        a.longitude,
        a.review_count,
        a.attributes_Price_Range,
        a.attributes_Accepts_Credit_Cards,
        a.attributes_Take_out,
        a.attributes_Delivery,
        a.attributes_Wheelchair_Accessible,
        a.attributes_Good_For_lunch,
        a.attributes_Good_For_dinner,
        a.attributes_Good_For_brunch,
        a.attributes_Good_For_breakfast,
        a.attributes_Takes_Reservations,
        a.stars as business_stars,
        s.sentimental_rating 
        from business_foodData a, sentiment s
        where a.business_id = s.user_id')

write.csv(business_foodData_sentiment,'D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV2/business_foodData_sentiments.csv')

write.csv(business_foodData,'D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV2/All_Food_Businesses.csv')

write.csv(sqldf('select distinct business_id from business_foodData'),
          'D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV2/All_Food_Businesses_BusinessID.csv')


review_user_business <- sqldf('select distinct user_id, business_id from review')

xx <- sqldf('select r.user_id, count(r.business_id)
            from review_user_business r, business_foodData b
            where r.business_id = b.business_id
            group by r.user_id
            having count(r.business_id) > 100')

head(xx)


xx2 <- sqldf('select a.user_id, b.business_id from 
             xx a,
             business_foodData b,
             review_user_business r
             where a.user_id = r.user_id
             and b.business_id = r.business_id')

head(xx2)

head(sqldf('select user_id, count(business_id) from xx2 group by user_id order by count(business_id)'))

write.csv(xx2,'D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV2/Our_Users_GreaterThan100_Businesses.csv')

###################################### Rename user colnames
colnames(review)[4] <- "votes_cool"
colnames(review)[6] <- "votes_funny"
colnames(review)[10] <- "votes_useful"

review2 <- sqldf('select r.* from review r, business_foodData b
                 where r.business_id = b.business_id')
nrow(review)
nrow(review2)

review_final_round <- sqldf('select r.user_id, r.business_id, round(avg(r.stars),2) as review_avg_stars
                            from review2 r, xx2 xx 
                            where r.user_id = xx.user_id 
                            group by r.user_id, r.business_id' )

write.csv(review_final_round,'D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV2/Our_Users_GreaterThan100_Businesses_Reviews.csv')

str(review_final_round)

head(sqldf('select user_id, count( business_id) from review_final group by user_id order by count(business_id)'))


head(review[1])

############# User_id, business_id, business features

nrow(business_foodData_relevantFeatures)

#Renaming businesses column names
colnames(business_foodData)[32] <- "attributes_Price_Range"
colnames(business_foodData)[40] <- "attributes_Accepts_Credit_Cards"
colnames(business_foodData)[44] <- "attributes_Take_out"
colnames(business_foodData)[55] <- "attributes_Delivery"
colnames(business_foodData)[60] <- "attributes_Wheelchair_Accessible"
colnames(business_foodData)[42] <- "attributes_Good_For_lunch"
colnames(business_foodData)[68] <- "attributes_Good_For_dinner"
colnames(business_foodData)[20] <- "attributes_Good_For_brunch"
colnames(business_foodData)[33] <- "attributes_Good_For_breakfast"
colnames(business_foodData)[51] <- "attributes_Takes_Reservations"
colnames(business_foodData)[32] <- "attributes_Price_Range"
colnames(business_foodData)[40] <- "attributes_Accepts_Credit_Cards"
colnames(business_foodData)[44] <- "attributes_Take_out"
colnames(business_foodData)[55] <- "attributes_Delivery"
colnames(business_foodData)[60] <- "attributes_Wheelchair_Accessible"
colnames(business_foodData)[42] <- "attributes_Good_For_lunch"
colnames(business_foodData)[68] <- "attributes_Good_For_dinner"
colnames(business_foodData)[20] <- "attributes_Good_For_brunch"
colnames(business_foodData)[33] <- "attributes_Good_For_breakfast"
colnames(business_foodData)[51] <- "attributes_Takes_Reservations"
colnames(business_foodData)[15] <- "attributes_Parking_lot"
colnames(business_foodData)[25] <- "attributes_Parking_street"
colnames(business_foodData)[34] <- "attributes_Parking_garage"
colnames(business_foodData)[43] <- "attributes_Parking_valet"
colnames(business_foodData)[76] <- "attributes_Parking_validated"
colnames(business_foodData)[93] <- "attributes_Good_For_Groups"
colnames(business_foodData)[75] <- "attributes_Good_For_kids"
colnames(business_foodData)[83] <- "attributes_Ambience_casual"
colnames(business_foodData)[96] <- "attributes_Ambience_romantic"
colnames(business_foodData)[98] <- "attributes_Ambience_upscale"
colnames(business_foodData)[13] <- "attributes_Ambience_classy"

nrow(business_foodData_relevantFeatures)

business_foodData_relevantFeatures <- 
sqldf('select business_id,
              latitude,
              longitude,
              review_count,
              attributes_Price_Range,
              attributes_Accepts_Credit_Cards,
              attributes_Take_out,
              attributes_Delivery,
              attributes_Wheelchair_Accessible,
              attributes_Good_For_lunch,
              attributes_Good_For_dinner,
              attributes_Good_For_brunch,
              attributes_Good_For_breakfast,
              attributes_Takes_Reservations,
              stars as business_stars
              from business_foodData')

write.csv(business_foodData_relevantFeatures,'D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV2/business_foodData_relevantFeatures.csv')

AllusersAllBusinessAllFeatures <-
  sqldf('select r.user_id, r.business_id, a.latitude, a.longitude, a.review_count, 
        a.attributes_Price_Range, a.attributes_Accepts_Credit_Cards, a.attributes_Take_out, 
        a.attributes_Delivery, a.attributes_Wheelchair_Accessible, a.attributes_Good_For_lunch,
        a.attributes_Good_For_dinner, a.attributes_Good_For_brunch, a.attributes_Good_For_breakfast,
        a.attributes_Takes_Reservations, a.stars as business_stars,
        s.sentimental_rating, r.review_avg_stars
        from business_foodData a, review_final_round r, sentiment s
        where a.business_id = r.business_id
        and a.business_id = s.user_id')

str(AllusersAllBusinessAllFeatures)
nrow(AllusersAllBusinessAllFeatures)

write.csv(AllusersAllBusinessAllFeatures,'D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV2/OurUsers_OurBusinesses_OurFeatures.csv')

ww <-
  sqldf('select r.user_id, r.business_id, a.latitude, a.longitude, a.review_count, 
        a.attributes_Price_Range, a.attributes_Accepts_Credit_Cards, a.attributes_Take_out, 
        a.attributes_Delivery, a.attributes_Wheelchair_Accessible, a.attributes_Good_For_lunch,
        a.attributes_Good_For_dinner, a.attributes_Good_For_brunch, a.attributes_Good_For_breakfast,
        a.attributes_Takes_Reservations, a.stars as business_stars, a.state, a.city, a.name,
        s.sentimental_rating, r.review_avg_stars
        from business_foodData a, review_final_round r, sentiment s
        where a.business_id = r.business_id
        and a.business_id = s.user_id')

write.csv(ww,'D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV2/OurUsers_OurBusinesses_OurFeatures2.csv')

str(ww)


sqldf("select count(distinct business_id) from ww")

sqldf('select count(distinct business_id) from AllusersAllBusinessAllFeatures')

b <- sqldf('select distinct business_id from business_foodData')
head(xx2)



head(b)

OurUsers_NewBusinesses <-
  sqldf('select a.user_id, b.business_id from
        b left join xx2 a on b.business_id = a.business_id
        where a.business_id is NULL')

head(OurUsers_NewBusinesses)
head(sqldf('select * from OurUsers_NewBusinesses where user_id is not null'))

sqldf('select a.user_id
      from xx2 a, b
      where a.business_id <> b.business_id
      group by ')


################################ Final dataframe for Model input
all_features_all_users <-
  sqldf('select b.user_id, b.business_id, b.latitude, b.longitude, b.review_count,
        b.attributes_Price_Range, b.attributes_Accepts_Credit_Cards, b.attributes_Take_out,
        b.attributes_Delivery, b.attributes_Wheelchair_Accessible, b.attributes_Good_For_lunch,
        b.attributes_Good_For_dinner, b.attributes_Good_For_brunch, b.attributes_Good_For_breakfast,
        b.attributes_Takes_Reservations,
        u.review_avg_stars, u.avg_votes_funny, u.avg_votes_useful, u.avg_votes_cool,
        b.avg_stars
        from finalData b, review_final_round u
        where b.business_id = u.business_id
        and b.user_id = u.user_id')

sqldf('select count(*) from all_features_all_users')

nrow(all_features_all_users)

write.csv(all_features_all_users, 'D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV/AllFeatures_AllUsers.csv')

################# Analysis for particular users
all_features_User1 <- sqldf('select * from all_features_all_users where user_id = \'9A2-wSoBUxlMd3LwmlGrrQ\'')
all_features_User2 <- sqldf('select * from all_features_all_users where user_id = \'Iu3Jo9ROp2IWC9FwtWOaUQ\'')
all_features_User3 <- sqldf('select * from all_features_all_users where user_id = \'ia1nTRAQEaFWv0cwADeK7g\'')
all_features_User1676 <- sqldf('select * from all_features_all_users where user_id = \'w1P9cvIVTxcLZvU5tXIhRw\'')
all_features_User200 <- sqldf('select * from all_features_all_users where user_id = \'gUr8qs00wFAk851yHMlgRQ\'')
all_features_User500 <- sqldf('select * from all_features_all_users where user_id = \'sfJP6W0E_JThj5eXLBd6pA\'')

sqldf('select count(*) from all_features_User1')

write.csv(all_features_User1, 'D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV/all_features_User1.csv')
write.csv(all_features_User500, 'D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV/all_features_User500.csv')


write.csv(review_final_round, 'D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV/User_features_all_users.csv')



####################### TESTING Models in R ######################################
final_data_u1_train <- final_data_u1[1:947,3:16]
final_data_u1_test <- final_data_u1[948:1184,3:16]

model <- glm(avg_stars ~.,data=final_data_u1_train, na.action=na.exclude)
install.packages("e1071")

svm.model <- svm(avg_stars ~ ., data = final_data_u1_train, cost = 100, gamma = 1)
svm.pred <- predict(svm.model, final_data_u1_test[])

str(finalData)
str(review_final_round)

sqldf('select count( distinct user_id) from review_final_round')


#####################3rd Dec... Trying SVM
data <- read.csv('D:/GRAD_SCHOOL/Fall2016/Project_Yelp/DatasetsInCSV/DatasetsInCSV/all_features_User1_0_1.csv')

data_train <- data[1:947,4:21]
data_test <- data[948:1184,4:21]
str(data_train)
str(data_test)

svm.model <- svm(avg_stars ~ ., data = data_train, cost = 100, gamma = 1, kernel = 'linear')

svm.pred <- predict(svm.model, data_test[,-19])

table(pred = svm.pred, true = data_test[,18])

accuracy <- rmse(data_test$avg_stars, svm.pred)
accuracy

### Rpart
rpart.model <- rpart(avg_stars ~ ., data = data_train)
rpart.pred <- predict(rpart.model, data_test, type = "vector")
accuracyr <- rmse(data_test$avg_stars, rpart.pred)
accuracyr

head(data_test[,-19],1)

