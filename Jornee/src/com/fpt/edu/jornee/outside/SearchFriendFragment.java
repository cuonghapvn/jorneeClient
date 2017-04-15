/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: SearchFriendFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.outside;

import static com.fpt.edu.jornee.utils.Constant.SERVER_HOST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.bean.OutsideSearchFriendResult;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.customview.CNEditText;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.JSONParser;

public class SearchFriendFragment extends Fragment {

	ListView lvListResult;
	SearchFriendListAdapter adapter;
	ArrayList<OutsideSearchFriendResult> listResult;
	JSONObject jsonObjectResult = null;
	JSONArray arrayResult = null;
	String authen_status = null; 
	String status = null;
	TextView resultTitle;
	CNEditText etKeyword;
	String currentSearchString = null;

	public SearchFriendFragment() {
		// Empty constructor required for fragment subclasses
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if(bundle!=null && bundle.containsKey(Constant.FRAGMENT_TITLE_BUNDLE)){
			getActivity().setTitle(bundle.getString(Constant.FRAGMENT_TITLE_BUNDLE));
		}
		View rootView = inflater.inflate(
				R.layout.fragment_searchfriend_result_layout, container, false);
		etKeyword = (CNEditText) rootView.findViewById(R.id.txtFilterFriends);
		etKeyword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (adapter != null) {
					adapter.clear();
					adapter.notifyDataSetChanged();
				}
				String input = s.toString().trim();
				if (input.length() > 0) {
					new SearchAsync().execute(input);
				} else {
					resultTitle.setVisibility(View.GONE);
					resultTitle
							.setText("");
				}
			}
		});

		resultTitle = (TextView) rootView
				.findViewById(R.id.tvResultDescription);
		lvListResult = (ListView) rootView.findViewById(R.id.lvResultSearching);
		setHasOptionsMenu(true);
		return rootView;
	}

	private ArrayList<OutsideSearchFriendResult> resultListFromJSON(
			JSONArray jsonArray) {
		ArrayList<OutsideSearchFriendResult> result = new ArrayList<OutsideSearchFriendResult>();
		try {
			JSONObject userResult;
			OutsideSearchFriendResult friendResult;
			int numOfResult = jsonArray.length();
			if (numOfResult > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					userResult = jsonArray.getJSONObject(i);
					friendResult = new OutsideSearchFriendResult();
					friendResult.setUsername(userResult.getString("username"));
					if (userResult.has("avatar")) {
						friendResult.setUserAvatarURL(userResult
								.getString("avatar"));
					}
					friendResult.setIsFollowing(userResult
							.getString("following"));
					result.add(friendResult);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private class SearchAsync extends AsyncTask<String, Void, JSONObject> {
		JSONObject jsonObject = null;
		String keyword;

		@Override
		protected JSONObject doInBackground(String... params) {
			try {
				keyword = params[0];
				SessionManager sessionManager = new SessionManager(
						getActivity().getApplicationContext());
				HashMap<String, String> user = new HashMap<String, String>();
				user = sessionManager.getUserDetails();
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("token", user
						.get(SessionManager.KEY_TOKEN)));
				nameValuePairs
						.add(new BasicNameValuePair("keyword", params[0]));

				JSONParser jsonParser = new JSONParser(getActivity().getApplicationContext());
				jsonObject = jsonParser.makeHttpRequest(SERVER_HOST + "search_friend", "POST", nameValuePairs);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return jsonObject;
		}

		@Override
		protected void onPreExecute() {
			resultTitle.setVisibility(View.GONE);
			resultTitle
					.setText("");
			listResult = new ArrayList<OutsideSearchFriendResult>();
			listResult.add(new OutsideSearchFriendResult());
			adapter = new SearchFriendListAdapter(getActivity()
					.getApplicationContext(), listResult,
					R.layout.loading_layout, getActivity());
			lvListResult.setAdapter(adapter);
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				if (result != null) {
					jsonObjectResult = result;
					authen_status = jsonObjectResult.getString("authen_status");
					if (jsonObjectResult.has("status")) {
						status = jsonObjectResult.getString("status");
					}
					if (jsonObjectResult.has("result")) {
						arrayResult = jsonObjectResult.getJSONArray("result");
					}
					if ("error".equals(authen_status)) {
						resultTitle.setVisibility(View.VISIBLE);
						resultTitle
								.setText("Error when we're trying to recognize you!");
						adapter.clear();
						adapter.notifyDataSetChanged();
					} else if ("fail".equals(authen_status)) {
						resultTitle.setVisibility(View.VISIBLE);
						resultTitle
								.setText("You must login first to use this function!");
						adapter.clear();
						adapter.notifyDataSetChanged();
					} else if ("ok".equals(authen_status)) {
						if ("error".equals(status)) {
							resultTitle.setVisibility(View.VISIBLE);
							resultTitle
									.setText("We cannot complete your request!");
							adapter.clear();
							adapter.notifyDataSetChanged();
						} else if ("ok".equals(status)) {
							listResult = new ArrayList<OutsideSearchFriendResult>();
							listResult = resultListFromJSON(arrayResult);
							if(listResult.size()>0){
								adapter = new SearchFriendListAdapter(getActivity()
										.getApplicationContext(), listResult,
										R.layout.fragment_searchfriend_result_row,
										getActivity());
								lvListResult.setAdapter(adapter);
							} else {
								resultTitle.setVisibility(View.VISIBLE);
								resultTitle
										.setText("We cannot find anyone name: '"+keyword+"'");
								adapter.clear();
								adapter.notifyDataSetChanged();
							}
							
						}
					}
				} else {
					Toast.makeText(
							getActivity(),
							"Error occur due to server's failures! Please try later!",
							Toast.LENGTH_LONG).show();
					adapter.clear();
					adapter.notifyDataSetChanged();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}
