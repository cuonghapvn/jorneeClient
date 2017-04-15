/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: CreateJourneyFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.journey;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.bean.*;
import com.fpt.edu.jornee.*;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.sqllite.DatabaseHandler;
import com.fpt.edu.jornee.utils.*;

public class CreateJourneyFragment extends Fragment {

	public final static String JOURNEY_ID = "journeyID";
	public final static String JOURNEY_NAME = "journeyName";
	public final static String JOURNEY_LIST_ENTRIES = "journeyListEntries";
	public final static String JOURNEY_ACTION = "journeyAction";

	static HashMap<String, String> user;
	SessionManager sessionManager;
	DatabaseHandler databaseHandler;
	ArrayList<Entry> journeyListEntries;
	ArrayList<Entry> currentListEntries;
	SelectEntryAdapter selectEntryAdapter;

	ImageView btnAdd;
	ImageView btnRemoveSelection;
	TextView btnSave;
	TextView btnCancel;
	EditText etJourneyName;
	String journeyID;
	String journeyName;
	String journeyAction;
	ListView lvEntries;
	MainActivity activity;

	public CreateJourneyFragment() {
		// Empty constructor required for fragment subclasses
	}

	@SuppressWarnings("unchecked")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = (MainActivity) getActivity();
		sessionManager = new SessionManager(getActivity()
				.getApplicationContext());
		user = sessionManager.getUserDetails();
		View rootView = inflater.inflate(
				R.layout.fragment_journey_create_layout, container, false);
		btnAdd = (ImageView) rootView.findViewById(R.id.btnAdd);
		btnRemoveSelection = (ImageView) rootView
				.findViewById(R.id.btnRemoveSelection);
		btnSave = (TextView) rootView.findViewById(R.id.btnSave);
		btnCancel = (TextView) rootView.findViewById(R.id.btnCancel);
		etJourneyName = (EditText) rootView.findViewById(R.id.etJourneyName);
		lvEntries = (ListView) rootView.findViewById(R.id.lvEntries);
		journeyListEntries = new ArrayList<Entry>();
		currentListEntries = new ArrayList<Entry>();
		databaseHandler = new DatabaseHandler(getActivity()
				.getApplicationContext());
		getActivity().setTitle("Create journey");
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey(JOURNEY_ID)) {
				journeyID = bundle.getString(JOURNEY_ID);
				System.out.println("Journey ID: " + journeyID);
				if (journeyID != null) {
					Journey journey = databaseHandler
							.getJourneyInfor(journeyID);
					currentListEntries = getEntryOfJourney(journey);
				}
			}
			if (bundle.containsKey(JOURNEY_NAME)) {
				journeyName = bundle.getString(JOURNEY_NAME);
				etJourneyName.setText(journeyName);

			}
			if (bundle.containsKey(JOURNEY_LIST_ENTRIES)) {
				journeyListEntries.addAll((ArrayList<Entry>) bundle
						.getSerializable(JOURNEY_LIST_ENTRIES));
			}
			if (bundle.containsKey(JOURNEY_ACTION)) {
				journeyAction = bundle.getString(JOURNEY_ACTION);
				System.out.println("Action: " + journeyAction);
				if (journeyName != null && journeyAction != null
						&& journeyAction.equals("update")) {
					getActivity().setTitle("Update: " + journeyName);
				}
			}
		}
		if (currentListEntries.size() > 0) {
			clearDupplicate();
		}
		selectEntryAdapter = new SelectEntryAdapter(getActivity()
				.getApplicationContext(), R.layout.journey_select_entry_row,
				journeyListEntries, getActivity(), btnRemoveSelection);
		lvEntries.setAdapter(selectEntryAdapter);

		btnAdd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Fragment fragment = new SelectEntryFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Home");
				bundle.putSerializable(JOURNEY_LIST_ENTRIES, journeyListEntries);
				bundle.putString(JOURNEY_NAME, etJourneyName.getText()
						.toString());
				bundle.putString(JOURNEY_ID, journeyID);
				bundle.putString(JOURNEY_ACTION, journeyAction);
				fragment.setArguments(bundle);
				activity.replaceFragment(fragment);
			}
		});
		btnRemoveSelection.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				journeyListEntries.removeAll(selectEntryAdapter.getChosenList());
				selectEntryAdapter.notifyDataSetChanged();
				selectEntryAdapter.getChosenList().clear();
				v.setBackgroundResource(R.drawable.btn_journey_remove_entry_inactive_button);
				v.setEnabled(false);
			}
		});
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				saveJourney();
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.clearBackStack();
				Fragment fragment = new HomeFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Home");
				bundle.putInt("defaultTab", 1);
				fragment.setArguments(bundle);
				activity.replaceFragment(fragment);
			}
		});

		setHasOptionsMenu(true);
		return rootView;

	}

	public void saveJourney() {
		if (etJourneyName.getText().toString().trim().length() > 0) {
			String username = null;
			if (sessionManager.isLoggedIn()) {
				username = user.get(SessionManager.KEY_USERNAME);
			}
			long id = -1;
			if ("update".equals(journeyAction)) {
				Journey journey = databaseHandler.getJourneyInfor(journeyID);
				journey.setJourneyName(etJourneyName.getText().toString()
						.trim());
				journey.setUserID(username);
				journey.setModifiedDate(DateTimeHelper
						.convertLocalDateToServerString(new Date()));
				journey.setEntriesID(listEntryID(journeyListEntries));
				saveJourneyIDToEntry(journeyListEntries, journey.getJourneyID());
				id = databaseHandler.updateJourney(journey);
			} else {
				Journey journey = new Journey();
				journey.setJourneyName(etJourneyName.getText().toString()
						.trim());
				journey.setUserID(username);
				journey.setCreatedDate(DateTimeHelper
						.convertLocalDateToServerString(new Date()));
				journey.setModifiedDate(DateTimeHelper
						.convertLocalDateToServerString(new Date()));
				journey.setEntriesID(listEntryID(journeyListEntries));
				System.out.println("Entries: "
						+ listEntryID(journeyListEntries));
				id = databaseHandler.addJourney(journey);
			}
			if (id != -1) {
				if(!"update".equals(journeyAction)){
					saveJourneyIDToEntry(journeyListEntries, ""+id);
				}
				activity.clearBackStack();
				Fragment fragment = new HomeFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Home");
				bundle.putInt("defaultTab", 1);
				fragment.setArguments(bundle);
				activity.replaceFragment(fragment);
			}
		} else {
			alert("Journey name cannot be empty");
			etJourneyName.requestFocus();
		}
	}

	public void saveJourneyIDToEntry(ArrayList<Entry> journeyListEntries,
			String journeyID) {
		String existJourneyID;
		if (journeyListEntries.size() > 0) {
			for (int i = 0; i < journeyListEntries.size(); i++) {
				Entry currentEntry = journeyListEntries.get(i);
				existJourneyID = currentEntry.getJourneyID();
				ArrayList<String> listHolder = new ArrayList<String>();
				boolean found = false;
				if(existJourneyID != null) {
					StringTokenizer stringTokenizer = new StringTokenizer(
							existJourneyID, ",");
					while (stringTokenizer.hasMoreElements()) {
						String tempID = stringTokenizer.nextElement().toString();
						listHolder.add(tempID);
						if (tempID.equals(journeyID)) {
							found = true;
						}
					}
				}
				if (!found) {
					listHolder.add(journeyID);
					StringBuilder builder = new StringBuilder();
					String delim = "";
					for (String string : listHolder) {
						builder.append(delim);
						builder.append(string);
						delim = ",";
					}
					currentEntry.setJourneyID(builder.toString());
					databaseHandler.updateEntry(currentEntry);
				}
			}
		}

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

	public void clearDupplicate() {
		for (int i = 0; i < currentListEntries.size(); i++) {
			if (!isExist(journeyListEntries, currentListEntries.get(i))) {
				journeyListEntries.add(currentListEntries.get(i));
			}
		}

	}

	public boolean isExist(ArrayList<Entry> list, Entry item) {
		boolean found = false;
		for (Entry entry : list) {
			if (entry.getEntryID().equals(item.getEntryID()))
				found = true;
		}
		return found;
	}

	public String listEntryID(ArrayList<Entry> currentListEntries) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < currentListEntries.size(); i++) {
			builder.append(currentListEntries.get(i).getEntryID());
			if (i < (currentListEntries.size() - 1)) {
				builder.append(",");
			}
		}
		return builder.toString();
	}

	private void alert(String message) {
		Toast.makeText(getActivity().getApplicationContext(), message,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// inflater.inflate(R.menu.outside, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	public void showConfirmGoBackDialog() {
		new AlertDialog.Builder(activity)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Unsaved journey")
				.setMessage("Do you want to save this journey?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								saveJourney();
							}

						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						activity.clearBackStack();
						Fragment fragment = new HomeFragment();
						Bundle bundle = new Bundle();
						bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Home");
						bundle.putInt("defaultTab", 1);
						fragment.setArguments(bundle);
						activity.replaceFragment(fragment);
					}

				}).show();
	}

}
