/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: Validator.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources.NotFoundException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.exception.JorneeContainSpecialCharacterException;
import com.fpt.edu.jornee.exception.JorneeEmptyInputException;
import com.fpt.edu.jornee.exception.JorneeException;
import com.fpt.edu.jornee.exception.JorneeInputLengthException;
import com.fpt.edu.jornee.exception.JorneeInvalidDateTimeException;
import com.fpt.edu.jornee.exception.JorneeInvalidEmailException;
import com.fpt.edu.jornee.exception.JorneeNotMatchException;

@SuppressLint("SimpleDateFormat")
public class Validator {

	private Pattern pattern;
	private Matcher matcher;
	Context context;

	public Validator(Context context) {
		this.context = context;
	}

	public boolean validateDateTimeFormat(String date)
			throws JorneeInvalidDateTimeException {
		Boolean isValid = true;
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT); // format
																					// date
		try {
			Date startDate = dateFormat.parse(date);
		} catch (ParseException e) {
			isValid = false;
			throw new JorneeInvalidDateTimeException(context.getResources()
					.getString(R.string.error_invalid_datetime_format));

		}

		return isValid;
	}

	public boolean validateStartEnd(String start, String end)
			throws JorneeInvalidDateTimeException {

		Boolean isValid = true;
//		SimpleDateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT); // format
																					// date
		DateFormat dateFormat = DateFormat.getDateTimeInstance();
		Date startDate;
		try {
			startDate = dateFormat.parse(start);
			Date endDate = dateFormat.parse(end);
			if (startDate.compareTo(endDate) >= 0) {
				isValid = false;
			}
		} catch (ParseException e) {
			isValid = false;
			System.out.println(e.getMessage());
			throw new JorneeInvalidDateTimeException(context.getResources()
					.getString(R.string.error_invalid_datetime_format));
		}

		return isValid;
	}

	public boolean validateEmptyInput(String input)
			throws JorneeEmptyInputException {

		Boolean isValid = true;
		if (null == input || input.trim().isEmpty()) {
			isValid = false;
			throw new JorneeEmptyInputException(context.getResources()
					.getString(R.string.error_required_field_message));
		}
		return isValid;
	}

	public boolean validateEmail(String email)
			throws JorneeInvalidEmailException {
		Boolean isValid = true;

		pattern = Pattern.compile(Constant.EMAIL_PATTERN);

		matcher = pattern.matcher(email);

		if (!matcher.matches()) {
			isValid = false;

			throw new JorneeInvalidEmailException(context.getResources()
					.getString(R.string.error_invalid_email_message));
		}
		return isValid;

	}

	public boolean validateMatch(String st1, String st2)
			throws JorneeNotMatchException {

		if (st1.equals(st2)) {
			return true;
		} else {
			throw new JorneeNotMatchException(context.getResources().getString(
					R.string.error_invalid_not_match));
		}
	}

	public boolean validateContainsSpecialCharacter(String s)
			throws NotFoundException, JorneeContainSpecialCharacterException,
			JorneeEmptyInputException {
		if (s == null || s.trim().isEmpty()) {

			throw new JorneeEmptyInputException(context.getResources()
					.getString(R.string.error_required_field_message));
		}

		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(s);
		boolean b = m.find();
		if (b) {
			System.out.println(" not match ");
			throw new JorneeContainSpecialCharacterException(context
					.getResources().getString(
							R.string.error_contain_special_character));
		}
		return true;
	}

	public boolean validateLength(String s) throws NotFoundException,
			JorneeEmptyInputException, JorneeInputLengthException {
		if (s == null || s.trim().isEmpty()) {

			throw new JorneeEmptyInputException(context.getResources()
					.getString(R.string.error_required_field_message));
		}

		if (s.trim().length() < 6) {
			throw new JorneeInputLengthException(context.getResources()
					.getString(R.string.error_input_length));
		}
		return true;
	}

}
