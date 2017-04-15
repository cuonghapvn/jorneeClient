/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: SelectEntryAdapter.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.journey;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fpt.edu.bean.Entry;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.customview.CNRectangleImageView;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.DateTimeHelper;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

public class SelectEntryAdapter extends ArrayAdapter<Entry> {
	ArrayList<Entry> listEntries;
	Context mContext;
	int resId;
	Activity mActivity;
	HashMap<String, String> user;
	SessionManager sessionManager;
	ArrayList<Entry> chosenList;
	ImageView btnRemoveSelection;

	public SelectEntryAdapter(Context context, int textViewResourceId,
			ArrayList<Entry> entries, Activity activity,
			ImageView btnRemoveSelection) {
		super(context, textViewResourceId, entries);
		listEntries = entries;
		mContext = context;
		mActivity = activity;
		sessionManager = new SessionManager(mContext);
		user = new HashMap<String, String>();
		user = sessionManager.getUserDetails();
		chosenList = new ArrayList<Entry>();
		if (btnRemoveSelection != null) {
			this.btnRemoveSelection = btnRemoveSelection;
		}
	}

	@Override
	public int getCount() {
		return listEntries.size();
	}

	@Override
	public Entry getItem(int position) {
		return listEntries.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listEntries.get(position).hashCode();
	}

	public ArrayList<Entry> getChosenList() {
		return chosenList;
	}

	public void setChosenList(ArrayList<Entry> chosenList) {
		this.chosenList = chosenList;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final Entry currentItem = listEntries.get(position);
		final View rowView = inflater.inflate(
				R.layout.journey_select_entry_row, parent, false);
		LinearLayout meContentView = (LinearLayout) rowView
				.findViewById(R.id.meContentView);
		TextView tvEntryID = (TextView) rowView.findViewById(R.id.tvEntryID);
		TextView tvJourneyDateTime = (TextView) rowView
				.findViewById(R.id.tvJourneyDateTime);
		tvEntryID.setText(currentItem.getEntryID());
		tvJourneyDateTime
				.setText(DateTimeHelper.contextDateTime(DateTimeHelper
						.convertStringServerTimeToLocalDate(currentItem
								.getDateTime())));
		FrameLayout frameImage = (FrameLayout) rowView
				.findViewById(R.id.frameImage);
		CNRectangleImageView ivEntryImage = (CNRectangleImageView) rowView
				.findViewById(R.id.ivEntryImage);
		TextView tvDescriptionWithImage = (TextView) rowView
				.findViewById(R.id.tvDescriptionWithImage);
		FrameLayout frameText = (FrameLayout) rowView
				.findViewById(R.id.frameText);
		TextView tvDescription = (TextView) rowView
				.findViewById(R.id.tvDescription);
		if (currentItem.getType() != null) {
			if (currentItem.getType().equals("image")) {
				frameImage.setVisibility(View.VISIBLE);
				if (currentItem.getPath() != null) {
					ivEntryImage.setVisibility(View.VISIBLE);
					UniversalImageHelper.loadImage(mContext, ivEntryImage,
							currentItem.getPath());
				}
				if (currentItem.getText() != null
						&& currentItem.getText().length() > 0) {
					tvDescriptionWithImage.setVisibility(View.VISIBLE);
					tvDescriptionWithImage.setText(currentItem.getText());
				}
			} else if (currentItem.getType().equals("text")) {
				frameText.setVisibility(View.VISIBLE);
				if (currentItem.getText() != null) {
					tvDescription.setVisibility(View.VISIBLE);
					tvDescription.setText(currentItem.getText());
				}
			} else if (currentItem.getType().equals("checkin")) {
				frameImage.setVisibility(View.VISIBLE);
				if (currentItem.getPath() != null) {
					ivEntryImage.setVisibility(View.VISIBLE);
					UniversalImageHelper.loadImage(mContext, ivEntryImage, currentItem
							.getPath());
				}
				if (currentItem.getText() != null
						&& currentItem.getText().length() > 0) {
					tvDescriptionWithImage.setVisibility(View.VISIBLE);
					tvDescriptionWithImage.setText(currentItem.getText());
				}
			} else {
				frameText.setVisibility(View.VISIBLE);
				tvDescription.setVisibility(View.VISIBLE);
				tvDescription.setText("Empty entry");
			}
		} else {
			frameText.setVisibility(View.VISIBLE);
			tvDescription.setVisibility(View.VISIBLE);
			tvDescription.setText("Empty entry");
		}
		final CheckBox cbSelectEntry = (CheckBox) rowView
				.findViewById(R.id.cbSelectEntry);
		if (isInList(currentItem)) {
			cbSelectEntry.setChecked(true);
		} else {
			cbSelectEntry.setChecked(false);
		}
		cbSelectEntry
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							addToList(currentItem);
						} else {
							removeFromList(currentItem);
						}
						if (btnRemoveSelection != null) {
							if (chosenList.size() > 0) {
								btnRemoveSelection.setEnabled(true);
								btnRemoveSelection
										.setBackgroundResource(R.drawable.btn_journey_remove_entry_button);
							} else {
								btnRemoveSelection.setEnabled(false);
								btnRemoveSelection
										.setBackgroundResource(R.drawable.btn_journey_remove_entry_inactive_button);
							}
						}
					}
				});
		rowView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cbSelectEntry.setChecked(!cbSelectEntry.isChecked());
			}
		});
		return rowView;
	}

	public void removeFromList(Entry id) {
		for (int i = 0; i < chosenList.size(); i++) {
			if (chosenList.get(i).getEntryID().equals(id.getEntryID())) {
				chosenList.remove(i);
			}
		}
	}

	public void addToList(Entry id) {
		if (!isInList(id)) {
			chosenList.add(id);
		}
	}

	public boolean isInList(Entry id) {
		boolean found = false;
		for (int i = 0; i < chosenList.size(); i++) {
			if (chosenList.get(i).getEntryID().equals(id.getEntryID())) {
				found = true;
			}
		}
		return found;
	}

}
