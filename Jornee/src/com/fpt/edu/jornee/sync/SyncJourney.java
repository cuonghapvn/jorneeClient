/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: SyncJourney.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.sync;

import static com.fpt.edu.jornee.utils.Constant.SERVER_HOST;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.fpt.edu.bean.Entry;
import com.fpt.edu.bean.Journey;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.sqllite.DatabaseHandler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageButton;
import android.widget.Toast;

public class SyncJourney extends AsyncTask<Void, Void, JSONObject> {
	JSONObject jsonObject = null;
	InputStream is = null;
	String json;
	Journey journey;
	DatabaseHandler databaseHandler;
	Context context;
	SessionManager sessionManager;
	HashMap<String, String> user;
	ArrayList<Entry> currentListEntries;
	ArrayList<String> listEntriesID;
	String function;
	ImageButton callView;

	public SyncJourney(Journey journey, Context context, ImageButton v,
			String function) {
		this.journey = journey;
		this.context = context;
		databaseHandler = new DatabaseHandler(context);
		if (journey.getJourneyID() != null) {
			journey = databaseHandler.getJourneyInfor(journey.getJourneyID());
			currentListEntries = getEntryOfJourney(journey);
			listEntriesID = getEntriesID(currentListEntries);
		}
		sessionManager = new SessionManager(context);
		user = sessionManager.getUserDetails();
		this.function = function;
		if (v != null) {
			callView = v;
		}
	}

