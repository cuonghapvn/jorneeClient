/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HostInLocation.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class HostInLocation implements Parcelable{
	private String status;
	private String username;
	private String regis_location;
	private String avatar;


	public HostInLocation() {
		super();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	// Parcelling part
	public HostInLocation(Parcel in) {
		String[] data = new String[4];

		in.readStringArray(data);
		this.status = data[0];
		this.username = data[1];
		this.regis_location = data[2];
		this.avatar = data[3];

	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { this.status, this.username,
				this.regis_location,this.avatar });
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public HostInLocation createFromParcel(Parcel in) {
			return new HostInLocation(in);
		}

		public HostInLocation[] newArray(int size) {
			return new HostInLocation[size];
		}
	};


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRegis_location() {
		return regis_location;
	}

	public void setRegis_location(String regis_location) {
		this.regis_location = regis_location;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	
	
	
	
	
	
	
	
	
	
	
}
