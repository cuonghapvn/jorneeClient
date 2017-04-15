/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: InterestingPlaceEntry.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.bean;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class InterestingPlaceEntry implements Serializable {

	public InterestingPlaceEntry(String path, String iPlaceName) {

		this.path = path;
		this.IPlaceName = iPlaceName;
	}

	public String path;
	public String IPlaceName;

//	@Override
//	public int describeContents() {
//		return 0;
//	}

	// Parcelling part
//	public InterestingPlaceEntry(Parcel in) {
//		String[] data = new String[2];
//
//		in.readStringArray(data);
//		this.path = data[0];
//		this.IPlaceName = data[1];
//	}
//
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeStringArray(new String[] { this.path, this.IPlaceName });
//	}
//
//	@SuppressWarnings("rawtypes")
//	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
//		public InterestingPlaceEntry createFromParcel(Parcel in) {
//			return new InterestingPlaceEntry(in);
//		}
//
//		public InterestingPlaceEntry[] newArray(int size) {
//			return new InterestingPlaceEntry[size];
//		}
//	};

}
