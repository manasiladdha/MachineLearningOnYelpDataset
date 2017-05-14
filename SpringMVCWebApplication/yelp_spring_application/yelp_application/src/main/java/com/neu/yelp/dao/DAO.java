package com.neu.yelp.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class DAO {
	
	private static MongoClient mongo=null;
	private static DB db = null;
    
    public DAO(){
    	/**** Connect to MongoDB ****/
		// Since 2.10.0, uses MongoClient
		mongo = new MongoClient("localhost", 27017);
    }
    
    public DB getConnection(){
    	
    	/**** Get database ****/
		// if database doesn't exists, MongoDB will create it for you
		db = mongo.getDB("yelpfinal");
		return db;
    }
}
