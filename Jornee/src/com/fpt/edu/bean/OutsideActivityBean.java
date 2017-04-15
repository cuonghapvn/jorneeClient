/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: OutsideActivityBean.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.bean;

import java.util.ArrayList;

public class OutsideActivityBean {
	String username;
	String userAvatarURL;
	String journeyDateTime;
	String startLocation;
	String lastLocation;
	String journeyName;
	String journeyID;
	String numberLiked;
	String numberComment;
	int numOfReturnEntry;
	int numOfTotalEntry;
	boolean isCurrentUserLiked;
	ArrayList<EntryInOutside> entries;

	public String getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(String startLocation) {
		this.startLocation = startLocation;
	}

	public String getNumberComment() {
		return numberComment;
	}

	public void setNumberComment(String numberComment) {
		this.numberComment = numberComment;
	}

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

	public String getJourneyDateTime() {
		return journeyDateTime;
	}

	public void setJourneyDateTime(String journeyDateTime) {
		this.journeyDateTime = journeyDateTime;
	}

	public String getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(String lastLocation) {
		this.lastLocation = lastLocation;
	}

	public String getJourneyName() {
		return journeyName;
	}

	public void setJourneyName(String journeyName) {
		this.journeyName = journeyName;
	}

	public String getJourneyID() {
		return journeyID;
	}

	public void setJourneyID(String journeyID) {
		this.journeyID = journeyID;
	}

	public String getNumberLiked() {
		return numberLiked;
	}

	public void setNumberLiked(String numberLiked) {
		this.numberLiked = numberLiked;
	}

	public boolean isCurrentUserLiked() {
		return isCurrentUserLiked;
	}

	public void setCurrentUserLiked(boolean isCurrentUserLiked) {
		this.isCurrentUserLiked = isCurrentUserLiked;
	}

	public ArrayList<EntryInOutside> getEntries() {
		return entries;
	}

	public void setEntries(ArrayList<EntryInOutside> entries) {
		this.entries = entries;
	}

	public int getNumOfReturnEntry() {
		return numOfReturnEntry;
	}

	public void setNumOfReturnEntry(int numOfReturnEntry) {
		this.numOfReturnEntry = numOfReturnEntry;
	}

	public int getNumOfTotalEntry() {
		return numOfTotalEntry;
	}

	public void setNumOfTotalEntry(int numOfTotalEntry) {
		this.numOfTotalEntry = numOfTotalEntry;
	}

}
