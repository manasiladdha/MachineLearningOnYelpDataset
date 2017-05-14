package com.neu.yelp.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.neu.yelp.pojo.BusinessDetails;
import com.neu.yelp.pojo.BusinessLatLong;
import com.neu.yelp.pojo.User;

public class BusinessDAO {

	private static DB db = null;

	private final static Map<String, String> state_latitudes;
	private final static Map<String, String> state_longitudes;

	static {
		state_latitudes = new HashMap<String, String>();
		state_latitudes.put("PA", "40.9769891");

		state_latitudes.put("NC", "35.1980086");

		state_latitudes.put("SC", "33.6091087");

		state_latitudes.put("WI", "44.820519");

		state_latitudes.put("IL", "39.6611544");

		state_latitudes.put("AZ", "34.0953973");

		state_latitudes.put("NV", "36.1249185");

		state_latitudes.put("FL", "30.2442304");

		state_latitudes.put("NM", "34.0934138");

		state_latitudes.put("QC", "46.8565177");

		state_latitudes.put("ON", "48.9329969");

		state_latitudes.put("TX", "31.1002504");

		state_latitudes.put("EDH", "55.9411418");

		state_latitudes.put("MLN", "55.8212996");

		state_latitudes.put("ELN", "55.9483733");

		state_latitudes.put("FIF", "56.2290967");

		state_latitudes.put("NTH", "52.3099549");

		state_latitudes.put("BW", "48.6436975");

		state_latitudes.put("RP", "49.9507273");

		state_latitudes.put("NW", "51.4091064");

		state_latitudes.put("AL", "32.5856499");

		state_latitudes.put("AK", "60.1393231");

	}

	static {
		state_longitudes = new HashMap<String, String>();
		state_longitudes.put("PA", "-79.8486916");

		state_longitudes.put("NC", "-82.1353218");

		state_longitudes.put("SC", "-83.1906554");

		state_longitudes.put("WI", "-94.0596968");

		state_longitudes.put("IL", "-93.9963367");

		state_longitudes.put("AZ", "-116.4251114");

		state_longitudes.put("NV", "-115.3150839");

		state_longitudes.put("FL", "-96.4307909");

		state_longitudes.put("NM", "-110.5202736");

		state_longitudes.put("QC", "-71.4817776");

		state_longitudes.put("ON", "-93.7137114");
		state_longitudes.put("TX", "-104.5720466");
		state_longitudes.put("EDH", "-3.2754233");
		state_longitudes.put("MLN", "-3.2477222");
		state_longitudes.put("ELN", "-3.2866725");
		state_longitudes.put("FIF", "-3.7023483");
		state_longitudes.put("NTH", "-1.3974093");
		state_longitudes.put("BW", "6.7604925");
		state_longitudes.put("RP", "6.188414");
		state_longitudes.put("NW", "5.4210145");
		state_longitudes.put("AL", "-88.9249562");
		state_longitudes.put("AK", "-176.4478893");
	}

	public BusinessDAO() {

		DAO dao = new DAO();
		db = dao.getConnection();

	}

