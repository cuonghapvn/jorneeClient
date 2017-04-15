/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: JourneyListAdapter.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.journey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.bean.Entry;
import com.fpt.edu.bean.Journey;
import com.fpt.edu.bean.OutsideSearchFriendResult;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.customview.CNSquareImageView;
import com.fpt.edu.jornee.entry.FullScreenViewActivity;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.sqllite.DatabaseHandler;
import com.fpt.edu.jornee.sync.SyncJourney;
import com.fpt.edu.jornee.sync.SyncReceiver;
import com.fpt.edu.jornee.utils.ConnectionDetector;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.DateTimeHelper;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

public class JourneyListAdapter extends ArrayAdapter<Journey> {
	ArrayList<Journey> listJourneies;
	Context mContext;
	int resId;
	Activity mActivity;
	HashMap<String, String> user;
	SessionManager sessionManager;
	static DatabaseHandler databaseHandler;
	boolean isInternetAvailable = false;
	private ConnectionDetector connectionDetector;

	public JourneyListAdapter(Context context, int textViewResourceId,
			ArrayList<Journey> objects, Activity activity) {
		super(context, textViewResourceId, objects);
		listJourneies = objects;
		mContext = context;
		mActivity = activity;
		sessionManager = new SessionManager(mContext);
		user = new HashMap<String, String>();
		user = sessionManager.getUserDetails();
		databaseHandler = new DatabaseHandler(mContext);
		connectionDetector = new ConnectionDetector(context);
	}

	@Override
	public int getCount() {
		return listJourneies.size();
	}

	@Override
	public Journey getItem(int position) {
		return listJourneies.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listJourneies.get(position).hashCode();
	}

	public boolean isInternetAvailable() {
		return isInternetAvailable;
	}

