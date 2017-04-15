/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: MessagingActivity.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.message;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.service.textservice.SpellCheckerService.Session;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.bean.Message;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.socketio.SocketIO;
import com.fpt.edu.jornee.socketio.SocketIOConnection;
import com.fpt.edu.jornee.utils.DateTimeHelper;
import com.readystatesoftware.viewbadger.BadgeView;

public class MessagingActivity extends Activity {

	private final static SocketIO mConnection = new SocketIOConnection();
	private TextView mSend;
	static EditText mess;
	static ListView lvMessage;
	static ArrayAdapter<String> adapter;
	static MessageListAdapter messageListAdapter;
	LinkedList<Message> messages;
	static String username = null;
	static HashMap<String, String> user;
	SessionManager sessionManager;
	BadgeView badgeMessage;
	static Button notifCount;
	static int mNotifCount = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		if (bundle.containsKey("username")) {
			username = bundle.getString("username");
		}
		sessionManager = new SessionManager(getApplicationContext());
		user = new HashMap<String, String>();
		user = sessionManager.getUserDetails();
		setTitle(username);
		setContentView(R.layout.chat_view_conversation_layout);
		mSend = (TextView) findViewById(R.id.btnSend);
		mSend.setEnabled(false);
		mess = (EditText) findViewById(R.id.etChatContent);
		lvMessage = (ListView) findViewById(R.id.listConversation);
		adapter = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_list_item_1);
		messages = new LinkedList<Message>();
		messageListAdapter = new MessageListAdapter(getApplicationContext(),
				R.layout.chat_row_left_text_layout, messages, this);
		lvMessage.setAdapter(messageListAdapter);
		final String wsuri = "ws://42.119.6.163:8888";
		System.out.println(wsuri);

		mess.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)) {
					sendMessage(v.getText().toString());
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

		mConnection.connectCustom(wsuri, new SocketIO.ConnectionHandler() {

			@Override
			public void onOpen() {
				alert("Connected to\n" + wsuri);
				mSend.setEnabled(true);
				mConnection.on("server_message", MyEvent.class,
						new SocketIO.EventHandler() {
							public void onEvent(Object event) {

								MyEvent evt = (MyEvent) event;
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
									messageListAdapter.add(message);
									messageListAdapter.notifyDataSetChanged();
									lvMessage.setSelection(messageListAdapter
											.getCount() - 1);
								} else {
									mNotifCount += 1;
									setNotifCount();
								}
							}
						});

				mConnection.on("error", Error.class,
						new SocketIO.EventHandler() {
							public void onEvent(Object event) {
								Error error = (Error) event;
								System.out.println("Error: " + error.message);
							}
						});
			}

			@Override
			public void onClose(int code, String reason) {
				mSend.setEnabled(false);
			}
		}, user.get(SessionManager.KEY_TOKEN));

	}

	@Override
	protected void onDestroy() {
		mConnection.disconnect();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_messaging, menu);
		View count = menu.findItem(R.id.badge).getActionView();
		notifCount = (Button) count.findViewById(R.id.notif_count);
		notifCount.setText(String.valueOf(mNotifCount));
		return super.onCreateOptionsMenu(menu);
	}

	private void setNotifCount() {
		invalidateOptionsMenu();
	}

	public static void sendMessage(String message) {
		SendEvent myclientevent = new SendEvent();
		myclientevent.message = message;
		myclientevent.other_user = username;
		mConnection.emit("client_message", myclientevent);
		Message messageOb = new Message();
		messageOb.setUsername(user.get(SessionManager.KEY_USERNAME));
		messageOb.setMessage(message);
		messageOb.setSentDate(DateTimeHelper.contextDateTime(new Date()));
		messageListAdapter.add(messageOb);
		messageListAdapter.notifyDataSetChanged();
		mess.setText("");
		lvMessage.setSelection(messageListAdapter.getCount() - 1);
	}

	private void alert(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	private static class MyEvent {

		public String message;

		public String from_user;

		public String time;

	}

	private static class SendEvent {

		public String message;

		public String other_user;

	}

	private static class Error {
		public String message;
	}

}
