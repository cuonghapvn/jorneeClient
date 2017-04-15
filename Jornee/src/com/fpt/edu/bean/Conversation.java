/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: Conversation.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.bean;

import java.util.Date;

public class Conversation implements Comparable<Conversation> {
	int conversationId;
	String currentUser;
	String contactUser;
	String lastMessage;
	int numberUnread;
	String avatar;
	Date modifiedDate;
	
	

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getConversationId() {
		return conversationId;
	}

	public void setConversationId(int conversationId) {
		this.conversationId = conversationId;
	}

	public String getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(String currentUser) {
		this.currentUser = currentUser;
	}

	public String getContactUser() {
		return contactUser;
	}

	public void setContactUser(String contactUser) {
		this.contactUser = contactUser;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}

	public int getNumberUnread() {
		return numberUnread;
	}

	public void setNumberUnread(int numberUnread) {
		this.numberUnread = numberUnread;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) { 
		this.modifiedDate = modifiedDate;
	}

	@Override
	public int compareTo(Conversation another) {
		if (getModifiedDate() == null || another.getModifiedDate() == null)
			return 0;
		if(getModifiedDate().compareTo(another.getModifiedDate()) > 0 ){
			return -1;
		} else if(getModifiedDate().compareTo(another.getModifiedDate()) == 0) {
			return 0;
		} else {
			return 1;
		}
	}

}
