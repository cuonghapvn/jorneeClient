/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: Entry.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.StringTokenizer;

import android.graphics.Bitmap;

import com.fpt.edu.jornee.utils.DateTimeHelper;
import com.google.android.gms.maps.model.LatLng;

public class Entry implements Serializable, Comparable<Entry> {

	public final static int ENTRY_STATUS_LOCAL_ONLY = 1;
	public final static int ENTRY_STATUS_WAITING_FOR_SYNCHRONIZING = 2;
	public final static int ENTRY_STATUS_UP_TO_DATE = 3;

	private static final long serialVersionUID = 671414104824280923L;
	private String entryID;
	private String type;
	private String coordinate;
	private String text;
	private String path;
	private String iplaceID;
	private String journeyID;
	private String dateTime;
	private String modifiedDate;
	private String placeName;
	private String vehicle;
	private String startPoint;
	private String syncDate;
	private String changedElement;
	private String address;
	private String street_number;
	private String route;
	private String subLocality;
	private String locality;
	private String administrative_area_level_2;
	private String administrative_area_level_1;
	private String country;
	private String serverEntryID;
	private Bitmap image;

	public Entry() {
		super();
	}

	public Entry(String entryID, String type, String coordinate, String text,
			String path, String iplaceID, String journeyID, String dateTime,
			String modifiedDate, String placeName, String vehicle,
			String startPoint, String syncDate, String changedElement,
			String address, String street_number, String route,
			String subLocality, String locality,
			String administrative_area_level_2,
			String administrative_area_level_1, String country,
			String serverEntryID) {
		super();
		this.entryID = entryID;
		this.type = type;
		this.coordinate = coordinate;
		this.text = text;
		this.path = path;
		this.iplaceID = iplaceID;
		this.journeyID = journeyID;
		this.dateTime = dateTime;
		this.modifiedDate = modifiedDate;
		this.placeName = placeName;
		this.vehicle = vehicle;
		this.startPoint = startPoint;
		this.syncDate = syncDate;
		this.changedElement = changedElement;
		this.address = address;
		this.street_number = street_number;
		this.route = route;
		this.subLocality = subLocality;
		this.locality = locality;
		this.administrative_area_level_2 = administrative_area_level_2;
		this.administrative_area_level_1 = administrative_area_level_1;
		this.country = country;
		this.serverEntryID = serverEntryID;
	}

	public Entry(String entryID, String type, String coordinate, String text,
			String path, String iplaceID, String journeyID, String dateTime,
			String modifiedDate, String placeName, String vehicle,
			String startPoint, String syncDate, String changedElement,
			String address, String street_number, String route,
			String locality, String administrative_area_level_2,
			String administrative_area_level_1, String country,
			String serverEntryID) {
		super();
		this.entryID = entryID;
		this.type = type;
		this.coordinate = coordinate;
		this.text = text;
		this.path = path;
		this.iplaceID = iplaceID;
		this.journeyID = journeyID;
		this.dateTime = dateTime;
		this.modifiedDate = modifiedDate;
		this.placeName = placeName;
		this.vehicle = vehicle;
		this.startPoint = startPoint;
		this.syncDate = syncDate;
		this.changedElement = changedElement;
		this.address = address;
		this.street_number = street_number;
		this.route = route;
		this.locality = locality;
		this.administrative_area_level_2 = administrative_area_level_2;
		this.administrative_area_level_1 = administrative_area_level_1;
		this.country = country;
		this.serverEntryID = serverEntryID;
	}

	public Entry(String entryID, String type, String coordinate, String text,
			String path, String iplaceID, String journeyID, String dateTime,
			String modifiedDate, String placeName, String serverEntryID) {
		super();
		this.entryID = entryID;
		this.type = type;
		this.coordinate = coordinate;
		this.text = text;
		this.path = path;
		this.iplaceID = iplaceID;
		this.journeyID = journeyID;
		this.dateTime = dateTime;
		this.modifiedDate = modifiedDate;
		this.placeName = placeName;
		this.serverEntryID = serverEntryID;
	}

	public Entry(String entryID, String type, String coordinate, String text,
			String path, String iplaceID, String journeyID, String dateTime,
			String modifiedDate, String placeName, String vehicle,
			String startPoint, String serverEntryID) {
		super();
		this.entryID = entryID;
		this.type = type;
		this.coordinate = coordinate;
		this.text = text;
		this.path = path;
		this.iplaceID = iplaceID;
		this.journeyID = journeyID;
		this.dateTime = dateTime;
		this.modifiedDate = modifiedDate;
		this.placeName = placeName;
		this.vehicle = vehicle;
		this.startPoint = startPoint;
		this.serverEntryID = serverEntryID;
	}

