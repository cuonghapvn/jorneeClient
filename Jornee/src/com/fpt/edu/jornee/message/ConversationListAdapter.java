/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: ConversationListAdapter.java
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
import android.widget.ImageView;
import android.widget.TextView;

import com.fpt.edu.bean.Conversation;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.DateTimeHelper;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

@SuppressLint("SimpleDateFormat")
public class ConversationListAdapter extends ArrayAdapter<Conversation> {
	ArrayList<Conversation> listConversation;
	Context mContext;
	int resId;
	Activity mActivity;
	HashMap<String, String> user;
	SessionManager sessionManager;

	public ConversationListAdapter(Context context, int textViewResourceId,
			ArrayList<Conversation> objects, Activity activity) {
		super(context, textViewResourceId, objects);
		listConversation = objects;
		mContext = context;
		mActivity = activity;
		sessionManager = new SessionManager(mContext);
		user = new HashMap<String, String>();
		user = sessionManager.getUserDetails();
	}

	@Override
	public int getCount() {
		return listConversation.size();
	}

	@Override
	public Conversation getItem(int position) {
		return listConversation.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listConversation.get(position).hashCode();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final Conversation currentItem = listConversation.get(position);
		View rowView = inflater.inflate(R.layout.fragment_message_conversation_item,
				parent, false);
		TextView tvUsername = (TextView) rowView.findViewById(R.id.tvUsername);
		TextView tvLastMessage = (TextView) rowView
				.findViewById(R.id.tvLastMessage);
		TextView tvNumberOfUnread = (TextView) rowView.findViewById(R.id.tvNumberOfUnread);
		TextView tvDatetime = (TextView) rowView.findViewById(R.id.tvDatetime);
		ImageView userAvatar = (ImageView) rowView.findViewById(R.id.userAvatar);
		
		if (currentItem.getNumberUnread() > 0 && currentItem.getContactUser().equals(currentItem.getCurrentUser())) {
			tvNumberOfUnread.setVisibility(View.VISIBLE);
			tvNumberOfUnread.setText(""+currentItem.getNumberUnread());
		}
		tvUsername.setText(currentItem.getContactUser());
		tvLastMessage.setText(currentItem.getCurrentUser() + ": "
				+ currentItem.getLastMessage());
		tvDatetime.setText(DateTimeHelper.contextDateTime(currentItem
				.getModifiedDate()));
		String link = Constant.SERVER_HOST + "thumbnail_"+currentItem.getAvatar();
		UniversalImageHelper.loadImage(mContext, userAvatar, link);
		rowView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Fragment fragment = new MessagingFragment();
				MainActivity mainActivity = (MainActivity) mActivity;
				Bundle bundle = new Bundle();
				bundle.putString(Constant.USERNAME_BUNDLE,
						currentItem.getContactUser());
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,
						currentItem.getContactUser());
				fragment.setArguments(bundle);
				mainActivity.replaceFragment(fragment);
			}
		});
		return rowView;
	}

}
