/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: MessageFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.message;

import static com.fpt.edu.jornee.utils.Constant.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fpt.edu.bean.Conversation;
import com.fpt.edu.bean.SocketMessageEvent;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.outside.EndlessScrollListener;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.sqllite.DatabaseHandler;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.DateTimeHelper;
import com.fpt.edu.jornee.utils.JSONParser;

public class MessageFragment extends Fragment {
	ListView lvConversation;
	ArrayList<Conversation> listConversation;
	ConversationListAdapter conversationListAdapter;
	DatabaseHandler databaseHandler;
	static HashMap<String, String> user;
	SessionManager sessionManager;
	String authen_status = null;
	String status = null;
	JSONArray arrayResultConversation = null;
	ProgressDialog progressDialog;
	ProgressBar pbLoadingConversation;
	boolean isStopLoading = false;

	public MessageFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if (bundle != null
				&& bundle.containsKey(Constant.FRAGMENT_TITLE_BUNDLE)) {
			getActivity().setTitle(
					bundle.getString(Constant.FRAGMENT_TITLE_BUNDLE));
		}
		sessionManager = new SessionManager(getActivity()
				.getApplicationContext());
		user = new HashMap<String, String>();
		user = sessionManager.getUserDetails();
		View rootView = inflater.inflate(R.layout.fragment_message_layout,
				container, false);
		pbLoadingConversation = (ProgressBar) rootView.findViewById(R.id.pbLoadingConversation);
		listConversation = new ArrayList<Conversation>();
		lvConversation = (ListView) rootView
				.findViewById(R.id.listConversation);
		conversationListAdapter = new ConversationListAdapter(getActivity()
				.getApplicationContext(), R.layout.fragment_message_layout_row,
				listConversation, getActivity());
		lvConversation.setAdapter(conversationListAdapter);
		lvConversation.setOnScrollListener(new EndlessScrollListener() {

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				if (!isStopLoading) {
					new LoadListConversationAsync(listConversation.size())
							.execute();
				}
			}
		});
		new LoadListConversationAsync(0).execute();
		setHasOptionsMenu(true);
		return rootView;

	}

	public void updateList(Object event) {
		SocketMessageEvent evt = (SocketMessageEvent) event;
		boolean found = false;
		if (listConversation.size() > 0) {
			for (Conversation item : listConversation) {
				if (item.getContactUser().equals(evt.from_user)) {
					item.setCurrentUser(evt.from_user);
					item.setLastMessage(evt.message);
					Date date = DateTimeHelper
							.convertStringServerTimeToLocalDate(evt.time);
					item.setModifiedDate(date);
					found = true;
					int numUnread = item.getNumberUnread();
					item.setNumberUnread(numUnread += 1);
					Collections.sort(listConversation);
					conversationListAdapter.notifyDataSetChanged();
				}
			}
			if (!found) {
				Conversation conversation = new Conversation();
				conversation.setCurrentUser(evt.from_user);
				conversation.setContactUser(evt.from_user);
				conversation.setAvatar(evt.avatar);
				conversation.setLastMessage(evt.message);
				Date date = DateTimeHelper
						.convertStringServerTimeToLocalDate(evt.time);
				conversation.setModifiedDate(date);
				conversation.setNumberUnread(1);
				listConversation.add(conversation);
				Collections.sort(listConversation);
				conversationListAdapter.notifyDataSetChanged();
			}
		}
	}

	private class LoadListConversationAsync extends
			AsyncTask<String, Void, JSONObject> {
		JSONObject jsonObject = null;
		int skip;

		public LoadListConversationAsync(int page) {
			this.skip = page;
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("token", user
						.get(SessionManager.KEY_TOKEN)));
				nameValuePairs.add(new BasicNameValuePair("skip", "" + skip));
				JSONParser jsonParser = new JSONParser(getActivity().getApplicationContext());
				jsonObject = jsonParser.makeHttpRequest(SERVER_HOST
						+ SOCIAL_LIST_CONVERSATIONS, "POST", nameValuePairs);
				
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return jsonObject;
		}

		@Override
		protected void onPreExecute() {
//			if (skip == 0) {
//				progressDialog = ProgressDialog.show(getActivity(), "",
//						"Loading ", true);
//			}

		}

		@Override
		protected void onPostExecute(JSONObject result) {
			try {
//				if (progressDialog.isShowing()) {
//					progressDialog.dismiss();
//				}
				pbLoadingConversation.setVisibility(View.INVISIBLE);
				if (result != null) {
					System.out.println(result);
					authen_status = result.getString("authen_status");
					if (result.has("status")) {
						status = result.getString("status");
					}
					if (result.has("list")) {
						arrayResultConversation = result.getJSONArray("list");
					}
					if ("error".equals(authen_status)) {
					} else if ("fail".equals(authen_status)) {
					} else if ("ok".equals(authen_status)) {
						if ("error".equals(status)) {

						} else if ("ok".equals(status)) {
							ArrayList<Conversation> tempList = listConversationFromJson(arrayResultConversation);
							if (tempList.size() > 0) {
								listConversation.addAll(tempList);
								conversationListAdapter.notifyDataSetChanged();
							} else {
								isStopLoading = true;
							}
						}
					}
				} else {
					Toast.makeText(
							getActivity(),
							"Error occur due to server's failures! Please try later!",
							Toast.LENGTH_LONG).show();
					if (skip == 1) {
						conversationListAdapter = new ConversationListAdapter(
								getActivity().getApplicationContext(),
								R.layout.fragment_message_layout_row,
								listConversation, getActivity());
						lvConversation.setAdapter(conversationListAdapter);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public ArrayList<Conversation> listConversationFromJson(JSONArray array) {
		ArrayList<Conversation> result = new ArrayList<Conversation>();
		try {
			if (array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					Conversation conversation = new Conversation();
					conversation.setContactUser(object.getString("chatter"));
					conversation.setCurrentUser(object
							.getString("last_chatter"));
					conversation.setLastMessage(object
							.getString("last_message"));
					conversation.setNumberUnread(object
							.getInt("unread_message"));
					Date date = DateTimeHelper
							.convertStringServerTimeToLocalDate(object
									.getString("time"));
					conversation.setModifiedDate(date);
					conversation.setAvatar(object.getString("avatar"));
					result.add(conversation);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return result;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.message, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			Fragment fragment = new CreateConversationFragment();
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

}
