/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: MessagingFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.message;

import static com.fpt.edu.jornee.utils.Constant.SERVER_HOST;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.bean.Message;
import com.fpt.edu.bean.SocketMessageEvent;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.socketio.SocketIO;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.DateTimeHelper;
import com.fpt.edu.jornee.utils.JSONParser;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.readystatesoftware.viewbadger.BadgeView;

public class MessagingFragment extends Fragment {
	public TextView mSend;
	static EditText mess;
	static ArrayAdapter<String> adapter;
	static MessageListAdapter messageListAdapter;
	static LinkedList<Message> messages;
	static String username = null;
	static HashMap<String, String> user;
	ArrayList<Chatter> chatters;
	SessionManager sessionManager;
	BadgeView badgeMessage;
	static Button notifCount;
	static int mNotifCount = 0;
	boolean stopLoading = false;
	static PullToRefreshListView lvMessages;
	JSONArray arrayResultChatter = null;
	JSONArray arrayResultMessage = null;
	String authen_status = null;
	String status = null;
	static MainActivity activity;

	public MessagingFragment() {
		// Empty constructor required for fragment subclasses
	}
	
	public static void hideSoftKeyboard(Activity activity) {
	    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if (bundle != null
				&& bundle.containsKey(Constant.FRAGMENT_TITLE_BUNDLE)) {
			getActivity().setTitle(
					bundle.getString(Constant.FRAGMENT_TITLE_BUNDLE));
		}
		if (bundle != null && bundle.containsKey(Constant.USERNAME_BUNDLE)) {
			username = bundle.getString(Constant.USERNAME_BUNDLE);
		}
		activity = (MainActivity) getActivity();
		View rootView = inflater.inflate(
				R.layout.chat_view_conversation_layout, container, false);
		lvMessages = (PullToRefreshListView) rootView
				.findViewById(R.id.listConversation);
		mess = (EditText) rootView.findViewById(R.id.etChatContent);
		mSend = (TextView) rootView.findViewById(R.id.btnSend);
		messages = new LinkedList<Message>();
		sessionManager = new SessionManager(getActivity()
				.getApplicationContext());
		user = new HashMap<String, String>();
		user = sessionManager.getUserDetails();
		new LoadConversationHistoryAsync(1).execute("");
		getActivity().setTitle(username);
		lvMessages.getLoadingLayoutProxy().setReleaseLabel("Release to load more");
		lvMessages.getLoadingLayoutProxy().setPullLabel("Pull to load more");
		lvMessages.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (!stopLoading) {
					String label = DateUtils.formatDateTime(getActivity()
							.getApplicationContext(), System
							.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
							label);
					new LoadConversationHistoryAsync(2).execute("");
				}
			}

		});
		lvMessages.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideSoftKeyboard(activity);
				return false;
			}
		});
		lvMessages.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideSoftKeyboard(activity);
			}
		});
		messageListAdapter = new MessageListAdapter(
				getActivity()
						.getApplicationContext(),
				R.layout.chat_row_left_text_layout,
				messages, getActivity());
		lvMessages.setAdapter(messageListAdapter);

		mess.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)) {
					if (v.getText().toString().trim().length() > 0) {
						sendMessage(v.getText().toString());
					}
				}
				return false;
			}
		});

		mSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mess.getText().toString().length() > 0) {
					sendMessage(mess.getText().toString());
				}
			}
		});

		setHasOptionsMenu(true);
		return rootView;

	}

	private static void setNotifCount() {
		activity.invalidateOptionsMenu();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public static void sendMessage(String message) {
		if(message.trim().length()>0){
			SendEvent myclientevent = new SendEvent();
			myclientevent.message = message.trim();
			myclientevent.other_user = username;
			MainActivity.mConnection.emit("client_message", myclientevent);
			Message messageOb = new Message();
			messageOb.setUsername(user.get(SessionManager.KEY_USERNAME));
			messageOb.setMessage(message);
			messageOb.setSentDate(DateTimeHelper.contextDateTime(new Date()));
			messages.addLast(messageOb);
			messageListAdapter.notifyDataSetChanged();
			lvMessages.getRefreshableView().setSelection(messageListAdapter.getCount()-1);
			mess.setText("");
		}
	}
	
	public static void receiveMessage(Object obj){
		SocketMessageEvent evt = (SocketMessageEvent) obj;
		if (evt.from_user.equals(username)) {
			System.out.println("mess : " + evt.message
					+ " name " + evt.from_user);
			Message message = new Message();
			message.setUsername(evt.from_user);
			message.setMessage(evt.message);
			Date date = DateTimeHelper
					.convertStringServerTimeToLocalDate(evt.time);
			message.setSentDate(DateTimeHelper
					.contextDateTime(date));
			messages.addLast(message);
			messageListAdapter.notifyDataSetChanged();
			lvMessages.getRefreshableView().setSelection(messageListAdapter.getCount()-1);
		} else {
			mNotifCount += 1;
			setNotifCount();
		}
	}

	public static class Chatter {
		public String user_id;
		public String avatar;
	}

	private static class SendEvent {
		public String message;
		public String other_user;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_messaging, menu);
		View count = menu.findItem(R.id.badge).getActionView();
		notifCount = (Button) count.findViewById(R.id.notif_count);
		notifCount.setText(String.valueOf(mNotifCount));
		count.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Fragment fragment = new MessageFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,
						"Message");
				fragment.setArguments(bundle);
				MainActivity activity = (MainActivity) getActivity();
				activity.replaceFragment(fragment);
			}
		});
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.badge:
			System.out.println("Touch roi ne!");
			Fragment fragment = new MessageFragment();
			getActivity().getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, fragment).commit();
			getActivity().setTitle("");
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class LoadConversationHistoryAsync extends
			AsyncTask<String, Void, JSONObject> {
		JSONObject jsonObject = null;
		int page;

		public LoadConversationHistoryAsync(int page) {
			this.page = page;
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			try {
				user = sessionManager.getUserDetails();
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("token", user
						.get(SessionManager.KEY_TOKEN)));
				nameValuePairs
						.add(new BasicNameValuePair("friend_id", username));
				nameValuePairs.add(new BasicNameValuePair("page", "" + page));

				JSONParser jsonParser = new JSONParser(getActivity().getApplicationContext());
				jsonObject = jsonParser.makeHttpRequest(SERVER_HOST
						+ "conversation_detail", "POST", nameValuePairs);
				
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
					if (result.has("chatters")) {
						arrayResultChatter = result.getJSONArray("chatters");
					}
					if (result.has("chat_lines")) {
						arrayResultMessage = result.getJSONArray("chat_lines");
					}
					if ("error".equals(authen_status)) {
						lvMessages.onRefreshComplete();
					} else if ("fail".equals(authen_status)) {
						lvMessages.onRefreshComplete();
					} else if ("ok".equals(authen_status)) {
						if ("error".equals(status)) {
							messageListAdapter = new MessageListAdapter(
									getActivity()
											.getApplicationContext(),
									R.layout.chat_row_left_text_layout,
									messages, getActivity());
							lvMessages.setAdapter(messageListAdapter);
							lvMessages.onRefreshComplete();
						} else if ("ok".equals(status)) {
							chatters = jsonToListChatter(arrayResultChatter);
							LinkedList<Message> messagesTemp = jsonToListMessage(arrayResultMessage);
							System.out.println("Tracking number of messages: "
									+ messagesTemp.size());
							if(chatters.size() > 0){
								for (Chatter chatter : chatters) {
									if (chatter.user_id.equals(username)) {
										messageListAdapter
												.setAvatar_friend(chatter.avatar);
									}
								}
							}
							if (messagesTemp.size() > 0) {
								if (page == 1) {
									//messages = messagesTemp;
									messages.addAll(messagesTemp);
									messageListAdapter.notifyDataSetChanged();
									lvMessages.getRefreshableView().setSelection(messageListAdapter.getCount()-1);
								} else if (page == 2) {
									for (int i = 0; i < messagesTemp.size(); i++) {
										messages.addFirst(messagesTemp
												.get(messagesTemp.size()
														- (i + 1)));
									}
									messageListAdapter.notifyDataSetChanged();
									lvMessages.onRefreshComplete();
								}
							} else {
								lvMessages.onRefreshComplete();
							}

						}
					}
				} else {
					Toast.makeText(
							getActivity(),
							"Error occur due to server's failures! Please try later!",
							Toast.LENGTH_LONG).show();
					if (page == 2) {
						lvMessages.onRefreshComplete();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public ArrayList<Chatter> jsonToListChatter(JSONArray jsonArray) {
		ArrayList<Chatter> result = new ArrayList<MessagingFragment.Chatter>();
		Chatter chatter = null;
		try {
			if (jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					chatter = new Chatter();
					chatter.user_id = object.getString("user_id");
					chatter.avatar = object.getString("avatar");
					result.add(chatter);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

	public LinkedList<Message> jsonToListMessage(JSONArray jsonArray) {
		LinkedList<Message> result = new LinkedList<Message>();
		Message mess = null;
		try {
			if (jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.getJSONObject(jsonArray
							.length() - (i + 1));
					mess = new Message();
					mess.setUsername(object.getString("chatter"));
					mess.setMessage(object.getString("message"));
					String date = DateTimeHelper.contextDateTime(DateTimeHelper
							.convertStringServerTimeToLocalDate(object
									.getString("time")));
					mess.setSentDate(date);
					result.add(mess);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

}
