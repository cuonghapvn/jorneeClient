/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: MainActivity.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee;

import static com.fpt.edu.jornee.utils.Constant.SERVER_HOST;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
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

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.bean.OutsideSearchFriendResult;
import com.fpt.edu.bean.SocketMessageEvent;
import com.fpt.edu.bean.SocketNotificationEvent;
import com.fpt.edu.bean.UserProfile;
import com.fpt.edu.jornee.account.LoginFragment;
import com.fpt.edu.jornee.account.SettingFragment;
import com.fpt.edu.jornee.account.UserProfileFragment;
import com.fpt.edu.jornee.customview.CNSquareImageView;
import com.fpt.edu.jornee.help.HelpFragment;
import com.fpt.edu.jornee.host.HostProfileFragment;
import com.fpt.edu.jornee.host.HostRegisterFragment;
import com.fpt.edu.jornee.journey.CreateJourneyFragment;
import com.fpt.edu.jornee.location.LocationFragment;
import com.fpt.edu.jornee.message.MessageFragment;
import com.fpt.edu.jornee.message.MessagingFragment;
import com.fpt.edu.jornee.outside.OutSideFragment;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.socketio.SocketIO;
import com.fpt.edu.jornee.socketio.SocketIO.EventHandler;
import com.fpt.edu.jornee.socketio.SocketIOConnection;
import com.fpt.edu.jornee.sqllite.DatabaseHandler;
import com.fpt.edu.jornee.sync.SyncService;
import com.fpt.edu.jornee.utils.AlertDialogManager;
import com.fpt.edu.jornee.utils.ConnectionDetector;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.MenuNavigationListAdapter;
import com.fpt.edu.jornee.utils.UniversalImageHelper;
import com.fpt.edu.jornee.widget.crouton.Crouton;
import com.fpt.edu.jornee.widget.crouton.Style;
import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends FragmentActivity {

	public static final String FUNCTION_NAME = "function name";

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private Bundle savedBundle;

	private boolean isConnectingToInternet;
	DatabaseHandler databaseHandler = new DatabaseHandler(this);
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mMenuNavigation;
	SessionManager session;
	HashMap<String, String> user;
	private ConnectionDetector connectionDetector;
	Context context;
	public static SocketIO mConnection;
	final String wsuri = "ws" + SERVER_HOST.substring(4);
	AlertDialogManager alert = new AlertDialogManager();
	AsyncTask<Void, Void, Void> mRegisterTask;
	AsyncTask<Void, Void, Void> mUnRegisterTask;
	String regId;
	public int mMessCount = 0;
	public int mNotifCount = 0;
	public boolean isConnecting = false;
	private static long back_pressed;
	String authen_status = null;
	String status = null;
	JSONObject jsonObjectResult = null;
	UserProfile userProfile;
	ArrayList<HashMap<String, String>> functionNames;
	FragmentActivity activity;
	private Crouton infiniteCrouton;
	public static ArrayList<OutsideSearchFriendResult> listFollowing;
	public static ArrayList<OutsideSearchFriendResult> listFollowers;

	static Messenger mService = null;
	static boolean mIsBound;

	private ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = new Messenger(service);
			try {
				Message msg = Message.obtain(null,
						SyncService.MSG_REGISTER_CLIENT);
				mService.send(msg);
			} catch (RemoteException e) {
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	private void checkIfServiceIsRunning() {
		if (SyncService.isRunning()) {
			doBindService();
		} else {
			startService(new Intent(MainActivity.this, SyncService.class));
			mIsBound = true;
		}
	}

	public static void sendMessageToService(int intvaluetosend) {
		if (mIsBound) {
			if (mService != null) {
				try {
					Message msg = Message.obtain(null,
							SyncService.MSG_SET_INT_VALUE, intvaluetosend, 0);
					mService.send(msg);
				} catch (RemoteException e) {
				}
			}
		}
	}

	void doBindService() {
		bindService(new Intent(this, SyncService.class), mServiceConnection,
				Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			if (mService != null) {
				try {
					Message msg = Message.obtain(null,
							SyncService.MSG_UNREGISTER_CLIENT);
					mService.send(msg);
				} catch (RemoteException e) {
				}
			}
			unbindService(mServiceConnection);
			mIsBound = false;
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkIfServiceIsRunning();
		overridePendingTransition(R.anim.slidein, R.anim.slideout);
		setContentView(R.layout.activity_main_layout);
		getActionBar().setDisplayShowHomeEnabled(false);
		context = this;
		connectionDetector = new ConnectionDetector(context);
		isConnectingToInternet = connectionDetector.isConnectingToInternet();
		session = new SessionManager(getApplicationContext());
		user = session.getUserDetails();
		listFollowing = new ArrayList<OutsideSearchFriendResult>();
		listFollowers = new ArrayList<OutsideSearchFriendResult>();
		if (isConnectingToInternet && session.isLoggedIn()) {
			new GetViewFollowAsync().execute();
		}
		socketInit();
		mTitle = mDrawerTitle = getTitle();
		mMenuNavigation = getResources().getStringArray(R.array.menuItems);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		functionNames = new ArrayList<HashMap<String, String>>();

		activity = this;
		for (int i = 0; i < 7; i++) {

			HashMap<String, String> map = new HashMap<String, String>();

			switch (i) {
			case 0:

				break;
			case 1:
				map.put(FUNCTION_NAME, "Home");

				break;
			case 2:
				map.put(FUNCTION_NAME, "Outside");

				break;
			case 3:
				map.put(FUNCTION_NAME, "Message");

				break;
			case 4:
				map.put(FUNCTION_NAME, "Host");

				break;
			case 5:
				map.put(FUNCTION_NAME, "Location");

				break;
//			case 6:
//				map.put(FUNCTION_NAME, "Setting");
//
//				break;
//			case 6:
//				map.put(FUNCTION_NAME, "Help");
//
//				break;
			case 6:
				map.put(FUNCTION_NAME, "Logout");

				break;

			default:
				break;
			}
			functionNames.add(map);
			MenuNavigationListAdapter adapter = new MenuNavigationListAdapter(
					this, functionNames);
			adapter.setLogin(session.isLoggedIn());
			mDrawerList.setAdapter(adapter);
		}
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {

				GetProfileAsync getProfileAsync = new GetProfileAsync();
				String[] input = new String[1];
				input[0] = session.getUserDetails().get(
						SessionManager.KEY_USERNAME);

				getProfileAsync.execute(input);

			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(1);
		}

		if (session.isLoggedIn()) {

			GCMRegistrar.checkDevice(this);

			// Make sure the manifest was properly set - comment out this line
			// while developing the app, then uncomment it when it's ready.
			GCMRegistrar.checkManifest(this);

			registerReceiver(mHandleMessageReceiver, new IntentFilter(
					Constant.DISPLAY_MESSAGE_ACTION));

			regId = GCMRegistrar.getRegistrationId(this);
			if (regId.equals("")) {
				GCMRegistrar.register(this, Constant.SENDER_ID);
			} else { // Device is already registered on GCM
				if (GCMRegistrar.isRegisteredOnServer(this)) {
				
				} else {
					// Try to register again, but not in the UI thread.
					// It's also necessary to cancel the thread onDestroy(),
					// hence the use of AsyncTask instead of a raw thread.
					mRegisterTask = new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							// Register on our server
							// On server creates a new user
							ServerUtilities.register(
									context,
									regId,
									session.getUserDetails().get(
											SessionManager.KEY_TOKEN));
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							mRegisterTask = null;
						}

					};
					mRegisterTask.execute(null, null, null);
				}
			}

		}

	}

	IntentFilter filter = new IntentFilter(
			ConnectivityManager.CONNECTIVITY_ACTION);
	BroadcastReceiver internetReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("app", "Network connectivity change");
			if (intent.getExtras() != null) {
				NetworkInfo ni = (NetworkInfo) intent.getExtras().get(
						ConnectivityManager.EXTRA_NETWORK_INFO);
				if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
					Log.i("app", "Network " + ni.getTypeName() + " connected");
					socketInit();
					if (infiniteCrouton != null) {
						infiniteCrouton.hide();
					}
				}
			}
			if (intent.getExtras().getBoolean(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
				if (infiniteCrouton != null) {
					infiniteCrouton.hide();
				}
				com.fpt.edu.jornee.widget.crouton.Configuration croutonConfiguration = new com.fpt.edu.jornee.widget.crouton.Configuration.Builder()
						.setDuration(7000).build();
				infiniteCrouton = showCrouton("No internet connection",
						Style.ALERT, croutonConfiguration);
				infiniteCrouton.show();
			}
		}
	};

	private Crouton showCrouton(String croutonText, Style croutonStyle,
			com.fpt.edu.jornee.widget.crouton.Configuration configuration) {
		final Crouton crouton;
		crouton = Crouton.makeText(this, croutonText, croutonStyle);
		crouton.setConfiguration(configuration);
		return crouton;

	}

	public ArrayList<OutsideSearchFriendResult> getListFollowing() {
		return listFollowing;
	}

	public void setListFollowing(
			ArrayList<OutsideSearchFriendResult> listFollowing) {
		this.listFollowing = listFollowing;
	}

	public ArrayList<OutsideSearchFriendResult> getListFollowers() {
		return listFollowers;
	}

	public void setListFollowers(
			ArrayList<OutsideSearchFriendResult> listFollowers) {
		this.listFollowers = listFollowers;
	}

	public void saveData(Bundle data) {
		savedBundle = data;
	}

	public Bundle getSavedData() {
		return savedBundle;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		switch (position) {
		case 0:
			clearBackStack();
			if (session.isLoggedIn()) {
				Fragment fragment0 = new UserProfileFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.USERNAME_BUNDLE,
						user.get(SessionManager.KEY_USERNAME));
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "User Profile");
				fragment0.setArguments(bundle);
				replaceFragment(fragment0);
				mDrawerList.setItemChecked(position, true);
				mDrawerLayout.closeDrawer(mDrawerList);
				break;

			} else {
				callLoginFragment();

				mDrawerList.setItemChecked(position, true);
				setTitle(mMenuNavigation[position]);
				mDrawerLayout.closeDrawer(mDrawerList);

				break;

			}

		case 1:
			Fragment fragment0 = new HomeFragment();
			clearBackStack();
			replaceFragment(fragment0);
			mDrawerList.setItemChecked(position, true);
			setTitle(mMenuNavigation[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
			break;

		case 2:
			clearBackStack();
			if (session.isLoggedIn()) {
				Fragment fOutside = new OutSideFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,
						mMenuNavigation[position]);
				fOutside.setArguments(bundle);
				replaceFragment(fOutside);

				mDrawerList.setItemChecked(position, true);
				mDrawerLayout.closeDrawer(mDrawerList);
				break;
			} else {
				callLoginFragment();
				mDrawerList.setItemChecked(position, true);
				setTitle(mMenuNavigation[position]);
				mDrawerLayout.closeDrawer(mDrawerList);
				break;

			}

		case 3:
			clearBackStack();
			if (session.isLoggedIn()) {
				Fragment fMessage = new MessageFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,
						mMenuNavigation[position]);
				fMessage.setArguments(bundle);
				replaceFragment(fMessage);
				mDrawerList.setItemChecked(position, true);
				setTitle(mMenuNavigation[position]);
				mDrawerLayout.closeDrawer(mDrawerList);
				break;
			} else {
				callLoginFragment();
				mDrawerList.setItemChecked(position, true);
				setTitle(mMenuNavigation[position]);
				mDrawerLayout.closeDrawer(mDrawerList);
				break;
			}

		case 4:
			clearBackStack();
			if (session.isLoggedIn()) {
				String isHost = session.getUserDetails().get(
						SessionManager.KEY_IS_HOST);
				if ("true".equals(isHost)) {

					Fragment fHostProfileFragment = new HostProfileFragment();
					Bundle bundle = new Bundle();
					bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,
							"Host Profile");
					fHostProfileFragment.setArguments(bundle);
					replaceFragment(fHostProfileFragment);

					mDrawerList.setItemChecked(position, true);
					mDrawerLayout.closeDrawer(mDrawerList);
					break;

				} else {

					Fragment fHostRegisterFragment = new HostRegisterFragment();
					Bundle bundle = new Bundle();
					bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,
							"Host Register");
					fHostRegisterFragment.setArguments(bundle);
					replaceFragment(fHostRegisterFragment);

					mDrawerList.setItemChecked(position, true);
					mDrawerLayout.closeDrawer(mDrawerList);
					break;

				}
			} else {
				callLoginFragment();

				mDrawerList.setItemChecked(position, true);
				setTitle(mMenuNavigation[position]);
				mDrawerLayout.closeDrawer(mDrawerList);
				break;
			}

		case 5:

			clearBackStack();
			Fragment fLocationFragment = new LocationFragment();
			Bundle bundle = new Bundle();
			bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Location");
			fLocationFragment.setArguments(bundle);
			replaceFragment(fLocationFragment);

			mDrawerList.setItemChecked(position, true);
			mDrawerLayout.closeDrawer(mDrawerList);
			break;

