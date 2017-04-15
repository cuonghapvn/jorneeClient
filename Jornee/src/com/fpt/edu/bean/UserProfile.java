/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: UserProfile.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.bean;

public class UserProfile {
	
	private String authen_status;
	private String status;
	private String username;
	private String email;
	private String dob;
	private String gender;
	private String num_of_following;
	private String num_of_follower;
	private boolean is_host;
	private boolean is_me;
	private boolean is_following;
	private String avatar;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNum_of_following() {
		return num_of_following;
	}

	public void setNum_of_following(String num_of_following) {
		this.num_of_following = num_of_following;
	}

	public String getNum_of_follower() {
		return num_of_follower;
	}

	public void setNum_of_follower(String num_of_follower) {
		this.num_of_follower = num_of_follower;
	}

	public boolean isIs_host() {
		return is_host;
	}

	public void setIs_host(boolean is_host) {
		this.is_host = is_host;
	}

	public boolean isIs_me() {
		return is_me;
	}

	public void setIs_me(boolean is_me) {
		this.is_me = is_me;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getAuthen_status() {
		return authen_status;
	}

	public void setAuthen_status(String authen_status) {
		this.authen_status = authen_status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isIs_following() {
		return is_following;
	}

	public void setIs_following(boolean is_following) {
		this.is_following = is_following;
	}

}
