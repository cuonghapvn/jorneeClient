/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: Location.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Location implements Parcelable {
	private String locality;
	private String administrative_area_level_1;
	private String administrative_area_level_2;
	private String country;
	private String location_lat;
	private String location_lng;

	
	

	public Location() {
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getAdministrative_area_level_1() {
		return administrative_area_level_1;
	}

	public void setAdministrative_area_level_1(String administrative_area_level_1) {
		this.administrative_area_level_1 = administrative_area_level_1;
	}

	public String getAdministrative_area_level_2() {
		return administrative_area_level_2;
	}

	public void setAdministrative_area_level_2(String administrative_area_level_2) {
		this.administrative_area_level_2 = administrative_area_level_2;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLocation_lat() {
		return location_lat;
	}

	public void setLocation_lat(String location_lat) {
		this.location_lat = location_lat;
	}

	public String getLocation_lng() {
		return location_lng;
	}

	public void setLocation_lng(String location_lng) {
		this.location_lng = location_lng;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	// Parcelling part
	public Location(Parcel in) {
		String[] data = new String[6];

		in.readStringArray(data);
		this.locality = data[0];
		this.administrative_area_level_1 = data[1];
		this.administrative_area_level_2 = data[2];
		this.country = data[3];
		this.location_lat = data[4];
		this.location_lng = data[5];

	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { this.locality, this.administrative_area_level_1,
				this.administrative_area_level_2,this.country,this.location_lat,this.location_lng });
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Location createFromParcel(Parcel in) {
			return new Location(in);
		}

		public Location[] newArray(int size) {
			return new Location[size];
		}
	};
}
