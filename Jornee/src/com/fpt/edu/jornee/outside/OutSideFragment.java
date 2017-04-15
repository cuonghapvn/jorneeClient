/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: OutSideFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.outside;

import static com.fpt.edu.jornee.utils.Constant.SERVER_HOST;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.bean.EntryInOutside;
import com.fpt.edu.bean.Message;
import com.fpt.edu.bean.SocketNotificationEvent;
import com.fpt.edu.bean.OutsideActivityBean;
import com.fpt.edu.jornee.HomeFragment;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.message.MessageFragment;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.socketio.SocketIO;
import com.fpt.edu.jornee.socketio.SocketIOConnection;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.DateTimeHelper;
import com.fpt.edu.jornee.utils.JSONParser;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.readystatesoftware.viewbadger.BadgeView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

public class OutSideFragment extends Fragment {
	private static final String TAG = "OutsideFragment";
	public SlidingUpPanelLayout slidingUpPanel;
	ImageButton btnMessage, btnNotification;
	RelativeLayout upPanel;
	ListView lvNotification;
	TextView txtEmpty;
	BadgeView badgeMessage, badgeNotification;
	ProgressBar loading;
	OutsideActivityListAdapter outsideActivityListAdapter;
	NotificationListAdapter notificationListAdapter;
	PullToRefreshListView lvActivity;
	LinkedList<OutsideActivityBean> outsideActivities;
	ArrayList<SocketNotificationEvent> notifications;
	JSONArray arrayOutsideResult = null;
	JSONArray arrayNotificationResult = null;
	String authen_status = null;
	String status = null;
	int currentPage = 1;
	boolean stopLoading = false;
	static HashMap<String, String> user;
	SessionManager sessionManager;
	View footerView;

	public void updateBadgeMessage() {
		MainActivity activity = (MainActivity) getActivity();
		int count = activity.mMessCount;
		if (count > 0) {
			if (!badgeMessage.isShown()) {
				badgeMessage.toggle();
			}
			badgeMessage.setText("" + count);
		} else {
			badgeMessage.hide();
		}
	}

	public void updateBadgeNotification() {
		MainActivity activity = (MainActivity) getActivity();
		int count = activity.mNotifCount;
		if (count > 0) {
			if (!badgeNotification.isShown()) {
				badgeNotification.toggle();
			}
			badgeNotification.setText("" + count);
		} else {
			badgeNotification.hide();
		}
	}