	public Entry(String entryID, String type, String coordinate, String text,
			String path, String iplaceID, String journeyID, String dateTime,
			String modifiedDate, String placeName, String serverEntryID,
			String vehicle, String startPoint, Bitmap image) {
		super();
		this.entryID = entryID;
		this.type = type;
		this.coordinate = coordinate;
		this.text = text;
		this.path = path;
		this.iplaceID = iplaceID;
		this.journeyID = journeyID;
		this.dateTime = dateTime;
		this.modifiedDate = modifiedDate;
		this.placeName = placeName;
		this.serverEntryID = serverEntryID;
		this.image = image;
		this.vehicle = vehicle;
		this.startPoint = startPoint;
	}

	public Entry(String entryID, String type, String coordinate, String text,
			String path, String iplaceID, String journeyID, String dateTime,
			String modifiedDate, String placeName, String vehicle,
			String startPoint, String syncDate, String changedElement,
			String serverEntryID) {
		super();
		this.entryID = entryID;
		this.type = type;
		this.coordinate = coordinate;
		this.text = text;
		this.path = path;
		this.iplaceID = iplaceID;
		this.journeyID = journeyID;
		this.dateTime = dateTime;
		this.modifiedDate = modifiedDate;
		this.placeName = placeName;
		this.vehicle = vehicle;
		this.startPoint = startPoint;
		this.syncDate = syncDate;
		this.changedElement = changedElement;
		this.serverEntryID = serverEntryID;
	}

	public Entry(String entryID, String type, String coordinate, String text,
			String path, String iplaceID, String journeyID, String dateTime,
			String modifiedDate, String placeName, String vehicle,
			String startPoint, String syncDate, String changedElement,
			String address, String serverEntryID) {
		super();
		this.entryID = entryID;
		this.type = type;
		this.coordinate = coordinate;
		this.text = text;
		this.path = path;
		this.iplaceID = iplaceID;
		this.journeyID = journeyID;
		this.dateTime = dateTime;
		this.modifiedDate = modifiedDate;
		this.placeName = placeName;
		this.vehicle = vehicle;
		this.startPoint = startPoint;
		this.syncDate = syncDate;
		this.changedElement = changedElement;
		this.address = address;
		this.serverEntryID = serverEntryID;
	}

	public String getSyncDate() {
		return syncDate;
	}

	public void setSyncDate(String syncDate) {
		this.syncDate = syncDate;
	}

	public String getChangedElement() {
		return changedElement;
	}

	public void setChangedElement(String changedElement) {
		this.changedElement = changedElement;
	}

	public String getVehicle() {
		return vehicle;
	}