	public ArrayList<BusinessLatLong> getUserBusinessLatLong(User user) {
		ArrayList<BusinessLatLong> reviewedBusinesses = new ArrayList<BusinessLatLong>();

		try {

			String state = getStateForUser(user);
			user.setHotState(state);

			DBCollection table = db.getCollection("OurUsers_OurBusinesses_OurFeatures");

			BasicDBObject andQuery = new BasicDBObject();
			List<BasicDBObject> obj = new ArrayList<BasicDBObject>();

			obj.add(new BasicDBObject("user_id", user.getUserid()));
			obj.add(new BasicDBObject("state", state));

			andQuery.put("$and", obj);

			DBCursor cursor = table.find(andQuery).limit(50);
			int reviewCount = table.find(andQuery).count();
			System.out.println("ReviewCount"+ reviewCount);
			user.setReviewCount(reviewCount);
			while (cursor.hasNext()) {
				DBObject object = cursor.next();
				BusinessLatLong business = new BusinessLatLong();
				business.setBusinessId(object.get("business_id").toString());
				business.setBusinessName(object.get("name").toString());
				business.setLatitude(object.get("latitude").toString());
				business.setLongitude(object.get("longitude").toString());
				business.setRating(object.get("review_avg_stars").toString());
				business.setBusinessCity(object.get("city").toString());
				business.setBusiness_stars(object.get("business_stars").toString());
				business.setSentimentalRatings(object.get("sentimental_rating").toString());
				reviewedBusinesses.add(business);
			}
			return reviewedBusinesses;

		} catch (Exception ex) {
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return reviewedBusinesses;
	}

	public String getStateForUser(User user) {

		DBCollection table = db.getCollection("users_hot_state");

		BasicDBObject whereQuery = new BasicDBObject();

		whereQuery.put("user_id", user.getUserid());

		DBCursor cursor = table.find(whereQuery);
		while (cursor.hasNext()) {
			DBObject object = cursor.next();
			return object.get("state").toString();
		}
		return null;
	}

	public String getStateLatitutde(String state) {
		// TODO Auto-generated method stub
		String lat = state_latitudes.get(state);

		return lat;
	}

	public String getStateLongitude(String state) {
		// TODO Auto-generated method stub
		String longitude = state_longitudes.get(state);
		return longitude;
	}

	public List<BusinessLatLong> getUserNewBusinessLatLong(User user) {
		ArrayList<BusinessLatLong> notReviewedBusinesses = new ArrayList<BusinessLatLong>();

		try {

			DBCollection table = db.getCollection("FilteredUsers_NewBusiness_Predictions");

			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("user_id", user.getUserid());

			DBCursor cursor = table.find(whereQuery);
			while (cursor.hasNext()) {
				DBObject object = cursor.next();
				BusinessLatLong business = new BusinessLatLong();
				business.setBusinessId(object.get("business_id").toString());

				DBCollection businessTable = db.getCollection("All_Food_Businesses");

				BasicDBObject whereBusinessQuery = new BasicDBObject();
				whereBusinessQuery.put("business_id", business.getBusinessId());

				DBCursor businessCursor = businessTable.find(whereBusinessQuery);
				while (businessCursor.hasNext()) {
					DBObject businessObject = businessCursor.next();
					business.setBusinessName(businessObject.get("name").toString());
					business.setLatitude(businessObject.get("latitude").toString());
					business.setLongitude(businessObject.get("longitude").toString());
					business.setBusinessCity(businessObject.get("city").toString());
					business.setRating(object.get("prediction").toString());
					notReviewedBusinesses.add(business);
				}

			}
			return notReviewedBusinesses;

		} catch (Exception ex) {
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return notReviewedBusinesses;

	}

	public List<BusinessLatLong> getUserNewBusinessLatLong2(User user) {
		ArrayList<BusinessLatLong> notReviewedBusinesses = new ArrayList<BusinessLatLong>();

		try {

			DBCollection table = db.getCollection("FilteredUsers_NewBusiness_Predictions2");

			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("user_id", user.getUserid());

			DBCursor cursor = table.find(whereQuery);
			while (cursor.hasNext()) {
				DBObject object = cursor.next();
				BusinessLatLong business = new BusinessLatLong();
				business.setBusinessId(object.get("business_id").toString());

				business.setBusinessName(object.get("name").toString());
				business.setLatitude(object.get("latitude").toString());
				business.setLongitude(object.get("longitude").toString());
				business.setBusinessCity(object.get("city").toString());
				business.setRating(object.get("prediction").toString());
				
//				DBCollection businessTable = db.getCollection("All_Food_Businesses");
//
//				BasicDBObject whereBusinessQuery = new BasicDBObject();
//				whereBusinessQuery.put("business_id", business.getBusinessId());
//
//				DBCursor businessCursor = businessTable.find(whereBusinessQuery);
//				while (businessCursor.hasNext()) {
//					DBObject businessObject = businessCursor.next();
//					business.setBusiness_stars(businessObject.get("stars").toString());
//					
//				}
				notReviewedBusinesses.add(business);

			}
			return notReviewedBusinesses;

		} catch (Exception ex) {
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return notReviewedBusinesses;

	}

}
