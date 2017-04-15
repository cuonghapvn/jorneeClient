/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HostProfileViewTipsFragment.java
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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.fpt.edu.bean.TipsQuestion;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.exception.JorneeException;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.JSONParser;

public final class HostProfileViewTipsFragment extends Fragment {

	public static final String KEY_QUESTION = "question";
	public static final String KEY_ANSWER = "answer";

	SessionManager session;
	JSONParser parser;
	Context context;
	ListView mListViewTips;
	TextView btnEdit;
	ArrayList<HashMap<String, String>> newDataMap;

	public static Fragment newInstance() {
		HostProfileViewTipsFragment fragment = new HostProfileViewTipsFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isAdded()) {
			context = getActivity().getApplicationContext();
		}
		session = new SessionManager(context);
		parser = new JSONParser(context);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(
				R.layout.fragment_host_profile_view_tips_layout, container,
				false);

		mListViewTips = (ListView) rootView
				.findViewById(R.id.host_profile_list_tips);
		btnEdit = (TextView) rootView
				.findViewById(R.id.host_profile_view_tips_btn_submit);
		// btnEdit.setVisibility(View.GONE);
		HostListTipsAdapter adapter = (HostListTipsAdapter) mListViewTips
				.getAdapter();
		if (adapter == null) {
			btnEdit.setVisibility(View.VISIBLE);

		}
		if (HostListTipsAdapter.isTextChanged) {

			btnEdit.setVisibility(View.VISIBLE);
		}
		btnEdit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isAdded()) {
					HostListTipsAdapter adapter = (HostListTipsAdapter) mListViewTips
							.getAdapter();
					if (adapter != null) {
						newDataMap = adapter.data;

						new AlertDialog.Builder(getActivity())
								.setMessage(
										"Are you sure you want to update your information ?")
								.setCancelable(false)
								.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {

												submitTipQuestion();
											}
										}).setNegativeButton("No", null).show();

					}
				}

			}
		});
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
		protected void onPreExecute() {
			if (isAdded()) {
			}
			super.onPreExecute();
		}

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
				ArrayList<HashMap<String, String>> arrayListTips = new ArrayList<HashMap<String, String>>();
				if (result != null) {
					JSONArray array = result.getJSONArray("tips");
					for (int i = 0; i < array.length(); i++) {

						HashMap<String, String> map = new HashMap<String, String>();
						map.put(KEY_QUESTION,
								array.getJSONObject(i).getString("question"));

						map.put(KEY_ANSWER,
								array.getJSONObject(i).getString("answer"));
						arrayListTips.add(map);
					}
					if (isAdded()) {
						HostListTipsAdapter adapter = new HostListTipsAdapter(
								getActivity(), arrayListTips);

						mListViewTips.setAdapter(adapter);
					}
				}
			} catch (Exception exception) {

			}
		}
	}

	private class Holder {
		private ArrayList<TipsQuestion> tipsQuestions;

		public ArrayList<TipsQuestion> getTipsQuestions() {
			return tipsQuestions;
		}

		public void setTipsQuestions(ArrayList<TipsQuestion> tipsQuestions) {
			this.tipsQuestions = tipsQuestions;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();

			builder.append("[");

			for (TipsQuestion tipsQuestion : tipsQuestions) {

				builder.append("{question:\"");
				builder.append(tipsQuestion.getContent());
				builder.append("\", answer: \"");
				builder.append(tipsQuestion.getAnswer());
				builder.append("\"},");

			}
			builder.append("]");

			return builder.toString();
		}

	}

	private void submitTipQuestion() {

		HashMap<String, String> user = session.getUserDetails();
		String token = user.get(SessionManager.KEY_TOKEN);
		ArrayList<TipsQuestion> tipsQuestions = new ArrayList<TipsQuestion>();

		for (int i = 0; i < newDataMap.size(); i++) {

			TipsQuestion question = new TipsQuestion();

			question.setContent(newDataMap.get(i).get(KEY_QUESTION));
			question.setAnswer(newDataMap.get(i).get(KEY_ANSWER));
			tipsQuestions.add(question);
		}

		String[] input = new String[2];

		Holder holder = new Holder();
		holder.setTipsQuestions(tipsQuestions);

		input[0] = token;
		input[1] = holder.toString();
		LoadResultAddTips loadResultAddTips = new LoadResultAddTips();
		loadResultAddTips.execute(input);
	}

	private class LoadResultAddTips extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... input) {

			JSONObject jsonObject = null;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("token", input[0]));
			nameValuePairs.add(new BasicNameValuePair("answer", input[1]));

			try {
				jsonObject = parser.makeHttpRequest(SERVER_HOST + "add_tips",
						"POST", nameValuePairs);
			} catch (JorneeException e) {
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

						} else if ("fail".equals(result.getString("status"))) {

						} else if ("error".equals(result.getString("status"))) {

						}

					} else if ("fail".equals(result.getString("authen_status"))) {

					} else if ("error"
							.equals(result.getString("authen_status"))) {
					}
				} catch (JSONException e) {

				}

			}
		}
	}

}
