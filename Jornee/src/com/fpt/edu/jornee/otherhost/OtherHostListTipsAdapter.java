/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: OtherHostListTipsAdapter.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.otherhost;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class OtherHostListTipsAdapter extends BaseAdapter {
	private FragmentActivity activity;
	private ArrayList<HashMap<String, String>> data;

	private boolean isLogin;
	SessionManager session;
	FragmentManager manager;

	private static LayoutInflater inflater = null;

	public OtherHostListTipsAdapter(FragmentActivity a,
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
					R.drawable.other_host_profile_view_tips_list_row, null);

			TextView question = (TextView) vi
					.findViewById(R.id.other_host_profile_list_tips_row_question);
			TextView answer = (TextView) vi
					.findViewById(R.id.other_host_profile_list_tips_row_answer);

			HashMap<String, String> tips = new HashMap<String, String>();
			tips = data.get(position);

			question.setText(tips
					.get(HostOtherProfileViewTipsFragment.KEY_QUESTION));

			answer.setText(tips
					.get(HostOtherProfileViewTipsFragment.KEY_ANSWER));

		}
		return vi;

	}

}
