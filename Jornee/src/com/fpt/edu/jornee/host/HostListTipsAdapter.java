/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HostListTipsAdapter.java
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
import android.text.Editable;
import android.text.TextWatcher;

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

public class HostListTipsAdapter extends BaseAdapter {
	private FragmentActivity activity;
	public ArrayList<HashMap<String, String>> data;

	private boolean isLogin;
	public static boolean isTextChanged = false;
	SessionManager session;
	FragmentManager manager;

	private static LayoutInflater inflater = null;

	public HostListTipsAdapter(FragmentActivity a,
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

	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		if (convertView == null) {
			HashMap<String, String> tips = new HashMap<String, String>();
			tips = data.get(position);
			vi = inflater.inflate(R.drawable.host_profile_view_tips_list_row,
					null);

			TextView question = (TextView) vi
					.findViewById(R.id.host_profile_list_tips_row_question);
			EditText answer = (EditText) vi
					.findViewById(R.id.host_profile_list_tips_row_answer);

			answer.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {

					data.get(position).put(
							HostProfileViewTipsFragment.KEY_ANSWER,
							s.toString());
					isTextChanged = true;
				}
			});

			question.setText(tips.get(HostProfileViewTipsFragment.KEY_QUESTION));

			answer.setText(tips.get(HostProfileViewTipsFragment.KEY_ANSWER));

		}
		return vi;

	}

}
