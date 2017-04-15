/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: MessageListAdapter.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.message;

import java.util.HashMap;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fpt.edu.bean.Message;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

public class MessageListAdapter extends ArrayAdapter<Message> {

	LinkedList<Message> listConversation;
	Context mContext;
	int resId;
	Activity mActivity;
	HashMap<String, String> user;
	SessionManager sessionManager;
	String avatar_friend;

	public MessageListAdapter(Context context, int textViewResourceId,
			LinkedList<Message> messages, Activity activity) {
		super(context, textViewResourceId, messages);
		listConversation = messages;
		mContext = context;
		mActivity = activity;
		sessionManager = new SessionManager(mContext);
		user = new HashMap<String, String>();
		user = sessionManager.getUserDetails();
	}

	public String getAvatar_friend() {
		return avatar_friend;
	}

	public void setAvatar_friend(String avatar_friend) {
		this.avatar_friend = avatar_friend;
	}

	@Override
	public int getCount() {
		return listConversation.size();
	}

	@Override
	public Message getItem(int position) {
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
		final Message currentItem = listConversation.get(position);
		TextView tvUsername;
		TextView tvMessage;
		TextView tvDateTime;
		ImageView ivAvatar;
		View rowView = null;
		if (currentItem.getUsername().equals(
				user.get(SessionManager.KEY_USERNAME))) {
			rowView = inflater.inflate(R.layout.chat_row_right_text_layout,
					parent, false);
			tvMessage = (TextView) rowView.findViewById(R.id.tvMessage);
			tvDateTime = (TextView) rowView.findViewById(R.id.tvDateTime);
			tvMessage.setText(currentItem.getMessage());
			tvDateTime.setText(currentItem.getSentDate());
		} else {
			rowView = inflater.inflate(R.layout.chat_row_left_text_layout,
					parent, false);
			tvUsername = (TextView) rowView.findViewById(R.id.tvUsername);
			tvMessage = (TextView) rowView.findViewById(R.id.tvMessage);
			tvDateTime = (TextView) rowView.findViewById(R.id.tvDateTime);
			ivAvatar = (ImageView) rowView.findViewById(R.id.ivAvatar);
			tvUsername.setText(currentItem.getUsername());
			tvMessage.setText(currentItem.getMessage());
			tvDateTime.setText(currentItem.getSentDate());
			String link = Constant.SERVER_HOST + "thumbnail_"+avatar_friend;
			UniversalImageHelper.loadImage(mContext, link, ivAvatar);
//			UrlImageViewHelper.setUrlDrawable(ivAvatar, link);
		}
		return rowView;
	}

}
