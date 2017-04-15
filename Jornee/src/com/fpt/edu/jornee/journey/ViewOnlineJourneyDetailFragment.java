/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: ViewOnlineJourneyDetailFragment.java
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.bean.Entry;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.customview.AnimationViewPager;
import com.fpt.edu.jornee.customview.AnimationViewPager.TransitionEffect;
import com.fpt.edu.jornee.customview.CNSquareImageView;
import com.fpt.edu.jornee.customview.OutlineContainer;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.sqllite.DatabaseHandler;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.DateTimeHelper;
import com.fpt.edu.jornee.utils.JSONParser;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

public class ViewOnlineJourneyDetailFragment extends Fragment {
	public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
	public final static String JOURNEY_ID = "journeyID";
	private AnimationViewPager vpListEntry;
	DatabaseHandler databaseHandler;
	ArrayList<Entry> listEntries;
	ImageButton btnNext, btnPrevious, btnSlide;
	MainAdapter mainAdapter;
	TextView tvCurrent;
	TextView tvNull;
	JSONArray arrayEntriesResult = null;
	String authen_status = null;
	String status = null;
	SessionManager sessionManager;
	HashMap<String, String> user;
	ProgressDialog progressDialog;

	String journeyID;
	int startPosition = -1;
	boolean isPlaying = false;
	boolean standLast = false;
	View rootView;

	public ViewOnlineJourneyDetailFragment() {

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_journey_view_details,
				container, false);
		Bundle bundle = getArguments();
		sessionManager = new SessionManager(getActivity()
				.getApplicationContext());
		user = new HashMap<String, String>();
		user = sessionManager.getUserDetails();
		listEntries = new ArrayList<Entry>();
		databaseHandler = new DatabaseHandler(getActivity()
				.getApplicationContext());
		if (bundle != null) {
			if (bundle.containsKey(JOURNEY_ID)) {
				journeyID = bundle.getString(JOURNEY_ID);
				if (journeyID != null) {
					new LoadJourneysAsync().execute();
				}
			}
		}
		setupAnimation(TransitionEffect.Standard, rootView);
		btnPrevious = (ImageButton) rootView.findViewById(R.id.btnPrevious);
		tvNull = (TextView) rootView.findViewById(R.id.tvNull);
		btnPrevious.setEnabled(false);
		btnNext = (ImageButton) rootView.findViewById(R.id.btnNext);
		tvCurrent = (TextView) rootView.findViewById(R.id.textView1);
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
		vpListEntry.setOffscreenPageLimit(3);
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

	private class LoadJourneysAsync extends AsyncTask<String, Void, JSONObject> {
		JSONObject jsonObject = null;

		@Override
		protected JSONObject doInBackground(String... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("token", user
						.get(SessionManager.KEY_TOKEN)));
				nameValuePairs.add(new BasicNameValuePair("server_id",
						journeyID));
				JSONParser jsonParser = new JSONParser(getActivity()
						.getApplicationContext());
				jsonObject = jsonParser.makeHttpRequest(SERVER_HOST
						+ "view_detail_journey", "POST", nameValuePairs);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return jsonObject;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(getActivity(), "", "Loading ",
					true);
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				if (result != null) {
					authen_status = result.getString("authen_status");
					if (result.has("status")) {
						status = result.getString("status");
					}
					if (result.has("entries")) {
						arrayEntriesResult = result.getJSONArray("entries");
					}
					if (result.has("journey_name")) {
						getActivity()
								.setTitle(result.getString("journey_name"));
					} else {
						getActivity()
						.setTitle(result.getString("Unknown journey!"));
					}
					if ("error".equals(authen_status)) {
					} else if ("fail".equals(authen_status)) {
					} else if ("ok".equals(authen_status)) {
						if ("error".equals(status)) {
						} else if ("ok".equals(status)) {
							listEntries = jsonToListEntry(arrayEntriesResult);

							if (listEntries.size() > 0) {
								Collections.sort(listEntries);
								mainAdapter = new MainAdapter();
								tvCurrent
										.setText("1/" + mainAdapter.getCount());
								vpListEntry = (AnimationViewPager) rootView
										.findViewById(R.id.jazzy_pager);
								vpListEntry
										.setTransitionEffect(TransitionEffect.Standard);
								vpListEntry.setAdapter(mainAdapter);
								vpListEntry.setPageMargin(30);
							} else {
								vpListEntry.setVisibility(View.INVISIBLE);
								tvNull.setText("Empty Journey");
								tvCurrent.setVisibility(View.INVISIBLE);
								btnNext.setVisibility(View.INVISIBLE);
								btnPrevious.setVisibility(View.INVISIBLE);
							}
						}
					}
				} else {
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<Entry> jsonToListEntry(JSONArray arrayEntriesResult) {
		ArrayList<Entry> result = new ArrayList<Entry>();
		JSONObject entriesResult;
		JSONObject placeResult;
		Entry entryItem;
		try {
			int arrayLength = arrayEntriesResult.length();
			if (arrayLength > 0) {
				for (int i = 0; i < arrayLength; i++) {
					entriesResult = arrayEntriesResult.getJSONObject(i);
					entryItem = new Entry();
					if (entriesResult.has("_id")) {
						entryItem.setEntryID(entriesResult.getString("_id"));
					}
					if (entriesResult.has("type")) {
						entryItem.setType(entriesResult.getString("type"));
					}
					if (entriesResult.has("text")) {
						entryItem.setText(entriesResult.getString("text"));
					}
					if (entriesResult.has("path")) {
						entryItem.setPath(entriesResult.getString("path"));
					}
					if (entriesResult.has("coordinate")) {
						entryItem.setCoordinate(entriesResult
								.getString("coordinate"));
					}
					if (entriesResult.has("create_date")) {
						entryItem.setDateTime(entriesResult
								.getString("create_date"));
					}
					if (entriesResult.has("place")) {
						placeResult = entriesResult.getJSONObject("place");
						if (placeResult.has("location")) {
							entryItem.setAddress(placeResult
									.getString("location"));
						}
						if (placeResult.has("place_name")) {
							entryItem.setPlaceName(placeResult
									.getString("place_name"));
						}
					}
					result.add(entryItem);
				}
			}

		} catch (Exception e) {
			return new ArrayList<Entry>();
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
				Toast.makeText(
						getActivity().getApplicationContext(),
						"View online journey on map is under construction!", Toast.LENGTH_SHORT)
						.show();
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

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			Entry currentItem = listEntries.get(position);
			ViewGroup itemView = (ViewGroup) getActivity().getLayoutInflater()
					.inflate(R.layout.fragment_journey_view_details_item,
							container, false);
			RelativeLayout comboButton = (RelativeLayout) itemView
					.findViewById(R.id.comboButton);
			comboButton.setVisibility(View.GONE);
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
			if (currentItem.getType() != null) {
				if (currentItem.getType().equals("image")) {
					frameImage.setVisibility(View.VISIBLE);
					if (currentItem.getPath() != null) {
						ivEntryImage.setVisibility(View.VISIBLE);
						UniversalImageHelper.loadImage(getActivity()
								.getApplicationContext(), ivEntryImage,
								Constant.SERVER_HOST
										+ Constant.IMAGE_TYPE_LARGE
										+ currentItem.getPath());
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
								Constant.SERVER_HOST
										+ Constant.IMAGE_TYPE_LARGE
										+ currentItem.getPath());
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
}
