/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: CreateConversationAdapter.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.message;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fpt.edu.bean.OutsideSearchFriendResult;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.customview.CNSquareImageView;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

public class CreateConversationAdapter extends
		ArrayAdapter<OutsideSearchFriendResult> {
	Context mContext;
	Activity mActivity;
	ArrayList<OutsideSearchFriendResult> mJourneys;
	int layoutId;
	View pendingView;
	HashMap<String, String> user;
	SessionManager sessionManager;
	
	public CreateConversationAdapter(Context contxt,
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
			btnFollow.setVisibility(View.INVISIBLE);
			tvUsername.setText(mJourneys.get(position).getUsername());
			String urlAvatar = Constant.SERVER_HOST + "thumbnail_"
					+ mJourneys.get(position).getUserAvatarURL();
			UniversalImageHelper.loadImage(mContext,userAvatar, urlAvatar);
			rowView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					gotoChat(mJourneys.get(position).getUsername());
				}
			});
		}
		return rowView;
	}

	private void gotoChat(String username) {
		Fragment fragment = new MessagingFragment();
		MainActivity mainActivity = (MainActivity) mActivity;
		Bundle bundle = new Bundle();
		bundle.putString(Constant.USERNAME_BUNDLE,
				username);
		bundle.putString(
				Constant.FRAGMENT_TITLE_BUNDLE,
				username);
		fragment.setArguments(bundle);
		mainActivity.replaceFragment(fragment);
	}

}