//		case 6:
//			clearBackStack();
//			Fragment fragment4 = new SettingFragment();
//			FragmentManager fragmentManager4 = getSupportFragmentManager();
//			fragmentManager4.beginTransaction()
//					.replace(R.id.content_frame, fragment4).commit();
//			mDrawerList.setItemChecked(position, true);
//			setTitle(mMenuNavigation[position]);
//			mDrawerLayout.closeDrawer(mDrawerList);
//			break;
//		case 6:
//			clearBackStack();
//			Fragment fragment5 = new HelpFragment();
//			FragmentManager fragmentManager5 = getSupportFragmentManager();
//			Bundle bundle = new Bundle();
//			bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Location");
//			fLocationFragment.setArguments(bundle);
//			fragmentManager5.beginTransaction()
//					.replace(R.id.content_frame, fragment5).commit();
//
//			// update selected item and title, then close the drawer
//			mDrawerList.setItemChecked(position, true);
//			setTitle(mMenuNavigation[position]);
//			mDrawerLayout.closeDrawer(mDrawerList);
			
			
//			Fragment fHelpFragment = new HelpFragment();
//			Bundle bundle1 = new Bundle();
//			bundle1.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Location");
//			fHelpFragment.setArguments(bundle1);
//			replaceFragment(fHelpFragment);
//
//			mDrawerList.setItemChecked(position, true);
//			mDrawerLayout.closeDrawer(mDrawerList);
//			break;
		case 6:
			clearBackStack();

			FragmentManager manager = getSupportFragmentManager();
			final String currenToken = user.get(SessionManager.KEY_TOKEN);

			mUnRegisterTask = new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					// Register on our server
					// On server creates a new user
					ServerUtilities.unregister(context, regId, currenToken);
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					mUnRegisterTask = null;
				}

			};
			mUnRegisterTask.execute(null, null, null);
			session.logoutUser(manager);

			finish();
			startActivity(getIntent());
			if (mConnection != null && mConnection.isConnected()) {
				mConnection.disconnect();
			}
			break;
		default:
			break;
		}

	}

	private void callLoginFragment() {
		Fragment fLoginFragment = new LoginFragment();

		Bundle bundle = new Bundle();
		bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Login");
		fLoginFragment.setArguments(bundle);

		replaceFragment(fLoginFragment);

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		CreateJourneyFragment createJourneyFragment = (CreateJourneyFragment) getSupportFragmentManager()
				.findFragmentByTag(CreateJourneyFragment.class.getName());
		OutSideFragment outsideFragment = (OutSideFragment) getSupportFragmentManager()
				.findFragmentByTag(OutSideFragment.class.getName());
		 if (createJourneyFragment != null && createJourneyFragment.isVisible()) {
			createJourneyFragment.showConfirmGoBackDialog();
		} else if (outsideFragment != null && outsideFragment.isVisible()
				&& (outsideFragment.slidingUpPanel.isExpanded() || outsideFragment.slidingUpPanel .isAnchored())) {
			outsideFragment.slidingUpPanel.collapsePane();
		} else {
			if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
				if (back_pressed + 2000 > System.currentTimeMillis()) {
					finish();
				} else {
					Toast.makeText(getBaseContext(),
							"Press once again to exit!", Toast.LENGTH_SHORT)
							.show();
				}
				back_pressed = System.currentTimeMillis();
			} else {
				super.onBackPressed();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		UniversalImageHelper.pause();
		unregisterReceiver(internetReceiver);
		if (mConnection != null && mConnection.isConnected()) {
			mConnection.disconnect();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		UniversalImageHelper.pause();
		registerReceiver(internetReceiver, filter);
		if (mConnection != null && !mConnection.isConnected()
				&& session != null && session.isLoggedIn() && !isConnecting) {
			socketInit();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		UniversalImageHelper.pause();
		if (mConnection != null && !mConnection.isConnected()) {
			socketInit();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		UniversalImageHelper.pause();
		if (mConnection != null && !mConnection.isConnected()) {
			socketInit();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		UniversalImageHelper.pause();
		if (mConnection != null && mConnection.isConnected()) {
			mConnection.disconnect();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		UniversalImageHelper.pause();
		try {
			if (mConnection != null && mConnection.isConnected()) {
				mConnection.disconnect();
			}
			doUnbindService();
		} catch (Throwable t) {
			Log.e("MainActivity", "Failed to unbind from the service", t);
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (mConnection != null && !mConnection.isConnected()) {
			socketInit();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mConnection != null && mConnection.isConnected()) {
			mConnection.disconnect();
		}
	}

	public void replaceFragment(Fragment fragment) {
		UniversalImageHelper.pause();
		String backStateName = fragment.getClass().getName();
		String fragmentTag = backStateName;
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.replace(R.id.content_frame, fragment, fragmentTag);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.addToBackStack(backStateName);
		ft.commit();
	}

	public void replaceFragmentAndDeleteExisting(Fragment fragment) {
		String backStateName = fragment.getClass().getName();
		String fragmentTag = backStateName;
		FragmentManager manager = getSupportFragmentManager();
		manager.popBackStack(backStateName,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		FragmentTransaction ft = manager.beginTransaction();
		ft.replace(R.id.content_frame, fragment, fragmentTag);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.addToBackStack(backStateName);
		ft.commit();
	}

	public void replaceFragmentWithOutBackStack(Fragment fragment) {
		String backStateName = fragment.getClass().getName();
		String fragmentTag = backStateName;
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.replace(R.id.content_frame, fragment, fragmentTag);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();
	}

	public void clearBackStack() {
		FragmentManager fm = getSupportFragmentManager();
		for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
			fm.popBackStack();
		}
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(
					Constant.EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());
			// Showing received message
			// lblMessage.append(newMessage + "\n");
			Toast.makeText(getApplicationContext(),
					"New Message: " + newMessage, Toast.LENGTH_LONG).show();
			// Releasing wake lock
			WakeLocker.release();
		}
	};

	public void socketInit() {
		if (!isConnecting && session != null && session.isLoggedIn()
				&& connectionDetector.isConnectingToInternet()) {
			isConnecting = true;
			mConnection = new SocketIOConnection();
			mConnection.connectCustom(wsuri, new SocketIO.ConnectionHandler() {

				@Override
				public void onOpen() {
					isConnecting = false;
					if (infiniteCrouton != null) {
						infiniteCrouton.hide();
					}
					MessagingFragment messagingFragment = (MessagingFragment) getSupportFragmentManager()
							.findFragmentByTag(MessagingFragment.class.getName());
					if (messagingFragment != null && messagingFragment.isVisible()) {
						messagingFragment.mSend.setEnabled(true);
					}
					mConnection.on("server_message", SocketMessageEvent.class, socketHandlerServerMessage());
					mConnection.on("server_noti", SocketNotificationEvent.class, socketHandlerServerNotification());
					mConnection.on("error", Error.class, new EventHandler() {

						@Override
						public void onEvent(Object event) {
							Error error = (Error) event;
							if (error.message.equals("unauthorized"))
								Toast.makeText(getApplicationContext(),"You have not logged in yet!", Toast.LENGTH_SHORT).show();
							else
								Toast.makeText(getApplicationContext(), "Cannot contact with server!", Toast.LENGTH_SHORT).show();
						}
					});
				}

				@Override
				public void onClose(int code, String reason) {
					isConnecting = false;
				}
			}, user.get(SessionManager.KEY_TOKEN));
		}
	}

	public EventHandler socketHandlerServerMessage() {
		return new SocketIO.EventHandler() {

			@Override
			public void onEvent(Object event) {
				MessagingFragment messagingFragment = (MessagingFragment) getSupportFragmentManager()
						.findFragmentByTag(MessagingFragment.class.getName());
				MessageFragment messageFragment = (MessageFragment) getSupportFragmentManager()
						.findFragmentByTag(MessageFragment.class.getName());
				OutSideFragment outSideFragment = (OutSideFragment) getSupportFragmentManager()
						.findFragmentByTag(OutSideFragment.class.getName());
				if (messagingFragment != null && messagingFragment.isVisible()) {
					MessagingFragment.receiveMessage(event);
				} else if (messageFragment != null && messageFragment.isVisible()) {
					mMessCount += 1;
					messageFragment.updateList(event);
				} else if (outSideFragment != null && outSideFragment.isVisible()) {
					mMessCount += 1;
					outSideFragment.updateBadgeMessage();
				} else {
					mMessCount += 1;
					SocketMessageEvent socketMessageEvent = (SocketMessageEvent) event;
					showMessageCrouton(socketMessageEvent);
				}

			}
		};
	}

	public EventHandler socketHandlerServerNotification() {
		return new SocketIO.EventHandler() {

			@Override
			public void onEvent(Object event) {
				mNotifCount += 1;
				OutSideFragment outSideFragment = (OutSideFragment) getSupportFragmentManager()
						.findFragmentByTag(OutSideFragment.class.getName());
				if (outSideFragment != null && outSideFragment.isVisible()) {
					outSideFragment.updateBadgeNotification();
				} else {
					SocketNotificationEvent socketNotificationEvent = (SocketNotificationEvent) event;
					showNotificationCrouton(socketNotificationEvent);

				}
			}
		};
	}

	private void showNotificationCrouton(SocketNotificationEvent event) {
		View view = getLayoutInflater().inflate(R.layout.notification_inapp_layout, null);
		CNSquareImageView ivSenderAvatar = (CNSquareImageView) view.findViewById(R.id.ivSenderAvatar);
		String link = Constant.SERVER_HOST + "thumbnail_" + event.getAvatar();
		UniversalImageHelper.loadImage(getApplicationContext(), ivSenderAvatar, link);
		TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvTitle.setText("New notification!");
		TextView tvNotiContent = (TextView) view.findViewById(R.id.tvNotiContent);
		tvNotiContent.setText(event.getNotificationMessage());
		TextView btnIgnore = (TextView) view.findViewById(R.id.btnFollow);
		btnIgnore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				infiniteCrouton.hide();
			}
		});
		if (infiniteCrouton != null) {
			infiniteCrouton.cancel();
		}
		infiniteCrouton = Crouton.make(this, view);
		infiniteCrouton.show();
	}

	private void showMessageCrouton(final SocketMessageEvent event) {
		View view = getLayoutInflater().inflate(R.layout.notification_inapp_layout, null);
		CNSquareImageView ivSenderAvatar = (CNSquareImageView) view.findViewById(R.id.ivSenderAvatar);
		String link = Constant.SERVER_HOST + "thumbnail_" + event.avatar;
		UniversalImageHelper.loadImage(getApplicationContext(), ivSenderAvatar, link);
		TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvTitle.setText("New message!");
		TextView tvNotiContent = (TextView) view.findViewById(R.id.tvNotiContent);
		String message = event.from_user + ": " + event.message;
		tvNotiContent.setText(message);
		TextView btnIgnore = (TextView) view.findViewById(R.id.btnFollow);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragment fragment = new MessagingFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.USERNAME_BUNDLE, event.from_user);
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,
						event.from_user);
				fragment.setArguments(bundle);
				replaceFragment(fragment);
				if (infiniteCrouton != null) {
					infiniteCrouton.hide();
				}
			}
		});
		btnIgnore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (infiniteCrouton != null) {
					infiniteCrouton.hide();
				}
			}
		});
		if (infiniteCrouton != null) {
			infiniteCrouton.hide();
		}
		infiniteCrouton = Crouton.make(this, view);
		infiniteCrouton.show();
	}

	private static class Error {
		public String message;
	}

	public void updateFollowInfor() {
		new GetViewFollowAsync().execute();
	}

	private class GetViewFollowAsync extends
			AsyncTask<Object, Void, JSONObject> {
		JSONObject jsonObject = null;
		InputStream is = null;
		String json;
		JSONArray arrayFollowerResult = null;
		JSONArray arrayFollowingResult = null;

		@Override
		protected JSONObject doInBackground(Object... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("token", user
						.get(SessionManager.KEY_TOKEN)));
				nameValuePairs.add(new BasicNameValuePair("username", user
						.get(SessionManager.KEY_USERNAME)));
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(SERVER_HOST + "view_follow");
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();
				jsonObject = new JSONObject(json);
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
					authen_status = result.getString("authen_status");
					if (result.has("status")) {
						status = result.getString("status");
					}
					if (result.has("follower")) {
						arrayFollowerResult = result.getJSONArray("follower");
					}
					if (result.has("following")) {
						arrayFollowingResult = result.getJSONArray("following");
					}
					if ("error".equals(authen_status)) {
					} else if ("fail".equals(authen_status)) {
					} else if ("ok".equals(authen_status)) {
						if ("error".equals(status)) {
						} else if ("ok".equals(status)) {
							listFollowing = OutsideSearchFriendResult
									.resultListFromJSON(arrayFollowingResult);
							listFollowers = OutsideSearchFriendResult
									.resultListFromJSON(arrayFollowerResult);
						}
					}
				} else {
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private class GetProfileAsync extends AsyncTask<String, Void, JSONObject> {
		JSONObject jsonObject = null;
		InputStream is = null;
		String json;

		@Override
		protected JSONObject doInBackground(String... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("token", user
						.get(SessionManager.KEY_TOKEN)));
				nameValuePairs
						.add(new BasicNameValuePair("username", params[0]));
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(SERVER_HOST + "user_profile");
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();
				jsonObject = new JSONObject(json);
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
					jsonObjectResult = result;
					userProfile = getUserProfileFromJSON(jsonObjectResult);

					session.updateUser(userProfile);

					getActionBar().setTitle(mDrawerTitle);
					invalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()

					MenuNavigationListAdapter adapter = new MenuNavigationListAdapter(
							activity, functionNames);
					adapter.setLogin(session.isLoggedIn());
					mDrawerList.setAdapter(adapter);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public UserProfile getUserProfileFromJSON(JSONObject jsonObjectResult) {
		UserProfile userProfile = new UserProfile();
		try {
			userProfile.setAuthen_status(jsonObjectResult
					.getString("authen_status"));
			if (jsonObjectResult.has("status")) {
				userProfile.setStatus(jsonObjectResult.getString("status"));
			}
			if (jsonObjectResult.has("username")) {
				userProfile.setUsername(jsonObjectResult.getString("username"));
			}
			if (jsonObjectResult.has("email")) {
				userProfile.setEmail(jsonObjectResult.getString("email"));
			}
			if (jsonObjectResult.has("dob")) {
				userProfile.setDob(jsonObjectResult.getString("dob"));
			}
			if (jsonObjectResult.has("gender")) {
				userProfile.setGender(jsonObjectResult.getString("gender"));
			}
			if (jsonObjectResult.has("avatar")) {
				userProfile.setAvatar(jsonObjectResult.getString("avatar"));
			}
			if (jsonObjectResult.has("num_of_following")) {
				userProfile.setNum_of_following(jsonObjectResult
						.getString("num_of_following"));
			}
			if (jsonObjectResult.has("num_of_follower")) {
				userProfile.setNum_of_follower(jsonObjectResult
						.getString("num_of_follower"));
			}
			if (jsonObjectResult.has("is_host")) {
				userProfile.setIs_host(jsonObjectResult.getBoolean("is_host"));
			}
			if (jsonObjectResult.has("is_me")) {
				userProfile.setIs_me(jsonObjectResult.getBoolean("is_me"));
			}
			if (jsonObjectResult.has("is_following")) {
				userProfile.setIs_following(jsonObjectResult
						.getBoolean("is_following"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userProfile;
	}

}
