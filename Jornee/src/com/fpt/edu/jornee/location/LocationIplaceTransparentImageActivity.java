/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: LocationIplaceTransparentImageActivity.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.location;

import java.util.ArrayList;

import com.fpt.edu.bean.InterestingPlaceEntry;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.customview.CNImageView;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class LocationIplaceTransparentImageActivity extends Activity {

	ListView listViewImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iplace_transparent_image);

		listViewImage = (ListView) findViewById(R.id.listViewImage);

		Intent i = getIntent();
		final ArrayList<InterestingPlaceEntry> interestingPlaceEntries = (ArrayList<InterestingPlaceEntry>) i
				.getSerializableExtra("interestingPlaceEntries");

		if (interestingPlaceEntries != null) {
			BaseAdapter adapterEntry = new BaseAdapter() {

				@Override
				public int getCount() {
					return interestingPlaceEntries.size();
				}

				@Override
				public Object getItem(int position) {
					return interestingPlaceEntries.get(position);
				}

				@Override
				public long getItemId(int position) {
					return 0;
				}

				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					View vi = LayoutInflater
							.from(parent.getContext())
							.inflate(
									R.layout.location_interestingplace_entry_horizontal_list_item,
									null);

					CNImageView imageView = (CNImageView) vi
							.findViewById(R.id.location_list_entry_interesting_place);

					TextView placeName = (TextView) vi
							.findViewById(R.id.textView_location_list_entry_interesting_place);
					placeName
							.setText(interestingPlaceEntries.get(position).IPlaceName);

					UniversalImageHelper
							.loadImage(
									LocationIplaceTransparentImageActivity.this,
									imageView,
									Constant.SERVER_HOST
											+ "medium_"
											+ interestingPlaceEntries
													.get(position).path);

					return vi;
				}

			};
			listViewImage.setAdapter(adapterEntry);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int eventaction = event.getAction();

		switch (eventaction) {
		case MotionEvent.ACTION_DOWN:

			finish();
			break;

		case MotionEvent.ACTION_MOVE:
			// finger moves on the screen
			break;

		case MotionEvent.ACTION_UP:
			// finger leaves the screen
			break;

		}

		return true;
	}

}
