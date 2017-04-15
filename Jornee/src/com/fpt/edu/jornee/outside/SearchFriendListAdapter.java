/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: SearchFriendListAdapter.java
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
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.bean.OutsideSearchFriendResult;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.account.UserProfileFragment;
import com.fpt.edu.jornee.customview.CNSquareImageView;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.JSONParser;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

public class SearchFriendListAdapter extends
		ArrayAdapter<OutsideSearchFriendResult> {

	Context mContext;
	Activity mActivity;
	ArrayList<OutsideSearchFriendResult> mJourneys;
	int layoutId;
	View pendingView;
	HashMap<String, String> user;
	SessionManager sessionManager;

	public SearchFriendListAdapter(Context contxt,
			ArrayList<OutsideSearchFriendResult> objects,
			int textViewResourceId, Activity activity) {
		super(contxt, textViewResourceId, objects);
		mContext = activity.getApplicationContext();
		mJourneys = objects;
		layoutId = textViewResourceId;
		mActivity = activity;
		sessionManager = new SessionManager(mContext);
		user = new HashMap<String, String>();
		user = sessionManager.getUserDetails();
	}

	@Override
	public int getCount() {
		return mJourneys.size();
	}

	@Override
	public OutsideSearchFriendResult getItem(int position) {
		return mJourneys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mJourneys.get(position).hashCode();
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.loading_layout, parent, false);
		if (layoutId != R.layout.loading_layout) {
			rowView = inflater.inflate(layoutId, parent, false);
			CNSquareImageView userAvatar = (CNSquareImageView) rowView
					.findViewById(R.id.userAvatar);
			TextView tvUsername = (TextView) rowView
					.findViewById(R.id.tvUsername);
			TextView btnFollow = (TextView) rowView
					.findViewById(R.id.btnFollow);
			tvUsername.setText(mJourneys.get(position).getUsername());
			String urlAvatar = Constant.SERVER_HOST + "thumbnail_"
					+ mJourneys.get(position).getUserAvatarURL();
			UniversalImageHelper.loadImage(mContext, urlAvatar, userAvatar);
			if ("true".equals(mJourneys.get(position).getIsFollowing())) {
				btnFollow.setText("Unfollow");
				btnFollow.setBackground(mContext.getResources().getDrawable(
						R.drawable.btn_green_button));
			} else if ("false".equals(mJourneys.get(position).getIsFollowing())) {
				btnFollow.setText("Follow");
			} else if ("not".equals(mJourneys.get(position).getIsFollowing())) {
				btnFollow.setVisibility(View.INVISIBLE);
			}
			tvUsername.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					gotoProfile(mJourneys.get(position).getUsername());
				}
			});

			userAvatar.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					gotoProfile(mJourneys.get(position).getUsername());
				}
			});

			btnFollow.setOnClickListener(new View.OnClickListener() {

				@SuppressLint("NewApi")
				@Override
				public void onClick(View v) {
					String textOnButton = ((TextView) v).getText().toString();
					((TextView) v).setText("Wait...");
					v.setBackground(mContext.getResources().getDrawable(
							R.drawable.btn_gray_button));
					v.setEnabled(false);
					new FollowAsync().execute(position, textOnButton, v);
				}
			});
			rowView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					gotoProfile(mJourneys.get(position).getUsername());
				}
			});
		}
		return rowView;
	}
	
	class FollowAsync extends AsyncTask<Object, Void, JSONObject> {
		JSONObject jsonObject = null;
		String action;
		OutsideSearchFriendResult friendResult;
		int position;
		View inputView;

		@Override
		protected JSONObject doInBackground(Object... params) {
			try {
				action = (String) params[1];
				position = (Integer) params[0];
				friendResult  = mJourneys.get(position) ;
				inputView = (View) params[2];
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs.add(new BasicNameValuePair("token", user
						.get(SessionManager.KEY_TOKEN)));
				nameValuePairs.add(new BasicNameValuePair("friend_id", friendResult.getUsername()));
				String function;
				if ("Follow".equals(action)) {
					function = "follow";
				} else {
					function = "unfollow";
				}

				JSONParser jsonParser = new JSONParser(mContext);
				jsonObject = jsonParser.makeHttpRequest(SERVER_HOST + function, "POST", nameValuePairs);
				
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return jsonObject;
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				if (result != null) {
					JSONObject jsonObjectResult = result;
					String authen_status = jsonObjectResult
							.getString("authen_status");
					String status = null;
					if (jsonObjectResult.has("status")) {
						status = jsonObjectResult.getString("status");
					}
					if ("error".equals(authen_status)) {
						inputView.setEnabled(true);
						if ("Follow".equals(action)) {
							((TextView) inputView).setText("Follow");
							inputView.setBackground(mContext.getResources()
									.getDrawable(R.drawable.btn_green_button));
						} else {
							((TextView) inputView).setText("Unfollow");
							inputView.setBackground(mContext.getResources()
									.getDrawable(R.drawable.btn_blue_button));
						}
						Toast.makeText(mContext,
								"Error when we're trying to recognize you!",
								Toast.LENGTH_SHORT).show();
					} else if ("fail".equals(authen_status)) {
						inputView.setEnabled(true);
						if ("Follow".equals(action)) {
							((TextView) inputView).setText("Follow");
							inputView.setBackground(mContext.getResources()
									.getDrawable(R.drawable.btn_green_button));
						} else {
							((TextView) inputView).setText("Unfollow");
							inputView.setBackground(mContext.getResources()
									.getDrawable(R.drawable.btn_blue_button));
						}
						Toast.makeText(mContext,
								"You must login first to use this function!",
								Toast.LENGTH_SHORT).show();
					} else if ("ok".equals(authen_status)) {
						if ("error".equals(status)) {
							inputView.setEnabled(true);
							if ("Follow".equals(action)) {
								((TextView) inputView).setText("Follow");
								inputView.setBackground(mContext.getResources()
										.getDrawable(R.drawable.btn_green_button));
							} else {
								((TextView) inputView).setText("Unfollow");
								inputView.setBackground(mContext.getResources()
										.getDrawable(R.drawable.btn_blue_button));
							}
							Toast.makeText(mContext,
									"We cannot complete your request!",
									Toast.LENGTH_SHORT).show();
						} else if ("ok".equals(status)) {
							inputView.setEnabled(true);
							((MainActivity) mActivity).updateFollowInfor();
							if ("Follow".equals(action)) {
								mJourneys.get(position).setIsFollowing("true");
								notifyDataSetChanged();
								((TextView) inputView).setText("Unfollow");
								inputView.setBackground(mContext.getResources()
										.getDrawable(R.drawable.btn_green_button));
							} else {
								mJourneys.get(position).setIsFollowing("false");
								notifyDataSetChanged();
								((TextView) inputView).setText("Follow");
								inputView.setBackground(mContext.getResources()
										.getDrawable(R.drawable.btn_blue_button));
							}
						}
					}
				} else {
					inputView.setEnabled(true);
					if ("Follow".equals(action)) {
						((TextView) inputView).setText("Follow");
						inputView.setBackground(mContext.getResources().getDrawable(
								R.drawable.btn_green_button));
					} else {
						((TextView) inputView).setText("Unfollow");
						inputView.setBackground(mContext.getResources().getDrawable(
								R.drawable.btn_blue_button));
					}
					Toast.makeText(
							mContext,
							"Error occur due to server's failures! Please try later!",
							Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void gotoProfile(String username) {
		Fragment fragment = new UserProfileFragment();
		MainActivity mainActivity = (MainActivity) mActivity;
		Bundle bundle = new Bundle();
		bundle.putString(Constant.USERNAME_BUNDLE, username);
		bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "User Profile");
		fragment.setArguments(bundle);
		mainActivity.replaceFragment(fragment);
	}

}
