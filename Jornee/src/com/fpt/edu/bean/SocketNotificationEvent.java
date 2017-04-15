/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: SocketNotificationEvent.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.bean;

public class SocketNotificationEvent implements Comparable<SocketNotificationEvent>{
	
	public final static String NOTIFICATION_TYPE_FOLLOW = "follow";
	public final static String NOTIFICATION_TYPE_SHARED = "shared";
	public final static String NOTIFICATION_TYPE_LIKED = "like";
	public final static String NOTIFICATION_TYPE_COMMENT = "comment";
	public final static String NOTIFICATION_TYPE_RATE = "rate";
	public final static String NOTIFICATION_TYPE_FEEDBACK = "feedback";
	public final static String NOTIFICATION_TYPE_MESSAGE = "message";
	
	
	String other_id;
	String type;
	String content_id;
	String time;
	String avatar;

	public String getOther_id() {
		return other_id;
	}

	public void setOther_id(String other_id) {
		this.other_id = other_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent_id() {
		return content_id;
	}

	public void setContent_id(String content_id) {
		this.content_id = content_id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getNotificationMessage() {
		if (type.equals(NOTIFICATION_TYPE_FOLLOW)) {
			return "'" + other_id + "' is following you.";
		}
		if (type.equals(NOTIFICATION_TYPE_SHARED)) {
			return "'" + other_id + "' shared a journey to you.";
		}
		if (type.equals(NOTIFICATION_TYPE_LIKED)) {
			return "'" + other_id + "' liked your journey.";
		}
		if (type.equals(NOTIFICATION_TYPE_COMMENT)) {
			return "'" + other_id + "' commented on your journey.";
		}
		if (type.equals(NOTIFICATION_TYPE_RATE)) {
			return "'" + other_id + "' rated your host profile.";
		}
		if (type.equals(NOTIFICATION_TYPE_FEEDBACK)) {
			return "'" + other_id + "' gave you a feedback.";
		}
		if (type.equals(NOTIFICATION_TYPE_MESSAGE)) {
			return "'" + other_id + "' send you a message.";
		}
		return "You got new notification from '" + other_id + "'";
	}
	
	public String getTypeString(){
		if (type.equals(NOTIFICATION_TYPE_FOLLOW)) {
			return "New follower";
		}
		if (type.equals(NOTIFICATION_TYPE_SHARED)) {
			return "New shared journey";
		}
		if (type.equals(NOTIFICATION_TYPE_LIKED)) {
			return "Journey is liked";
		}
		if (type.equals(NOTIFICATION_TYPE_COMMENT)) {
			return "New comment in journey";
		}
		if (type.equals(NOTIFICATION_TYPE_RATE)) {
			return "New host rate";
		}
		if (type.equals(NOTIFICATION_TYPE_FEEDBACK)) {
			return "New feedback";
		}
		if (type.equals(NOTIFICATION_TYPE_MESSAGE)) {
			return "New message";
		}
		return "New notification from '" + other_id + "'";
	}

	@Override
	public int compareTo(SocketNotificationEvent another) {
		if (getTime() == null || another.getTime() == null)
			return 0;
		if(getTime().compareTo(another.getTime()) > 0 ){
			return -1;
		} else if(getTime().compareTo(another.getTime()) == 0) {
			return 0;
		} else {
			return 1;
		}
	}
}
