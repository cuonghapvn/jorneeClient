/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HostProfileViewFeedbackFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.host;

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
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.JSONParser;

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

public final class HostProfileViewFeedbackFragment extends Fragment {

	public static final String KEY_USERNAME = "username";
	public static final String KEY_FEEDBACK = "feedback";

	SessionManager session;
	JSONParser parser;
	Context context;
	ListView mListViewFeedbacks;

	public static Fragment newInstance() {
		HostProfileViewFeedbackFragment fragment = new HostProfileViewFeedbackFragment();

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(
				R.layout.fragment_host_profile_view_feedback_layout, container,
				false);
		if (isAdded()) {
			context = getActivity().getApplicationContext();
		}
		session = new SessionManager(context);
		parser = new JSONParser(context);
		mListViewFeedbacks = (ListView) rootView
				.findViewById(R.id.host_profile_list_feedback);
		LoadHostProfileTips hostProfileTips = new LoadHostProfileTips();
		hostProfileTips.execute();
		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private class LoadHostProfileTips extends
			AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... input) {

			JSONObject jsonObject = null;

			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);

				nameValuePairs.add(new BasicNameValuePair("token", session
						.getUserDetails().get(SessionManager.KEY_TOKEN)));

				jsonObject = parser.makeHttpRequest(SERVER_HOST
						+ "host_profile", "POST", nameValuePairs);

			} catch (Exception exception) {

				return null;
			}

			return jsonObject;

		}

		@Override
		protected void onPostExecute(JSONObject result) {

			try {
				ArrayList<HashMap<String, String>> arrayListTips = new ArrayList<HashMap<String, String>>();

				if (result != null) {

					JSONArray array = result.getJSONArray("feedback");

					if (array.length() > 0) {

						for (int i = 0; i < array.length(); i++) {

							HashMap<String, String> map = new HashMap<String, String>();
							map.put(KEY_USERNAME, array.getJSONObject(i)
									.getString("username"));

							map.put(KEY_FEEDBACK, array.getJSONObject(i)
									.getString("feedback"));
							arrayListTips.add(map);
						}
					} else {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(KEY_USERNAME,
								"You have no feedback. Improve your profile to get more !");
						arrayListTips.add(map);
					}

					if (isAdded()) {
						HostListFeedbacksAdapter adapter = new HostListFeedbacksAdapter(
								getActivity(), arrayListTips);
						mListViewFeedbacks.setAdapter(adapter);
					}

				}
			} catch (Exception exception) {
			}
		}
	}

}
