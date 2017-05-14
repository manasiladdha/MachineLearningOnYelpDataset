package com.neu.yelp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.neu.yelp.pojo.User;


public class UserDAO extends DAO {

	private static DB db = null;

	public UserDAO() {

		DAO dao = new DAO();
		db = dao.getConnection();

	}

	public String authenticateUser(String userName, String pwd) {
		try {
			User user = new User();
			String userid = getUserByUserName(userName, pwd);
			if (userid != null) {
				user.setUsername(userName);
				user.setPassword(pwd);
				user.setUserid(userid);
				System.out.println("The user exists");
				return userid;
			} else {
				System.out.println("The user do not exist");
				return userid;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "";
		}
	}

	public String getUserByUserName(String uname, String pwd) {

		try {

			User user = new User();
			/**** Get collection / table from 'testdb' ****/
			// if collection doesn't exists, MongoDB will create it for you
			DBCollection table = db.getCollection("UserAccount");

			BasicDBObject andQuery = new BasicDBObject();
			List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
			obj.add(new BasicDBObject("username", uname));
			obj.add(new BasicDBObject("password", pwd));
			andQuery.put("$and", obj);

			System.out.println(andQuery.toString());

			DBCursor cursor = table.find(andQuery);
			while (cursor.hasNext()) {
				DBObject object = cursor.next();
				String val = object.get("userid").toString();
				//System.out.println(cursor.next());
				return val;
			}

		} catch (Exception ex) {
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
	
}
