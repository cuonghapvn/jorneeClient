/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: FollowAsync1.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.account;

import static com.fpt.edu.jornee.utils.Constant.SERVER_HOST;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.JSONParser;

public class FollowAsync1 extends AsyncTask<Object, Void, JSONObject> {
	JSONObject jsonObject = null;
	InputStream is = null;
	String json;
	String action;
	String username;
	View inputView;
	Context context;
	MainActivity activity;
	HashMap<String, String> user;

	@SuppressWarnings("unchecked")
	@Override
	protected JSONObject doInBackground(Object... params) {
		try {
			action = (String) params[1];
			username = (String) params[0];
			inputView = (View) params[2];
			context = (Context) params[3];
			activity = (MainActivity) params[5];
			user = (HashMap<String, String>) params[4];
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("token", user
					.get(SessionManager.KEY_TOKEN)));
			nameValuePairs.add(new BasicNameValuePair("friend_id", username));
			String function;
			if ("Follow".equals(action)) {
				function = "follow";
			} else {
				function = "unfollow";
			}

			JSONParser jsonParser = new JSONParser(context);
			jsonObject = jsonParser.makeHttpRequest(SERVER_HOST + function, "POST", nameValuePairs);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

	@SuppressLint("NewApi")
	@Override
	protected void onPostExecute(JSONObject result) {
		try {
			if (result != null) {
				JSONObject jsonObjectResult = result;
				String authen_status = jsonObjectResult
						.getString("authen_status");
				String status = null;
				if (jsonObjectResult.has("status")) {
					status = jsonObjectResult.getString("status");
				}
				if ("error".equals(authen_status)) {
					inputView.setEnabled(true);
					if ("Follow".equals(action)) {
						((TextView) inputView).setText("Follow");
						inputView.setBackground(context.getResources()
								.getDrawable(R.drawable.btn_green_button));
					} else {
						((TextView) inputView).setText("Unfollow");
						inputView.setBackground(context.getResources()
								.getDrawable(R.drawable.btn_blue_button));
					}
					Toast.makeText(context,
							"Error when we're trying to recognize you!",
							Toast.LENGTH_SHORT).show();
				} else if ("fail".equals(authen_status)) {
					inputView.setEnabled(true);
					if ("Follow".equals(action)) {
						((TextView) inputView).setText("Follow");
						inputView.setBackground(context.getResources()
								.getDrawable(R.drawable.btn_green_button));
					} else {
						((TextView) inputView).setText("Unfollow");
						inputView.setBackground(context.getResources()
								.getDrawable(R.drawable.btn_blue_button));
					}
					Toast.makeText(context,
							"You must login first to use this function!",
							Toast.LENGTH_SHORT).show();
				} else if ("ok".equals(authen_status)) {
					if ("error".equals(status)) {
						inputView.setEnabled(true);
						if ("Follow".equals(action)) {
							((TextView) inputView).setText("Follow");
							inputView.setBackground(context.getResources()
									.getDrawable(R.drawable.btn_green_button));
						} else {
							((TextView) inputView).setText("Unfollow");
							inputView.setBackground(context.getResources()
									.getDrawable(R.drawable.btn_blue_button));
						}
						Toast.makeText(context,
								"We cannot complete your request!",
								Toast.LENGTH_SHORT).show();
					} else if ("ok".equals(status)) {
						inputView.setEnabled(true);
						activity.updateFollowInfor();
						if ("Follow".equals(action)) {
							((TextView) inputView).setText("Unfollow");
							inputView.setBackground(context.getResources()
									.getDrawable(R.drawable.btn_green_button));
						} else {
							((TextView) inputView).setText("Follow");
							inputView.setBackground(context.getResources()
									.getDrawable(R.drawable.btn_blue_button));
						}
					}
				}
			} else {
				inputView.setEnabled(true);
				if ("Follow".equals(action)) {
					((TextView) inputView).setText("Follow");
					inputView.setBackground(context.getResources().getDrawable(
							R.drawable.btn_green_button));
				} else {
					((TextView) inputView).setText("Unfollow");
					inputView.setBackground(context.getResources().getDrawable(
							R.drawable.btn_blue_button));
				}
				Toast.makeText(
						context,
						"Error occur due to server's failures! Please try later!",
						Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
