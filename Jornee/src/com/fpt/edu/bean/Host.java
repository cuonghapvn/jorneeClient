/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: Host.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.bean;

public class Host {
	private String hostID;	
	private String  userID;
	private String areaID;	
	private String  userAndRate;	
	private String desctiprion;	
	private String status;
	private String commentId;
	private String activeTime;	
	private String hostTips;
	private String modifiedDate;
	
	
	
	
	public Host(String hostID, String userID, String areaID,
			String userAndRate, String desctiprion, String status,
			String commentId, String activeTime, String hostTips,
			String modifiedDate) {
		super();
		this.hostID = hostID;
		this.userID = userID;
		this.areaID = areaID;
		this.userAndRate = userAndRate;
		this.desctiprion = desctiprion;
		this.status = status;
		this.commentId = commentId;
		this.activeTime = activeTime;
		this.hostTips = hostTips;
		this.modifiedDate = modifiedDate;
	}
	public String getHostID() {
		return hostID;
	}
	public void setHostID(String hostID) {
		this.hostID = hostID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getAreaID() {
		return areaID;
	}
	public void setAreaID(String areaID) {
		this.areaID = areaID;
	}
	public String getUserAndRate() {
		return userAndRate;
	}
	public void setUserAndRate(String userAndRate) {
		this.userAndRate = userAndRate;
	}
	public String getDesctiprion() {
		return desctiprion;
	}
	public void setDesctiprion(String desctiprion) {
		this.desctiprion = desctiprion;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCommentId() {
		return commentId;
	}
	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}
	public String getActiveTime() {
		return activeTime;
	}
	public void setActive_time(String activeTime) {
		this.activeTime = activeTime;
	}
	public String getHostTips() {
		return hostTips;
	}
	public void setHostTips(String hostTips) {
		this.hostTips = hostTips;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	
	
	
	

}