	@Override
	protected JSONObject doInBackground(Void... params) {
		try {
			System.out.println("Function: " + function);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("token", user
					.get(SessionManager.KEY_TOKEN)));
			if (!function.equals(Journey.JOURNEY_FUNCTION_DELETE)) {
				nameValuePairs.add(new BasicNameValuePair("journey_name",
						journey.getJourneyName()));
				nameValuePairs.add(new BasicNameValuePair("create_date",
						journey.getCreatedDate()));
				nameValuePairs.add(new BasicNameValuePair("entries", "["
						+ listEntriesID.get(1) + "]"));
				System.out.println("Function: " + listEntriesID.get(1));
				if (journey.getShared() != null) {
					if ("_private".equals(journey.getShared())) {
						nameValuePairs.add(new BasicNameValuePair("type",
								"private"));
						nameValuePairs.add(new BasicNameValuePair("user_id",
								"[]"));
					} else if ("_public".equals(journey.getShared())) {
						nameValuePairs.add(new BasicNameValuePair("type",
								"public"));
						nameValuePairs.add(new BasicNameValuePair("user_id",
								"[]"));
					} else if ("_friends".equals(journey.getShared())) {
						nameValuePairs.add(new BasicNameValuePair("type",
								"friends"));
						nameValuePairs.add(new BasicNameValuePair("user_id",
								"[]"));
					} else {
						nameValuePairs.add(new BasicNameValuePair("type",
								"specific"));
						nameValuePairs.add(new BasicNameValuePair("user_id",
								"["
										+ validShareStringToServer(journey
												.getShared()) + "]"));
					}
				} else {
					nameValuePairs
							.add(new BasicNameValuePair("type", "private"));
					nameValuePairs.add(new BasicNameValuePair("user_id", "[]"));
				}
			}
			if (!function.equals(Journey.JOURNEY_FUNCTION_CREATE)) {
				nameValuePairs.add(new BasicNameValuePair("server_id", journey
						.getServerJourneyID()));
			}

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(SERVER_HOST + function);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
			jsonObject = new JSONObject(json);
			System.out.println(jsonObject.toString());
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
				String journey_id = null;
				if (jsonObjectResult.has("status")) {
					status = jsonObjectResult.getString("status");
				}
				if (jsonObjectResult.has("journey_id")) {
					journey_id = jsonObjectResult.getString("journey_id");
				}
				if ("error".equals(authen_status)) {
					Toast.makeText(context,
							"Error when we're trying to recognize you!",
							Toast.LENGTH_SHORT).show();
				} else if ("fail".equals(authen_status)) {
					Toast.makeText(context,
							"You must login first to use this function!",
							Toast.LENGTH_SHORT).show();
				} else if ("ok".equals(authen_status)) {
					if ("error".equals(status)) {
						Toast.makeText(context,
								"We cannot complete your request!",
								Toast.LENGTH_SHORT).show();
					} else if ("ok".equals(status)) {
						if (Journey.JOURNEY_FUNCTION_CREATE.equals(function)) {
							String currentModifiedDate = journey
									.getModifiedDate();
							journey = databaseHandler.getJourneyInfor(journey
									.getJourneyID());
							journey.setServerJourneyID(journey_id);
							journey.setSyncedDate(currentModifiedDate);
							databaseHandler.updateJourney(journey);
						} else if (Journey.JOURNEY_FUNCTION_UPDATE
								.equals(function)) {
							String currentModifiedDate = journey
									.getModifiedDate();
							journey = databaseHandler.getJourneyInfor(journey
									.getJourneyID());
							journey.setSyncedDate(currentModifiedDate);
							if(journey_id!=null){
								journey.setServerJourneyID(journey_id);
							}
							databaseHandler.updateJourney(journey);
						} else if (Journey.JOURNEY_FUNCTION_DELETE
								.equals(function)) {
							if (journey.getJourneyID() != null) {
								journey.setServerJourneyID(null);
								journey.setSyncedDate(null);
								databaseHandler.updateJourney(journey);
							}
							Toast.makeText(context,
									"Journey is deleted from server!",
									Toast.LENGTH_SHORT).show();
						}
						if (callView != null) {
							journey = databaseHandler.getJourneyInfor(journey
									.getJourneyID());
							// if (isEntryInJourneyUpToDate(journey)) {
							callView.setImageResource(R.drawable.ic_action_ok_icon);
							callView.setEnabled(false);
							// } else {
							// callView.setBackgroundResource(R.drawable.btn_blue_button_selector);
							// callView.setImageResource(R.drawable.ic_sync_sync);
							// callView.setEnabled(true);
							// }
						}
					}
				}
			} else {

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String validShareStringToServer(String source) {
		StringBuilder stringBuilder = new StringBuilder();
		StringTokenizer stringTokenizer = new StringTokenizer(source, ",");
		String delimiter = "";
		while (stringTokenizer.hasMoreElements()) {
			stringBuilder.append(delimiter);
			delimiter = ",";
			stringBuilder.append("\""
					+ stringTokenizer.nextElement().toString() + "\"");
		}
		return stringBuilder.toString();
	}

	public boolean isEntryInJourneyUpToDate(Journey journey) {
		boolean result = true;
		StringTokenizer tokenizer = new StringTokenizer(journey.getEntriesID(),
				",");
		Entry fromDatabase;
		while (tokenizer.hasMoreElements()) {
			String id = (String) tokenizer.nextElement();
			fromDatabase = new Entry();
			fromDatabase = databaseHandler.getEntryInfor(id);
			if (fromDatabase != null) {
				if (!(fromDatabase.getEntrySyncStatus() == Entry.ENTRY_STATUS_UP_TO_DATE)) {
					result = false;
				}
			}
		}
		return result;

	}

	public ArrayList<String> getEntriesID(ArrayList<Entry> currentListEntries) {
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<Entry> onlyLocal = new ArrayList<Entry>();
		ArrayList<Entry> onlyServer = new ArrayList<Entry>();
		for (Entry entry : currentListEntries) {
			if (entry.getServerEntryID() == null
					|| entry.getServerEntryID().isEmpty()) {
				onlyLocal.add(entry);
			} else if (entry.getServerEntryID() != null
					&& entry.getServerEntryID().trim().length() > 0) {
				onlyServer.add(entry);
			}
		}
		String localID = listLocalEntryID(onlyLocal);
		System.out.println("localID" + localID);
		result.add(localID);
		String serverID = listServerEntryID(onlyServer);
		System.out.println("serverID" + serverID);
		result.add(serverID);
		return result;
	}

	public ArrayList<Entry> getEntryOfJourney(Journey journey) {
		ArrayList<Entry> result = new ArrayList<Entry>();
		StringTokenizer tokenizer = new StringTokenizer(journey.getEntriesID(),
				",");
		Entry fromDatabase;
		while (tokenizer.hasMoreElements()) {
			String id = (String) tokenizer.nextElement();
			fromDatabase = new Entry();
			fromDatabase = databaseHandler.getEntryInfor(id);
			if (fromDatabase != null) {
				result.add(fromDatabase);
			}
		}
		return result;
	}

	public String listLocalEntryID(ArrayList<Entry> currentListEntries) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < currentListEntries.size(); i++) {
			builder.append(currentListEntries.get(i).getEntryID());
			if (i < (currentListEntries.size() - 1)) {
				builder.append(",");
			}
		}
		return builder.toString();
	}

	public String listServerEntryID(ArrayList<Entry> currentListEntries) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < currentListEntries.size(); i++) {
			builder.append("\"" + currentListEntries.get(i).getServerEntryID()
					+ "\"");
			if (i < (currentListEntries.size() - 1)) {
				builder.append(",");
			}
		}
		return builder.toString();
	}

}
