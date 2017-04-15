/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: UserProfileFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.account;

import static com.fpt.edu.jornee.utils.Constant.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.bean.EntryInOutside;
import com.fpt.edu.bean.OutsideActivityBean;
import com.fpt.edu.bean.UserProfile;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.customview.CNSquareImageView;
import com.fpt.edu.jornee.message.MessagingFragment;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.HttpUpload;
import com.fpt.edu.jornee.utils.JSONParser;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

public class UserProfileFragment extends Fragment {
	private static final int GET_PIC_CODE = 1;

	String username;
	TextView tvUsername;
	TextView btnFollow;
	TextView btnChat;
	TextView tvNumberOfFollowing;
	TextView tvNumberOfFollowers;
	TextView txtGender;
	TextView txtDoB;
	TextView txtEmail;
	TextView txtEmpty;
	LinearLayout followInforFollowers;
	LinearLayout followInforFollowing;
	LinearLayout llBtnContact;
	RelativeLayout infoRelaLayout;
	ListView lvJourney;
	JSONObject jsonObjectResult = null;
	LinkedList<OutsideActivityBean> listJourneys;
	UserJourneyAdapter userJourneyAdapter;
	JSONArray arrayJourneysResult = null;
	String authen_status = null;
	String status = null;
	UserProfile userProfile;
	CNSquareImageView userAvatar;
	ImageView ivHostMarker;
	ProgressDialog progressDialog;
	SessionManager sessionManager;
	HashMap<String, String> user;
	ImageView btnEditProfileAvatar;
	String imagePath;
	ProgressBar progressBar;
	ProgressBar loadingProfile;

