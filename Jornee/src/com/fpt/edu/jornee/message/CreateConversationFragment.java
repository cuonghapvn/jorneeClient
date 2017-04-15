/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: CreateConversationFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.message;

import static com.fpt.edu.jornee.utils.Constant.SERVER_HOST;

import java.util.ArrayList;
import java.util.Collections;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.fpt.edu.bean.OutsideSearchFriendResult;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.customview.CNEditText;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.JSONParser;

public class CreateConversationFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	ArrayList<OutsideSearchFriendResult> list;
	ListView lvListResult;
	TextView tvResult;
	CNEditText etKeyword;
	CreateConversationAdapter adapter;
	String authen_status = null;
	String status = null;
	JSONArray arrayFollowerResult = null;
	JSONArray arrayFollowingResult = null;
	SessionManager sessionManager;
	HashMap<String, String> user;
	ArrayList<OutsideSearchFriendResult> finalResult;
	ArrayList<OutsideSearchFriendResult> finalResultHolding;

	public CreateConversationFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		sessionManager = new SessionManager(getActivity()
				.getApplicationContext());
		user = new HashMap<String, String>();
		user = sessionManager.getUserDetails();
		View rootView = inflater.inflate(
				R.layout.fragment_create_conversation_layout, container, false);
		lvListResult = (ListView) rootView.findViewById(R.id.lvResultSearching);
		tvResult = (TextView) rootView.findViewById(R.id.tvResultDescription);
		etKeyword = (CNEditText) rootView.findViewById(R.id.txtFilterFriends);
		String username = user.get(SessionManager.KEY_USERNAME);
		new GetViewFollowAsync().execute(username);
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
				ArrayList<OutsideSearchFriendResult> search = searchInList(
						finalResultHolding, s.toString());
				System.out.println("Tracking: " + finalResultHolding.size());
				adapter.clear();
				adapter.addAll(search);
				adapter.notifyDataSetChanged();
			}
		});
		return rootView;
	}

	private class GetViewFollowAsync extends
			AsyncTask<Object, Void, JSONObject> {
		JSONObject jsonObject = null;

		@Override
		protected JSONObject doInBackground(Object... params) {
			try {
				SessionManager sessionManager = new SessionManager(
						getActivity().getApplicationContext());
				String username = (String) params[0];
				HashMap<String, String> user = new HashMap<String, String>();
				user = sessionManager.getUserDetails();
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("token", user
						.get(SessionManager.KEY_TOKEN)));
				nameValuePairs
						.add(new BasicNameValuePair("username", username));
				JSONParser jsonParser = new JSONParser(getActivity().getApplicationContext());
				jsonObject = jsonParser.makeHttpRequest(SERVER_HOST + "view_follow", "POST", nameValuePairs);
				
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return jsonObject;
		}

		@Override
		protected void onPreExecute() {
			list = new ArrayList<OutsideSearchFriendResult>();
			list.add(new OutsideSearchFriendResult());
			adapter = new CreateConversationAdapter(getActivity()
					.getApplicationContext(), list, R.layout.loading_layout,
					getActivity());
			lvListResult.setAdapter(adapter);
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				if (result != null) {
					System.out.println(result.toString());
					authen_status = result.getString("authen_status");
					if (result.has("status")) {
						status = result.getString("status");
					}
					if (result.has("follower")) {
						arrayFollowerResult = result.getJSONArray("follower");
					}
					if (result.has("following")) {
						arrayFollowingResult = result.getJSONArray("following");
					}
					if ("error".equals(authen_status)) {
						tvResult.setText("Error when we're trying to recognize you!");
						adapter.clear();
						adapter.notifyDataSetChanged();
					} else if ("fail".equals(authen_status)) {
						tvResult.setText("You must login first to use this function!");
						adapter.clear();
						adapter.notifyDataSetChanged();
					} else if ("ok".equals(authen_status)) {
						if ("error".equals(status)) {
							tvResult.setText("We cannot complete your request!");
							adapter.clear();
							adapter.notifyDataSetChanged();
						} else if ("ok".equals(status)) {
							tvResult.setVisibility(View.GONE);
							ArrayList<OutsideSearchFriendResult> listFollowing = new ArrayList<OutsideSearchFriendResult>();
							ArrayList<OutsideSearchFriendResult> listFollowers = new ArrayList<OutsideSearchFriendResult>();
							listFollowing = resultListFromJSON(arrayFollowingResult);
							listFollowers = resultListFromJSON(arrayFollowerResult);
							finalResult = mergeList(listFollowing,
									listFollowers);
							Collections.sort(finalResult);
							finalResultHolding = new ArrayList<OutsideSearchFriendResult>();
							finalResultHolding.addAll(finalResult);
							adapter = new CreateConversationAdapter(
									getActivity().getApplicationContext(),
									finalResult,
									R.layout.fragment_searchfriend_result_row,
									getActivity());
							lvListResult.setAdapter(adapter);
						}
					}
				} else {
					adapter.clear();
					adapter.notifyDataSetChanged();
					tvResult.setText("Error occur due to server's failures! Please try later!");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

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

	public ArrayList<OutsideSearchFriendResult> mergeList(
			ArrayList<OutsideSearchFriendResult> arr1,
			ArrayList<OutsideSearchFriendResult> arr2) {
		ArrayList<OutsideSearchFriendResult> result = new ArrayList<OutsideSearchFriendResult>();
		result.addAll(arr1);
		for (OutsideSearchFriendResult outsideSearchFriendResult : arr2) {
			if (!isExistInList(result, outsideSearchFriendResult.getUsername())) {
				result.add(outsideSearchFriendResult);
			}
		}
		return result;
	}

	public boolean isExistInList(ArrayList<OutsideSearchFriendResult> list,
			String username) {
		boolean result = false;
		for (OutsideSearchFriendResult outsideSearchFriendResult : list) {
			if (outsideSearchFriendResult.getUsername().equals(username)) {
				result = true;
			}
		}
		return result;
	}

	public ArrayList<OutsideSearchFriendResult> searchInList(
			ArrayList<OutsideSearchFriendResult> list, String username) {
		ArrayList<OutsideSearchFriendResult> result = new ArrayList<OutsideSearchFriendResult>();
		for (OutsideSearchFriendResult outsideSearchFriendResult : list) {
			if (outsideSearchFriendResult.getUsername().contains(username)) {
				result.add(outsideSearchFriendResult);
			}
		}
		Collections.sort(result);
		return result;
	}
}
