/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: JorneeInputLengthException.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.exception;

public class JorneeInputLengthException extends Exception {

	public JorneeInputLengthException() {
		super();
	}

	public JorneeInputLengthException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public JorneeInputLengthException(String detailMessage) {
		super(detailMessage);
	}

	public JorneeInputLengthException(Throwable throwable) {
		super(throwable);
	}

}
