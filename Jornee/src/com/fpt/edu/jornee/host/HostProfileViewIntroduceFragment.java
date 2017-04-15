/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HostProfileViewIntroduceFragment.java
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
import com.fpt.edu.jornee.account.LoginFragment;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.JSONParser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
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
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public final class HostProfileViewIntroduceFragment extends Fragment {

	EditText introduce;
	EditText aboutPlace;
	SessionManager session;
	JSONParser parser;
	Context context;
	TextView btnEdit;

	public static Fragment newInstance() {
		HostProfileViewIntroduceFragment fragment = new HostProfileViewIntroduceFragment();
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
				R.layout.fragment_host_profile_view_introduce_layout,
				container, false);
		btnEdit = (TextView) rootView
				.findViewById(R.id.host_profile_view_introduce_btnSubmit);
		btnEdit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isAdded()) {
					new AlertDialog.Builder(getActivity())
							.setMessage(
									"Are you sure you want to update your information ?")
							.setCancelable(false)
							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

											LoadEditIntroduceResult editIntroduceResult = new LoadEditIntroduceResult();
											String[] data = new String[2];
											data[0] = introduce.getText()
													.toString();
											data[1] = aboutPlace.getText()
													.toString();
											editIntroduceResult.execute(data);

										}
									}).setNegativeButton("No", null).show();

				}
			}
		});
		if (isAdded()) {
			context = getActivity().getApplicationContext();
		}
		session = new SessionManager(context);
		parser = new JSONParser(context);
		introduce = (EditText) rootView
				.findViewById(R.id.host_profile_view_introduce);
		aboutPlace = (EditText) rootView
				.findViewById(R.id.host_profile_view_introduce_about_place);
		LoadHostProfileIntroduce hostProfileIntroduce = new LoadHostProfileIntroduce();
		hostProfileIntroduce.execute();

		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private class LoadHostProfileIntroduce extends
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

				return jsonObject;

			} catch (Exception exception) {

			}

			return jsonObject;
		}

		@Override
		protected void onPostExecute(JSONObject result) {

			try {
				introduce.setText(result.getString("description"));
				aboutPlace.setText(result.getString("about_place"));

			} catch (Exception exception) {

			}
		}
	}

	private class LoadEditIntroduceResult extends
			AsyncTask<String, Void, JSONObject> {

		String updatedIntroduce = "";
		String updatedAboutPlace = "";

		@Override
		protected JSONObject doInBackground(String... data) {

			JSONObject jsonObject = null;
			try {
				// {token: "token", description: "description"}

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);

				nameValuePairs.add(new BasicNameValuePair("token", session
						.getUserDetails().get(SessionManager.KEY_TOKEN)));

				nameValuePairs.add(new BasicNameValuePair("description",
						data[0]));
				nameValuePairs.add(new BasicNameValuePair("about_place",
						data[1]));
				// TODO /edit_host_description

				updatedIntroduce = data[0];
				updatedAboutPlace = data[1];

				jsonObject = parser.makeHttpRequest(Constant.SERVER_HOST
						+ "edit_host_description", "POST", nameValuePairs);
				// Getting the parsed data as a List construct

			} catch (Exception e) {
				Log.d("Exception", e.toString());

				return null;
			}
			return jsonObject;
		}

		@Override
		protected void onPostExecute(JSONObject result) {

			if (null != result) {
				try {
					if ("ok".equals(result.getString("authen_status"))) {

						if ("ok".equals(result.getString("status"))) {

							introduce.setText(updatedIntroduce);
							aboutPlace.setText(updatedAboutPlace);
						} else if ("fail".equals(result.getString("status"))) {

						} else if ("error".equals(result.getString("status"))) {

						}

					} else if ("fail".equals(result.getString("authen_status"))) {
					} else if ("error"
							.equals(result.getString("authen_status"))) {

					}
				} catch (JSONException e) {

				}

			} else {

			}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
		}
		// Checks whether a hardware keyboard is available
		if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
			Log.e("Keyboad", "visible");
		} else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
			Log.e("Keyboad", "hidden");

		}
	}
}
