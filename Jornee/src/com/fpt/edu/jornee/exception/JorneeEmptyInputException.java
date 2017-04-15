/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: JorneeEmptyInputException.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.exception;

public class JorneeEmptyInputException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JorneeEmptyInputException() {
		super();
	}

	public JorneeEmptyInputException(String detailMessage,
			Throwable throwable) {
		super(detailMessage, throwable);
	}

	public JorneeEmptyInputException(String detailMessage) {
		super(detailMessage);
	}

	public JorneeEmptyInputException(Throwable throwable) {
		super(throwable);
	}

}
