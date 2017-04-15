/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: OutsideSearchFriendResult.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.bean;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class OutsideSearchFriendResult implements Serializable, Comparable<OutsideSearchFriendResult> {

	private String username;
	private String userAvatarURL;
	private String isFollowing;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserAvatarURL() {
		return userAvatarURL;
	}

	public void setUserAvatarURL(String userAvatarURL) {
		this.userAvatarURL = userAvatarURL;
	}

	public String getIsFollowing() {
		return isFollowing;
	}
	
	@Override
	public int compareTo(OutsideSearchFriendResult another) {
		return this.getUsername().compareToIgnoreCase(another.username);
	}

	public void setIsFollowing(String isFollowing) {
		this.isFollowing = isFollowing;
	}
	
	public static ArrayList<OutsideSearchFriendResult> mergeList(
			ArrayList<OutsideSearchFriendResult> arr1,
			ArrayList<OutsideSearchFriendResult> arr2) {
		ArrayList<OutsideSearchFriendResult> result = new ArrayList<OutsideSearchFriendResult>();
		result.addAll(arr1);
		for (OutsideSearchFriendResult outsideSearchFriendResult : arr2) {
			if (!isExistInList(result, outsideSearchFriendResult.getUsername())) {
				result.add(outsideSearchFriendResult);
			}
		}
		return result;
	}

	public static boolean isExistInList(ArrayList<OutsideSearchFriendResult> list,
			String username) {
		boolean result = false;
		for (OutsideSearchFriendResult outsideSearchFriendResult : list) {
			if (outsideSearchFriendResult.getUsername().equals(username)) {
				result = true;
			}
		}
		return result;
	}
	
	public static ArrayList<OutsideSearchFriendResult> resultListFromJSON(
			JSONArray jsonArray) {
		ArrayList<OutsideSearchFriendResult> result = new ArrayList<OutsideSearchFriendResult>();
		try {
			JSONObject userResult;
			OutsideSearchFriendResult friendResult;
			int numOfResult = jsonArray.length();
			if (numOfResult > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					userResult = jsonArray.getJSONObject(i);
					friendResult = new OutsideSearchFriendResult();
					friendResult.setUsername(userResult.getString("username"));
					if (userResult.has("avatar")) {
						friendResult.setUserAvatarURL(userResult
								.getString("avatar"));
					}
					friendResult.setIsFollowing(userResult
							.getString("following"));
					result.add(friendResult);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
