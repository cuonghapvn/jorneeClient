/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: LocationListAllQuestionTips.java
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

import android.content.Context;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.bean.HostInLocation;
import com.fpt.edu.bean.Location;
import com.fpt.edu.bean.TipsQuestion;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.account.LoginFragment;
import com.fpt.edu.jornee.host.HostProfileFragment;
import com.fpt.edu.jornee.otherhost.HostOtherProfileFragment;
import com.fpt.edu.jornee.outside.SearchFriendFragment;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.JSONParser;

public class LocationListAllQuestionTips extends Fragment {

	public static String KEY_HOST_REGIS_LOCATION = "regis_location";
	public static String KEY_HOST_AVATAR = "avatar";
	public static String KEY_HOST_USERNAME = "username";
	public static String KEY_HOST_STATUS = "status";
	JSONParser parser;
	Context context;
	SessionManager session;
	ListView listAllQuestionTips;
	ArrayList<TipsQuestion> questions;
	Button btnBack;
	Location prcLocation;
	String[] question;

	public LocationListAllQuestionTips() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_location_list_all_question_tips, container,
				false);

		setHasOptionsMenu(true);
		questions = new ArrayList<TipsQuestion>();
		context = getActivity().getApplicationContext();
		parser = new JSONParser(context);
		session = new SessionManager(context);
		Bundle extras = getArguments();
		if (extras != null) {
			questions = extras.getParcelableArrayList("all_questions");
			prcLocation = extras.getParcelable("location_detail");
			getActivity().setTitle(
					extras.getString(Constant.FRAGMENT_TITLE_BUNDLE));

		}
		btnBack = (Button) rootView
				.findViewById(R.id.location_list_all_question_tip_btnBack);
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Fragment fragment = new LocationFragment();

				MainActivity activity = (MainActivity) getActivity();
				activity.replaceFragment(fragment);
			}
		});

		listAllQuestionTips = (ListView) rootView
				.findViewById(R.id.location_list_all_question_tip);

		question = new String[questions.size()];
		try {
			for (int i = 0; i < questions.size(); i++) {
				question[i] = new String(questions.get(i).getContent()
						.getBytes("UTF-8"), "UTF-8");

			}
		} catch (Exception exception) {

		}

		listAllQuestionTips.setAdapter(adapter);
		listAllQuestionTips.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Fragment fragment = new LocationListAllAnswerTips();
				Bundle bundle = new Bundle();
				bundle.putString("QuestionID", questions.get(position)
						.getQuestionId());
				bundle.putParcelable("detail_location", prcLocation);
				bundle.putParcelableArrayList("all_questions", questions);
				bundle.putParcelable("location_detail", prcLocation);
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Tips Answers");

				fragment.setArguments(bundle);
				MainActivity activity = (MainActivity) getActivity();
				activity.replaceFragment(fragment);

			}
		});
		return rootView;
	}

	BaseAdapter adapter = new BaseAdapter() {

		@Override
		public int getCount() {
			return question.length;
		}

		@Override
		public String getItem(int position) {
			return question[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View vi = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.location_list_history_options, null);

			TextView textview = (TextView) vi
					.findViewById(R.id.location_list_history_options_textView);

			textview.setText(question[position]);
			return vi;
		}

	};

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

		if (isAdded()) {
			Fragment fragment0 = new LoginFragment();

			MainActivity activity = (MainActivity) getActivity();
			activity.replaceFragment(fragment0);
		}
	}

}
