/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HomeListJourneyFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.journey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.fpt.edu.bean.Journey;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.sqllite.DatabaseHandler;
import com.fpt.edu.jornee.sync.SyncReceiver;
import com.fpt.edu.jornee.utils.Constant;

public final class HomeListJourneyFragment extends Fragment {

	static HashMap<String, String> user;
	static SessionManager sessionManager;
	private static SwipeListView swipeListView;
	static JourneyListAdapter adapter;
	static ArrayList<Journey> journeys;
	static DatabaseHandler databaseHandler;
	ImageView btnAdd;
	static MainActivity activity;

	public static Fragment newInstance() {
		HomeListJourneyFragment fragment = new HomeListJourneyFragment();

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = (MainActivity) getActivity();
		sessionManager = new SessionManager(activity
				.getApplicationContext());
		user = sessionManager.getUserDetails();
		View rootView = inflater.inflate(
				R.layout.fragment_home_view_list_journey_layout, container,
				false);
		databaseHandler = new DatabaseHandler(getActivity()
				.getApplicationContext());
		swipeListView = (SwipeListView) rootView
				.findViewById(R.id.example_lv_list);
		btnAdd = (ImageView) rootView.findViewById(R.id.btnAdd);
		if(sessionManager.isLoggedIn()){
			journeys = getJourneyOfUser(user.get(SessionManager.KEY_USERNAME));
		} else {
			journeys = getJourneyOfUser(null);
		}
		Collections.sort(journeys);
		adapter = new JourneyListAdapter(getActivity().getApplicationContext(),
				R.layout.fragment_journey_home_list_layout_row, journeys,
				getActivity());
		swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onOpened(int position, boolean toRight) {
			}

			@Override
			public void onClosed(int position, boolean fromRight) {
			}

			@Override
			public void onListChanged() {
			}

			@Override
			public void onMove(int position, float x) {
			}

			@Override
			public void onStartOpen(int position, int action, boolean right) {
				Log.d("swipe", String.format("onStartOpen %d - action %d",
						position, action));
				swipeListView.closeOpenedItems();
			}

			@Override
			public void onStartClose(int position, boolean right) {
				Log.d("swipe", String.format("onStartClose %d", position));
			}

			@Override
			public void onClickFrontView(int position) {
				Fragment fragment = new ViewJourneyDetailFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, journeys
						.get(position).getJourneyName());
				bundle.putString(CreateJourneyFragment.JOURNEY_NAME, journeys
						.get(position).getJourneyName());
				bundle.putString(CreateJourneyFragment.JOURNEY_ID, journeys
						.get(position).getJourneyID());
				fragment.setArguments(bundle);
				MainActivity activity = (MainActivity) getActivity();
				activity.replaceFragment(fragment);
			}

			@Override
			public void onClickBackView(int position) {


			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {
				// for (int position : reverseSortedPositions) {
				// data.remove(position);
				// }
				// adapter.notifyDataSetChanged();
			}

		});

		btnAdd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				callAddNewJourney();
			}
		});

		swipeListView.setAdapter(adapter);
		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	public static void refreshData(){
		sessionManager = new SessionManager(activity
				.getApplicationContext());
		if(sessionManager.isLoggedIn()){
			journeys = getJourneyOfUser(user.get(SessionManager.KEY_USERNAME));
		} else {
			journeys = getJourneyOfUser(null);
		}
		Collections.sort(journeys);
		adapter.notifyDataSetChanged();
	}

	public static ArrayList<Journey> getJourneyOfUser(String userID) {
		ArrayList<Journey> temp = databaseHandler.getAllJourneys();
		ArrayList<Journey> result = new ArrayList<Journey>();
		int size = temp.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (temp.get(i).getUserID() == null
						|| temp.get(i).getUserID().isEmpty()
						|| temp.get(i).getUserID().equals(userID)) {
					result.add(temp.get(i));
				}
			}
		}
		return result;
	}

	public void callAddNewJourney() {
		Fragment fragment = new CreateJourneyFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Create new journey");
		fragment.setArguments(bundle);
		MainActivity activity = (MainActivity) getActivity();
		activity.replaceFragment(fragment);
	}
}
