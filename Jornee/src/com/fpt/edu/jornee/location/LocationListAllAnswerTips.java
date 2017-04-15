/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: LocationListAllAnswerTips.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.location;

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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fpt.edu.bean.HostInLocation;
import com.fpt.edu.bean.Location;
import com.fpt.edu.bean.TipsQuestion;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.account.LoginFragment;
import com.fpt.edu.jornee.otherhost.HostOtherProfileFragment;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.JSONParser;

public class LocationListAllAnswerTips extends Fragment {

	public static String KEY_HOST_USER_ID = "user_id";
	public static String KEY_HOST_AVATAR = "avatar";
	public static String KEY_HOST_TIP = "tip";
	JSONParser parser;
	Context context;
	SessionManager session;
	ListView listAllAnswer;
	String questionID = "";
	Button btnBack;
	Location prcLocation;
	ArrayList<TipsQuestion> questions;


	ProgressBar progressBar1Answer;
	public LocationListAllAnswerTips() {
		// Empty constructor required for fragment subclasses
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_location_list_all_answer_tips, container,
				false);

		progressBar1Answer = (ProgressBar) rootView.findViewById(R.id.progressBar1Answer);
		setHasOptionsMenu(true);
		context = getActivity().getApplicationContext();
		parser = new JSONParser(context);
		session = new SessionManager(context);
		Bundle extras = getArguments();
		if (extras != null) {
			questionID = extras.getString("QuestionID");

			prcLocation = extras.getParcelable("detail_location");

			questions = extras.getParcelableArrayList("all_questions");
			prcLocation = extras.getParcelable("location_detail");
			progressBar1Answer.setVisibility(View.GONE);
			
			getActivity().setTitle(
					extras.getString(Constant.FRAGMENT_TITLE_BUNDLE));
		

		}
		btnBack = (Button) rootView
				.findViewById(R.id.location_list_all_answer_tip_btnBack);
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Fragment fragment = new LocationListAllQuestionTips();
				Bundle extras1 = new Bundle();
				extras1.putParcelableArrayList("all_questions", questions);
				extras1.putParcelable("location_detail", prcLocation);
				fragment.setArguments(extras1);
				MainActivity activity = (MainActivity) getActivity();
				activity.replaceFragment(fragment);
			}
		});

		listAllAnswer = (ListView) rootView
				.findViewById(R.id.location_list_all_answer_tip);

		// listAllAnswer.setAdapter(adapter);
		LoadResultListAnswerTips answerTips = new LoadResultListAnswerTips();
		answerTips.execute();
		return rootView;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.outside, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	public void callLogin() {

		Fragment fragment = new LoginFragment();

		MainActivity activity = (MainActivity) getActivity();
		activity.replaceFragment(fragment);
	}

	private class LoadResultListAnswerTips extends
			AsyncTask<String, Void, JSONObject> {

		@Override
		protected void onPreExecute() {

			listAllAnswer.setVisibility(View.GONE);
			progressBar1Answer.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected JSONObject doInBackground(String... location) {

			JSONObject jsonObject = null;

			if (location != null) {

				try {

					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							5);

					nameValuePairs.add(new BasicNameValuePair("token", session
							.getUserDetails().get(SessionManager.KEY_TOKEN)));

					nameValuePairs.add(new BasicNameValuePair("question_id",
							questionID));
					nameValuePairs.add(new BasicNameValuePair("page", "1"));

					nameValuePairs.add(new BasicNameValuePair("country",
							prcLocation.getCountry()));
					nameValuePairs.add(new BasicNameValuePair(
							"administrative_area_level_1", prcLocation
									.getAdministrative_area_level_1()));
					nameValuePairs.add(new BasicNameValuePair("locality",
							prcLocation.getLocality()));

					jsonObject = parser.makeHttpRequest(Constant.SERVER_HOST
							+ "all_tips", "POST", nameValuePairs);
					// Getting the parsed data as a List construct

				} catch (Exception e) {
					Log.d("Exception", e.toString());

					return null;
				}
			}
			return jsonObject;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			
			if (null != result) {
				try {
					if ("ok".equals(result.getString("authen_status"))) {

						if ("ok".equals(result.getString("status"))) {


							listAllAnswer.setVisibility(View.VISIBLE);
							progressBar1Answer.setVisibility(View.GONE);
							JSONArray array = result.getJSONArray("hosts");

							ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

							for (int i = 0; i < array.length(); i++) {
								JSONObject object = array.getJSONObject(i);
								HashMap<String, String> hostAnswer = new HashMap<String, String>();
								hostAnswer.put(KEY_HOST_AVATAR,
										object.getString(KEY_HOST_AVATAR));
								hostAnswer.put(KEY_HOST_TIP,
										object.getString(KEY_HOST_TIP));
								hostAnswer.put(KEY_HOST_USER_ID,
										object.getString(KEY_HOST_USER_ID));
								data.add(hostAnswer);
							}

							if (isAdded()) {
								LocationListAllAnswerAdapter adapter = new LocationListAllAnswerAdapter(
										getActivity(), data);

								listAllAnswer.setAdapter(adapter);

							}

							listAllAnswer
									.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> parent,
												View view, int position, long id) {

											@SuppressWarnings("unchecked")
											HashMap<String, String> map = (HashMap<String, String>) parent
													.getItemAtPosition(position);
											Fragment fragment = new HostOtherProfileFragment();
											Bundle bundle = new Bundle();
											bundle.putString("host_username",
													map.get(KEY_HOST_USER_ID));
											
											bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,"Host Profile");

											fragment.setArguments(bundle);
											MainActivity activity = (MainActivity) getActivity();
											activity.replaceFragment(fragment);
										}
									});
						} else if ("fail".equals(result.getString("status"))) {

							Toast.makeText(context,
									"This location currently have no host !",
									Toast.LENGTH_LONG).show();

						} else if ("error".equals(result.getString("status"))) {

						}

					} else if ("fail".equals(result.getString("authen_status"))) {
						callLogin();
					} else if ("error"
							.equals(result.getString("authen_status"))) {

					}
				} catch (JSONException e) {

				}

			}

		}
	}

}