	public OutSideFragment() {
		// Empty constructor required for fragment subclasses
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if (bundle != null
				&& bundle.containsKey(Constant.FRAGMENT_TITLE_BUNDLE)) {
			getActivity().setTitle(
					bundle.getString(Constant.FRAGMENT_TITLE_BUNDLE));
		}
		footerView = inflater
				.inflate(R.layout.loading_layout, container, false);
		View rootView = inflater.inflate(R.layout.fragment_outside_layout,
				container, false);
		txtEmpty = (TextView) rootView.findViewById(R.id.txtEmpty);
		loading = (ProgressBar) rootView.findViewById(R.id.loading);
		sessionManager = new SessionManager(getActivity()
				.getApplicationContext());
		user = new HashMap<String, String>();
		user = sessionManager.getUserDetails();
		slidingUpPanel = (SlidingUpPanelLayout) rootView
				.findViewById(R.id.sliding_layout);
		upPanel = (RelativeLayout) rootView.findViewById(R.id.upPanel);
		btnMessage = (ImageButton) rootView.findViewById(R.id.btnMessage);
		btnNotification = (ImageButton) rootView
				.findViewById(R.id.btnNotification);
		badgeMessage = new BadgeView(getActivity().getApplicationContext(),
				btnMessage);
		badgeMessage.setBackgroundResource(R.drawable.bg_number_notif);
		badgeMessage.setMinimumHeight(20);
		badgeMessage.setMinimumWidth(20);
		badgeNotification = new BadgeView(
				getActivity().getApplicationContext(), btnNotification);
		updateBadgeNotification();
		updateBadgeMessage();
		lvNotification = (ListView) rootView
				.findViewById(R.id.listViewNotification);
		lvActivity = (PullToRefreshListView) rootView
				.findViewById(R.id.jazzy_pager);
		lvActivity.setMode(Mode.BOTH);
		new LoadOutsideAsync().execute("" + 1);
		btnMessage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivity activity = (MainActivity) getActivity();
				activity.mMessCount = 0;
				activity.clearBackStack();
				Fragment fragment = new MessageFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Message");
				fragment.setArguments(bundle);
				activity.replaceFragment(fragment);
			}
		});
		lvActivity
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

					@SuppressWarnings("rawtypes")
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						String label = DateUtils.formatDateTime(getActivity()
								.getApplicationContext(), System
								.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						new LoadOutsideAsync().execute("" + 0);

					}

					@SuppressWarnings("rawtypes")
					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						if (!stopLoading) {
							String label = DateUtils.formatDateTime(
									getActivity().getApplicationContext(),
									System.currentTimeMillis(),
									DateUtils.FORMAT_SHOW_TIME
											| DateUtils.FORMAT_SHOW_DATE
											| DateUtils.FORMAT_ABBREV_ALL);
							refreshView.getLoadingLayoutProxy()
									.setLastUpdatedLabel(label);
							new LoadOutsideAsync().execute("" + 2);
						} else {
							lvActivity.onRefreshComplete();
						}

					}

				});
		notifications = new ArrayList<SocketNotificationEvent>();
		notificationListAdapter = new NotificationListAdapter(getActivity()
				.getApplicationContext(),
				R.layout.notification_list_row_layout, notifications,
				getActivity());
		lvNotification.setAdapter(notificationListAdapter);
		new LoadNotificationAsync(notifications.size()).execute("");
		lvNotification.setOnScrollListener(new EndlessScrollListener(){

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				new LoadNotificationAsync(notifications.size()).execute("");
			}
			
		});
		LayoutParams params = upPanel.getLayoutParams();
		slidingUpPanel.setShadowDrawable(getResources().getDrawable(
				R.drawable.above_shadow));
		slidingUpPanel.setPanelHeight(params.height);
		slidingUpPanel.setAnchorPoint(0.3f);
		slidingUpPanel.setDragView(lvNotification);
		slidingUpPanel.setSlidingEnabled(false);
		slidingUpPanel.setEnableDragViewTouchEvents(true);
		btnNotification.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				slidePanelUp();
				MainActivity activity = (MainActivity) getActivity();
				if(activity.mNotifCount>0){
					notifications.clear();
					notificationListAdapter.notifyDataSetChanged();
					activity.mNotifCount = 0;
					updateBadgeNotification();
					new LoadNotificationAsync(notifications.size()).execute("");
				}
			}
		});
		slidingUpPanel.setPanelSlideListener(new PanelSlideListener() {
			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				Log.i(TAG, "onPanelSlide, offset " + slideOffset);
			}

			@Override
			public void onPanelExpanded(View panel) {
				Log.i(TAG, "onPanelExpanded");

			}

			@Override
			public void onPanelCollapsed(View panel) {
				Log.i(TAG, "onPanelCollapsed");

			}

			@Override
			public void onPanelAnchored(View panel) {
				Log.i(TAG, "onPanelAnchored");

			}
		});
		setHasOptionsMenu(true);
		return rootView;

	}

	public void slidePanelUp() {
		if (slidingUpPanel.isExpanded() || slidingUpPanel.isAnchored()) {
			slidingUpPanel.collapsePane();
		} else {
			slidingUpPanel.expandPane(slidingUpPanel.getAnchorPoint());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.outside, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search:
			Fragment fragment = new SearchFriendFragment();
			Bundle bundle = new Bundle();
			bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,
					"Search for friend...");
			fragment.setArguments(bundle);
			MainActivity activity = (MainActivity) getActivity();
			activity.replaceFragment(fragment);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class LoadNotificationAsync extends
			AsyncTask<String, Void, JSONObject> {
		JSONObject jsonObject = null;
		int skip;

		public LoadNotificationAsync(int skip) {
			this.skip = skip;
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			try {
				SessionManager sessionManager = new SessionManager(
						getActivity().getApplicationContext());
				HashMap<String, String> user = new HashMap<String, String>();
				user = sessionManager.getUserDetails();
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("token", user
						.get(SessionManager.KEY_TOKEN)));
				nameValuePairs.add(new BasicNameValuePair("skip", "" + skip));

				JSONParser jsonParser = new JSONParser(getActivity().getApplicationContext());
				jsonObject = jsonParser.makeHttpRequest(SERVER_HOST
						+ "list_notification", "POST", nameValuePairs);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return jsonObject;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				if (result != null) {
					System.out.println(result);
					authen_status = result.getString("authen_status");
					if (result.has("status")) {
						status = result.getString("status");
					}
					if (result.has("list")) {
						arrayNotificationResult = result.getJSONArray("list");
					}
					if ("error".equals(authen_status)) {
					} else if ("fail".equals(authen_status)) {
					} else if ("ok".equals(authen_status)) {
						if ("error".equals(status)) {
						} else if ("ok".equals(status)) {
							ArrayList<SocketNotificationEvent> tempList = new ArrayList<SocketNotificationEvent>();
							tempList = resultListNotificationFromJSON(arrayNotificationResult);
							notifications.addAll(tempList);
							Collections.sort(notifications);
							notificationListAdapter.notifyDataSetChanged();
						}

					}
				}

			} catch (Exception e) {
			}
		}
	}

	private class LoadOutsideAsync extends AsyncTask<String, Void, JSONObject> {
		JSONObject jsonObject = null;
		int page;

		@Override
		protected JSONObject doInBackground(String... params) {
			try {
				page = Integer.parseInt(params[0]);
				user = sessionManager.getUserDetails();
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("token", user
						.get(SessionManager.KEY_TOKEN)));
				nameValuePairs.add(new BasicNameValuePair("page", params[0]));
				JSONParser jsonParser = new JSONParser(getActivity().getApplicationContext());
				jsonObject = jsonParser.makeHttpRequest(SERVER_HOST + "outside", "POST", nameValuePairs);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return jsonObject;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				if (result != null) {
					System.out.println(result);
					authen_status = result.getString("authen_status");
					if (result.has("status")) {
						status = result.getString("status");
					}
					if (result.has("journeys")) {
						arrayOutsideResult = result.getJSONArray("journeys");
					}
					if ("error".equals(authen_status)) {
					} else if ("fail".equals(authen_status)) {
					} else if ("ok".equals(authen_status)) {
						if ("error".equals(status)) {
						} else if ("ok".equals(status)) {
							LinkedList<OutsideActivityBean> resultList = resultListActivityFromJSON(arrayOutsideResult);
							if (resultList.size() > 0) {
								txtEmpty.setVisibility(View.INVISIBLE);
								if (page == 1) {
									loading.setVisibility(View.INVISIBLE);
									lvActivity.setVisibility(View.VISIBLE);
									outsideActivities = resultList;
									outsideActivityListAdapter = new OutsideActivityListAdapter(
											getActivity()
													.getApplicationContext(),
											outsideActivities,
											R.layout.outside_activity_item_5image,
											getActivity());
									lvActivity
											.setAdapter(outsideActivityListAdapter);
								} else if (page > 1) {
									lvActivity.setVisibility(View.VISIBLE);
									outsideActivityListAdapter
											.addAll(resultList);
									outsideActivityListAdapter
											.notifyDataSetChanged();
									lvActivity.onRefreshComplete();
								} else if (page == 0) {
									lvActivity.setVisibility(View.VISIBLE);
									for (int i = 0; i < resultList.size(); i++) {
										outsideActivities.addFirst(resultList
												.get(resultList.size()
														- (i + 1)));
									}
									outsideActivityListAdapter
											.notifyDataSetChanged();
									lvActivity.onRefreshComplete();
								}
							} else {
								loading.setVisibility(View.INVISIBLE);
								lvActivity.setVisibility(View.VISIBLE);
								if( page == 1){
									txtEmpty.setText(getActivity().getResources().getString(R.string.outsideEmptyMessage));
									txtEmpty.setVisibility(View.VISIBLE);
								}
								if (page == 2) {
									System.out
											.println("No more data. Stop loading!");
									stopLoading = true;
									lvActivity.onRefreshComplete();
								}
								if (page == 0) {
									lvActivity.onRefreshComplete();
								}
							}

						}
					}
				} else {
					Toast.makeText(
							getActivity(),
							"Error occur due to server's failures! Please try later!",
							Toast.LENGTH_LONG).show();
					if (page == 0) {
						lvActivity.onRefreshComplete();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	private ArrayList<SocketNotificationEvent> resultListNotificationFromJSON(JSONArray jsonArray){
		ArrayList<SocketNotificationEvent> arrayList = new ArrayList<SocketNotificationEvent>();
		try {
			JSONObject object;
			SocketNotificationEvent notification;
			if(jsonArray.length()>0){
				for (int i = 0; i < jsonArray.length(); i++) {
					object = jsonArray.getJSONObject(i);
					notification = new SocketNotificationEvent();
					if(object.has("other_id")){
						notification.setOther_id(object.getString("other_id"));
					}
					if(object.has("type")){
						notification.setType(object.getString("type"));
					}
					if(object.has("content_id")){
						notification.setContent_id(object.getString("content_id"));
					}
					if(object.has("time")){
						notification.setTime(object.getString("time"));
					}
					if(object.has("avatar")){
						notification.setAvatar(object.getString("avatar"));
					}
					arrayList.add(notification);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return arrayList;
	}

	private LinkedList<OutsideActivityBean> resultListActivityFromJSON(
			JSONArray jsonArray) {
		LinkedList<OutsideActivityBean> result = new LinkedList<OutsideActivityBean>();
		try {
			JSONObject journeyJSONResult;
			JSONArray entriesJSONResult;
			JSONObject entriesResult;
			OutsideActivityBean journeyResult;
			EntryInOutside entryInOutside;
			int numOfResult = jsonArray.length();
			if (numOfResult > 0) {
				for (int i = 0; i < numOfResult; i++) {
					journeyJSONResult = jsonArray.getJSONObject(i);
					journeyResult = new OutsideActivityBean();
					if (journeyJSONResult.has("_id")) {
						journeyResult.setJourneyID(journeyJSONResult
								.getString("_id"));
					}
					if (journeyJSONResult.has("user_id")) {
						journeyResult.setUsername(journeyJSONResult
								.getString("user_id"));
					}
					if (journeyJSONResult.has("avatar")) {
						journeyResult.setUserAvatarURL(journeyJSONResult
								.getString("avatar"));
					}
					if (journeyJSONResult.has("journey_name")) {
						journeyResult.setJourneyName(journeyJSONResult
								.getString("journey_name"));
					}
					if (journeyJSONResult.has("liked")) {
						journeyResult.setNumberLiked(journeyJSONResult
								.getString("liked"));
					}
					if (journeyJSONResult.has("comment")) {
						journeyResult.setNumberComment(journeyJSONResult
								.getString("comment"));
					}
					if (journeyJSONResult.has("create_date")) {
						journeyResult.setJourneyDateTime(journeyJSONResult
								.getString("create_date"));
					}
					if (journeyJSONResult.has("num_of_all_entries")) {
						journeyResult.setNumOfTotalEntry(journeyJSONResult
								.getInt("num_of_all_entries"));
					}
					if (journeyJSONResult.has("num_of_return_entries")) {
						journeyResult.setNumOfReturnEntry(journeyJSONResult
								.getInt("num_of_return_entries"));
					}
					if (journeyJSONResult.has("start_point")) {
						journeyResult.setStartLocation(journeyJSONResult
								.getString("start_point"));
					}
					if (journeyJSONResult.has("end_point")) {
						journeyResult.setLastLocation(journeyJSONResult
								.getString("end_point"));
					}
					if (journeyJSONResult.has("is_liked")) {
						journeyResult.setCurrentUserLiked(journeyJSONResult
								.getBoolean("is_liked"));
					}
					if (journeyJSONResult.has("entries")) {
						ArrayList<EntryInOutside> entries = new ArrayList<EntryInOutside>();
						entriesJSONResult = journeyJSONResult
								.getJSONArray("entries");
						if (entriesJSONResult.length() > 0) {
							for (int j = 0; j < entriesJSONResult.length(); j++) {
								entryInOutside = new EntryInOutside();
								entriesResult = entriesJSONResult
										.getJSONObject(j);
								if (entriesResult.has("type")) {
									entryInOutside.setType(entriesResult
											.getString("type"));
								}
								if (entriesResult.has("data")) {
									entryInOutside.setData(entriesResult
											.getString("data"));
								}
								entries.add(entryInOutside);
							}

						}
						journeyResult.setEntries(entries);
					}
					result.add(journeyResult);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
