/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: LocationListAllHost.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.location;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.fpt.edu.bean.HostInLocation;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.account.LoginFragment;
import com.fpt.edu.jornee.host.HostProfileFragment;
import com.fpt.edu.jornee.otherhost.HostOtherProfileFragment;
import com.fpt.edu.jornee.outside.SearchFriendFragment;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.JSONParser;

public class LocationListAllHost extends Fragment {

	public static String KEY_HOST_REGIS_LOCATION = "regis_location";
	public static String KEY_HOST_AVATAR = "avatar";
	public static String KEY_HOST_USERNAME = "username";
	public static String KEY_HOST_STATUS = "status";
	JSONParser parser;
	Context context;
	SessionManager session;
	ListView listAllHost;
	ArrayList<HostInLocation> data;
	Button btnBack;

	public LocationListAllHost() {
		// Empty constructor required for fragment subclasses
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_location_list_all_host, container, false);

		setHasOptionsMenu(true);
		data = new ArrayList<HostInLocation>();
		context = getActivity().getApplicationContext();
		parser = new JSONParser(context);
		session = new SessionManager(context);
		Bundle extras = getArguments();
		if (extras != null) {
			data = extras.getParcelableArrayList("all_host");
			getActivity().setTitle(
					extras.getString(Constant.FRAGMENT_TITLE_BUNDLE));

		}
		btnBack = (Button) rootView
				.findViewById(R.id.location_list_all_host_btnBack);
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Fragment fragment = new LocationFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,
						"Search for location...");
				fragment.setArguments(bundle);
				MainActivity activity = (MainActivity) getActivity();
				activity.replaceFragment(fragment);
			}
		});

		listAllHost = (ListView) rootView
				.findViewById(R.id.location_list_all_host);

		ArrayList<HashMap<String, String>> datahost = new ArrayList<HashMap<String, String>>();

		for (HostInLocation a : data) {
			HashMap<String, String> map = new HashMap<String, String>();

			map.put(KEY_HOST_USERNAME, a.getUsername());
			map.put(KEY_HOST_AVATAR, a.getAvatar());
			map.put(KEY_HOST_REGIS_LOCATION, a.getRegis_location());
			map.put(KEY_HOST_STATUS, a.getStatus());
			datahost.add(map);
		}
		if (isAdded()) {
			LocationListAllHostAdapter adapter = new LocationListAllHostAdapter(
					getActivity(), datahost);
			listAllHost.setAdapter(adapter);
		}
		listAllHost.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) parent
						.getItemAtPosition(position);

				Fragment fragment = new HostOtherProfileFragment();
				Bundle bundle = new Bundle();
				bundle.putString("host_username", map.get(KEY_HOST_USERNAME));
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Host Profile");

				fragment.setArguments(bundle);
				MainActivity activity = (MainActivity) getActivity();
				activity.replaceFragment(fragment);

			}
		});
		return rootView;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.outside, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	public void callLogin() {

		if (isAdded()) {
			Fragment fragment0 = new LoginFragment();

			MainActivity activity = (MainActivity) getActivity();
			activity.replaceFragment(fragment0);
		}
	}

}
