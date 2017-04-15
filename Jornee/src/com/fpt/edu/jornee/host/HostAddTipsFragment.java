/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HostAddTipsFragment.java
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
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fpt.edu.bean.TipsQuestion;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.account.LoginFragment;
import com.fpt.edu.jornee.customview.CNImageView;
import com.fpt.edu.jornee.exception.JorneeException;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.ImageLoader;
import com.fpt.edu.jornee.utils.JSONParser;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

public final class HostAddTipsFragment extends Fragment {

	TextView txtQuestion1;
	TextView txtQuestion2;
	TextView txtQuestion3;
	TextView txtQuestion4;
	TextView txtQuestion5;
	EditText answer_question1;
	EditText answer_question2;
	EditText answer_question3;
	EditText answer_question4;
	EditText answer_question5;
	Button btnSubmit;
	Button btnCancel;
	CNImageView avatar;
	TextView username;
	JSONParser parser;
	SessionManager session;
	ArrayList<TipsQuestion> data;
	String[] answer;
	TextView errorText;
	Context context;
	ImageLoader imageLoader;

	HashMap<String, String> user;

	public static Fragment newInstance() {
		HostAddTipsFragment fragment = new HostAddTipsFragment();

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		data = new ArrayList<TipsQuestion>();
		answer = new String[10];
		parser = new JSONParser(context);
		if (isAdded()) {

			context = getActivity().getApplicationContext();
			session = new SessionManager(getActivity().getApplicationContext());
		}
		imageLoader = new ImageLoader(context);
		user = session.getUserDetails();
		View rootView = inflater.inflate(
				R.layout.fragment_host_add_tips_layout, container, false);

		username = (TextView) rootView.findViewById(R.id.addtipUsername);
		avatar = (CNImageView) rootView.findViewById(R.id.addtipUserAvatar);

		username.setText(user.get(SessionManager.KEY_USERNAME));

		Bundle bundle = getArguments();
		if (bundle != null
				&& bundle.containsKey(Constant.FRAGMENT_TITLE_BUNDLE)) {
			getActivity().setTitle(
					bundle.getString(Constant.FRAGMENT_TITLE_BUNDLE));
		}
		if (isAdded()) {
			UniversalImageHelper.loadImage(getActivity()
					.getApplicationContext(), avatar, Constant.SERVER_HOST
					+ "thumbnail_" + user.get(SessionManager.KEY_AVATAR));
		}
		txtQuestion1 = (TextView) rootView.findViewById(R.id.txtQuestion1);
		txtQuestion2 = (TextView) rootView.findViewById(R.id.txtQuestion2);
		txtQuestion3 = (TextView) rootView.findViewById(R.id.txtQuestion3);
		txtQuestion4 = (TextView) rootView.findViewById(R.id.txtQuestion4);
		txtQuestion5 = (TextView) rootView.findViewById(R.id.txtQuestion5);
		answer_question1 = (EditText) rootView
				.findViewById(R.id.answer_question1);
		answer_question2 = (EditText) rootView
				.findViewById(R.id.answer_question2);
		answer_question3 = (EditText) rootView
				.findViewById(R.id.answer_question3);
		answer_question4 = (EditText) rootView
				.findViewById(R.id.answer_question4);
		answer_question5 = (EditText) rootView
				.findViewById(R.id.answer_question5);

		btnSubmit = (Button) rootView.findViewById(R.id.btnSubmitHostAddTips);
		btnSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				submitTipQuestion();

			}

		});
		btnCancel = (Button) rootView.findViewById(R.id.btnSkipHostAddTips);
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cancelAddTipQuestion();
			}

		});

		errorText = (TextView) rootView
				.findViewById(R.id.errorTextViewHostAddTip);

		Bundle extras = getArguments();
		if (extras != null) {
			data = extras.getParcelableArrayList("arraylist");
			if (data.size() >= 5) {

				txtQuestion1.setText(data.get(0).getContent());
				txtQuestion2.setText(data.get(1).getContent());
				txtQuestion3.setText(data.get(2).getContent());
				txtQuestion4.setText(data.get(3).getContent());
				txtQuestion5.setText(data.get(4).getContent());

			} else {
				errorText.setText(context.getResources().getString(
						R.string.error_general_error_message));
			}

		}
		return rootView;
	}

	private void cancelAddTipQuestion() {
		if (isAdded()) {
			Fragment fLoginFragment = new HostProfileFragment();

			Bundle bundle = new Bundle();
			bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Host Profile");
			fLoginFragment.setArguments(bundle);

			MainActivity activity = (MainActivity) getActivity();
			activity.replaceFragment(fLoginFragment);
		}
	}

	private void submitTipQuestion() {

		try {
			HashMap<String, String> user = session.getUserDetails();
			String token = user.get(SessionManager.KEY_TOKEN);
			ArrayList<TipsQuestion> tipsQuestions = new ArrayList<TipsQuestion>();

			if (data.size() >= 5) {

				answer[0] = answer_question1.getText().toString();
				answer[1] = answer_question2.getText().toString();
				answer[2] = answer_question3.getText().toString();
				answer[3] = answer_question4.getText().toString();
				answer[4] = answer_question5.getText().toString();

				for (int i = 0; i < data.size(); i++) {
					TipsQuestion question = new TipsQuestion();

					question.setContent(data.get(i).getContent());
					question.setAnswer(answer[i]);
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
		} catch (Exception e) {
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

	/***
	 * 
	 * @author tuan
	 *Load result add tips from server
	 */
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

							cancelAddTipQuestion();

						} else if ("fail".equals(result.getString("status"))) {

							errorText.setText(context.getResources().getString(
									R.string.error_general_error_message));
						} else if ("error".equals(result.getString("status"))) {

							errorText.setText(context.getResources().getString(
									R.string.error_general_error_message));
						}

					} else if ("fail".equals(result.getString("authen_status"))) {
						callLogin();
					} else if ("error"
							.equals(result.getString("authen_status"))) {

						errorText.setText(context.getResources().getString(
								R.string.error_general_error_message));

					}
				} catch (JSONException e) {

					errorText.setText(context.getResources().getString(
							R.string.error_general_error_message));

				}

			} else {

				errorText.setText(context.getResources().getString(
						R.string.error_general_error_message));

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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
