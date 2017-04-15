/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: MenuNavigationListAdapter.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.utils;

import java.util.ArrayList;
import java.util.HashMap;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.fpt.edu.bean.UserProfile;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.customview.CNSquareImageView;
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

public class MenuNavigationListAdapter extends BaseAdapter {
	private static final int TYPE_ITEM1 = 0;
	private static final int TYPE_ITEM2 = 1;

	private FragmentActivity activity;
	private ArrayList<HashMap<String, String>> data;
	private String avatar;

	private boolean isLogin = false;
	SessionManager session;
	FragmentManager manager;
	int[] menuIcons = new int[10];

	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public MenuNavigationListAdapter(FragmentActivity a,
			ArrayList<HashMap<String, String>> d) {

		menuIcons[0] = R.drawable.menu_home;
		menuIcons[1] = R.drawable.menu_outside;
		menuIcons[2] = R.drawable.menu_message;
		menuIcons[3] = R.drawable.menu_host;
		menuIcons[4] = R.drawable.menu_location;
//		menuIcons[5] = R.drawable.menu_setting;
//		menuIcons[6] = R.drawable.menu_help;
		menuIcons[5] = R.drawable.menu_logout;

		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());

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

	int type;

	@Override
	public int getItemViewType(int position) {

		if (position == 0) {
			type = TYPE_ITEM1;
		} else if (position != 0) {
			type = TYPE_ITEM2;
		}
		return type;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	public static class ViewHolder {
		public TextView textView;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		int type = getItemViewType(position);

		if (convertView == null) {
			if (type == TYPE_ITEM1) {
				vi = inflater.inflate(
						R.drawable.menu_navigation_list_row_user_avatar, null);
				CNSquareImageView userAvatar = (CNSquareImageView) vi
						.findViewById(R.id.userAvatar);
				HashMap<String, String> user = session.getUserDetails();
				TextView login = (TextView) vi.findViewById(R.id.login);
				TextView username = (TextView) vi.findViewById(R.id.username);
				if (isLogin) {
					username.setText(user.get(SessionManager.KEY_USERNAME));

					if (session.getUserDetails().get(
							SessionManager.KEY_OFFLINE_AVATAR) != null
							&& session.getUserDetails().get(
									SessionManager.KEY_OFFLINE_AVATAR) != "") {

						UniversalImageHelper.loadImage(
								activity.getApplicationContext(), userAvatar,
								user.get(SessionManager.KEY_OFFLINE_AVATAR));
					} else {

						UniversalImageHelper.loadImage(
								activity.getApplicationContext(),
								userAvatar,
								Constant.SERVER_HOST + "thumbnail_"
										+ user.get(SessionManager.KEY_AVATAR));
					}
				} else {
					vi = inflater.inflate(
							R.drawable.menu_navigation_list_row_function, null);
					TextView functionName = (TextView) vi
							.findViewById(R.id.functionName);
					functionName.setText("Login");
				}

			}
			if (type == TYPE_ITEM2) {
				vi = inflater.inflate(
						R.drawable.menu_navigation_list_row_function, null);
				TextView functionName = (TextView) vi
						.findViewById(R.id.functionName);
				ImageView imageViewIcon = (ImageView) vi
						.findViewById(R.id.imageViewIcon);
				TextView textViewSeparation = (TextView) vi.findViewById(R.id.textViewSeparation);
				imageViewIcon.setBackgroundResource(menuIcons[position - 1]);
				HashMap<String, String> functionNames = new HashMap<String, String>();
				functionNames = data.get(position);
				
				
				
				
				
				if (functionNames.get(MainActivity.FUNCTION_NAME).equals(
						"Logout")) {
					
					if (!isLogin) {
						functionName.setText("");
						imageViewIcon.setVisibility(View.GONE);
						textViewSeparation.setVisibility(View.GONE);
					}else{

						functionName.setText(functionNames
								.get(MainActivity.FUNCTION_NAME));
					}
					
					
				} else {
					functionName.setText(functionNames
							.get(MainActivity.FUNCTION_NAME));
				}
				
				
				
			}
			
		
		}
		return vi;

	}

}
