/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HostOtherProfileViewFeedbackFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.otherhost;

import static com.fpt.edu.jornee.utils.Constant.SERVER_HOST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.R.layout;
import com.fpt.edu.jornee.host.HostListFeedbacksAdapter;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.JSONParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public final class HostOtherProfileViewFeedbackFragment extends Fragment {

	public static final String KEY_USERNAME = "username";
	public static final String KEY_FEEDBACK = "feedback";

	SessionManager session;
	JSONParser parser;
	Context context;
	ListView mListViewFeedbacks;
	String username;

	public static Fragment newInstance() {
		HostOtherProfileViewFeedbackFragment fragment = new HostOtherProfileViewFeedbackFragment();

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if (bundle != null && bundle.containsKey("host_username_other")) {

			username = bundle.getString("host_username_other");
		}
		View rootView = inflater.inflate(
				R.layout.fragment_host_profile_view_feedback_layout, container,
				false);
		context = getActivity().getApplicationContext();
		session = new SessionManager(context);
		parser = new JSONParser(context);
		mListViewFeedbacks = (ListView) rootView
				.findViewById(R.id.host_profile_list_feedback);
		LoadOtherHostProfileFeedback hostProfileTips = new LoadOtherHostProfileFeedback();
		hostProfileTips.execute();
		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private class LoadOtherHostProfileFeedback extends
			AsyncTask<String, Void, JSONObject> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected JSONObject doInBackground(String... input) {

			JSONObject jsonObject = null;

			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);

				nameValuePairs.add(new BasicNameValuePair("token", session
						.getUserDetails().get(SessionManager.KEY_TOKEN)));

				nameValuePairs.add(new BasicNameValuePair("host_username",
						username));

				jsonObject = parser.makeHttpRequest(SERVER_HOST
						+ "other_host_profile", "POST", nameValuePairs);

				return jsonObject;

			} catch (Exception exception) {

				return jsonObject;

			}

		}

		@Override
		protected void onPostExecute(JSONObject result) {

			if (null != result) {
				try {
					if ("ok".equals(result.getString("authen_status"))) {

						if ("ok".equals(result.getString("status"))) {

							try {
								ArrayList<HashMap<String, String>> arrayListTips = new ArrayList<HashMap<String, String>>();

								if (result != null) {

									JSONArray array = result
											.getJSONArray("feedback");

									if (array.length() > 0) {

										for (int i = 0; i < array.length(); i++) {

											HashMap<String, String> map = new HashMap<String, String>();
											map.put(KEY_USERNAME, array
													.getJSONObject(i)
													.getString("username"));

											map.put(KEY_FEEDBACK, array
													.getJSONObject(i)
													.getString("feedback"));
											arrayListTips.add(map);

										}
									} else {
										HashMap<String, String> map = new HashMap<String, String>();
										map.put(KEY_USERNAME,
												"No available feedback for this host . Be the first one rate and feedback for this host !");
										arrayListTips.add(map);
									}

									OtherHostListFeedbacksAdapter adapter = new OtherHostListFeedbacksAdapter(
											getActivity(), arrayListTips);
									mListViewFeedbacks.setAdapter(adapter);

								}
							} catch (Exception exception) {
							}
						}

					}
				} catch (JSONException e) {

				}

			}
		}
	}

}
