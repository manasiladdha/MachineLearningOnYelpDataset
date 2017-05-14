package com.neu.yelp.pojo;

public class BusinessLatLong {

	private String latitude;
	private String longitude;
	private String businessId;
	private String businessName;
	private String rating;
	private String sentimentalRatings;
	private String business_stars;
	
	public String getSentimentalRatings() {
		return sentimentalRatings;
	}

	public void setSentimentalRatings(String sentimentalRatings) {
		this.sentimentalRatings = sentimentalRatings;
	}

	public String getBusiness_stars() {
		return business_stars;
	}

	public void setBusiness_stars(String business_stars) {
		this.business_stars = business_stars;
	}

	
	
	public String getBusinessCity() {
		return businessCity;
	}

	public void setBusinessCity(String businessCity) {
		this.businessCity = businessCity;
	}

	private String businessCity;
	

	
	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

}