	public UserProfileFragment() {
		// Empty constructor required for fragment subclasses
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_user_profile_layout,
				container, false);
		tvUsername = (TextView) rootView.findViewById(R.id.tvUsername);
		txtGender = (TextView) rootView.findViewById(R.id.txtGender);
		txtDoB = (TextView) rootView.findViewById(R.id.txtDoB);
		txtEmail = (TextView) rootView.findViewById(R.id.txtEmail);
		btnFollow = (TextView) rootView.findViewById(R.id.btnFollow);
		ivHostMarker = (ImageView) rootView.findViewById(R.id.ivHostMarker);
		lvJourney = (ListView) rootView.findViewById(R.id.listJourney);
		txtEmpty = (TextView) rootView.findViewById(R.id.txtEmpty);
		infoRelaLayout = (RelativeLayout) rootView.findViewById(R.id.infoRelaLayout);
		progressBar = (ProgressBar) rootView.findViewById(R.id.loading);
		loadingProfile = (ProgressBar) rootView.findViewById(R.id.loadingProfile);
		followInforFollowers = (LinearLayout) rootView
				.findViewById(R.id.followInforFollowers);
		followInforFollowing = (LinearLayout) rootView
				.findViewById(R.id.followInforFollowing);
		llBtnContact = (LinearLayout) rootView.findViewById(R.id.llBtnContact);
		btnEditProfileAvatar = (ImageView) rootView
				.findViewById(R.id.btnEditProfileAvatar);
		btnEditProfileAvatar.setVisibility(View.GONE);
		btnFollow.setText("Follow");
		btnChat = (TextView) rootView.findViewById(R.id.btnChat);
		btnChat.setText("Chat");
		tvNumberOfFollowing = (TextView) rootView
				.findViewById(R.id.tvNumberOfFollowing);
		tvNumberOfFollowers = (TextView) rootView
				.findViewById(R.id.tvNumberOfFollowers);
		userAvatar = (CNSquareImageView) rootView.findViewById(R.id.userAvatar);
		Bundle bundle = getArguments();
		if (bundle != null
				&& bundle.containsKey(Constant.FRAGMENT_TITLE_BUNDLE)) {
			getActivity().setTitle(
					bundle.getString(Constant.FRAGMENT_TITLE_BUNDLE));
		}
		if (bundle != null && bundle.containsKey(Constant.USERNAME_BUNDLE)) {
			username = bundle.getString(Constant.USERNAME_BUNDLE);
			tvUsername.setText(username);
			if (bundle.containsKey("imagePath")) {
				imagePath = bundle.getString("imagePath");

			}

		} else {
			username = user.get(SessionManager.KEY_USERNAME);
		}
		userProfile = new UserProfile();
		sessionManager = new SessionManager(getActivity()
				.getApplicationContext());
		user = new HashMap<String, String>();
		user = sessionManager.getUserDetails();
		new GetProfileAsync().execute(username);
		txtEmpty.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loadJourney();
			}
		});
		setHasOptionsMenu(true);
		return rootView;

	}

	private class GetProfileAsync extends AsyncTask<String, Void, JSONObject> {
		JSONObject jsonObject = null;

		@Override
		protected JSONObject doInBackground(String... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("token", user
						.get(SessionManager.KEY_TOKEN)));
				nameValuePairs
						.add(new BasicNameValuePair("username", params[0]));
				JSONParser jsonParser = new JSONParser(getActivity()
						.getApplicationContext());
				jsonObject = jsonParser.makeHttpRequest(SERVER_HOST
						+ SOCIAL_USER_PROFILE, "POST", nameValuePairs);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return jsonObject;
		}

		@Override
		protected void onPreExecute() {
//			progressDialog = ProgressDialog.show(getActivity(), "", "Loading ",
//					true);
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(JSONObject result) {
			try {
//				if (progressDialog.isShowing()) {
//					progressDialog.dismiss();
//				}
				loadingProfile.setVisibility(View.INVISIBLE);
				infoRelaLayout.setVisibility(View.VISIBLE);
				if (result != null) {
					jsonObjectResult = result;
					userProfile = getUserProfileFromJSON(jsonObjectResult);
					if ("error".equals(userProfile.getAuthen_status())) {
						Toast.makeText(getActivity(),
								"Error when we're trying to recognize you!",
								Toast.LENGTH_SHORT).show();
					} else if ("fail".equals(userProfile.getAuthen_status())) {
						Toast.makeText(getActivity(),
								"You must login first to use this function!",
								Toast.LENGTH_SHORT).show();
					} else if ("ok".equals(userProfile.getAuthen_status())) {
						if ("error".equals(userProfile.getStatus())) {
							Toast.makeText(getActivity(),
									"We cannot complete your request!",
									Toast.LENGTH_SHORT).show();
						} else if ("ok".equals(userProfile.getStatus())) {
							// tvUsername.setText(userProfile.getUsername());

							loadJourney();
							tvNumberOfFollowers.setText(userProfile
									.getNum_of_follower());
							followInforFollowers
									.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											viewFollow("Followers");
										}
									});
							tvNumberOfFollowing.setText(userProfile
									.getNum_of_following());
							followInforFollowing
									.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											viewFollow("Following");
										}
									});
							if (imagePath != null) {
								System.out.println(imagePath);
								UniversalImageHelper.loadImage(getActivity()
										.getApplicationContext(), userAvatar,
										imagePath);
							} else {
								String urlAvatar = Constant.SERVER_HOST
										+ "thumbnail_"
										+ userProfile.getAvatar();
								UniversalImageHelper.loadImage(getActivity()
										.getApplicationContext(), userAvatar,
										urlAvatar);
							}
							if (userProfile.isIs_me()) {
								btnChat.setBackgroundDrawable(getActivity()
										.getResources().getDrawable(
												R.drawable.btn_gray_button));
								btnChat.setEnabled(false);
								llBtnContact.setVisibility(View.GONE);

								// btnFollow.setBackgroundDrawable(getActivity()
								// .getResources().getDrawable(
								// R.drawable.btn_gray_button));
								// btnFollow.setEnabled(false);
								btnEditProfileAvatar
										.setVisibility(View.VISIBLE);
								btnEditProfileAvatar
										.setOnClickListener(new View.OnClickListener() {

											@Override
											public void onClick(View v) {
												Intent intent = new Intent(
														Intent.ACTION_PICK);
												intent.setType("image/*");
												startActivityForResult(intent,
														GET_PIC_CODE);

											}
										});
								btnFollow.setText("Edit");
								btnFollow
										.setOnClickListener(new View.OnClickListener() {

											@Override
											public void onClick(View v) {
												Bundle bundle = new Bundle();
												bundle.putString("email",
														userProfile.getEmail());

												bundle.putString("gender",
														userProfile.getGender());

												bundle.putString("dob",
														userProfile.getDob());

												Fragment fragment = new EditProfileFragment();

												fragment.setArguments(bundle);
												MainActivity activity = (MainActivity) getActivity();
												activity.replaceFragment(fragment);

											}
										});
							}
							if (userProfile.isIs_following()) {
								btnFollow.setText("Unfollow");
								btnFollow
										.setBackgroundDrawable(getActivity()
												.getResources()
												.getDrawable(
														R.drawable.btn_green_button_selector));
							}
							if (userProfile.isIs_host()) {
								ivHostMarker.setVisibility(View.VISIBLE);
							}
							if (!"Edit".equals(btnFollow.getText().toString())) {
								btnFollow
										.setOnClickListener(new View.OnClickListener() {
											@SuppressLint("NewApi")
											@Override
											public void onClick(View v) {
												String textOnButton = ((TextView) v)
														.getText().toString();
												((TextView) v).setText("Wait...");
												v.setBackground(getActivity()
														.getApplicationContext()
														.getResources()
														.getDrawable(
																R.drawable.btn_gray_button));
												v.setEnabled(false);
												new FollowAsync().execute(
														userProfile
																.getUsername(),
														textOnButton,
														v);
											}
										});
							}
							btnChat.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									Fragment fragment = new MessagingFragment();
									MainActivity mainActivity = (MainActivity) getActivity();
									Bundle bundle = new Bundle();
									bundle.putString(Constant.USERNAME_BUNDLE,
											userProfile.getUsername());
									bundle.putString(
											Constant.FRAGMENT_TITLE_BUNDLE,
											userProfile.getUsername());
									fragment.setArguments(bundle);
									mainActivity.replaceFragment(fragment);
								}
							});
							if (userProfile.getGender().isEmpty()) {
								txtGender.setText("N/A");
							} else {
								txtGender.setText(userProfile.getGender());
							}
							if (userProfile.getDob().isEmpty()) {
								txtDoB.setText("N/A");
							} else {
								txtDoB.setText(userProfile.getDob());
							}
							if (userProfile.getEmail().isEmpty()) {
								txtDoB.setText("N/A");
							} else {
								txtEmail.setText(userProfile.getEmail());
							}

						}
					}
				} else {
					Toast.makeText(
							getActivity(),
							"Error occur due to server's failures! Please try later!",
							Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	class FollowAsync extends AsyncTask<Object, Void, JSONObject> {
		JSONObject jsonObject = null;
		String action;
		String username;
		int position;
		View inputView;

		@Override
		protected JSONObject doInBackground(Object... params) {
			try {
				action = (String) params[1];
				username =  (String) params[0];
				inputView = (View) params[2];
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs.add(new BasicNameValuePair("token", user
						.get(SessionManager.KEY_TOKEN)));
				nameValuePairs.add(new BasicNameValuePair("friend_id", username));
				String function;
				if ("Follow".equals(action)) {
					function = "follow";
				} else {
					function = "unfollow";
				}

				JSONParser jsonParser = new JSONParser(getActivity().getApplicationContext());
				jsonObject = jsonParser.makeHttpRequest(SERVER_HOST + function, "POST", nameValuePairs);
				
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
					JSONObject jsonObjectResult = result;
					String authen_status = jsonObjectResult
							.getString("authen_status");
					String status = null;
					if (jsonObjectResult.has("status")) {
						status = jsonObjectResult.getString("status");
					}
					if ("error".equals(authen_status)) {
						inputView.setEnabled(true);
						if ("Follow".equals(action)) {
							((TextView) inputView).setText("Follow");
							inputView.setBackground(getActivity().getApplicationContext().getResources()
									.getDrawable(R.drawable.btn_green_button));
						} else {
							((TextView) inputView).setText("Unfollow");
							inputView.setBackground(getActivity().getApplicationContext().getResources()
									.getDrawable(R.drawable.btn_blue_button));
						}
						Toast.makeText(getActivity().getApplicationContext(),
								"Error when we're trying to recognize you!",
								Toast.LENGTH_SHORT).show();
					} else if ("fail".equals(authen_status)) {
						inputView.setEnabled(true);
						if ("Follow".equals(action)) {
							((TextView) inputView).setText("Follow");
							inputView.setBackground(getActivity().getApplicationContext().getResources()
									.getDrawable(R.drawable.btn_green_button));
						} else {
							((TextView) inputView).setText("Unfollow");
							inputView.setBackground(getActivity().getApplicationContext().getResources()
									.getDrawable(R.drawable.btn_blue_button));
						}
						Toast.makeText(getActivity().getApplicationContext(),
								"You must login first to use this function!",
								Toast.LENGTH_SHORT).show();
					} else if ("ok".equals(authen_status)) {
						if ("error".equals(status)) {
							inputView.setEnabled(true);
							if ("Follow".equals(action)) {
								((TextView) inputView).setText("Follow");
								inputView.setBackground(getActivity().getApplicationContext().getResources()
										.getDrawable(R.drawable.btn_green_button));
							} else {
								((TextView) inputView).setText("Unfollow");
								inputView.setBackground(getActivity().getApplicationContext().getResources()
										.getDrawable(R.drawable.btn_blue_button));
							}
							Toast.makeText(getActivity().getApplicationContext(),
									"We cannot complete your request!",
									Toast.LENGTH_SHORT).show();
						} else if ("ok".equals(status)) {
							inputView.setEnabled(true);
							((MainActivity) getActivity()).updateFollowInfor();
							if ("Follow".equals(action)) {
								((TextView) inputView).setText("Unfollow");
								inputView.setBackground(getActivity().getApplicationContext().getResources()
										.getDrawable(R.drawable.btn_green_button));
							} else {
								((TextView) inputView).setText("Follow");
								inputView.setBackground(getActivity().getApplicationContext().getResources()
										.getDrawable(R.drawable.btn_blue_button));
							}
						}
					}
				} else {
					inputView.setEnabled(true);
					if ("Follow".equals(action)) {
						((TextView) inputView).setText("Follow");
						inputView.setBackground(getActivity().getApplicationContext().getResources().getDrawable(
								R.drawable.btn_green_button));
					} else {
						((TextView) inputView).setText("Unfollow");
						inputView.setBackground(getActivity().getApplicationContext().getResources().getDrawable(
								R.drawable.btn_blue_button));
					}
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

	public void loadJourney() {
		new LoadJourneysAsync(username).execute("");
	}

	private class LoadJourneysAsync extends AsyncTask<String, Void, JSONObject> {
		JSONObject jsonObject = null;
		String username;

		public LoadJourneysAsync(String username) {
			this.username = username;
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			try {
				user = sessionManager.getUserDetails();
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("token", user
						.get(SessionManager.KEY_TOKEN)));
				nameValuePairs
						.add(new BasicNameValuePair("username", username));

				JSONParser jsonParser = new JSONParser(getActivity()
						.getApplicationContext());
				jsonObject = jsonParser.makeHttpRequest(SERVER_HOST
						+ "user_journeys", "POST", nameValuePairs);

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return jsonObject;
		}

		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
			txtEmpty.setVisibility(View.INVISIBLE);
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				progressBar.setVisibility(View.INVISIBLE);
				if (result != null) {
					authen_status = result.getString("authen_status");
					if (result.has("status")) {
						status = result.getString("status");
					}
					if (result.has("journeys")) {
						arrayJourneysResult = result.getJSONArray("journeys");
					}
					if ("error".equals(authen_status)) {
					} else if ("fail".equals(authen_status)) {
					} else if ("ok".equals(authen_status)) {
						if ("error".equals(status)) {
							lvJourney.setVisibility(View.INVISIBLE);
							txtEmpty.setVisibility(View.VISIBLE);
							txtEmpty.setText("Cannot get journeys of this user");
						} else if ("ok".equals(status)) {
							LinkedList<OutsideActivityBean> resultList = resultListActivityFromJSON(arrayJourneysResult);
							if (resultList.size() > 0) {
								listJourneys = resultList;
								userJourneyAdapter = new UserJourneyAdapter(
										getActivity().getApplicationContext(),
										listJourneys,
										R.layout.fragment_user_profile_journeys_items,
										getActivity());
								lvJourney.setAdapter(userJourneyAdapter);
							} else {
								lvJourney.setVisibility(View.INVISIBLE);
								txtEmpty.setVisibility(View.VISIBLE);
								if (userProfile.isIs_me()) {
									txtEmpty.setText("You haven't uploaded any journey!");
								} else {
									txtEmpty.setText("This user haven't shared any journey to you!");
								}
							}
						}
					}
				} else {
					lvJourney.setVisibility(View.INVISIBLE);
					txtEmpty.setVisibility(View.VISIBLE);
					txtEmpty.setText("Cannot load journey's data from server!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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

	public void viewFollow(String selectedTabs) {
		Fragment fragment = new ViewFollowFragment();
		MainActivity mainActivity = (MainActivity) getActivity();
		Bundle bundle = new Bundle();
		bundle.putString(Constant.USERNAME_BUNDLE, userProfile.getUsername());
		bundle.putString(ViewFollowFragment.SELECTED_TAB, selectedTabs);
		bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Follow Information");
		fragment.setArguments(bundle);
		mainActivity.replaceFragment(fragment);
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

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// inflater.inflate(R.menu.message, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GET_PIC_CODE && resultCode == Activity.RESULT_OK) {
			Uri imgUri = data.getData();
			String imgPath = getPath(imgUri);
			Log.d("DEBUG", "Choose: " + imgPath);
			HttpUpload upload = new HttpUpload(getActivity(), imgPath,
					getActivity());

			upload.execute();
		}
	}

	// Get the path from Uri
	@SuppressWarnings("deprecation")
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().managedQuery(uri, projection, null, null,
				null);
		getActivity().startManagingCursor(cursor);
		int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

}