	public void setInternetAvailable(boolean isInternetAvailable) {
		this.isInternetAvailable = isInternetAvailable;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final Journey currentItem = listJourneies.get(position);
		ArrayList<Entry> entries = getEntryOfJourney(currentItem);
		if (entries.size() > 0) {
			Collections.sort(entries);
		}
		View rowView = inflater.inflate(
				R.layout.fragment_journey_home_list_layout_row, parent, false);
		RelativeLayout front = (RelativeLayout) rowView
				.findViewById(R.id.front);
		front.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		TextView tvJourneyName = (TextView) rowView
				.findViewById(R.id.tvJourneyName);
		TextView tvJourneyCreateDate = (TextView) rowView
				.findViewById(R.id.tvJourneyCreateDate);
		TextView btnNumberOfEntries = (TextView) rowView
				.findViewById(R.id.btnNumberOfEntries);
		Button btnDelete = (Button) rowView.findViewById(R.id.btnDelete);
		Button btnEdit = (Button) rowView.findViewById(R.id.btnEdit);
		Button btnShare = (Button) rowView.findViewById(R.id.btnShare);
		ImageButton btnSync = (ImageButton) rowView.findViewById(R.id.btnSync);
		switch (currentItem.getJourneyStatus()) {
		case Journey.JOURNEY_STATUS_LOCAL_ONLY:
			btnSync.setImageResource(R.drawable.ic_action_sync_icon);
			btnSync.setEnabled(true);
			break;
		case Journey.JOURNEY_STATUS_WAITING_FOR_SYNCHRONIZING:
			btnSync.setImageResource(R.drawable.ic_action_update_icon);
			btnSync.setEnabled(true);
			break;
		case Journey.JOURNEY_STATUS_UP_TO_DATE:
			if (isEntryInJourneyUpToDate(currentItem)) {
				btnSync.setImageResource(R.drawable.ic_action_ok_icon);
				btnSync.setEnabled(false);
			} else {
				btnSync.setImageResource(R.drawable.ic_action_update_icon);
				btnSync.setEnabled(true);
			}
			break;

		default:
			break;
		}
		btnSync.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				if (connectionDetector.isConnectingToInternet()) {
					if (!(currentItem.getJourneyStatus() == Journey.JOURNEY_STATUS_UP_TO_DATE)) {
						if (isEntryInJourneyUpToDate(currentItem)) {
							if (currentItem.getJourneyFunction() != null) {
								new SyncJourney(currentItem,
										(MainActivity) mActivity,
										(ImageButton) v, currentItem
												.getJourneyFunction())
										.execute();
							}
						} else {
							final Dialog dialog = new Dialog(mActivity);
							dialog.setContentView(R.layout.journey_sync_journey_dialog_layout);
							final RadioGroup rgSyncOption = (RadioGroup) dialog
									.findViewById(R.id.rgSyncOption);
							Button btnSave = (Button) dialog
									.findViewById(R.id.btnSubmit);
							Button btnCancel = (Button) dialog
									.findViewById(R.id.btnCancel);
							btnSave.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v1) {
									if (currentItem.getJourneyFunction() != null) {
										new SyncJourney(currentItem,
												(MainActivity) mActivity,
												(ImageButton) v, currentItem
														.getJourneyFunction())
												.execute();
									}
									if (rgSyncOption.getCheckedRadioButtonId() == R.id.rbWholeJourney) {
										Intent intent = new Intent(mContext,
												SyncReceiver.class);
										intent.putExtra("journeyID",
												currentItem.getJourneyID());
										mActivity.sendBroadcast(intent);
									}
									dialog.dismiss();
								}
							});
							btnCancel
									.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											dialog.dismiss();
										}
									});
							dialog.setTitle("Some entries are unsync");
							dialog.setCancelable(false);
							dialog.show();
						}
					} else {
						if (!isEntryInJourneyUpToDate(currentItem)) {
							final Dialog dialog = new Dialog(mActivity);
							dialog.setContentView(R.layout.journey_sync_journey_dialog_layout);
							final RadioGroup rgSyncOption = (RadioGroup) dialog
									.findViewById(R.id.rgSyncOption);
							RadioButton rbOnlyJourneyInfo = (RadioButton) dialog
									.findViewById(R.id.rbOnlyJourneyInfo);
							rbOnlyJourneyInfo.setVisibility(View.GONE);
							RadioButton rbWholeJourney = (RadioButton) dialog
									.findViewById(R.id.rbWholeJourney);
							rbWholeJourney
									.setText("All entries will be sync with server!");
							rbWholeJourney.setChecked(true);
							Button btnSave = (Button) dialog
									.findViewById(R.id.btnSubmit);
							Button btnCancel = (Button) dialog
									.findViewById(R.id.btnCancel);
							btnSave.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v1) {
									if (currentItem.getJourneyFunction() != null) {
										new SyncJourney(currentItem,
												(MainActivity) mActivity,
												(ImageButton) v, currentItem
														.getJourneyFunction())
												.execute();
									}
									Intent intent = new Intent(mContext,
											SyncReceiver.class);
									intent.putExtra("journeyID",
											currentItem.getJourneyID());
									mActivity.sendBroadcast(intent);
									dialog.dismiss();
								}
							});
							btnCancel
									.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											dialog.dismiss();
										}
									});
							dialog.setTitle("Some entries are unsync");
							dialog.setCancelable(false);
							dialog.show();
						}
					}
				} else {
					Toast.makeText(mContext, "No internet connection",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		if (!sessionManager.isLoggedIn()) {
			btnShare.setBackgroundResource(R.drawable.btn_gray_button);
			btnShare.setEnabled(false);
			btnSync.setVisibility(View.GONE);
		}
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
		btnNumberOfEntries.setText(entries.size() + " entries");
		tvJourneyName.setText(currentItem.getJourneyName());
		if (currentItem.getCreatedDate() != null) {
			tvJourneyCreateDate.setText(DateTimeHelper.contextDateTime(DateTimeHelper
							.convertStringServerTimeToLocalDate(currentItem
									.getCreatedDate())));
		}

		if (entries.size() == 0) {
			emptyJourneySpan.setVisibility(View.VISIBLE);
		} else {
			notEmptyJourneySpan.setVisibility(View.VISIBLE);
			if (entries.size() > 0) {
				if (entries.get(0).getType().equals("text")) {
					txtMeMediaThumbnail1.setVisibility(View.VISIBLE);
					txtMeMediaThumbnail1
							.setText(entries.get(0).getText() == null ? "Empty"
									: entries.get(0).getText());
					txtMeMediaThumbnail1
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									gotoDetailsWithId(currentItem, 0);
								}
							});
				} else if (entries.get(0).getType().equals("image")
						|| entries.get(0).getType().equals("checkin")) {
					imgMeMediaThumbnail1.setVisibility(View.VISIBLE);
					UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail1,
							entries.get(0).getPath());
					imgMeMediaThumbnail1
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									gotoDetailsWithId(currentItem, 0);
								}
							});
				}
			}
			if (entries.size() > 1) {
				if (entries.get(1).getType().equals("text")) {
					txtMeMediaThumbnail2.setVisibility(View.VISIBLE);
					txtMeMediaThumbnail2
							.setText(entries.get(1).getText() == null ? "Empty"
									: entries.get(1).getText());
					txtMeMediaThumbnail2
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									gotoDetailsWithId(currentItem, 1);
								}
							});
				} else if (entries.get(1).getType().equals("image")
						|| entries.get(1).getType().equals("checkin")) {
					imgMeMediaThumbnail2.setVisibility(View.VISIBLE);
					UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail2,
							entries.get(1).getPath());
					imgMeMediaThumbnail2
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									gotoDetailsWithId(currentItem, 1);
								}
							});
				}
			}
			if (entries.size() > 2) {
				if (entries.get(2).getType().equals("text")) {
					txtMeMediaThumbnail3.setVisibility(View.VISIBLE);
					txtMeMediaThumbnail3
							.setText(entries.get(2).getText() == null ? "Empty"
									: entries.get(2).getText());
					txtMeMediaThumbnail3
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									gotoDetailsWithId(currentItem, 2);
								}
							});
				} else if (entries.get(2).getType().equals("image")
						|| entries.get(2).getType().equals("checkin")) {
					imgMeMediaThumbnail3.setVisibility(View.VISIBLE);
					UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail3,
							entries.get(2).getPath());
					imgMeMediaThumbnail3
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									gotoDetailsWithId(currentItem, 2);
								}
							});
				}
			}
			if (entries.size() > 3) {
				if (entries.get(3).getType().equals("text")) {
					txtMeMediaThumbnail4.setVisibility(View.VISIBLE);
					txtMeMediaThumbnail4
							.setText(entries.get(3).getText() == null ? "Empty"
									: entries.get(3).getText());
					txtMeMediaThumbnail4
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									gotoDetailsWithId(currentItem, 3);
								}
							});
				} else if (entries.get(3).getType().equals("image")
						|| entries.get(3).getType().equals("checkin")) {
					imgMeMediaThumbnail4.setVisibility(View.VISIBLE);
					UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail4,
							entries.get(3).getPath());
					imgMeMediaThumbnail4
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									gotoDetailsWithId(currentItem, 3);
								}
							});
				}
			}
			if (entries.size() > 4) {
				if (entries.get(4).getType().equals("text")) {
					txtMeMediaThumbnail5.setVisibility(View.VISIBLE);
					txtMeMediaThumbnail5
							.setText(entries.get(0).getText() == null ? "Empty"
									: entries.get(4).getText());
					txtMeMediaThumbnail5
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									gotoDetailsWithId(currentItem, 4);
								}
							});
				} else if (entries.get(4).getType().equals("image")
						|| entries.get(4).getType().equals("checkin")) {
					imgMeMediaThumbnail5.setVisibility(View.VISIBLE);
					UniversalImageHelper.loadImage(mContext, imgMeMediaThumbnail5,
							entries.get(4).getPath());
					imgMeMediaThumbnail5
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									gotoDetailsWithId(currentItem, 4);
								}
							});
				}
			}
		}

		btnDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				removeItem(position);
			}
		});
		btnEdit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity activity = (MainActivity) mActivity;
				Fragment fragment = new CreateJourneyFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Home");
				bundle.putString(CreateJourneyFragment.JOURNEY_NAME,
						currentItem.getJourneyName());
				bundle.putString(CreateJourneyFragment.JOURNEY_ID,
						currentItem.getJourneyID());
				bundle.putString(CreateJourneyFragment.JOURNEY_ACTION, "update");
				fragment.setArguments(bundle);
				activity.replaceFragment(fragment);
			}
		});
		btnShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				shareJourney(position);
			}
		});
		return rowView;
	}
	
	public void viewImageFullscreen(String path){
		if (path != null && !path.isEmpty()) {
			Intent fullsreenView = new Intent(
					mActivity,
					FullScreenViewActivity.class);
			fullsreenView.putExtra("path", path);
			mActivity.startActivity(fullsreenView);
		}
	}

	public void gotoDetailsWithId(Journey journey, int position) {
		Fragment fragment = new ViewJourneyDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,
				journey.getJourneyName());
		bundle.putString(CreateJourneyFragment.JOURNEY_NAME,
				journey.getJourneyName());
		bundle.putString(CreateJourneyFragment.JOURNEY_ID,
				journey.getJourneyID());
		bundle.putInt("startPosition", position);
		fragment.setArguments(bundle);
		((MainActivity) mActivity).replaceFragment(fragment);
	}

	public ArrayList<Entry> getEntryOfJourney(Journey journey) {
		ArrayList<Entry> result = new ArrayList<Entry>();
		StringTokenizer tokenizer = new StringTokenizer(journey.getEntriesID(),
				",");
		Entry fromDatabase;
		while (tokenizer.hasMoreElements()) {
			String id = (String) tokenizer.nextElement();
			fromDatabase = new Entry();
			fromDatabase = databaseHandler.getEntryInfor(id);
			if (fromDatabase != null) {
//				if (fromDatabase.getPath() != null) {
//					UrlImageViewHelper.loadUrlDrawable(mContext,
//							fromDatabase.getPath());
//				}
				result.add(fromDatabase);
			}
		}
		return result;
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
								if (listJourneies.get(position)
										.getJourneyStatus() != Journey.JOURNEY_STATUS_LOCAL_ONLY) {
									new SyncJourney(
											listJourneies.get(position),
											(MainActivity) mActivity, null,
											Journey.JOURNEY_FUNCTION_DELETE)
											.execute();
								}
								databaseHandler.deleteJourney(listJourneies
										.get(position));
								listJourneies.remove(position);
								refreshView();
								notifyDataSetChanged();
							}

						}).setNegativeButton("No", null).show();

	}

	public static boolean isEntryInJourneyUpToDate(Journey journey) {
		boolean result = true;
		StringTokenizer tokenizer = new StringTokenizer(journey.getEntriesID(),
				",");
		Entry fromDatabase;
		while (tokenizer.hasMoreElements()) {
			String id = (String) tokenizer.nextElement();
			fromDatabase = new Entry();
			fromDatabase = databaseHandler.getEntryInfor(id);
			if (fromDatabase != null) {
				if (!(fromDatabase.getEntrySyncStatus() == Entry.ENTRY_STATUS_UP_TO_DATE)) {
					result = false;
				}
			}
		}
		return result;

	}

	public void shareJourney(final int position) {
		Journey journey = listJourneies.get(position);
		final String preEditShared = journey.getShared() == null ? "" : journey
				.getShared();
		final Dialog dialog = new Dialog(mActivity);
		ArrayList<OutsideSearchFriendResult> friendResults = ((MainActivity) mActivity).getListFollowers();
		Collections.sort(friendResults);
		ArrayList<String> listFriend = new ArrayList<String>();
		for (OutsideSearchFriendResult item : friendResults) {
			listFriend.add(item.getUsername());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
				android.R.layout.simple_list_item_1, listFriend);
		dialog.setContentView(R.layout.journey_share_journey_dialog_layout);
		final RadioGroup rgShareOption = (RadioGroup) dialog
				.findViewById(R.id.rgShareOption);
		RadioButton rbSpecificPeople = (RadioButton) dialog
				.findViewById(R.id.rbSpecificPeople);
		final MultiAutoCompleteTextView mactvSharePeople = (MultiAutoCompleteTextView) dialog
				.findViewById(R.id.mactvSharePeople);
		RadioButton rbOnlyMe = (RadioButton) dialog.findViewById(R.id.rbOnlyMe);
		RadioButton rbPublic = (RadioButton) dialog.findViewById(R.id.rbPublic);
		if (journey.getShared() != null) {
			if ("_private".equals(journey.getShared())) {
				rbOnlyMe.setChecked(true);
			} else if ("_public".equals(journey.getShared())) {
				rbPublic.setChecked(true);
			} else {
				rbSpecificPeople.setChecked(true);
				mactvSharePeople.setEnabled(true);
				mactvSharePeople.setText(journey.getShared());
			}
		}

		mactvSharePeople.setAdapter(adapter);
		mactvSharePeople
				.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		rbSpecificPeople
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							mactvSharePeople.setEnabled(true);
						} else {
							mactvSharePeople.setEnabled(false);
						}

					}
				});

		Button btnSave = (Button) dialog.findViewById(R.id.btnSubmit);
		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Journey journey = listJourneies.get(position);
				boolean status = true;
				switch (rgShareOption.getCheckedRadioButtonId()) {
				case R.id.rbOnlyMe:
					journey.setShared("_private");
					break;

				case R.id.rbPublic:
					journey.setShared("_public");
					break;

				case R.id.rbSpecificPeople:
					if (mactvSharePeople.getText().toString().trim().length() > 0) {
						journey.setShared(validShareString(mactvSharePeople
								.getText().toString().replaceAll("\\s+", "")));
					} else {
						mactvSharePeople.requestFocus();
						mactvSharePeople
								.setHint("You cannot leave this field empty!");
						status = false;
					}
					break;

				default:
					break;
				}
				if (status) {
					if (!preEditShared.equals(journey.getShared())) {
						Journey fromdDatabase = databaseHandler
								.getJourneyInfor(journey.getJourneyID());
						fromdDatabase.setModifiedDate(DateTimeHelper
								.convertLocalDateToServerString(new Date()));
						fromdDatabase.setShared(journey.getShared());
						databaseHandler.updateJourney(fromdDatabase);
					}
					refreshView();
					dialog.dismiss();
				}
			}
		});
		dialog.setTitle("Share this journey to:");
		dialog.setCancelable(false);
		dialog.show();
	}

	public void refreshView() {
		if (sessionManager.isLoggedIn()) {
			listJourneies = HomeListJourneyFragment.getJourneyOfUser(user
					.get(SessionManager.KEY_USERNAME));
		} else {
			listJourneies = HomeListJourneyFragment.getJourneyOfUser(null);
		}
		Collections.sort(listJourneies);
		this.notifyDataSetChanged();
	}

	public String validShareString(String source) {
		StringTokenizer stringTokenizer = new StringTokenizer(source, ",");
		ArrayList<String> temp = new ArrayList<String>();
		while (stringTokenizer.hasMoreElements()) {
			String token = stringTokenizer.nextElement().toString();
			if (!isInList(temp, token)) {
				temp.add(token.trim());
			}
		}
		return arrayToString(temp);
	}

	public boolean isInList(ArrayList<String> list, String string) {
		boolean found = false;
		for (String string2 : list) {
			if (string.equals(string2)) {
				found = true;
			}
		}
		return found;
	}

	public String arrayToString(ArrayList<String> list) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			builder.append(list.get(i));
			if (i < (list.size() - 1)) {
				builder.append(",");
			}
		}
		return builder.toString();
	}

}
