/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: SyncService.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.sync;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.fpt.edu.bean.Entry;
import com.fpt.edu.bean.Journey;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.sqllite.DatabaseHandler;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.MyMultipartEntity;
import com.fpt.edu.jornee.utils.MyMultipartEntity.ProgressListener;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class SyncService extends Service {
	private static boolean isRunning = false;
	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_SET_INT_VALUE = 3;
	public static final int MSG_SET_STRING_VALUE = 4;
	public static final int NOTI_ID_JOURNEY = 101;
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	DatabaseHandler databaseHandler;
	ArrayList<Journey> journeys = new ArrayList<Journey>();
	ArrayList<Journey> journeysHolding = new ArrayList<Journey>();
	boolean uploadRunning = false;
	boolean processRunning = false;
	boolean journeyProcessRunning = false;

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("SyncService", "Received start id " + startId + ": " + intent);
		databaseHandler = new DatabaseHandler(getApplicationContext());
		Journey journey = null;
		if (intent != null && intent.hasExtra("journeyID")) {
			journey = databaseHandler.getJourneyInfor(intent
					.getStringExtra("journeyID"));
		}
		if (journey != null) {
			if (!uploadRunning) {
				journeys.add(journey);
				processUpload();
			} else {
				journeysHolding.add(journey);
			}
		}
		return START_STICKY;
	}

	public void processUpload() {
		uploadRunning = true;
		int count = 0;
		Journey journey;
		while (count < journeys.size()) {
			journey = journeys.get(count);
			processJourney(journey);
			count++;
		}
		if (journeysHolding.size() > 0) {
			journeys.clear();
			journeys.addAll(journeysHolding);
			journeysHolding.clear();
			processUpload();
		}
		uploadRunning = false;
	}

	public boolean processJourney(Journey journey) {
		boolean result = false;
		if (journeyProcessRunning) {
			result = processJourney(journey);
		} else {
			journeyProcessRunning = true;
			ArrayList<Entry> entries = getEntryOfJourney(journey);
			for (Entry entry : entries) {
				if (entry.getEntrySyncStatus() != Entry.ENTRY_STATUS_UP_TO_DATE) {
					processEntry(entry);
				}
			}
			journeyProcessRunning = false;
			result = true;
		}
		return result;
	}

	public boolean processEntry(Entry entry) {
		boolean result = false;
		if (processRunning) {
			result = processEntry(entry);
		} else {
			SyncEntry syncEntry = new SyncEntry(getApplicationContext(), entry);
			syncEntry.execute();
			result = true;
		}
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

	class SyncEntry extends AsyncTask<Void, Integer, JSONObject> {
		int serverResponseCode = 0;

		private Context context;
		private Entry entry;
		InputStream is = null;
		JSONObject jObj = null;
		String json = "";
		private HttpClient client;

		SessionManager sessionManager;
		NotificationManager mNotifyManager;
		NotificationCompat.Builder mBuilder;
		DatabaseHandler databaseHandler;
		private long totalSize;

		private static final String url = Constant.SERVER_HOST + "create_entry";

		public SyncEntry(Context context, Entry entry) {
			super();
			this.context = context;
			this.entry = entry;
			sessionManager = new SessionManager(context);
			databaseHandler = new DatabaseHandler(context);
		}

		@Override
		protected void onPreExecute() {
			int timeout = 600000;
			client = new DefaultHttpClient();
			client.getParams().setParameter("Connection", "Keep-Alive");
			client.getParams().setParameter("Content-Type",
					"multipart/form-data;");
			client.getParams().setParameter("http.socket.timeout", timeout);
			client.getParams().setParameter("http.connection.timeout", timeout);
			mNotifyManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mBuilder = new NotificationCompat.Builder(context);
			mBuilder.setContentTitle("Upload entry id: " + entry.getEntryID())
					.setContentText("Upload in progress")
					.setSmallIcon(R.drawable.ic_launcher);
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			try {

				HttpPost post = new HttpPost(url);
				MultipartEntity entity = new MyMultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE, null,
						Charset.forName("UTF-8"), new ProgressListener() {
							@Override
							public void transferred(long num) {
								publishProgress((int) ((num / (float) totalSize) * 100));
								Log.d("DEBUG", num + " - " + totalSize);
							}
						});
				if (entry.getPath() != null) {
					File file = new File(entry.getPath());
					if (entry.getEntrySyncStatus() != Entry.ENTRY_STATUS_LOCAL_ONLY) {
						if (entry.getChangedElement().equals("both")
								|| entry.getChangedElement().equals("image")) {
							if (file.isFile()) {
								ContentBody cbFile = new FileBody(file,
										"image/jpeg");
								entity.addPart("upload", cbFile);
							}
						}
					} else if (file.isFile()) {
						ContentBody cbFile = new FileBody(file, "image/jpeg");
						entity.addPart("upload", cbFile);
					}
				}
				entity.addPart("token", new StringBody(sessionManager
						.getUserDetails().get(SessionManager.KEY_TOKEN),
						Charset.forName("UTF-8")));
				entity.addPart(
						"type",
						new StringBody(entry.getType() == null ? "" : entry
								.getType(), Charset.forName("UTF-8")));
				entity.addPart(
						"text",
						new StringBody(entry.getText() == null ? "" : entry
								.getText(), Charset.forName("UTF-8")));
				if (entry.getPosition() != null) {
					entity.addPart("coordinate",
							new StringBody(
									"[" + entry.getPosition().latitude + ","
											+ entry.getPosition().longitude
											+ "]", Charset.forName("UTF-8")));
				} else {
					entity.addPart("coordinate",
							new StringBody("[]", Charset.forName("UTF-8")));
				}

				entity.addPart(
						"entry_id",
						new StringBody(entry.getServerEntryID() == null ? ""
								: entry.getServerEntryID(), Charset
								.forName("UTF-8")));

				entity.addPart(
						"place_name",
						new StringBody(entry.getPlaceName() == null ? ""
								: entry.getPlaceName(), Charset
								.forName("UTF-8")));
				entity.addPart(
						"create_date",
						new StringBody(entry.getDateTime() == null ? "" : entry
								.getDateTime(), Charset.forName("UTF-8")));
				entity.addPart(
						"journey_id",
						new StringBody(entry.getJourneyID() == null ? "[]"
								: "["
										+ validJourneyIDString(entry
												.getJourneyID()) + "]"));

				System.out.println("Function: " + validJourneyIDString(entry
						.getJourneyID()));
				entity.addPart(
						"street_number",
						new StringBody(entry.getStreet_number() == null ? ""
								: entry.getStreet_number(), Charset
								.forName("UTF-8")));
				entity.addPart("route", new StringBody(
						entry.getRoute() == null ? "" : entry.getRoute(),
						Charset.forName("UTF-8")));
				entity.addPart(
						"sublocality",
						new StringBody(entry.getSubLocality() == null ? ""
								: entry.getSubLocality(), Charset
								.forName("UTF-8")));
				entity.addPart("locality", new StringBody(
						entry.getLocality() == null ? "" : entry.getLocality(),
						Charset.forName("UTF-8")));
				entity.addPart("administrative_area_level_2", new StringBody(
						entry.getAdministrative_area_level_2() == null ? ""
								: entry.getAdministrative_area_level_2(),
						Charset.forName("UTF-8")));
				entity.addPart("administrative_area_level_1", new StringBody(
						entry.getAdministrative_area_level_1() == null ? ""
								: entry.getAdministrative_area_level_1(),
						Charset.forName("UTF-8")));
				entity.addPart("country", new StringBody(
						entry.getCountry() == null ? "" : entry.getCountry(),
						Charset.forName("UTF-8")));

				totalSize = entity.getContentLength();
				post.setEntity(entity);
				HttpResponse response = client.execute(post);
				HttpEntity httpEntity = response.getEntity();
				is = httpEntity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();
				jObj = new JSONObject(json);
				System.out.println(jObj.toString());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			return jObj;
		}

		public String validJourneyIDString(String source) {
			StringTokenizer stringTokenizer = new StringTokenizer(source, ",");
			Journey journey;
			StringBuilder builder = new StringBuilder();
			String delim = "";
			while (stringTokenizer.hasMoreElements()) {
				String object = stringTokenizer.nextElement().toString();
				journey = databaseHandler.getJourneyInfor(object);
				if (journey != null && journey.getServerJourneyID() != null) {
					builder.append(delim);
					builder.append("\"" + journey.getServerJourneyID() + "\"");
					delim = ",";
				}
			}
			return builder.toString();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			processRunning = true;
			mBuilder.setProgress(100, progress[0], false);
			// Displays the progress bar for the first time.
			mNotifyManager.notify(0, mBuilder.build());
			// pd.setProgress((int) (progress[0]));
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// pd.dismiss();
			mBuilder.setContentText("Upload complete").setOngoing(false)
					.setProgress(0, 0, false);
			mNotifyManager.notify(0, mBuilder.build());
			mNotifyManager.cancelAll();
			processRunning = false;
			try {
				if (result != null) {
					JSONObject jsonObjectResult = result;
					String authen_status = jsonObjectResult
							.getString("authen_status");
					String status = null;
					String entry_id = null;
					if (jsonObjectResult.has("status")) {
						status = jsonObjectResult.getString("status");
					}
					if (jsonObjectResult.has("entry_id")) {
						entry_id = jsonObjectResult.getString("entry_id");
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
							Entry entryToSave = databaseHandler
									.getEntryInfor(entry.getEntryID());
							if (entry_id != null) {
								entryToSave.setServerEntryID(entry_id);
							}
							entryToSave.setSyncDate(entry.getModifiedDate());
							entryToSave.setChangedElement("none");
							databaseHandler.updateEntry(entryToSave);
						}
					}
				} else {

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("MyService", "Service Started.");
		isRunning = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("MyService", "Service Stopped.");
		isRunning = false;
	}

	public static boolean isRunning() {
		return isRunning;
	}

	@SuppressLint("HandlerLeak")
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				break;
			case MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				break;
			case MSG_SET_INT_VALUE:
				Log.i("MyService", "Receice from Broadcast: " + msg.arg1);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

}
