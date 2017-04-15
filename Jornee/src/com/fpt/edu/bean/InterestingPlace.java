/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: InterestingPlace.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.bean;

import java.util.ArrayList;

public class InterestingPlace {

	private ArrayList<InterestingPlaceEntry> entries;
	private Coordinate coordinate;
	private ArrayList<String> categories;

	public ArrayList<InterestingPlaceEntry> getEntries() {
		return entries;
	}

	public void setEntries(ArrayList<InterestingPlaceEntry> entries) {
		this.entries = entries;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public ArrayList<String> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}





}
