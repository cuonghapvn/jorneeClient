/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HostOtherProfileFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.otherhost;

import static com.fpt.edu.jornee.utils.Constant.SERVER_HOST;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.account.LoginFragment;
import com.fpt.edu.jornee.account.UserProfileFragment;
import com.fpt.edu.jornee.customview.CNImageView;
import com.fpt.edu.jornee.exception.JorneeEmptyInputException;
import com.fpt.edu.jornee.message.MessagingFragment;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.ImageLoader;
import com.fpt.edu.jornee.utils.JSONParser;
import com.fpt.edu.jornee.utils.UniversalImageHelper;
import com.fpt.edu.jornee.utils.Validator;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class HostOtherProfileFragment extends Fragment {

	TextView txtUsername;
	RatingBar ratingBarView;
	TextView txtReputation;
	CNImageView imgAvatarHostOtherProfile;
	ImageView btnRateAction;
	Button btnProfile;
	Button btnContact;
	TextView txtLocation;
	TextView txtActiveStatus;
	TextView txtFreeFrom;
	TextView txtFreeTo;
	TextView dialogFreefrom;
	TextView dialogFreeto;
	String username;
	FragmentActivity activity;

	DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();

	AutoCompleteTextView location;
	JSONArray ratingResults;
	ImageLoader imageLoader;
	SessionManager session;
	Context context1;
	String country = "";
	String administrative_area_level_1 = "";
	String administrative_area_level_2 = "";
	String locality = "";
	ViewPager pager;
	TabPageIndicator indicator;
	Dialog editLocationDialog;
	Validator validator;
	Dialog editTimeDialog;
	private static final String[] CONTENT = new String[] { "Introduce", "Tips",
			"Feedback" };

	Context context;

	JSONParser parser;
	ProgressBar progressBar1otherprofile;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if (bundle != null && bundle.containsKey("host_username")) {
			username = bundle.getString("host_username");

			getActivity().setTitle(
					bundle.getString(Constant.FRAGMENT_TITLE_BUNDLE));

		}
		if (isAdded()) {
			validator = new Validator(getActivity());
			context = getActivity().getApplicationContext();

			activity = getActivity();
		}
		View rootView = inflater.inflate(
				R.layout.fragment_other_host_profile_layout, container, false);

		progressBar1otherprofile = (ProgressBar) rootView
				.findViewById(R.id.progressBar1otherprofile);
		context1 = rootView.getContext();
		txtUsername = (TextView) rootView
				.findViewById(R.id.txtUsernameHostProfile);

		ratingBarView = (RatingBar) rootView
				.findViewById(R.id.ratingbarViewHostProfile);
		ratingBarView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// custom dialog
				final Dialog dialog = new Dialog(activity);

				dialog.setContentView(R.layout.dialog_host_profile_rate_result);
				dialog.getWindow().setBackgroundDrawable(
						new ColorDrawable(android.graphics.Color.TRANSPARENT));

				RatingBar ratingBar = (RatingBar) dialog
						.findViewById(R.id.hostProfile_everage_rate);
				TextView ratingBar1_number = (TextView) dialog
						.findViewById(R.id.hostProfileRatingBar_1_numberRate);
				TextView ratingBar2_number = (TextView) dialog
						.findViewById(R.id.hostProfileRatingBar_2_numberRate);
				TextView ratingBar3_number = (TextView) dialog
						.findViewById(R.id.hostProfileRatingBar_3_numberRate);
				TextView ratingBar4_number = (TextView) dialog
						.findViewById(R.id.hostProfileRatingBar_4_numberRate);
				TextView ratingBar5_number = (TextView) dialog
						.findViewById(R.id.hostProfileRatingBar_5_numberRate);

				TextView ratingBareverage_number = (TextView) dialog
						.findViewById(R.id.hostProfile_numberRate_everage);

				try {

					ratingBar1_number.setText(ratingResults.getJSONObject(0)
							.getString("one"));
					ratingBar2_number.setText(ratingResults.getJSONObject(1)
							.getString("two"));
					ratingBar3_number.setText(ratingResults.getJSONObject(2)
							.getString("three"));
					ratingBar4_number.setText(ratingResults.getJSONObject(3)
							.getString("four"));
					ratingBar5_number.setText(ratingResults.getJSONObject(4)
							.getString("five"));

					ratingBar.setRating(Float.parseFloat(ratingResults
							.getJSONObject(5).getString("everage")));
					ratingBareverage_number.setText(ratingResults
							.getJSONObject(6).getString("number_of_rate"));
				} catch (Exception e) {
				}

				dialog.show();

				return false;

			}
		});

		txtReputation = (TextView) rootView
				.findViewById(R.id.txtReputationHostProfile);
		btnRateAction = (ImageView) rootView
				.findViewById(R.id.btnRateActionHostprofile);

		btnRateAction.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// custom dialog
				final Dialog dialog = new Dialog(activity);
				dialog.setContentView(R.layout.dialog_host_profile_rate_action);
				dialog.setTitle("Rate action ...");

				// set the custom dialog components - text, image and button
				CNImageView avatar = (CNImageView) dialog
						.findViewById(R.id.hostProfileRateActionAvatar);
				TextView username = (TextView) dialog
						.findViewById(R.id.hostProfileRateActionUsername);
				final RatingBar ratingbar = (RatingBar) dialog
						.findViewById(R.id.hostProfileRateActionRatingBar);
				final EditText summary = (EditText) dialog
						.findViewById(R.id.hostProfileRateActionSumary);
				summary.setHint("Sumary");
				final EditText comment = (EditText) dialog
						.findViewById(R.id.hostProfileRateActionComment);
				comment.setHint("Comment");
				TextView btnSubmit = (TextView) dialog
						.findViewById(R.id.hostProfileBtnSubmit);

				if (isAdded()) {
					UniversalImageHelper.loadImage(
							getActivity().getApplicationContext(),
							avatar,
							Constant.SERVER_HOST
									+ "thumbnail_"
									+ session.getUserDetails().get(
											SessionManager.KEY_AVATAR));

				}
				username.setText(session.getUserDetails().get(
						SessionManager.KEY_USERNAME));

				// if button is clicked, close the custom dialog
				btnSubmit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						try {
							if (validator.validateEmptyInput(summary.getText()
									.toString())
									&& validator.validateEmptyInput(comment
											.getText().toString())) {

								String[] input = new String[3];
								input[0] = ratingbar.getRating() + "";
								input[1] = summary.getText().toString() + "  "
										+ comment.getText().toString();

								LoadRateAction loadRateAction = new LoadRateAction();
								loadRateAction.execute(input);
								dialog.dismiss();
							} else {
								dialog.dismiss();

								Toast.makeText(
										context,
										context.getResources()
												.getString(
														R.string.error_required_field_message),
										Toast.LENGTH_LONG).show();
							}
						} catch (JorneeEmptyInputException e) {
							dialog.dismiss();

							Toast.makeText(
									context,
									context.getResources()
											.getString(
													R.string.error_required_field_message),
									Toast.LENGTH_LONG).show();
						}
					}
				});

				dialog.show();
			}
		});

		imgAvatarHostOtherProfile = (CNImageView) rootView
				.findViewById(R.id.imgAvatarHostOtherProfile);
		btnProfile = (Button) rootView.findViewById(R.id.btnProfileHostProfile);
		btnProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Fragment fragment0 = new UserProfileFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.USERNAME_BUNDLE, username);
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "User Profile");
				fragment0.setArguments(bundle);
				MainActivity activity = (MainActivity) getActivity();
				activity.replaceFragment(fragment0);
			}
		});
		btnContact = (Button) rootView.findViewById(R.id.btnContactHostProfile);
		btnContact.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Fragment fragment = new MessagingFragment();
				MainActivity mainActivity = (MainActivity) getActivity();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.USERNAME_BUNDLE, username);
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, username);
				fragment.setArguments(bundle);
				mainActivity.replaceFragment(fragment);
			}
		});

		txtLocation = (TextView) rootView
				.findViewById(R.id.txtLocationHostProfile);

		txtActiveStatus = (TextView) rootView
				.findViewById(R.id.txtActivateStatusHostProfile);

		txtFreeTo = (TextView) rootView.findViewById(R.id.txtFreeToHostProfile);

		txtFreeFrom = (TextView) rootView
				.findViewById(R.id.txtFreeFromHostProfile);

		setHasOptionsMenu(true);
		parser = new JSONParser(context);

		pager = (ViewPager) rootView.findViewById(R.id.otherHostpager);
		indicator = (TabPageIndicator) rootView
				.findViewById(R.id.otherHostindicator);

		FragmentStatePagerAdapter adapter = new TapPageAdapter(
				getChildFragmentManager());
		pager.setAdapter(adapter);
		indicator.setViewPager(pager);

		imageLoader = new ImageLoader(context);
		session = new SessionManager(context);
		LoadOtherHostProfile loadHostProfile = new LoadOtherHostProfile();
		loadHostProfile.execute();
		return rootView;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	class TapPageAdapter extends FragmentStatePagerAdapter implements
			IconPagerAdapter {

		public TapPageAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length];
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {

			switch (position) {
			case 0:
				// return HostOtherProfileViewIntroduceFragment.newInstance();
				Fragment fragmentHostOtherProfileViewIntroduceFragment = HostOtherProfileViewIntroduceFragment
						.newInstance();
				Bundle bundle = new Bundle();
				bundle.putString("host_username_other", username);
				fragmentHostOtherProfileViewIntroduceFragment
						.setArguments(bundle);
				return fragmentHostOtherProfileViewIntroduceFragment;
			case 1:
				Fragment fragment = HostOtherProfileViewTipsFragment
						.newInstance();
				Bundle bundle1 = new Bundle();
				bundle1.putString("host_username_other", username);
				fragment.setArguments(bundle1);
				return fragment;
				// return HostOtherProfileViewTipsFragment.newInstance();

			case 2:
				// return HostOtherProfileViewFeedbackFragment.newInstance();
				Fragment fragmentHostOtherProfileViewFeedbackFragment = HostOtherProfileViewFeedbackFragment
						.newInstance();
				Bundle bundle2 = new Bundle();
				bundle2.putString("host_username_other", username);
				fragmentHostOtherProfileViewFeedbackFragment
						.setArguments(bundle2);
				return fragmentHostOtherProfileViewFeedbackFragment;
			}
			return null;

		}

		@Override
		public int getIconResId(int index) {
			return 0;

		}
	}

	private class LoadRateAction extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected JSONObject doInBackground(String... input) {

			JSONObject jsonObject = null;

			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);

				nameValuePairs.add(new BasicNameValuePair("token", session
						.getUserDetails().get(SessionManager.KEY_TOKEN)));

				nameValuePairs.add(new BasicNameValuePair("host_username",
						username));
				nameValuePairs.add(new BasicNameValuePair("rate", input[0]));

				nameValuePairs
						.add(new BasicNameValuePair("feedback", input[1]));
				jsonObject = parser.makeHttpRequest(SERVER_HOST + "feedback",
						"POST", nameValuePairs);
				return jsonObject;

			} catch (Exception exception) {
				return jsonObject;

			}

		}

		@Override
		protected void onPostExecute(JSONObject result) {

			if (null != result) {
				try {
					if ("ok".equals(result.getString("authen_status"))) {

						if ("ok".equals(result.getString("status"))) {
							Fragment fragment = new HostOtherProfileFragment();
							Bundle bundle = new Bundle();
							bundle.putString("host_username", username);

							fragment.setArguments(bundle);
							MainActivity activity = (MainActivity) getActivity();
							activity.replaceFragment(fragment);

						}
					}

				} catch (JSONException e) {

				}

			}
		}
	}

	private class LoadOtherHostProfile extends
			AsyncTask<String, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			progressBar1otherprofile.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected JSONObject doInBackground(String... input) {

			JSONObject jsonObject = null;

			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);

				nameValuePairs.add(new BasicNameValuePair("token", session
						.getUserDetails().get(SessionManager.KEY_TOKEN)));

				nameValuePairs.add(new BasicNameValuePair("host_username",
						username));

				jsonObject = parser.makeHttpRequest(SERVER_HOST
						+ "other_host_profile", "POST", nameValuePairs);

				return jsonObject;

			} catch (Exception exception) {

				return jsonObject;

			}

		}

		@Override
		protected void onPostExecute(JSONObject result) {

			if (null != result) {
				try {
					if ("ok".equals(result.getString("authen_status"))) {

						if ("ok".equals(result.getString("status"))) {

							progressBar1otherprofile.setVisibility(View.GONE);
							ratingResults = result.getJSONArray("rate");
							txtUsername.setText(result.getString("username"));
							JSONArray array = result.getJSONArray("rate");
							JSONObject rateobj = array.getJSONObject(5);

							ratingBarView.setRating(Float.parseFloat(result
									.getJSONArray("rate").getJSONObject(5)
									.getString("everage")));
							txtReputation.setText(result.getJSONArray("rate")
									.getJSONObject(6)
									.getString("number_of_rate"));

							if (isAdded()) {
								UniversalImageHelper.loadImage(getActivity()
										.getApplicationContext(),
										imgAvatarHostOtherProfile,
										Constant.SERVER_HOST + "thumbnail_"
												+ result.getString("avatar"));
							}
							txtLocation.setText(result.getString("location"));
							if (result.getBoolean("active_status") == true) {
								txtActiveStatus.setText(context.getResources()
										.getString(
												R.string.host_status_activate));
							} else {
								txtActiveStatus
										.setText(context
												.getResources()
												.getString(
														R.string.host_status_deactivate));
							}

							txtFreeFrom.setText(result
									.getString("active_start"));

							txtFreeTo.setText(result.getString("active_end"));

						}

					}
				} catch (JSONException e) {

					e.printStackTrace();
				}

			}
		}
	}

	public void callLogin() {

		if (isAdded()) {
			Fragment fragment0 = new LoginFragment();

			MainActivity activity = (MainActivity) getActivity();
			activity.replaceFragment(fragment0);
		}
	}

}
