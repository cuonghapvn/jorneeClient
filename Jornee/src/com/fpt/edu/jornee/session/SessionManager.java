/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: SessionManager.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.session;

import java.util.HashMap;

import com.fpt.edu.bean.UserProfile;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.account.LoginFragment;
import com.fpt.edu.jornee.account.RegisterFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;

	// Editor for Shared preferences
	Editor editor;

	// Context
	Context context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "JorneePref";

	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";

	public static final String KEY_TOKEN = "token";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_AVATAR = "avatar";
	public static final String KEY_IS_HOST = "isHost";
	public static final String KEY_OFFLINE_AVATAR = "offlineAvatar";


	

	// Constructor
	public SessionManager(Context context){
		this.context = context;
		pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
		
	}

	/**
	 * Create login session
	 * */
	public void createLoginSession(String token, String username , String avatar , String isHost) {
		// Storing login value as TRUE
		editor.putBoolean(IS_LOGIN, true);

		// Storing token in pref
		editor.putString(KEY_TOKEN, token);

		// Storing name in pref
		editor.putString(KEY_USERNAME, username);

		// Storing avatar in pref
		editor.putString(KEY_AVATAR, avatar);

		// Storing is host in pref
		editor.putString(KEY_IS_HOST, isHost);
		

		// commit changes
		editor.commit();
	}
	
	public void changeIsHostStatus(String newIsHost){
		editor.putString(KEY_IS_HOST, newIsHost);
		editor.commit();
	}
	public void setOfflineAvatar(String avatar){
		editor.putString(KEY_OFFLINE_AVATAR, avatar);
		editor.commit();
	}
	public void updateUser(UserProfile profile){
		editor.putString(KEY_AVATAR, profile.getAvatar());
		editor.commit();
	}

	/**
	 * Check login method wil check user login status If false it will redirect
	 * user to login page Else won't do anything
	 * */
	public void checkLogin(FragmentManager manager) {
		// Check login status
		if (!this.isLoggedIn()) {

			Fragment fragment0 = new LoginFragment();
			FragmentTransaction fragmentTransaction = manager
					.beginTransaction();
			fragmentTransaction.replace(R.id.content_frame, fragment0);
			fragmentTransaction.commit();
		}

	}

	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();

		// user token id
		user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
		// user name
		user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
		
		user.put(KEY_AVATAR, pref.getString(KEY_AVATAR, null));

		user.put(KEY_IS_HOST, pref.getString(KEY_IS_HOST, null));
		
		user.put(KEY_OFFLINE_AVATAR, pref.getString(KEY_OFFLINE_AVATAR, null));



		// return user
		return user;
	}

	/**
	 * Clear session details
	 * */
	public void logoutUser(FragmentManager manager) {
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();

		Fragment fragment0 = new LoginFragment();
		FragmentTransaction fragmentTransaction = manager.beginTransaction();
		fragmentTransaction.replace(R.id.content_frame, fragment0);
		fragmentTransaction.commit();
	}

	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn() {
		return pref.getBoolean(IS_LOGIN, false);
	}
}
