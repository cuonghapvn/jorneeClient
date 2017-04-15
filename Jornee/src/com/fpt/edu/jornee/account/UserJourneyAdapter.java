/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: UserJourneyAdapter.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.account;

import java.util.HashMap;
import java.util.LinkedList;

import com.fpt.edu.bean.Journey;
import com.fpt.edu.bean.OutsideActivityBean;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.customview.CNSquareImageView;
import com.fpt.edu.jornee.journey.CreateJourneyFragment;
import com.fpt.edu.jornee.journey.ViewOnlineJourneyDetailFragment;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.sqllite.DatabaseHandler;
import com.fpt.edu.jornee.sync.SyncJourney;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.DateTimeHelper;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserJourneyAdapter extends ArrayAdapter<OutsideActivityBean> {
	Context mContext;
	LinkedList<OutsideActivityBean> mJourneys;
	int layoutId;
	Activity mActivity;
	HashMap<String, String> user;
	SessionManager sessionManager;

	public UserJourneyAdapter(Context contxt,
			LinkedList<OutsideActivityBean> outsideActivities,
			int textViewResourceId, Activity activity) {
		super(contxt, textViewResourceId, outsideActivities);
		mContext = contxt;
		mJourneys = outsideActivities;
		layoutId = textViewResourceId;
		mActivity = activity;
		sessionManager = new SessionManager(mContext);
		user = new HashMap<String, String>();
		user = sessionManager.getUserDetails();
	}

	@Override
	public int getCount() {
		return mJourneys.size();
	}

	@Override
	public OutsideActivityBean getItem(int position) {
		return mJourneys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mJourneys.get(position).hashCode();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final OutsideActivityBean activityBean = mJourneys.get(position);
		View rowView = inflater.inflate(
				R.layout.fragment_user_profile_journeys_items, parent, false);
		TextView tvJourneyName = (TextView) rowView
				.findViewById(R.id.tvJourneyName);
		TextView tvJourneyCreateDate = (TextView) rowView
				.findViewById(R.id.tvJourneyCreateDate);
		TextView btnNumberOfEntries = (TextView) rowView
				.findViewById(R.id.btnNumberOfEntries);
		RelativeLayout emptyJourneySpan = (RelativeLayout) rowView
				.findViewById(R.id.emptyJourneySpan);
		LinearLayout notEmptyJourneySpan = (LinearLayout) rowView
				.findViewById(R.id.notEmptyJourneySpan);
		TextView txtMeMediaThumbnail1 = (TextView) rowView
				.findViewById(R.id.txtMeMediaThumbnail1);
		TextView txtMeMediaThumbnail2 = (TextView) rowView
				.findViewById(R.id.txtMeMediaThumbnail2);
		TextView txtMeMediaThumbnail3 = (TextView) rowView
				.findViewById(R.id.txtMeMediaThumbnail3);
		TextView txtMeMediaThumbnail4 = (TextView) rowView
				.findViewById(R.id.txtMeMediaThumbnail4);
		TextView txtMeMediaThumbnail5 = (TextView) rowView
				.findViewById(R.id.txtMeMediaThumbnail5);
		CNSquareImageView imgMeMediaThumbnail1 = (CNSquareImageView) rowView
				.findViewById(R.id.imgMeMediaThumbnail1);
		CNSquareImageView imgMeMediaThumbnail2 = (CNSquareImageView) rowView
				.findViewById(R.id.imgMeMediaThumbnail2);
		CNSquareImageView imgMeMediaThumbnail3 = (CNSquareImageView) rowView
				.findViewById(R.id.imgMeMediaThumbnail3);
		CNSquareImageView imgMeMediaThumbnail4 = (CNSquareImageView) rowView
				.findViewById(R.id.imgMeMediaThumbnail4);
		CNSquareImageView imgMeMediaThumbnail5 = (CNSquareImageView) rowView
				.findViewById(R.id.imgMeMediaThumbnail5);
		btnNumberOfEntries.setText(activityBean.getNumOfTotalEntry()
				+ " entries");
		tvJourneyName.setText(activityBean.getJourneyName());
		LinearLayout llJourneyDelete = (LinearLayout) rowView
				.findViewById(R.id.llJourneyDelete);
		TextView btnJourneyDelete = (TextView) rowView
				.findViewById(R.id.btnJourneyDelete);
		if (user.get(SessionManager.KEY_USERNAME).equals(
				activityBean.getUsername())) {
			llJourneyDelete.setVisibility(View.VISIBLE);
			btnJourneyDelete.setText("Delete");
			btnJourneyDelete.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					removeItem(position);
				}
			});
		}
		TextView btnJourneyViewMore = (TextView) rowView
				.findViewById(R.id.btnJourneyViewMore);
		btnJourneyViewMore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				viewJourneyDetail(activityBean);
			}
		});
		if (activityBean.getJourneyDateTime() != null) {
			tvJourneyCreateDate.setText("Create date: "
					+ DateTimeHelper.contextDateTime(DateTimeHelper
							.convertStringServerTimeToLocalDate(activityBean
									.getJourneyDateTime())));
		}
		if (activityBean.getEntries().size() == 0) {
			emptyJourneySpan.setVisibility(View.VISIBLE);
		} else {
			notEmptyJourneySpan.setVisibility(View.VISIBLE);
			if (activityBean.getEntries().size() > 0) {
				if (activityBean.getEntries().get(0).getType().equals("text")) {
					txtMeMediaThumbnail1.setVisibility(View.VISIBLE);
					txtMeMediaThumbnail1.setText(activityBean.getEntries()
							.get(0).getData() == null ? "Empty" : activityBean
							.getEntries().get(0).getData());
				} else if (activityBean.getEntries().get(0).getType()
						.equals("image")
						|| activityBean.getEntries().get(0).getType()
								.equals("checkin")) {
					imgMeMediaThumbnail1.setVisibility(View.VISIBLE);
					UniversalImageHelper.loadImage(mContext,
							imgMeMediaThumbnail1, Constant.SERVER_HOST
									+ Constant.IMAGE_TYPE_THUMBNAIL
									+ activityBean.getEntries().get(0)
											.getData());
				}
			}
			if (activityBean.getEntries().size() > 1) {
				if (activityBean.getEntries().get(1).getType().equals("text")) {
					txtMeMediaThumbnail2.setVisibility(View.VISIBLE);
					txtMeMediaThumbnail2.setText(activityBean.getEntries()
							.get(1).getData() == null ? "Empty" : activityBean
							.getEntries().get(1).getData());
				} else if (activityBean.getEntries().get(1).getType()
						.equals("image")
						|| activityBean.getEntries().get(1).getType()
								.equals("checkin")) {
					imgMeMediaThumbnail2.setVisibility(View.VISIBLE);
					UniversalImageHelper.loadImage(mContext,
							imgMeMediaThumbnail2, Constant.SERVER_HOST
									+ Constant.IMAGE_TYPE_THUMBNAIL
									+ activityBean.getEntries().get(1)
											.getData());
				}
			}
			if (activityBean.getEntries().size() > 2) {
				if (activityBean.getEntries().get(2).getType().equals("text")) {
					txtMeMediaThumbnail3.setVisibility(View.VISIBLE);
					txtMeMediaThumbnail3.setText(activityBean.getEntries()
							.get(2).getData() == null ? "Empty" : activityBean
							.getEntries().get(2).getData());
				} else if (activityBean.getEntries().get(2).getType()
						.equals("image")
						|| activityBean.getEntries().get(2).getType()
								.equals("checkin")) {
					imgMeMediaThumbnail3.setVisibility(View.VISIBLE);
					UniversalImageHelper.loadImage(mContext,
							imgMeMediaThumbnail3, Constant.SERVER_HOST
									+ Constant.IMAGE_TYPE_THUMBNAIL
									+ activityBean.getEntries().get(2)
											.getData());
				}
			}
			if (activityBean.getEntries().size() > 3) {
				if (activityBean.getEntries().get(3).getType().equals("text")) {
					txtMeMediaThumbnail4.setVisibility(View.VISIBLE);
					txtMeMediaThumbnail4.setText(activityBean.getEntries()
							.get(3).getData() == null ? "Empty" : activityBean
							.getEntries().get(3).getData());
				} else if (activityBean.getEntries().get(3).getType()
						.equals("image")
						|| activityBean.getEntries().get(3).getType()
								.equals("checkin")) {
					imgMeMediaThumbnail4.setVisibility(View.VISIBLE);
					UniversalImageHelper.loadImage(mContext,
							imgMeMediaThumbnail4, Constant.SERVER_HOST
									+ Constant.IMAGE_TYPE_THUMBNAIL
									+ activityBean.getEntries().get(3)
											.getData());
				}
			}
			if (activityBean.getEntries().size() > 4) {
				if (activityBean.getEntries().get(4).getType().equals("text")) {
					txtMeMediaThumbnail5.setVisibility(View.VISIBLE);
					txtMeMediaThumbnail5.setText(activityBean.getEntries()
							.get(0).getData() == null ? "Empty" : activityBean
							.getEntries().get(4).getData());
				} else if (activityBean.getEntries().get(4).getType()
						.equals("image")
						|| activityBean.getEntries().get(4).getType()
								.equals("checkin")) {
					imgMeMediaThumbnail5.setVisibility(View.VISIBLE);
					UniversalImageHelper.loadImage(mContext,
							imgMeMediaThumbnail5, Constant.SERVER_HOST
									+ Constant.IMAGE_TYPE_THUMBNAIL
									+ activityBean.getEntries().get(4)
											.getData());
				}
			}
		}

		return rowView;
	}

	public void removeItem(final int position) {
		new AlertDialog.Builder(mActivity)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Delete Journey")
				.setMessage("Are you sure you want to delete this journey?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								DatabaseHandler databaseHandler = new DatabaseHandler(
										mContext);
								Journey journey = databaseHandler
										.getJourneyInforServerID(mJourneys.get(
												position).getJourneyID());
								if (journey == null) {
									journey = new Journey();
									journey.setServerJourneyID(mJourneys.get(
											position).getJourneyID());
								}
								new SyncJourney(journey,
										(MainActivity) mActivity, null,
										Journey.JOURNEY_FUNCTION_DELETE)
										.execute();
								mJourneys.remove(position);
								notifyDataSetChanged();
							}

						}).setNegativeButton("No", null).show();

	}

	public void viewJourneyDetail(OutsideActivityBean activityBean) {
		Fragment fragment = new ViewOnlineJourneyDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,
				activityBean.getJourneyName());
		bundle.putString(CreateJourneyFragment.JOURNEY_NAME,
				activityBean.getJourneyName());
		bundle.putString(CreateJourneyFragment.JOURNEY_ID,
				activityBean.getJourneyID());
		fragment.setArguments(bundle);
		MainActivity activity = (MainActivity) mActivity;
		activity.replaceFragment(fragment);
	}

}
