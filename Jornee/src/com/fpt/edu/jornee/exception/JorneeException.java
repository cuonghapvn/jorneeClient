/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: JorneeException.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.exception;

@SuppressWarnings("serial")
public class JorneeException extends Exception {

	public JorneeException() {
		super();
	}

	public JorneeException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public JorneeException(String detailMessage) {
		super(detailMessage);
	}

	public JorneeException(Throwable throwable) {
		super(throwable);
	}

}