	public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}

	public String getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(String startPoint) {
		this.startPoint = startPoint;
	}

	public String getEntryID() {
		return entryID;
	}

	public void setEntryID(String entryID) {
		this.entryID = entryID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getIplaceID() {
		return iplaceID;
	}

	public void setIplaceID(String iplaceID) {
		this.iplaceID = iplaceID;
	}

	public String getJourneyID() {
		return journeyID;
	}

	public void setJourneyID(String journeyID) {
		this.journeyID = journeyID;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getServerEntryID() {
		return serverEntryID;
	}

	public void setServerEntryID(String serverEntryID) {
		this.serverEntryID = serverEntryID;
	}

	public String getStreet_number() {
		return street_number;
	}

	public void setStreet_number(String street_number) {
		this.street_number = street_number;
	}

	public String getRoute() {
		return route;
	}

	public String getSubLocality() {
		return subLocality;
	}

	public void setSubLocality(String subLocality) {
		this.subLocality = subLocality;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getAdministrative_area_level_2() {
		return administrative_area_level_2;
	}

	public void setAdministrative_area_level_2(
			String administrative_area_level_2) {
		this.administrative_area_level_2 = administrative_area_level_2;
	}

	public String getAdministrative_area_level_1() {
		return administrative_area_level_1;
	}

	public void setAdministrative_area_level_1(
			String administrative_area_level_1) {
		this.administrative_area_level_1 = administrative_area_level_1;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public LatLng getPosition() {
		LatLng result = null;
		double lat, lng;
		if (this.coordinate != null) {
			try {
				StringTokenizer stringTokenizer = new StringTokenizer(
						this.coordinate, ";");
				lat = Double.parseDouble(stringTokenizer.nextElement()
						.toString());
				lng = Double.parseDouble(stringTokenizer.nextElement()
						.toString());
				result = new LatLng(lat, lng);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return result;
			}
		}
		return result;
	}

	@Override
	public int compareTo(Entry another) {
		if (getDateTime() == null || another.getDateTime() == null)
			return 0;
		Date thisDate = DateTimeHelper
				.convertStringServerTimeToLocalDate(getDateTime());
		Date anotherDate = DateTimeHelper
				.convertStringServerTimeToLocalDate(another.getDateTime());
		return thisDate.compareTo(anotherDate);
	}

	public void clone(Entry other) {
		this.setAddress(other.getAddress());
		this.setAdministrative_area_level_1(other
				.getAdministrative_area_level_1());
		this.setAdministrative_area_level_2(other
				.getAdministrative_area_level_2());
		this.setChangedElement(other.getChangedElement());
		this.setCoordinate(other.getCoordinate());
		this.setCountry(other.getCountry());
		this.setDateTime(other.getDateTime());
		this.setEntryID(other.getEntryID());
		this.setImage(other.getImage());
		this.setIplaceID(other.getIplaceID());
		this.setJourneyID(other.getJourneyID());
		this.setLocality(other.getLocality());
		this.setModifiedDate(other.getModifiedDate());
		this.setPath(other.getPath());
		this.setPlaceName(other.getPlaceName());
		this.setRoute(other.getRoute());
		this.setServerEntryID(other.getServerEntryID());
		this.setStartPoint(other.getStartPoint());
		this.setStreet_number(other.getStreet_number());
		this.setSubLocality(other.getSubLocality());
		this.setSyncDate(other.getSyncDate());
		this.setText(other.getText());
		this.setType(other.getType());
		this.setVehicle(other.getVehicle());

	}

	public String compareEdit(Entry another) {
		if ((this.getPath() == null && another.getPath() == null)
				|| (this.getPath() != null && this.getPath().equalsIgnoreCase(
						another.getPath()))) {
			if ((this.getPlaceName() == null ? another.getPlaceName() == null
					: this.getPlaceName().equalsIgnoreCase(
							another.getPlaceName()))
					&& (this.getDateTime() == null ? another.getDateTime() == null
							: this.getDateTime().equalsIgnoreCase(
									another.getDateTime()))
					&& (this.getCoordinate() == null ? another.getCoordinate() == null
							: this.getCoordinate().equalsIgnoreCase(
									another.getCoordinate()))
					&& (this.getText() == null ? another.getText() == null
							: this.getText()
									.equalsIgnoreCase(another.getText()))
					&& (this.getVehicle() == null ? another.getVehicle() == null
							: this.getVehicle().equalsIgnoreCase(
									another.getVehicle()))
					&& (this.getAddress() == null ? another.getAddress() == null
							: this.getAddress().equalsIgnoreCase(
									another.getAddress()))
					&& (this.getType() == null ? another.getType() == null
							: this.getType()
									.equalsIgnoreCase(another.getType()))
					&& (this.getStartPoint() == null ? another.getStartPoint() == null
							: this.getStartPoint().equalsIgnoreCase(
									another.getStartPoint()))

			) {
				return "none";
			} else {
				return "text";
			}
		} else {
			if ((this.getPlaceName() == null ? another.getPlaceName() == null
					: this.getPlaceName().equalsIgnoreCase(
							another.getPlaceName()))
					&& (this.getDateTime() == null ? another.getDateTime() == null
							: this.getDateTime().equalsIgnoreCase(
									another.getDateTime()))
					&& (this.getCoordinate() == null ? another.getCoordinate() == null
							: this.getCoordinate().equalsIgnoreCase(
									another.getCoordinate()))
					&& (this.getText() == null ? another.getText() == null
							: this.getText()
									.equalsIgnoreCase(another.getText()))
					&& (this.getVehicle() == null ? another.getVehicle() == null
							: this.getVehicle().equalsIgnoreCase(
									another.getVehicle()))
					&& (this.getAddress() == null ? another.getAddress() == null
							: this.getAddress().equalsIgnoreCase(
									another.getAddress()))
					&& (this.getType() == null ? another.getType() == null
							: this.getType()
									.equalsIgnoreCase(another.getType()))
					&& (this.getStartPoint() == null ? another.getStartPoint() == null
							: this.getStartPoint().equalsIgnoreCase(
									another.getStartPoint()))

			) {
				return "image";
			} else {
				return "both";
			}
		}

	}

	public int getEntrySyncStatus() {
		if (this.getServerEntryID() != null) {
			if (getSyncDate().equals(getModifiedDate())) {
				return ENTRY_STATUS_UP_TO_DATE;
			} else {
				return ENTRY_STATUS_WAITING_FOR_SYNCHRONIZING;
			}
		} else {
			return ENTRY_STATUS_LOCAL_ONLY;
		}
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

}
