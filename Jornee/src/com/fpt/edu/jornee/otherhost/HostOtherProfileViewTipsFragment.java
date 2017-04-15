/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HostOtherProfileViewTipsFragment.java
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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.JSONParser;

public final class HostOtherProfileViewTipsFragment extends Fragment {

	public static final String KEY_QUESTION = "question";
	public static final String KEY_ANSWER = "answer";
	String username;
	SessionManager session;
	JSONParser parser;
	Context context;
	ListView mListViewTips;

	public static Fragment newInstance() {
		HostOtherProfileViewTipsFragment fragment = new HostOtherProfileViewTipsFragment();
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
				R.layout.fragment_other_host_profile_view_tips_layout,
				container, false);
		context = getActivity().getApplicationContext();
		session = new SessionManager(context);
		parser = new JSONParser(context);
		mListViewTips = (ListView) rootView
				.findViewById(R.id.host_profile_list_tips);
		LoadOtherHostProfileTips hostProfileTips = new LoadOtherHostProfileTips();
		hostProfileTips.execute(username);
		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private class LoadOtherHostProfileTips extends
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
											.getJSONArray("tips");

									for (int i = 0; i < array.length(); i++) {

										HashMap<String, String> map = new HashMap<String, String>();
										map.put(KEY_QUESTION,
												array.getJSONObject(i)
														.getString("question"));

										map.put(KEY_ANSWER,
												array.getJSONObject(i)
														.getString("answer"));
										arrayListTips.add(map);
									}

									OtherHostListTipsAdapter adapter = new OtherHostListTipsAdapter(
											getActivity(), arrayListTips);
									mListViewTips.setAdapter(adapter);

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
