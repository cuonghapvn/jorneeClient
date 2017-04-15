/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: GCMIntentService.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fpt.edu.bean.SocketNotificationEvent;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

	SessionManager sessionManager;
	String other_id = "";
	String type = "";
	String content_id = "";
	String time = "";
	String message = "Jornee message";
	static SocketNotificationEvent notificationEntity;

	public GCMIntentService() {
		super(Constant.SENDER_ID);
		notificationEntity = new SocketNotificationEvent();

	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {
	   sessionManager = new SessionManager(getApplicationContext());
		ServerUtilities.register(context, registrationId, sessionManager
				.getUserDetails().get(SessionManager.KEY_TOKEN));
	}

	/**
	 * Method called on device un registred
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		// displayMessage(context, getString(R.string.gcm_unregistered));

		// displayMessage(context, getString(R.string.gcm_unregistered));
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			sessionManager = new SessionManager(getApplicationContext());

			ServerUtilities.unregister(context, registrationId, sessionManager
					.getUserDetails().get(SessionManager.KEY_TOKEN));

			Log.i(TAG, "Device unregistered");
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(TAG, "Ignoring unregister callback");
		}

	}

	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
		other_id = intent.getExtras().getString("other_id");
		type = intent.getExtras().getString("type");
		content_id = intent.getExtras().getString("content_id");
		time = intent.getExtras().getString("time");
		notificationEntity.setOther_id(other_id);
		notificationEntity.setType(type);
		notificationEntity.setTime(time);
		generateNotification(context, message, type, content_id, time);
	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		generateNotification(context, message, type, content_id, time);
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		// displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		// displayMessage(context, getString(R.string.gcm_recoverable_error,
		// errorId));
		return super.onRecoverableError(context, errorId);
	}

	@SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message,
			String type, String contentid, String time) {

		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, notificationEntity.getNotificationMessage() , when);
		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, notificationEntity.getTypeString(), notificationEntity.getNotificationMessage(), intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);

	}

}
