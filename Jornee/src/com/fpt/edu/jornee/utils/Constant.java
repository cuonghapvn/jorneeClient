/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: Constant.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.utils;

public class Constant {
	public static final String DATE_FORMAT = "yyyyy-mm-dd hh:mm:ss"; // Date format
//	public static final String TIME_FORMAT = "HH:mm"; // Time format
	public static final String SERVER_HOST = "http://jornee.herokuapp.com/";
	
	/**
	 * Image type
	 */
	public static final String IMAGE_TYPE_THUMBNAIL = "thumbnail_";
	public static final String IMAGE_TYPE_SMALL = "small_";
	public static final String IMAGE_TYPE_MEDIUM = "medium_";
	public static final String IMAGE_TYPE_LARGE = "large_";
	public static final String IMAGE_TYPE_ORIGINAL = "original_";
	
	/**
	 * Server function
	 */
	public static final String SOCIAL_SEARCH_FRIEND = "search_friend";
	public static final String SOCIAL_FOLLOW = "follow";
	public static final String SOCIAL_UNFOLLOW = "unfollow";
	public static final String SOCIAL_VIEW_FOLLOW = "view_follow";
	public static final String SOCIAL_OUTSIDE = "outside";
	public static final String SOCIAL_USER_PROFILE = "user_profile";
	public static final String SOCIAL_USERS_JOURNEY = "user_journeys";
	public static final String SOCIAL_LIST_CONVERSATIONS = "list_conversations";
	public static final String SOCIAL_CONVERSATION_DETAIL = "conversation_detail";
	public static final String SOCIAL_LIST_NOTIFICATION = "list_notification";
	public static final String SOCIAL_SHARE_IPLACE = "share_iplace";
	
	/**
	 * Server response
	 */
	public static final String RESPONSE_AUTHEN_STATUS = "authen_status";
	public static final String RESPONSE_AUTHEN_STATUS_ERROR = "error";
	public static final String RESPONSE_AUTHEN_STATUS_FAIL = "fail";
	public static final String RESPONSE_AUTHEN_STATUS_OK = "ok";
	public static final String RESPONSE_STATUS = "status";
	public static final String RESPONSE_STATUS_ERROR = "error";
	public static final String RESPONSE_STATUS_OK = "ok";
	
	/**
	 * Google API
	 */
	public static final String URL_AUTOCOMPLETE = "https://maps.googleapis.com/maps/api/place/autocomplete/";
	public static final String API_KEY = "key=AIzaSyCb-bORk00XZslcplMH3iw9eHYzaHnZav0";
	public static final String FRAGMENT_TITLE_BUNDLE = "fragmentTitle";
	public static final String USERNAME_BUNDLE = "username";
	public static final String URL_ADDRESS_COMPONENT = "https://maps.googleapis.com/maps/api/geocode/";
	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public static final int MAX_ATTEMPTS = 5;
	public static final int BACKOFF_MILLI_SECONDS = 2000;
	
	// Google project id
	public static final String SENDER_ID = "674660603332";

	public static final String TAG = "Jornee";

	public static final String DISPLAY_MESSAGE_ACTION = "com.fpt.edu.rssreader.DISPLAY_MESSAGE";

	public static final String EXTRA_MESSAGE = "message";
	/**
	 * Error message
	 */

}
