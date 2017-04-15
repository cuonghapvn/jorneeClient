/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: Transport.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.bean;

public class Transport {
	private String transportId;
	private String startPoint;
	private String endPoint;
	private String vehicleId;
	private String modifiedDate;

	public Transport(String transportId, String startPoint, String endPoint,
			String vehicleId, String modifiedDate) {
		super();
		this.transportId = transportId;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.vehicleId = vehicleId;
		this.modifiedDate = modifiedDate;
	}

	public String getTransportId() {
		return transportId;
	}

	public void setTransportId(String transportId) {
		this.transportId = transportId;
	}

	public String getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(String startPoint) {
		this.startPoint = startPoint;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

}
