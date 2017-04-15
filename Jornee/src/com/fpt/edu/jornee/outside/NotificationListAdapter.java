/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: NotificationListAdapter.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.outside;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fpt.edu.bean.SocketNotificationEvent;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.account.UserProfileFragment;
import com.fpt.edu.jornee.customview.CNSquareImageView;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.DateTimeHelper;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

public class NotificationListAdapter extends
		ArrayAdapter<SocketNotificationEvent> {
	ArrayList<SocketNotificationEvent> listNotifications;
	Context mContext;
	int resId;
	Activity mActivity;
	HashMap<String, String> user;
	SessionManager sessionManager;

	public NotificationListAdapter(Context context, int textViewResourceId,
			ArrayList<SocketNotificationEvent> objects, Activity activity) {
		super(context, textViewResourceId, objects);
		listNotifications = objects;
		mContext = context;
		mActivity = activity;
		sessionManager = new SessionManager(mContext);
		user = new HashMap<String, String>();
		user = sessionManager.getUserDetails();
	}

	@Override
	public int getCount() {
		return listNotifications.size();
	}

	@Override
	public SocketNotificationEvent getItem(int position) {
		return listNotifications.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listNotifications.get(position).hashCode();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final SocketNotificationEvent currentItem = listNotifications.get(position);
		View rowView = inflater.inflate(R.layout.notification_list_row_layout,
				parent, false);
		CNSquareImageView ivSenderAvatar = (CNSquareImageView) rowView
				.findViewById(R.id.ivSenderAvatar);
		String link = Constant.SERVER_HOST + Constant.IMAGE_TYPE_THUMBNAIL
				+ currentItem.getAvatar();
		UniversalImageHelper.loadImage(mContext, ivSenderAvatar, link);
		TextView tvSender = (TextView) rowView.findViewById(R.id.tvSender);
		TextView tvTitle = (TextView) rowView.findViewById(R.id.tvTitle);
		TextView tvTime = (TextView) rowView.findViewById(R.id.tvTime);
		tvSender.setText(currentItem.getOther_id());
		tvTitle.setText(currentItem.getNotificationMessage());
		tvTime.setText(DateTimeHelper.contextDateTime(DateTimeHelper
				.convertStringServerTimeToLocalDate(currentItem.getTime())));
		rowView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickNotification(currentItem);
			}
		});
		return rowView;
	}

	public void onClickNotification(SocketNotificationEvent currentItem) {
		Fragment fragment = null;
		if (currentItem.getType().equals(
				SocketNotificationEvent.NOTIFICATION_TYPE_FOLLOW)) {
			fragment = new UserProfileFragment();
			Bundle bundle = new Bundle();
			bundle.putString(Constant.USERNAME_BUNDLE,
					currentItem.getOther_id());
			bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "User Profile");
			fragment.setArguments(bundle);
		}

		if (fragment != null) {
			MainActivity mainActivity = (MainActivity) mActivity;
			mainActivity.replaceFragment(fragment);
		}
	}
}
