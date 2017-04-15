/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: ViewJourneyDetailFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.journey;

import static com.fpt.edu.jornee.utils.Constant.SERVER_HOST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.bean.Entry;
import com.fpt.edu.bean.Journey;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.customview.ActionItem;
import com.fpt.edu.jornee.customview.AnimationViewPager;
import com.fpt.edu.jornee.customview.AnimationViewPager.TransitionEffect;
import com.fpt.edu.jornee.customview.CNSquareImageView;
import com.fpt.edu.jornee.customview.OutlineContainer;
import com.fpt.edu.jornee.customview.QuickAction;
import com.fpt.edu.jornee.entry.EditEntryActivity;
import com.fpt.edu.jornee.entry.FullScreenViewActivity;
import com.fpt.edu.jornee.entry.ViewDetailEntryActivity;
import com.fpt.edu.jornee.imagehelpers.UrlImageViewHelper;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.sqllite.DatabaseHandler;
import com.fpt.edu.jornee.sync.SyncEntry;
import com.fpt.edu.jornee.utils.ConnectionDetector;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.DateTimeHelper;
import com.fpt.edu.jornee.utils.JSONParser;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

public class ViewJourneyDetailFragment extends Fragment {
	public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
	public final static String JOURNEY_ID = "journeyID";
	private AnimationViewPager vpListEntry;
	DatabaseHandler databaseHandler;
	SessionManager sessionManager;
	ArrayList<Entry> listEntries;
	ImageButton btnNext, btnPrevious, btnSlide;
	MainAdapter mainAdapter;
	TextView tvCurrent;
	private ConnectionDetector connectionDetector;
	TextView tvNull;

	String journeyID;
	int startPosition = -1;
	boolean isPlaying = false;
	boolean standLast = false;
	View rootView;

