/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HostOtherProfileViewIntroduceFragment.java
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

import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.R.layout;
import com.fpt.edu.jornee.account.LoginFragment;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.JSONParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public final class HostOtherProfileViewIntroduceFragment extends Fragment {

	TextView introduce;
	TextView aboutPlace;
	SessionManager session;
	JSONParser parser;
	Context context;
	String username;

	public static Fragment newInstance() {
		HostOtherProfileViewIntroduceFragment fragment = new HostOtherProfileViewIntroduceFragment();
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
				R.layout.fragment_other_host_profile_view_introduce_layout,
				container, false);

		context = getActivity().getApplicationContext();
		session = new SessionManager(context);
		parser = new JSONParser(context);
		introduce = (TextView) rootView
				.findViewById(R.id.host_profile_view_introduce);
		aboutPlace = (TextView) rootView
				.findViewById(R.id.host_profile_view_introduce_about_place);
		LoadOtherHostProfileIntroduce hostProfileIntroduce = new LoadOtherHostProfileIntroduce();
		hostProfileIntroduce.execute();
		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private class LoadOtherHostProfileIntroduce extends
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

							introduce.setText(result.getString("description"));
							aboutPlace.setText(result.getString("about_place"));

						}

					}
				} catch (JSONException e) {

				}

			}
		}
	}

	public void callLogin() {

		if (isAdded()) {
			Fragment fragment0 = new LoginFragment();

			MainActivity activity = (MainActivity) getActivity();
			activity.replaceFragment(fragment0);
		}
	}
}
