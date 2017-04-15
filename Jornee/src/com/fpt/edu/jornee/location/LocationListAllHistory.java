/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: LocationListAllHistory.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.location;

import java.util.ArrayList;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.account.LoginFragment;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.JSONParser;

public class LocationListAllHistory extends Fragment {
	JSONParser parser;
	Context context;
	SessionManager session;
	ListView listAllHistoryOptions;
	ProgressDialog progressDialog;
	ArrayList<String> data;
	String language;
	Button btnBack;
	ProgressBar progressBar1;

	public LocationListAllHistory() {

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_location_list_all_history, container, false);

		setHasOptionsMenu(true);
		progressBar1 = (ProgressBar) rootView.findViewById(R.id.progressBar1);
		data = new ArrayList<String>();
		context = getActivity().getApplicationContext();
		parser = new JSONParser(context);
		session = new SessionManager(context);
		Bundle extras = getArguments();

		listAllHistoryOptions = (ListView) rootView
				.findViewById(R.id.location_list_all_history_options_list_view);
		listAllHistoryOptions.setVisibility(View.GONE);

		if (extras != null) {
			data = extras.getStringArrayList("string_results");
			language = extras.getString("language");
			progressBar1.setVisibility(View.GONE);
			listAllHistoryOptions.setVisibility(View.VISIBLE);
			
			getActivity().setTitle(
					extras.getString(Constant.FRAGMENT_TITLE_BUNDLE));
			

		}

		listAllHistoryOptions.setAdapter(adapter);
		listAllHistoryOptions.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String[] input = new String[2];
				input[0] = parent.getItemAtPosition(position).toString();
				LoadResultListHistoryImage loadResultListHistoryImage = new LoadResultListHistoryImage();
				loadResultListHistoryImage.execute(input);

			}
		});
		btnBack = (Button) rootView
				.findViewById(R.id.location_list_all_history_btnBack);

		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Fragment fragment = new LocationFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,
						"Search for location...");
				fragment.setArguments(bundle);
				MainActivity activity = (MainActivity) getActivity();
				activity.replaceFragment(fragment);
			}
		});

		return rootView;

	}

	private BaseAdapter adapter = new BaseAdapter() {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public String getItem(int position) {
			return data.get(position);
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

			textview.setText(data.get(position));
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

	private class LoadResultListHistoryImage extends
			AsyncTask<String, Void, JSONObject> {

		@Override
		protected void onPreExecute() {

			listAllHistoryOptions.setVisibility(View.GONE);
			progressBar1.setVisibility(View.VISIBLE);
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
					nameValuePairs.add(new BasicNameValuePair("page_name",
							location[0]));
					nameValuePairs.add(new BasicNameValuePair("language",
							language));

					jsonObject = parser.makeHttpRequest(Constant.SERVER_HOST
							+ "place_information", "POST", nameValuePairs);
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

							progressBar1.setVisibility(View.VISIBLE);

							JSONObject object = result.getJSONObject("results");

							JSONArray imageArray = object
									.getJSONArray("images");

							ArrayList<String> stArrayList = new ArrayList<String>();
							for (int i = 0; i < imageArray.length(); i++) {
								stArrayList.add(i, imageArray.getString(i));
							}

							Bundle extras1 = new Bundle();
							extras1.putStringArrayList("image_array",
									stArrayList);
							extras1.putString("content",
									object.getString("content"));
							extras1.putString(Constant.FRAGMENT_TITLE_BUNDLE,"Facts");
							extras1.putStringArrayList("string_results", data);
							extras1.putString("language", language);

							if (isAdded()) {
								Fragment fragment0 = new LocationListAllHistoryDetail();
								fragment0.setArguments(extras1);

								MainActivity activity = (MainActivity) getActivity();
								activity.replaceFragment(fragment0);
							}

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
