/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: LocationListAllAnswerAdapter.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.location;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.customview.CNImageView;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.ImageLoader;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

public class LocationListAllAnswerAdapter extends BaseAdapter {

	private FragmentActivity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public LocationListAllAnswerAdapter(FragmentActivity a,
			ArrayList<HashMap<String, String>> d) {

		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.location_list_answer_tips_list_row,
					null);

		CNImageView avatar = (CNImageView) vi
				.findViewById(R.id.location_list_answer_avatar);
		TextView username = (TextView) vi
				.findViewById(R.id.location_list_answer_tips_list_row_username);
		TextView answer = (TextView) vi
				.findViewById(R.id.location_list_answer_tips_list_row_answer);
		username.setTextColor(Color.parseColor("#ffffff"));
		answer.setTextColor(Color.parseColor("#ffffff"));

		HashMap<String, String> hostAnswer = new HashMap<String, String>();
		hostAnswer = data.get(position);

		UniversalImageHelper
				.loadImage(
						activity.getApplicationContext(),
						avatar,
						Constant.SERVER_HOST
								+ "thumbnail_"
								+ hostAnswer
										.get(LocationListAllAnswerTips.KEY_HOST_AVATAR));

		username.setText(hostAnswer
				.get(LocationListAllAnswerTips.KEY_HOST_USER_ID));
		answer.setText(hostAnswer.get(LocationListAllAnswerTips.KEY_HOST_TIP));

		return vi;
	}
}
