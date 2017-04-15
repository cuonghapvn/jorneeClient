/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: ViewFollowListFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.account;

import static com.fpt.edu.jornee.utils.Constant.*;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.fpt.edu.bean.OutsideSearchFriendResult;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.outside.SearchFriendListAdapter;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.JSONParser;

public class ViewFollowListFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	ArrayList<OutsideSearchFriendResult> list;
	ListView lvListResult;
	TextView tvResult;
	SearchFriendListAdapter adapter;
	ArrayList<OutsideSearchFriendResult> listFollowing;
	ArrayList<OutsideSearchFriendResult> listFollowers;
	String authen_status = null;
	String status = null;
	JSONArray arrayFollowerResult = null;
	JSONArray arrayFollowingResult = null;

	public ViewFollowListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_viewfollow_list_layout, container, false);
		lvListResult = (ListView) rootView.findViewById(R.id.lvList);
		tvResult = (TextView) rootView.findViewById(R.id.tvResult);
		if(savedInstanceState!=null){
			String chosenTabs = savedInstanceState.getString("tabsName");
			String username = savedInstanceState.getString("username");
			new GetViewFollowAsync().execute(username, chosenTabs);
		} else {
			String chosenTabs = getArguments().getString("tabsName");
			String username = getArguments().getString("username");
			new GetViewFollowAsync().execute(username, chosenTabs);
		}
		return rootView;
	}

	private class GetViewFollowAsync extends
			AsyncTask<Object, Void, JSONObject> {
		JSONObject jsonObject = null;
		String chosen;

		@Override
		protected JSONObject doInBackground(Object... params) {
			try {
				SessionManager sessionManager = new SessionManager(
						getActivity().getApplicationContext());
				String username = (String) params[0];
				chosen = (String) params[1];
				HashMap<String, String> user = new HashMap<String, String>();
				user = sessionManager.getUserDetails();
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("token", user
						.get(SessionManager.KEY_TOKEN)));
				nameValuePairs
						.add(new BasicNameValuePair("username", username));
				JSONParser jsonParser = new JSONParser(getActivity().getApplicationContext());
				jsonObject = jsonParser.makeHttpRequest(SERVER_HOST + SOCIAL_VIEW_FOLLOW, "POST", nameValuePairs);
				
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
			adapter = new SearchFriendListAdapter(getActivity()
					.getApplicationContext(), list, R.layout.loading_layout,
					getActivity());
			lvListResult.setAdapter(adapter);
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				if (result != null) {
					authen_status = result.getString(RESPONSE_AUTHEN_STATUS);
					if (result.has(RESPONSE_STATUS)) {
						status = result.getString(RESPONSE_STATUS);
					}
					if (result.has("follower")) {
						arrayFollowerResult = result.getJSONArray("follower");
					}
					if (result.has("following")) {
						arrayFollowingResult = result.getJSONArray("following");
					}
					if (RESPONSE_AUTHEN_STATUS_ERROR.equals(authen_status)) {
						tvResult.setText("Error when we're trying to recognize you!");
						adapter.clear();
						adapter.notifyDataSetChanged();
					} else if (RESPONSE_AUTHEN_STATUS_FAIL.equals(authen_status)) {
						tvResult.setText("You must login first to use this function!");
						adapter.clear();
						adapter.notifyDataSetChanged();
					} else if (RESPONSE_AUTHEN_STATUS_OK.equals(authen_status)) {
						if (RESPONSE_STATUS_ERROR.equals(status)) {
							tvResult.setText("We cannot complete your request!");
							adapter.clear();
							adapter.notifyDataSetChanged();
						} else if (RESPONSE_STATUS_OK.equals(status)) {
							tvResult.setVisibility(View.GONE);
							listFollowing = new ArrayList<OutsideSearchFriendResult>();
							listFollowers = new ArrayList<OutsideSearchFriendResult>();
							listFollowing = resultListFromJSON(arrayFollowingResult);
							listFollowers = resultListFromJSON(arrayFollowerResult);
							if ("Following".equals(chosen)) {
								if (listFollowing.size() == 0) {
									tvResult.setVisibility(View.VISIBLE);
									tvResult.setText(getActivity()
											.getResources()
											.getString(
													R.string.message_empty_following_list));
								}
								adapter = new SearchFriendListAdapter(
										getActivity().getApplicationContext(),
										listFollowing,
										R.layout.fragment_searchfriend_result_row,
										getActivity());
							} else {
								if (listFollowers.size() == 0) {
									tvResult.setVisibility(View.VISIBLE);
									tvResult.setText(getActivity()
											.getResources()
											.getString(
													R.string.message_empty_followers_list));
								}
								adapter = new SearchFriendListAdapter(
										getActivity().getApplicationContext(),
										listFollowers,
										R.layout.fragment_searchfriend_result_row,
										getActivity());
							}
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
}
