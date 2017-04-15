/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: Category.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.bean;

public class Category {

	private String categoryID;
	private String categoryName;
	private String modifiedDate;
	public String getCategoryID() {
		return categoryID;
	}
	
	
	public Category(String categoryID, String categoryName, String modifiedDate) {
		super();
		this.categoryID = categoryID;
		this.categoryName = categoryName;
		this.modifiedDate = modifiedDate;
	}


	public void setCategoryID(String categoryID) {
		this.categoryID = categoryID;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	
}
