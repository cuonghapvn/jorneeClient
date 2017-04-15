/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: OutsideActivityListAdapter.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.outside;

import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fpt.edu.bean.OutsideActivityBean;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.account.UserProfileFragment;
import com.fpt.edu.jornee.customview.CNSquareImageView;
import com.fpt.edu.jornee.journey.CreateJourneyFragment;
import com.fpt.edu.jornee.journey.ViewOnlineJourneyDetailFragment;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.DateTimeHelper;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

public class OutsideActivityListAdapter extends
		ArrayAdapter<OutsideActivityBean> {

	Context mContext;
	LinkedList<OutsideActivityBean> mJourneys;
	int layoutId;
	Activity mActivity;

	public OutsideActivityListAdapter(Context contxt,
			LinkedList<OutsideActivityBean> outsideActivities,
			int textViewResourceId, Activity activity) {
		super(contxt, textViewResourceId, outsideActivities);
		mContext = contxt;
		mJourneys = outsideActivities;
		layoutId = textViewResourceId;
		mActivity = activity;
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
		CNSquareImageView imgMeMediaThumbnail1;
		CNSquareImageView imgMeMediaThumbnail2;
		CNSquareImageView imgMeMediaThumbnail3;
		CNSquareImageView imgMeMediaThumbnail4;
		CNSquareImageView imgMeMediaThumbnail5;
		TextView txtMeMediaThumbnail1;
		TextView txtMeMediaThumbnail2;
		TextView txtMeMediaThumbnail3;
		TextView txtMeMediaThumbnail4;
		TextView txtMeMediaThumbnail5;
		View rowView;
		int res = R.layout.outside_activity_item_0image;
		rowView = inflater.inflate(res, parent, false);
		switch (activityBean.getNumOfReturnEntry()) {
		case 0:
			res = R.layout.outside_activity_item_0image;
			rowView = inflater.inflate(res, parent, false);
			break;
		case 1:
			res = R.layout.outside_activity_item_1image;
			rowView = inflater.inflate(res, parent, false);
			imgMeMediaThumbnail1 = (CNSquareImageView) rowView
					.findViewById(R.id.imgMeMediaThumbnail1);
			txtMeMediaThumbnail1 = (TextView) rowView
					.findViewById(R.id.txtMeMediaThumbnail1);
			if (activityBean.getEntries().get(0).getType().equals("text")) {
				txtMeMediaThumbnail1.setText(activityBean.getEntries().get(0)
						.getData());
				txtMeMediaThumbnail1
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								popupEntryText(((TextView) v).getText()
										.toString());
							}
						});
			} else if (activityBean.getEntries().get(0).getType()
					.equals("image") || activityBean.getEntries().get(0).getType()
					.equals("checkin")) {
				txtMeMediaThumbnail1.setVisibility(View.INVISIBLE);
				imgMeMediaThumbnail1.setVisibility(View.VISIBLE);
				UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail1,
						Constant.SERVER_HOST + "large_"
								+ activityBean.getEntries().get(0).getData());
			}
			break;
		case 2:
			res = R.layout.outside_activity_item_2image;
			rowView = inflater.inflate(res, parent, false);
			imgMeMediaThumbnail1 = (CNSquareImageView) rowView
					.findViewById(R.id.imgMeMediaThumbnail1);
			txtMeMediaThumbnail1 = (TextView) rowView
					.findViewById(R.id.txtMeMediaThumbnail1);
			if (activityBean.getEntries().get(0).getType().equals("text")) {
				txtMeMediaThumbnail1.setText(activityBean.getEntries().get(0)
						.getData());
				txtMeMediaThumbnail1
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								popupEntryText(((TextView) v).getText()
										.toString());
							}
						});
			} else if (activityBean.getEntries().get(0).getType()
					.equals("image") || activityBean.getEntries().get(0).getType()
					.equals("checkin")) {
				txtMeMediaThumbnail1.setVisibility(View.INVISIBLE);
				imgMeMediaThumbnail1.setVisibility(View.VISIBLE);
				UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail1,
						Constant.SERVER_HOST + "large_"
								+ activityBean.getEntries().get(0).getData());
			}
			imgMeMediaThumbnail2 = (CNSquareImageView) rowView
					.findViewById(R.id.imgMeMediaThumbnail2);
			txtMeMediaThumbnail2 = (TextView) rowView
					.findViewById(R.id.txtMeMediaThumbnail2);
			if (activityBean.getEntries().get(1).getType().equals("text")) {
				txtMeMediaThumbnail2.setText(activityBean.getEntries().get(1)
						.getData());
				txtMeMediaThumbnail2
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								popupEntryText(((TextView) v).getText()
										.toString());
							}
						});
			} else if (activityBean.getEntries().get(1).getType()
					.equals("image") || activityBean.getEntries().get(1).getType()
					.equals("checkin")) {
				txtMeMediaThumbnail2.setVisibility(View.INVISIBLE);
				imgMeMediaThumbnail2.setVisibility(View.VISIBLE);
				UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail2,
						Constant.SERVER_HOST + "large_"
								+ activityBean.getEntries().get(1).getData());
			}
			break;
		case 3:
			res = R.layout.outside_activity_item_3image;
			rowView = inflater.inflate(res, parent, false);
			imgMeMediaThumbnail1 = (CNSquareImageView) rowView
					.findViewById(R.id.imgMeMediaThumbnail1);
			txtMeMediaThumbnail1 = (TextView) rowView
					.findViewById(R.id.txtMeMediaThumbnail1);
			if (activityBean.getEntries().get(0).getType().equals("text")) {
				txtMeMediaThumbnail1.setText(activityBean.getEntries().get(0)
						.getData());
				txtMeMediaThumbnail1
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								popupEntryText(((TextView) v).getText()
										.toString());
							}
						});
			} else if (activityBean.getEntries().get(0).getType()
					.equals("image") || activityBean.getEntries().get(0).getType()
					.equals("checkin")) {
				txtMeMediaThumbnail1.setVisibility(View.INVISIBLE);
				imgMeMediaThumbnail1.setVisibility(View.VISIBLE);
				UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail1,
						Constant.SERVER_HOST + "large_"
								+ activityBean.getEntries().get(0).getData());
			}
			imgMeMediaThumbnail2 = (CNSquareImageView) rowView
					.findViewById(R.id.imgMeMediaThumbnail2);
			txtMeMediaThumbnail2 = (TextView) rowView
					.findViewById(R.id.txtMeMediaThumbnail2);
			if (activityBean.getEntries().get(1).getType().equals("text")) {
				txtMeMediaThumbnail2.setText(activityBean.getEntries().get(1)
						.getData());
				txtMeMediaThumbnail2
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								popupEntryText(((TextView) v).getText()
										.toString());
							}
						});
			} else if (activityBean.getEntries().get(1).getType()
					.equals("image") || activityBean.getEntries().get(1).getType()
					.equals("checkin")) {
				txtMeMediaThumbnail2.setVisibility(View.INVISIBLE);
				imgMeMediaThumbnail2.setVisibility(View.VISIBLE);
				UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail2,
						Constant.SERVER_HOST + "large_"
								+ activityBean.getEntries().get(1).getData());
			}
			imgMeMediaThumbnail3 = (CNSquareImageView) rowView
					.findViewById(R.id.imgMeMediaThumbnail3);
			txtMeMediaThumbnail3 = (TextView) rowView
					.findViewById(R.id.txtMeMediaThumbnail3);
			if (activityBean.getEntries().get(2).getType().equals("text")) {
				txtMeMediaThumbnail3.setText(activityBean.getEntries().get(2)
						.getData());
				txtMeMediaThumbnail3
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								popupEntryText(((TextView) v).getText()
										.toString());
							}
						});
			} else if (activityBean.getEntries().get(2).getType()
					.equals("image") || activityBean.getEntries().get(2).getType()
					.equals("checkin")) {
				txtMeMediaThumbnail3.setVisibility(View.INVISIBLE);
				imgMeMediaThumbnail3.setVisibility(View.VISIBLE);
				UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail3,
						Constant.SERVER_HOST + "large_"
								+ activityBean.getEntries().get(2).getData());
			}
			break;
		case 4:
			res = R.layout.outside_activity_item_4image;
			rowView = inflater.inflate(res, parent, false);
			imgMeMediaThumbnail1 = (CNSquareImageView) rowView
					.findViewById(R.id.imgMeMediaThumbnail1);
			txtMeMediaThumbnail1 = (TextView) rowView
					.findViewById(R.id.txtMeMediaThumbnail1);
			if (activityBean.getEntries().get(0).getType().equals("text")) {
				txtMeMediaThumbnail1.setText(activityBean.getEntries().get(0)
						.getData());
				txtMeMediaThumbnail1
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								popupEntryText(((TextView) v).getText()
										.toString());
							}
						});
			} else if (activityBean.getEntries().get(0).getType()
					.equals("image") || activityBean.getEntries().get(0).getType()
					.equals("checkin")) {
				txtMeMediaThumbnail1.setVisibility(View.INVISIBLE);
				imgMeMediaThumbnail1.setVisibility(View.VISIBLE);
				UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail1,
						Constant.SERVER_HOST + "large_"
								+ activityBean.getEntries().get(0).getData());
			}
			imgMeMediaThumbnail2 = (CNSquareImageView) rowView
					.findViewById(R.id.imgMeMediaThumbnail2);
			txtMeMediaThumbnail2 = (TextView) rowView
					.findViewById(R.id.txtMeMediaThumbnail2);
			if (activityBean.getEntries().get(1).getType().equals("text")) {
				txtMeMediaThumbnail2.setText(activityBean.getEntries().get(1)
						.getData());
				txtMeMediaThumbnail2
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								popupEntryText(((TextView) v).getText()
										.toString());
							}
						});
			} else if (activityBean.getEntries().get(1).getType()
					.equals("image") || activityBean.getEntries().get(1).getType()
					.equals("checkin")) {
				txtMeMediaThumbnail2.setVisibility(View.INVISIBLE);
				imgMeMediaThumbnail2.setVisibility(View.VISIBLE);
				UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail2,
						Constant.SERVER_HOST + "large_"
								+ activityBean.getEntries().get(1).getData());
			}
			imgMeMediaThumbnail3 = (CNSquareImageView) rowView
					.findViewById(R.id.imgMeMediaThumbnail3);
			txtMeMediaThumbnail3 = (TextView) rowView
					.findViewById(R.id.txtMeMediaThumbnail3);
			if (activityBean.getEntries().get(2).getType().equals("text")) {
				txtMeMediaThumbnail3.setText(activityBean.getEntries().get(2)
						.getData());
				txtMeMediaThumbnail3
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								popupEntryText(((TextView) v).getText()
										.toString());
							}
						});
			} else if (activityBean.getEntries().get(2).getType()
					.equals("image") || activityBean.getEntries().get(2).getType()
					.equals("checkin")) {
				txtMeMediaThumbnail3.setVisibility(View.INVISIBLE);
				imgMeMediaThumbnail3.setVisibility(View.VISIBLE);
				UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail3,
						Constant.SERVER_HOST + "large_"
								+ activityBean.getEntries().get(2).getData());
			}
			imgMeMediaThumbnail4 = (CNSquareImageView) rowView
					.findViewById(R.id.imgMeMediaThumbnail4);
			txtMeMediaThumbnail4 = (TextView) rowView
					.findViewById(R.id.txtMeMediaThumbnail4);
			if (activityBean.getEntries().get(3).getType().equals("text")) {
				txtMeMediaThumbnail4.setText(activityBean.getEntries().get(3)
						.getData());
				txtMeMediaThumbnail4
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								popupEntryText(((TextView) v).getText()
										.toString());
							}
						});
			} else if (activityBean.getEntries().get(3).getType()
					.equals("image") || activityBean.getEntries().get(3).getType()
					.equals("checkin")) {
				txtMeMediaThumbnail4.setVisibility(View.INVISIBLE);
				imgMeMediaThumbnail4.setVisibility(View.VISIBLE);
				UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail4,
						Constant.SERVER_HOST + "large_"
								+ activityBean.getEntries().get(3).getData());
			}
			break;
		case 5:
			res = R.layout.outside_activity_item_5image;
			rowView = inflater.inflate(res, parent, false);
			imgMeMediaThumbnail1 = (CNSquareImageView) rowView
					.findViewById(R.id.imgMeMediaThumbnail1);
			txtMeMediaThumbnail1 = (TextView) rowView
					.findViewById(R.id.txtMeMediaThumbnail1);
			if (activityBean.getEntries().get(0).getType().equals("text")) {
				System.out.println("Entry with text");
				txtMeMediaThumbnail1.setText(activityBean.getEntries().get(0)
						.getData());
				txtMeMediaThumbnail1
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								popupEntryText(((TextView) v).getText()
										.toString());
							}
						});
			} else if (activityBean.getEntries().get(0).getType()
					.equals("image") || activityBean.getEntries().get(0).getType()
					.equals("checkin")) {
				txtMeMediaThumbnail1.setVisibility(View.INVISIBLE);
				imgMeMediaThumbnail1.setVisibility(View.VISIBLE);
				UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail1,
						Constant.SERVER_HOST + "large_"
								+ activityBean.getEntries().get(0).getData());
			}
			imgMeMediaThumbnail2 = (CNSquareImageView) rowView
					.findViewById(R.id.imgMeMediaThumbnail2);
			txtMeMediaThumbnail2 = (TextView) rowView
					.findViewById(R.id.txtMeMediaThumbnail2);
			if (activityBean.getEntries().get(1).getType().equals("text")) {
				System.out.println("Entry with text");
				txtMeMediaThumbnail2.setText(activityBean.getEntries().get(1)
						.getData());
				txtMeMediaThumbnail2
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								popupEntryText(((TextView) v).getText()
										.toString());
							}
						});
			} else if (activityBean.getEntries().get(1).getType()
					.equals("image") || activityBean.getEntries().get(1).getType()
					.equals("checkin")) {
				txtMeMediaThumbnail2.setVisibility(View.INVISIBLE);
				imgMeMediaThumbnail2.setVisibility(View.VISIBLE);
				UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail2,
						Constant.SERVER_HOST + "large_"
								+ activityBean.getEntries().get(1).getData());
			}
			imgMeMediaThumbnail3 = (CNSquareImageView) rowView
					.findViewById(R.id.imgMeMediaThumbnail3);
			txtMeMediaThumbnail3 = (TextView) rowView
					.findViewById(R.id.txtMeMediaThumbnail3);
			if (activityBean.getEntries().get(2).getType().equals("text")) {
				System.out.println("Entry with text");
				txtMeMediaThumbnail3.setText(activityBean.getEntries().get(2)
						.getData());
				txtMeMediaThumbnail3
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								popupEntryText(((TextView) v).getText()
										.toString());
							}
						});
			} else if (activityBean.getEntries().get(2).getType()
					.equals("image") || activityBean.getEntries().get(2).getType()
					.equals("checkin")) {
				txtMeMediaThumbnail3.setVisibility(View.INVISIBLE);
				imgMeMediaThumbnail3.setVisibility(View.VISIBLE);
				UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail3,
						Constant.SERVER_HOST + "large_"
								+ activityBean.getEntries().get(2).getData());
			}
			imgMeMediaThumbnail4 = (CNSquareImageView) rowView
					.findViewById(R.id.imgMeMediaThumbnail4);
			txtMeMediaThumbnail4 = (TextView) rowView
					.findViewById(R.id.txtMeMediaThumbnail4);
			if (activityBean.getEntries().get(3).getType().equals("text")) {
				System.out.println("Entry with text");
				txtMeMediaThumbnail4.setText(activityBean.getEntries().get(3)
						.getData());
				txtMeMediaThumbnail4
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								popupEntryText(((TextView) v).getText()
										.toString());
							}
						});
			} else if (activityBean.getEntries().get(3).getType()
					.equals("image") || activityBean.getEntries().get(3).getType()
					.equals("checkin")) {
				txtMeMediaThumbnail4.setVisibility(View.INVISIBLE);
				imgMeMediaThumbnail4.setVisibility(View.VISIBLE);
				UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail4,
						Constant.SERVER_HOST + "large_"
								+ activityBean.getEntries().get(3).getData());
			}
			imgMeMediaThumbnail5 = (CNSquareImageView) rowView
					.findViewById(R.id.imgMeMediaThumbnail5);
			txtMeMediaThumbnail5 = (TextView) rowView
					.findViewById(R.id.txtMeMediaThumbnail5);
			if (activityBean.getEntries().get(4).getType().equals("text")) {
				System.out.println("Entry with text");
				txtMeMediaThumbnail5.setText(activityBean.getEntries().get(4)
						.getData());
				txtMeMediaThumbnail5
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								popupEntryText(((TextView) v).getText()
										.toString());
							}
						});
			} else if (activityBean.getEntries().get(4).getType()
					.equals("image") || activityBean.getEntries().get(4).getType()
					.equals("checkin")) {
				txtMeMediaThumbnail5.setVisibility(View.INVISIBLE);
				imgMeMediaThumbnail5.setVisibility(View.VISIBLE);
				UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail5,
						Constant.SERVER_HOST + "large_"
								+ activityBean.getEntries().get(4).getData());
			}
			break;
		default:
			break;
		}
		CNSquareImageView ivAvatarThumbnail = (CNSquareImageView) rowView
				.findViewById(R.id.ivAvatarThumbnail);
		UniversalImageHelper.loadImage(
				mContext,
				ivAvatarThumbnail,
				Constant.SERVER_HOST + "thumbnail_"
						+ activityBean.getUserAvatarURL());
		TextView tvUsername = (TextView) rowView.findViewById(R.id.tvUsername);
		tvUsername.setText(activityBean.getUsername());
		tvUsername.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoProfile(activityBean.getUsername());
			}
		});
		TextView tvJourneyDateTime = (TextView) rowView
				.findViewById(R.id.tvJourneyDateTime);
		if(activityBean.getJourneyDateTime()!=null){
			tvJourneyDateTime.setText(DateTimeHelper.contextDateTime(DateTimeHelper
					.convertStringServerTimeToLocalDate(activityBean
							.getJourneyDateTime())));
		} else {
			tvJourneyDateTime.setText("N/A");
		}
		
		TextView tvStartPoint = (TextView) rowView
				.findViewById(R.id.tvStartPoint);
		tvStartPoint.setText(activityBean.getStartLocation());
		TextView tvEndPoint = (TextView) rowView.findViewById(R.id.tvEndPoint);
		tvEndPoint.setText(activityBean.getLastLocation());
		TextView tvJourneyContent = (TextView) rowView
				.findViewById(R.id.tvJourneyContent);
		tvJourneyContent.setText(activityBean.getJourneyName() + (activityBean.getNumOfTotalEntry() == 0 ? "" : " (" + activityBean.getNumOfTotalEntry() +" entries)"));
		tvJourneyContent.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				viewJourneyDetail(activityBean);
			}
		});
		ImageView ivJourneyLikeIcon = (ImageView) rowView
				.findViewById(R.id.ivJourneyLikeIcon);
		if (activityBean.isCurrentUserLiked()) {
			ivJourneyLikeIcon.setBackgroundResource(R.drawable.ic_like_orange);
		}
		ImageView btnJourneyViewMore = (ImageView) rowView
				.findViewById(R.id.btnJourneyViewMore);
		btnJourneyViewMore.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				viewJourneyDetail(activityBean);
			}
		});
		TextView tvJourneyLikeNumber = (TextView) rowView
				.findViewById(R.id.tvJourneyLikeNumber);
		tvJourneyLikeNumber.setText(activityBean.getNumberLiked());
		TextView tvJourneyCommentNumber = (TextView) rowView
				.findViewById(R.id.tvJourneyCommentNumber);
		tvJourneyCommentNumber.setVisibility(View.INVISIBLE);
		tvJourneyCommentNumber.setText(activityBean.getNumberComment());

		return rowView;
	}
	
	public void viewJourneyDetail(OutsideActivityBean activityBean){
		Fragment fragment = new ViewOnlineJourneyDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, activityBean.getJourneyName());
		bundle.putString(CreateJourneyFragment.JOURNEY_NAME, activityBean.getJourneyName());
		bundle.putString(CreateJourneyFragment.JOURNEY_ID, activityBean.getJourneyID());
		fragment.setArguments(bundle);
		MainActivity activity = (MainActivity) mActivity;
		activity.replaceFragment(fragment);
	}

	private void gotoProfile(String username) {
		Fragment fragment = new UserProfileFragment();
		MainActivity mainActivity = (MainActivity) mActivity;
		Bundle bundle = new Bundle();
		bundle.putString("username", username);
		System.out
				.println("Insert: username - " + bundle.getString("username"));
		bundle.putString("previousFragment", "SearchFriend");
		fragment.setArguments(bundle);
		mainActivity.saveData(bundle);
		mainActivity.getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		mainActivity.setTitle("User Profile");
	}

	private void popupEntryText(String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setMessage(text);
		builder.create().show();
	}

}