	public ViewJourneyDetailFragment() {

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_journey_view_details,
				container, false);
		Bundle bundle = getArguments();
		listEntries = new ArrayList<Entry>();
		databaseHandler = new DatabaseHandler(getActivity()
				.getApplicationContext());
		sessionManager = new SessionManager(getActivity()
				.getApplicationContext());
		connectionDetector = new ConnectionDetector(getActivity()
				.getApplicationContext());
		if (bundle != null) {
			if (bundle.containsKey(JOURNEY_ID)) {
				journeyID = bundle.getString(JOURNEY_ID);
				System.out.println("Journey ID: " + journeyID);
				if (journeyID != null) {
					Journey journey = databaseHandler
							.getJourneyInfor(journeyID);
					if (journey != null) {
						listEntries = getEntryOfJourney(journey);
						getActivity().setTitle(journey.getJourneyName());
						Collections.sort(listEntries);
					}
				}
			}
			if (bundle.containsKey("startPosition")) {
				startPosition = bundle.getInt("startPosition");
			}
		}
		setupAnimation(TransitionEffect.Standard, rootView);
		btnPrevious = (ImageButton) rootView.findViewById(R.id.btnPrevious);
		tvNull = (TextView) rootView.findViewById(R.id.tvNull);
		btnPrevious.setEnabled(false);
		btnNext = (ImageButton) rootView.findViewById(R.id.btnNext);
		tvCurrent = (TextView) rootView.findViewById(R.id.textView1);
		mainAdapter = new MainAdapter();
		if (listEntries.size() == 0) {
			vpListEntry.setVisibility(View.INVISIBLE);
			tvNull.setText("Empty Journey");
			tvCurrent.setVisibility(View.INVISIBLE);
			btnNext.setVisibility(View.INVISIBLE);
			btnPrevious.setVisibility(View.INVISIBLE);
		}
		tvCurrent.setText("1/" + mainAdapter.getCount());
		vpListEntry
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						tvCurrent.setText((position + 1) + "/"
								+ mainAdapter.getCount());
						if (vpListEntry.getCurrentItem() == (mainAdapter
								.getCount() - 1)) {
							btnNext.setImageResource(R.drawable.ic_action_replay);
							standLast = true;
						} else if (vpListEntry.getCurrentItem() == 0) {
							btnPrevious.setEnabled(false);
							btnNext.setImageResource(R.drawable.ic_action_next);
						} else {
							btnPrevious.setEnabled(true);
							btnNext.setImageResource(R.drawable.ic_action_next);
						}
					}
				});
		vpListEntry.setOffscreenPageLimit(0);
		if (startPosition != -1) {
			vpListEntry.setCurrentItem(startPosition);
		}
		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (standLast) {
					vpListEntry.setCurrentItem(0);
					standLast = false;
					btnNext.setImageResource(R.drawable.ic_action_next);
				} else {
					vpListEntry.setCurrentItem(vpListEntry.getCurrentItem() + 1);
				}
			}
		});

		btnPrevious.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				vpListEntry.setCurrentItem(vpListEntry.getCurrentItem() - 1);
			}
		});
		setHasOptionsMenu(true);
		return rootView;

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
				result.add(fromDatabase);
			}
		}
		return result;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add("Toggle Fade");
		String[] effects = this.getResources().getStringArray(
				R.array.jazzy_effects);
		for (String effect : effects)
			menu.add(effect);
		inflater.inflate(R.menu.journey_details, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString().equals("Toggle Fade")) {
			vpListEntry.setFadeEnabled(!vpListEntry.getFadeEnabled());
		} else {
			if(item.getTitle().toString().equals("mapView")){
				Fragment fragment = new ViewJourneyOnMapFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Home");
				bundle.putString(CreateJourneyFragment.JOURNEY_ID, journeyID);
				fragment.setArguments(bundle);
				MainActivity activity = (MainActivity) getActivity();
				activity.replaceFragmentAndDeleteExisting(fragment);
			} else {
				TransitionEffect effect = TransitionEffect.valueOf(item.getTitle()
						.toString());
				setupAnimation(effect, vpListEntry.getCurrentItem(), rootView);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private void setupAnimation(TransitionEffect effect, View rootView) {
		vpListEntry = (AnimationViewPager) rootView
				.findViewById(R.id.jazzy_pager);
		vpListEntry.setFadeEnabled(true);
		vpListEntry.setTransitionEffect(effect);
		vpListEntry.setAdapter(new MainAdapter());
		vpListEntry.setPageMargin(30);
	}

	private void setupAnimation(TransitionEffect effect, int currentItem,
			View rootView) {
		vpListEntry = (AnimationViewPager) rootView
				.findViewById(R.id.jazzy_pager);
		vpListEntry.setTransitionEffect(effect);
		vpListEntry.setAdapter(new MainAdapter());
		vpListEntry.setCurrentItem(currentItem);
		vpListEntry.setPageMargin(30);
	}

	private class MainAdapter extends PagerAdapter {

		public static final int ACTION_REMOVE_FROM_JOURNEY = 1;
		public static final int ACTION_DELETE_ENTRY = 2;

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			final Entry currentItem = listEntries.get(position);
			ViewGroup itemView = (ViewGroup) getActivity().getLayoutInflater()
					.inflate(R.layout.fragment_journey_view_details_item,
							container, false);
			TextView tvDateTime = (TextView) itemView
					.findViewById(R.id.tvDateTime);
			TextView tvLocation = (TextView) itemView
					.findViewById(R.id.tvLocation);
			FrameLayout frameImage = (FrameLayout) itemView
					.findViewById(R.id.frameImage);
			CNSquareImageView ivEntryImage = (CNSquareImageView) itemView
					.findViewById(R.id.ivEntryImage);
			TextView tvDescriptionWithImage = (TextView) itemView
					.findViewById(R.id.tvDescriptionWithImage);
			FrameLayout frameText = (FrameLayout) itemView
					.findViewById(R.id.frameText);
			TextView tvDescription = (TextView) itemView
					.findViewById(R.id.tvDescription);
			ImageButton btnEdit = (ImageButton) itemView
					.findViewById(R.id.btnEdit);
			btnEdit.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent editEntryIntent = new Intent(getActivity(),
							EditEntryActivity.class);
					editEntryIntent.putExtra("action", "edit");
					editEntryIntent.putExtra("selectedEntry", currentItem);
					startActivityForResult(editEntryIntent,
							ViewDetailEntryActivity.ACTION_EDIT_ENTRY);
				}
			});
			ImageButton btnDelete = (ImageButton) itemView
					.findViewById(R.id.btnDelete);
			ImageButton btnShare = (ImageButton) itemView
					.findViewById(R.id.btnShare);
			final QuickAction quickAction = new QuickAction(getActivity()
					.getApplicationContext(), QuickAction.VERTICAL);
			ActionItem menuShareInterestingPlace = new ActionItem(
					ACTION_REMOVE_FROM_JOURNEY, "Remove from journey",
					getResources().getDrawable(R.drawable.ic_action_remove));
			menuShareInterestingPlace.setSticky(true);
			quickAction.addActionItem(menuShareInterestingPlace);
			ActionItem menuShareToFacebook = new ActionItem(
					ACTION_DELETE_ENTRY, "Delete this entry", getResources()
							.getDrawable(R.drawable.ic_action_delete));
			menuShareToFacebook.setSticky(true);
			quickAction.addActionItem(menuShareToFacebook);
			quickAction
					.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
						@Override
						public void onItemClick(QuickAction source, int pos,
								int actionId) {
							source.dismiss();
							if (actionId == ACTION_REMOVE_FROM_JOURNEY) {
								Toast.makeText(
										getActivity().getApplicationContext(),
										"Remove from journey", Toast.LENGTH_SHORT)
										.show();
								
							} else if (actionId == ACTION_DELETE_ENTRY) {
								Toast.makeText(
										getActivity().getApplicationContext(),
										"Delete this entry", Toast.LENGTH_SHORT)
										.show();
							}
						}
					});
			btnDelete.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					quickAction.show(v);
				}
			});
			btnShare.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (currentItem.getEntrySyncStatus() == Entry.ENTRY_STATUS_LOCAL_ONLY) {
						Toast.makeText(
								getActivity()
										.getApplicationContext(),
								"Entry must be uploaded to server before it can be shared!",
								Toast.LENGTH_SHORT).show();
					} else if (currentItem.getAddress() == null
							|| currentItem.getAddress().isEmpty()) {
						Toast.makeText(
								getActivity()
										.getApplicationContext(),
								"Entry must have address information before it can be shared!",
								Toast.LENGTH_SHORT).show();

					} else {
						shareInterestingPlace(currentItem);
					}
				}
			});

			ImageButton btnSync = (ImageButton) itemView
					.findViewById(R.id.btnSync);
			TableRow rlStartPoint = (TableRow) itemView
					.findViewById(R.id.rlStartPoint);
			TableRow rlVehicle = (TableRow) itemView
					.findViewById(R.id.rlVehicle);
			TextView tvStartPoint = (TextView) itemView
					.findViewById(R.id.tvStartPoint);
			TextView tvVehicle = (TextView) itemView
					.findViewById(R.id.tvVehicle);

			switch (currentItem.getEntrySyncStatus()) {
			case Entry.ENTRY_STATUS_LOCAL_ONLY:
				btnSync.setImageResource(R.drawable.ic_sync_local);
				btnSync.setBackgroundResource(R.drawable.btn_blue_button_selector);
				btnSync.setEnabled(true);
				break;
			case Entry.ENTRY_STATUS_WAITING_FOR_SYNCHRONIZING:
				btnSync.setImageResource(R.drawable.ic_sync_sync);
				btnSync.setBackgroundResource(R.drawable.btn_blue_button_selector);
				btnSync.setEnabled(true);
				break;
			case Entry.ENTRY_STATUS_UP_TO_DATE:
				btnSync.setImageResource(R.drawable.ic_sync_uptodate);
				btnSync.setBackgroundResource(R.drawable.btn_green_button_selector);
				btnSync.setEnabled(false);
				break;

			default:
				break;
			}

			btnSync.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					if (connectionDetector.isConnectingToInternet()) {
						Entry entry = databaseHandler.getEntryInfor(currentItem
								.getEntryID());
						if (entry.getEntrySyncStatus() != Entry.ENTRY_STATUS_UP_TO_DATE) {
							new SyncEntry(
									getActivity().getApplicationContext(),
									entry).execute();
						}
					} else {
						Toast.makeText(getActivity().getApplicationContext(),
								"No internet connection", Toast.LENGTH_SHORT)
								.show();
					}
				}
			});

			if (!sessionManager.isLoggedIn()) {
				btnSync.setVisibility(View.GONE);
			}
			if (currentItem.getType() != null) {
				if (currentItem.getType().equals("image")) {
					frameImage.setVisibility(View.VISIBLE);
					if (currentItem.getPath() != null) {
						ivEntryImage.setVisibility(View.VISIBLE);
						UniversalImageHelper.loadImage(getActivity()
								.getApplicationContext(), ivEntryImage,
								currentItem.getPath());
						ivEntryImage.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								viewImageFullscreen(currentItem.getPath());
							}
						});
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
						UniversalImageHelper.loadImage(getActivity()
								.getApplicationContext(), ivEntryImage,
								currentItem.getPath());
					}
					if (currentItem.getText() != null
							&& currentItem.getText().length() > 0) {
						tvDescriptionWithImage.setVisibility(View.VISIBLE);
						tvDescriptionWithImage.setText(currentItem.getText());
					}
					if (currentItem.getStartPoint() != null
							&& currentItem.getStartPoint().trim().length() > 0) {
						rlStartPoint.setVisibility(View.VISIBLE);
						tvStartPoint.setText(currentItem.getStartPoint());
					}
					if (currentItem.getVehicle() != null
							&& currentItem.getVehicle().trim().length() > 0) {
						rlVehicle.setVisibility(View.VISIBLE);
						tvVehicle.setText(currentItem.getVehicle());
						System.out.println("Vehicle: "
								+ currentItem.getVehicle());
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

			if (currentItem.getDateTime() != null) {
				tvDateTime.setText(DateTimeHelper
						.contextDateTime(DateTimeHelper
								.convertStringServerTimeToLocalDate(currentItem
										.getDateTime())));
			} else {
				tvDateTime.setText("N/A");
			}
			if (currentItem.getPlaceName() != null
					&& currentItem.getPlaceName().trim().length() > 0) {
				tvLocation.setText(currentItem.getPlaceName());
			} else if (currentItem.getAddress() != null
					&& currentItem.getAddress().trim().length() > 0) {
				tvLocation.setText(currentItem.getAddress());
			} else {
				tvLocation.setText("N/A");
			}
			container.addView(itemView, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			vpListEntry.setObjectForPosition(itemView, position);
			return itemView;
		}

		public void viewImageFullscreen(String path) {
			if (path != null && !path.isEmpty()) {
				Intent fullsreenView = new Intent(getActivity(),
						FullScreenViewActivity.class);
				fullsreenView.putExtra("path", path);
				getActivity().startActivity(fullsreenView);
			}
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object obj) {
			container.removeView(vpListEntry.findViewFromObject(position));
		}

		@Override
		public int getCount() {
			return listEntries.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			if (view instanceof OutlineContainer) {
				return ((OutlineContainer) view).getChildAt(0) == obj;
			} else {
				return view == obj;
			}
		}
	}

	public void shareInterestingPlace(final Entry entry) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View dialog_layout = inflater.inflate(
				R.layout.journey_share_interesting_place_dialog_layout,
				(ViewGroup) getActivity().findViewById(R.id.rootLayout));
		final EditText tvIplaceName = (EditText) dialog_layout
				.findViewById(R.id.tvIplaceName);
		final Spinner snCategory = (Spinner) dialog_layout
				.findViewById(R.id.snCategory);
		AlertDialog.Builder db = new AlertDialog.Builder(getActivity());
		db.setView(dialog_layout);
		db.setTitle("Share this place...");
		db.setCancelable(false);
		db.setPositiveButton("Share", null);
		db.setNegativeButton("Cancel", null);
		final AlertDialog dialog = db.show();
		Button shareButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		Button cancelButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		shareButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String category = String.valueOf(snCategory.getSelectedItem())
						.toLowerCase();
				String iplace_name = tvIplaceName.getText().toString();
				if (iplace_name.trim().length() == 0) {
					Toast.makeText(getActivity().getApplicationContext(),
							"Name the place before you share it!",
							Toast.LENGTH_SHORT).show();
					tvIplaceName.requestFocus();
				} else if (snCategory.getSelectedItem() == null) {
					Toast.makeText(getActivity().getApplicationContext(),
							"Select category for the place!",
							Toast.LENGTH_SHORT).show();
					snCategory.requestFocus();
				} else {
					new ShareInterestingPlaceAsync(entry, iplace_name,
							category, sessionManager.getUserDetails())
							.execute();
					dialog.dismiss();
				}
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	class ShareInterestingPlaceAsync extends
			AsyncTask<Object, Void, JSONObject> {
		JSONObject jsonObject = null;
		HashMap<String, String> user;
		Entry entry;
		String iplaceName;
		String category;

		public ShareInterestingPlaceAsync(Entry entry, String iplaceName,
				String category, HashMap<String, String> user) {
			this.entry = entry;
			this.iplaceName = iplaceName;
			this.category = category;
			this.user = user;
		}

		@Override
		protected JSONObject doInBackground(Object... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("token", user
						.get(SessionManager.KEY_TOKEN)));
				nameValuePairs.add(new BasicNameValuePair("iplace_name",
						iplaceName));
				nameValuePairs
						.add(new BasicNameValuePair("category", category));
				nameValuePairs.add(new BasicNameValuePair("entry_id", entry
						.getServerEntryID()));

				JSONParser jsonParser = new JSONParser(getActivity()
						.getApplicationContext());
				jsonObject = jsonParser.makeHttpRequest(SERVER_HOST
						+ "share_iplace", "POST", nameValuePairs);

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return jsonObject;
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				if (result != null) {
					String authen_status = result.getString("authen_status");
					String status = null;
					if (result.has("status")) {
						status = result.getString("status");
					}
					if ("error".equals(authen_status)) {
						Toast.makeText(getActivity().getApplicationContext(),
								"Error when we're trying to recognize you!",
								Toast.LENGTH_SHORT).show();
					} else if ("fail".equals(authen_status)) {
						Toast.makeText(getActivity().getApplicationContext(),
								"You must login first to use this function!",
								Toast.LENGTH_SHORT).show();
					} else if ("ok".equals(authen_status)) {
						if ("error".equals(status)) {
							Toast.makeText(
									getActivity().getApplicationContext(),
									"We cannot complete your request!",
									Toast.LENGTH_SHORT).show();
						} else if ("ok".equals(status)) {
							Toast.makeText(
									getActivity().getApplicationContext(),
									"Share place successfully!",
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Toast.makeText(
							getActivity().getApplicationContext(),
							"Error occur due to server's failures! Please try later!",
							Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
