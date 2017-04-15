/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: SelectEntryFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.journey;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.fpt.edu.bean.Entry;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.sqllite.DatabaseHandler;
import com.fpt.edu.jornee.utils.Constant;

public class SelectEntryFragment extends Fragment {
	ListView lvEntries;
	SelectEntryAdapter selectEntryAdapter;
	ArrayList<Entry> entries;
	DatabaseHandler databaseHandler;
	TextView btnSave;
	TextView btnCancel;
	String journeyID;
	String journeyName;
	String journeyAction;
	ArrayList<Entry> journeyListEntries;

	public SelectEntryFragment() {
		// Empty constructor required for fragment subclasses
	}

	@SuppressWarnings("unchecked")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater
				.inflate(R.layout.fragment_journey_select_entry_layout,
						container, false);
		lvEntries = (ListView) rootView.findViewById(R.id.lvEntries);
		btnSave = (TextView) rootView.findViewById(R.id.btnSave);
		btnCancel = (TextView) rootView.findViewById(R.id.btnCancel);
		databaseHandler = new DatabaseHandler(getActivity()
				.getApplicationContext());
		entries = databaseHandler.getAllEntry();

		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey(CreateJourneyFragment.JOURNEY_ID)) {
				journeyID = bundle.getString(CreateJourneyFragment.JOURNEY_ID);
			}
			if (bundle.containsKey(CreateJourneyFragment.JOURNEY_NAME)) {
				journeyName = bundle
						.getString(CreateJourneyFragment.JOURNEY_NAME);
			}
			if (bundle.containsKey(CreateJourneyFragment.JOURNEY_LIST_ENTRIES)) {
				journeyListEntries = (ArrayList<Entry>) bundle
						.getSerializable(CreateJourneyFragment.JOURNEY_LIST_ENTRIES);
				if (journeyListEntries.size() > 0) {
					clearDupplicate();
				}
			}
			if (bundle.containsKey(CreateJourneyFragment.JOURNEY_ACTION)) {
				journeyAction = bundle
						.getString(CreateJourneyFragment.JOURNEY_ACTION);
			}
		}
		selectEntryAdapter = new SelectEntryAdapter(getActivity()
				.getApplicationContext(), R.layout.journey_select_entry_row,
				entries, getActivity(), null);
		lvEntries.setAdapter(selectEntryAdapter);
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity activity = (MainActivity) getActivity();
				journeyListEntries.addAll(selectEntryAdapter.getChosenList());
				Fragment fragment = new CreateJourneyFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,
						"Home");
				bundle.putSerializable(CreateJourneyFragment.JOURNEY_LIST_ENTRIES, journeyListEntries);
				bundle.putString(CreateJourneyFragment.JOURNEY_NAME, journeyName);
				bundle.putString(CreateJourneyFragment.JOURNEY_ID, journeyID);
				bundle.putString(CreateJourneyFragment.JOURNEY_ACTION, journeyAction);
				fragment.setArguments(bundle);
				activity.replaceFragment(fragment);
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivity activity = (MainActivity) getActivity();
				Fragment fragment = new CreateJourneyFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,
						"Home");
				bundle.putSerializable(CreateJourneyFragment.JOURNEY_LIST_ENTRIES, journeyListEntries);
				bundle.putString(CreateJourneyFragment.JOURNEY_NAME, journeyName);
				bundle.putString(CreateJourneyFragment.JOURNEY_ID, journeyID);
				bundle.putString(CreateJourneyFragment.JOURNEY_ACTION, journeyAction);
				fragment.setArguments(bundle);
				activity.replaceFragment(fragment);
			}
		});
		
		
		setHasOptionsMenu(true);
		return rootView;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	public void clearDupplicate() {
		ArrayList<Entry> temp = new ArrayList<Entry>();
		for (int i = 0; i < entries.size(); i++) {
			if (!isExist(journeyListEntries, entries.get(i))) {
				temp.add(entries.get(i));
			}
		}
		entries.clear();
		entries.addAll(temp);

	}

	public boolean isExist(ArrayList<Entry> list, Entry item) {
		boolean found = false;
		for (Entry entry : list) {
			if (entry.getEntryID().equals(item.getEntryID()))
				found = true;
		}
		return found;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		inflater.inflate(R.menu.outside, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

}
