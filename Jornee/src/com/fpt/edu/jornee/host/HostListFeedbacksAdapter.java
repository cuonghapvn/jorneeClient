/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HostListFeedbacksAdapter.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.host;

import java.util.ArrayList;
import java.util.HashMap;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.session.SessionManager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HostListFeedbacksAdapter extends BaseAdapter {
	private FragmentActivity activity;
	private ArrayList<HashMap<String, String>> data;

	private boolean isLogin;
	SessionManager session;
	FragmentManager manager;

	private static LayoutInflater inflater = null;

	public HostListFeedbacksAdapter(FragmentActivity a,
			ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		session = new SessionManager(activity.getApplicationContext());
		manager = activity.getSupportFragmentManager();
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
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

	public static class ViewHolder {
		public TextView textView;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		if (convertView == null) {

			vi = inflater.inflate(
					R.drawable.host_profile_view_feedback_list_row, null);

			TextView username = (TextView) vi
					.findViewById(R.id.host_profile_list_feedback_row_username);
			TextView feedback = (TextView) vi
					.findViewById(R.id.host_profile_list_feedback_row_feedback);

			HashMap<String, String> tips = new HashMap<String, String>();
			tips = data.get(position);

			username.setText(tips
					.get(HostProfileViewFeedbackFragment.KEY_USERNAME));

			feedback.setText(tips
					.get(HostProfileViewFeedbackFragment.KEY_FEEDBACK));

		}
		return vi;

	}

}
