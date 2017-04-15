/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: LocationListAllHistoryDetail.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.location;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devsmart.android.ui.HorizontalListView;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.account.LoginFragment;
import com.fpt.edu.jornee.customview.CNImageView;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.ImageLoader;
import com.fpt.edu.jornee.utils.JSONParser;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

public class LocationListAllHistoryDetail extends Fragment {

	JSONParser parser;
	Context context;
	SessionManager session;
	HorizontalListView listAllHistoryImage;
	ImageLoader imageLoader;
	ProgressDialog progressDialog;
	ArrayList<String> dataImage;
	ArrayList<String> data;
	String language;
	String content;
	// Button btnBack;
	TextView txtContent;
	ProgressBar progressBar1HistoryDetail;

	public LocationListAllHistoryDetail() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_location_list_all_history_details, container,
				false);
		setHasOptionsMenu(true);
		progressBar1HistoryDetail = (ProgressBar) rootView
				.findViewById(R.id.progressBar1HistoryDetail);
		data = new ArrayList<String>();
		language = "";
		content = "";
		imageLoader = new ImageLoader(getActivity());
		dataImage = new ArrayList<String>();
		context = getActivity().getApplicationContext();
		parser = new JSONParser(context);
		session = new SessionManager(context);
		Bundle extras = getArguments();
		listAllHistoryImage = (HorizontalListView) rootView
				.findViewById(R.id.location_list_all_history_image);
		listAllHistoryImage.setAdapter(mAdapter);

		txtContent = (TextView) rootView
				.findViewById(R.id.location_list_all_history_content);
		txtContent.setMovementMethod(new ScrollingMovementMethod());
		// btnBack = (Button) rootView
		// .findViewById(R.id.location_list_history_details_btnBack);

		if (extras != null) {
			dataImage = extras.getStringArrayList("image_array");
			content = extras.getString("content");
			data = extras.getStringArrayList("string_results");
			language = extras.getString("language");
			progressBar1HistoryDetail.setVisibility(View.GONE);
			getActivity().setTitle(
					extras.getString(Constant.FRAGMENT_TITLE_BUNDLE));

		}
		txtContent.setText(content);
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

	private BaseAdapter mAdapter = new BaseAdapter() {

		@Override
		public int getCount() {
			return dataImage.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View vi = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.location_list_history_horizontal_list_item, null);

			CNImageView imageView = (CNImageView) vi
					.findViewById(R.id.location_list_history_image);

			if (isAdded()) {
				UniversalImageHelper.loadImage(getActivity()
						.getApplicationContext(), imageView, dataImage
						.get(position));
			}
			return vi;
		}

	};

}
