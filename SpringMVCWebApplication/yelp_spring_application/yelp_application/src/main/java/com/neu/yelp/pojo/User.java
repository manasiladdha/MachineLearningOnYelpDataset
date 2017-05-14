package com.neu.yelp.pojo;

public class User {

	private String username;
	private String password;
	private String userid;
	private String hotState;
	private int reviewCount;
	
	
	public int getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}

	public String getHotState() {
		return hotState;
	}

	public void setHotState(String hotState) {
		this.hotState = hotState;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	
}
