/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: DateTimeHelper.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.utils;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressLint("SimpleDateFormat")
public class DateTimeHelper {
	
	public static Date convertStringServerTimeToLocalDate(String fromServer) {
		Date fromGmt = null;
		try {
			SimpleDateFormat sdfgmt = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSS");
			Date inptdate = sdfgmt.parse(fromServer);
			fromGmt = new Date(inptdate.getTime()
					+ TimeZone.getDefault().getOffset(new Date().getTime()));
		} catch (ParseException ex) {
			Logger.getLogger(DateTimeHelper.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return fromGmt;
	}

	public static Date convertStringServerToServerDate(String fromServer) {
		Date inptdate = null;
		try {
			SimpleDateFormat sdfgmt = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSS");
			inptdate = sdfgmt.parse(fromServer);
		} catch (ParseException ex) {
			Logger.getLogger(DateTimeHelper.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return inptdate;
	}
	
	public static String convertStringServerToLocalString(String fromServer) {
        String result = null;
        try {
            SimpleDateFormat sdfgmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date inptdate = sdfgmt.parse(fromServer);
            Date fromGmt = new Date(inptdate.getTime() + TimeZone.getDefault().getOffset(new Date().getTime()));
            result = sdfgmt.format(fromGmt);
        } catch (ParseException ex) {
            Logger.getLogger(DateTimeHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
	
	public static Date convertLocalDateToServerDate(Date date) {
        String format = "yyyy/MM/dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gmtTime = new Date(sdf.format(date));
        return gmtTime;
    }
	
	public static String convertLocalDateToServerString(Date date) {
		String result = null;
        String format = "yyyy/MM/dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gmtTime = new Date(sdf.format(date));
        SimpleDateFormat sdfgmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        result = sdfgmt.format(gmtTime);
        return result;
    }
	
	public static String contextDateTime(Date date){
        String result = null;
        if(isToday(date)){
            String format = "hh:mma";
            result = new SimpleDateFormat(format)
                .format(date);
        } else if (isYesterday(date)){
            String format = "hh:mma";
            result = new SimpleDateFormat(format)
                .format(date);
            result += " yesterday";
        } else {
            String format = "hh:mma yyyy-MM-dd";
            result = new SimpleDateFormat(format)
                .format(date);
        }
        return result;
    }

    public static boolean isToday(Date date) {
        boolean result = false;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date);
        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
            result = true;
        }
        return result;
    }
    
    public static boolean isYesterday(Date date) {
        boolean result = false;
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.DAY_OF_YEAR, -1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date);
        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
            result = true;
        }
        return result;
    }
}
