/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: JorneeInvalidDateTimeException.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.exception;

public class JorneeInvalidDateTimeException extends Exception{

	public JorneeInvalidDateTimeException() {
		super();
	}

	public JorneeInvalidDateTimeException(String detailMessage,
			Throwable throwable) {
		super(detailMessage, throwable);
	}

	public JorneeInvalidDateTimeException(String detailMessage) {
		super(detailMessage);
	}

	public JorneeInvalidDateTimeException(Throwable throwable) {
		super(throwable);
	}

}
