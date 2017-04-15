/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: Journey.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.bean;

import java.io.Serializable;
import java.util.Date;

import com.fpt.edu.jornee.utils.DateTimeHelper;

public class Journey  implements Serializable, Comparable<Journey> {
	
	public final static int JOURNEY_STATUS_LOCAL_ONLY = 1;
	public final static int JOURNEY_STATUS_WAITING_FOR_SYNCHRONIZING = 2;
	public final static int JOURNEY_STATUS_UP_TO_DATE = 3;
	
	public final static String JOURNEY_FUNCTION_CREATE = "create_journey";
	public final static String JOURNEY_FUNCTION_UPDATE = "update_journey";
	public final static String JOURNEY_FUNCTION_DELETE = "delete_journey";
	
	private static final long serialVersionUID = 1L;
	private String journeyID;
	private String journeyName;
	private String userLiked;
	private String commentID;
	private String userID;
	private String entriesID;
	private String shared;
	private String createdDate;
	private String modifiedDate;
	private String syncedDate;
	private String serverJourneyID;

	public Journey(String journeyID, String journeyName, String userLiked,
			String commentID, String userID, String entriesID, String shared, String createdDate,
			String modifiedDate, String syncedDate, String serverJourneyID) {
		super();
		this.journeyID = journeyID;
		this.journeyName = journeyName;
		this.userLiked = userLiked;
		this.commentID = commentID;
		this.userID = userID;
		this.entriesID = entriesID;
		this.shared = shared;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.syncedDate = syncedDate;
		this.serverJourneyID = serverJourneyID;
	}

	public Journey() {
		super();
	}

	public String getJourneyID() {
		return journeyID;
	}

	public void setJourneyID(String journeyID) {
		this.journeyID = journeyID;
	}

	public String getJourneyName() {
		return journeyName;
	}

	public void setJourneyName(String journeyName) {
		this.journeyName = journeyName;
	}

	public String getUserLiked() {
		return userLiked;
	}

	public void setUserLiked(String userLiked) {
		this.userLiked = userLiked;
	}

	public String getCommentID() {
		return commentID;
	}

	public void setCommentID(String commentID) {
		this.commentID = commentID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getShared() {
		return shared;
	}

	public void setShared(String shared) {
		this.shared = shared;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getServerJourneyID() {
		return serverJourneyID;
	}

	public void setServerJourneyID(String serverJourneyID) {
		this.serverJourneyID = serverJourneyID;
	}

	public String getEntriesID() {
		return entriesID;
	}

	public void setEntriesID(String entriesID) {
		this.entriesID = entriesID;
	}

	@Override
	public int compareTo(Journey another) {
		if (getModifiedDate() == null || another.getModifiedDate() == null)
			return 0;
		Date thisDate = DateTimeHelper.convertStringServerTimeToLocalDate(getModifiedDate());
		Date anotherDate = DateTimeHelper.convertStringServerTimeToLocalDate(another.getModifiedDate());
		if(thisDate.compareTo(anotherDate) > 0 ){
			return -1;
		} else if(thisDate.compareTo(anotherDate) == 0) {
			return 0;
		} else {
			return 1;
		}
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getSyncedDate() {
		return syncedDate;
	}

	public void setSyncedDate(String syncedDate) {
		this.syncedDate = syncedDate;
	}
	
	public int getJourneyStatus(){
		if(this.getServerJourneyID()!=null){
			if(getSyncedDate().equals(getModifiedDate())){
				return JOURNEY_STATUS_UP_TO_DATE;
			}
			else {
				return JOURNEY_STATUS_WAITING_FOR_SYNCHRONIZING;
			}
		} else {
			return JOURNEY_STATUS_LOCAL_ONLY;
		}
	}
	
	public String getJourneyFunction(){
		if(this.getServerJourneyID()!=null){
			if(getSyncedDate().equals(getModifiedDate())){
				return null;
			}
			else {
				return JOURNEY_FUNCTION_UPDATE;
			}
		} else {
			return JOURNEY_FUNCTION_CREATE;
		}
	}

}
