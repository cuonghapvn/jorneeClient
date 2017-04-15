/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HostProfileFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.host;

import static com.fpt.edu.jornee.utils.Constant.SERVER_HOST;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView.Validator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.account.LoginFragment;
import com.fpt.edu.jornee.account.UserProfileFragment;
import com.fpt.edu.jornee.customview.CNImageView;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.ImageLoader;
import com.fpt.edu.jornee.utils.JSONParser;
import com.fpt.edu.jornee.utils.UniversalImageHelper;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class HostProfileFragment extends Fragment {

	TextView txtUsername;
	RatingBar ratingBarView;
	TextView txtReputation;
	CNImageView userAvatarHostProfile;
	Button btnProfile;
	Button btnContact;
	ImageView btnRateActionHostprofile;
	TextView txtLocation;
	TextView txtActiveStatus;
	TextView txtFreeFrom;
	TextView txtFreeTo;
	TextView dialogFreefrom;
	TextView dialogFreeto;
	LinearLayout thumbnail;
	LinearLayout linearLayout1;

	com.fpt.edu.jornee.utils.Validator validator;
	ViewPager pager;
	TabPageIndicator indicator;
	ProgressBar progressBar1;

	private Handler mHandler = new Handler();
	String str;
	TextWatcher textWatcher;

	FragmentActivity activity;
	DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();

	// SimpleDateFormat fmtDateAndTime = new
	// SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");

	AutoCompleteTextView location;
	JSONArray ratingResults;
	ImageLoader imageLoader;
	SessionManager session;
	ProgressDialog progressDialog;

	Context context1;
	String country = "";
	String administrative_area_level_1 = "";
	String administrative_area_level_2 = "";
	String locality = "";
	Dialog editLocationDialog;
	Dialog editTimeDialog;
	// RelativeLayout content;
	ScrollView content;
	private static final String[] CONTENT = new String[] { "Introduce", "Tips",
			"Feedback" };

	Context context;

	JSONParser parser;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_host_profile_layout,
				container, false);

		Bundle bundle = getArguments();
		if (bundle != null
				&& bundle.containsKey(Constant.FRAGMENT_TITLE_BUNDLE)) {
			getActivity().setTitle(
					bundle.getString(Constant.FRAGMENT_TITLE_BUNDLE));
		}
		content = (ScrollView) rootView
				.findViewById(R.id.host_profile_scroll_view);

		content.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						Rect r = new Rect();
						// r will be populated with the coordinates of your view
						// that area still visible.
						content.getWindowVisibleDisplayFrame(r);

						int screenHeight = content.getRootView().getHeight();
						int heightDiff = screenHeight - (r.bottom - r.top);
						boolean visible = heightDiff > screenHeight / 3;

					}
				});
		if (isAdded()) {
			context = getActivity().getApplicationContext();
			activity = getActivity();
			validator = new com.fpt.edu.jornee.utils.Validator(context);

		}
		context1 = rootView.getContext();
		txtUsername = (TextView) rootView
				.findViewById(R.id.txtUsernameHostProfile);
		progressBar1 = (ProgressBar) rootView.findViewById(R.id.progressBar1);
		progressBar1.bringToFront();
		thumbnail = (LinearLayout) rootView.findViewById(R.id.thumbnail);
		txtUsername.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				content.post(new Runnable() {
					public void run() {
						content.scrollTo(0, content.getBottom());

					}
				});
			}
		});
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

		userAvatarHostProfile = (CNImageView) rootView
				.findViewById(R.id.userAvatarHostProfile);
		btnProfile = (Button) rootView.findViewById(R.id.btnProfileHostProfile);
		btnProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Fragment fragment0 = new UserProfileFragment();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.USERNAME_BUNDLE, session
						.getUserDetails().get(SessionManager.KEY_USERNAME));
				bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "User Profile");
				fragment0.setArguments(bundle);
				MainActivity activity = (MainActivity) getActivity();
				activity.replaceFragment(fragment0);
			}
		});
		btnContact = (Button) rootView.findViewById(R.id.btnContactHostProfile);
		if (isAdded()) {
			btnContact.setBackgroundDrawable(getActivity().getResources()
					.getDrawable(R.drawable.btn_gray_button));
		}
		txtLocation = (TextView) rootView
				.findViewById(R.id.txtLocationHostProfile);

		txtLocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				editLocationDialog = new Dialog(activity);

				editLocationDialog.getWindow().setBackgroundDrawable(
						new ColorDrawable(android.graphics.Color.TRANSPARENT));
				editLocationDialog
						.setContentView(R.layout.dialog_host_profile_edit_location);
				location = (AutoCompleteTextView) editLocationDialog
						.findViewById(R.id.hostProfileEditLocationAutocompleteTextView);

				final Runnable mFilterTask = new Runnable() {

					@Override
					public void run() {

						ParserAutoCompleteTask parserTask = new ParserAutoCompleteTask();
						parserTask.execute(str.toString());
					}
				};
				textWatcher = new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {

						str = s.toString();
						mHandler.removeCallbacks(mFilterTask);
						mHandler.postDelayed(mFilterTask, 2000);
					}
				};
				location.addTextChangedListener(textWatcher);
				location.setThreshold(2);
				TextView btnSubmit = (TextView) editLocationDialog
						.findViewById(R.id.hostProfileEditLocationBtnSubmit);

				TextView btnCancel = (TextView) editLocationDialog
						.findViewById(R.id.hostProfileEditLocationBtnCancel);
				final EditText aboutPlace = (EditText) editLocationDialog
						.findViewById(R.id.hostProfileEditLocationAboutPlace);
				btnCancel.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						editLocationDialog.dismiss();
					}
				});
				btnSubmit.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							if (validator.validateEmptyInput(country)) {
								String location[] = new String[5];
								location[0] = locality;
								location[1] = administrative_area_level_1;
								location[2] = administrative_area_level_2;
								location[3] = country;
								location[4] = aboutPlace.getText().toString();
								LoadEditLocationResult editLocationResult = new LoadEditLocationResult();
								editLocationResult.execute(location);
							} else {
								Toast.makeText(context,
										"Please select location ",
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							Toast.makeText(context, "Please select location ",
									Toast.LENGTH_SHORT).show();

						}

					}
				});
				editLocationDialog.show();
			}
		});

		txtActiveStatus = (TextView) rootView
				.findViewById(R.id.txtActivateStatusHostProfile);

		txtActiveStatus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ("Activated".equals(txtActiveStatus.getText().toString())) {
					LoadEditStatusDeactivate deactivate = new LoadEditStatusDeactivate();
					deactivate.execute();
				} else {
					LoadEditStatusActivate activate = new LoadEditStatusActivate();
					activate.execute();
				}
			}
		});

		txtFreeTo = (TextView) rootView.findViewById(R.id.txtFreeToHostProfile);
		txtFreeTo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				editFreetime();
			}
		});

		txtFreeFrom = (TextView) rootView
				.findViewById(R.id.txtFreeFromHostProfile);

		txtFreeFrom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				editFreetime();
			}
		});
		setHasOptionsMenu(true);
		parser = new JSONParser(context);

		pager = (ViewPager) rootView.findViewById(R.id.pager);
		indicator = (TabPageIndicator) rootView.findViewById(R.id.indicator);
		FragmentStatePagerAdapter adapter = new TapPageAdapter(
				getChildFragmentManager());
		pager.setAdapter(adapter);
		indicator.setViewPager(pager);
		linearLayout1 = (LinearLayout) rootView
				.findViewById(R.id.linearLayout1);
		btnRateActionHostprofile = (ImageView) rootView
				.findViewById(R.id.btnRateActionHostprofile);

		btnRateActionHostprofile.setVisibility(View.GONE);
		linearLayout1.setVisibility(View.GONE);
		imageLoader = new ImageLoader(context);
		session = new SessionManager(context);
		txtUsername.setVisibility(View.GONE);
		ratingBarView.setVisibility(View.GONE);
		txtReputation.setVisibility(View.GONE);
		userAvatarHostProfile.setVisibility(View.GONE);
		btnProfile.setVisibility(View.GONE);
		btnContact.setVisibility(View.GONE);
		txtLocation.setVisibility(View.GONE);
		txtActiveStatus.setVisibility(View.GONE);
		txtFreeFrom.setVisibility(View.GONE);
		txtFreeTo.setVisibility(View.GONE);
		thumbnail.setVisibility(View.GONE);
		// dialogFreefrom.setVisibility(View.GONE);
		// dialogFreeto.setVisibility(View.GONE);

		pager.setVisibility(View.GONE);
		indicator.setVisibility(View.GONE);
		LoadHostProfile loadHostProfile = new LoadHostProfile();
		loadHostProfile.execute();

		return rootView;
	}

	private void editFreetime() {
		editTimeDialog = new Dialog(activity);

		editTimeDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		editTimeDialog
				.setContentView(R.layout.dialog_host_profile_edit_free_time);
		dialogFreefrom = (TextView) editTimeDialog
				.findViewById(R.id.host_profile_edit_time_free_from);
		dialogFreeto = (TextView) editTimeDialog
				.findViewById(R.id.host_profile_edit_time_free_to);
		TextView btnSubmit = (TextView) editTimeDialog
				.findViewById(R.id.host_profile_edit_time_btnSubmit);

		dialogFreefrom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				chooseFromDate();
			}
		});
		dialogFreeto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				chooseToDate();
			}
		});

		btnSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (!("Click to set".equals(dialogFreefrom.getText()
							.toString()))
							&& !("Click to set".equals(dialogFreeto.getText()
									.toString()))) {
						if (validator.validateStartEnd(dialogFreefrom.getText()
								.toString(),
								(dialogFreeto.getText().toString()))) {
							LoadEditTime editTime = new LoadEditTime();
							String[] time = new String[2];
							time[0] = dialogFreefrom.getText().toString();
							time[1] = dialogFreeto.getText().toString();
							editTime.execute(time);
						} else {

							Toast.makeText(context,
									"From date must be smaller than to day ",
									Toast.LENGTH_LONG).show();
						}
					} else {

						Toast.makeText(context, "Please set your free time ",
								Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {

					Toast.makeText(context,
							"From date must be smaller than to day ",
							Toast.LENGTH_LONG).show();

				}
			}
		});

		editTimeDialog.show();
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
				return HostProfileViewIntroduceFragment.newInstance();
			case 1:
				return HostProfileViewTipsFragment.newInstance();

			case 2:
				return HostProfileViewFeedbackFragment.newInstance();
			}
			return null;

		}

		@Override
		public int getIconResId(int index) {
			return 0;

		}
	}

	private class LoadHostProfile extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... input) {

			JSONObject jsonObject = null;

			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);

				nameValuePairs.add(new BasicNameValuePair("token", session
						.getUserDetails().get(SessionManager.KEY_TOKEN)));

				jsonObject = parser.makeHttpRequest(SERVER_HOST
						+ "host_profile", "POST", nameValuePairs);

				return jsonObject;

			} catch (Exception exception) {

			}

			return jsonObject;

		}

		@Override
		protected void onPostExecute(JSONObject result) {

			if (null != result) {
				try {
					if ("ok".equals(result.getString("authen_status"))) {

						if ("ok".equals(result.getString("status"))) {

							btnRateActionHostprofile
									.setVisibility(View.VISIBLE);
							linearLayout1.setVisibility(View.VISIBLE);
							txtUsername.setVisibility(View.VISIBLE);
							ratingBarView.setVisibility(View.VISIBLE);
							txtReputation.setVisibility(View.VISIBLE);
							userAvatarHostProfile.setVisibility(View.VISIBLE);
							btnProfile.setVisibility(View.VISIBLE);
							// btnContact.setVisibility(View.VISIBLE);
							txtLocation.setVisibility(View.VISIBLE);
							txtActiveStatus.setVisibility(View.VISIBLE);
							txtFreeFrom.setVisibility(View.VISIBLE);
							txtFreeTo.setVisibility(View.VISIBLE);
							thumbnail.setVisibility(View.VISIBLE);
							// dialogFreefrom.setVisibility(View.GONE);
							// dialogFreeto.setVisibility(View.GONE);

							pager.setVisibility(View.VISIBLE);
							indicator.setVisibility(View.VISIBLE);
							progressBar1.setVisibility(View.GONE);

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
								UniversalImageHelper
										.loadImage(
												getActivity()
														.getApplicationContext(),
												userAvatarHostProfile,
												Constant.SERVER_HOST
														+ "thumbnail_"
														+ session
																.getUserDetails()
																.get(SessionManager.KEY_AVATAR));

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

				}

			}
		}
	}

	private class ParserAutoCompleteTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		@Override
		protected List<HashMap<String, String>> doInBackground(String... place) {

			List<HashMap<String, String>> places = null;

			try {

				String input = "input=" + URLEncoder.encode(place[0], "utf-8");

				// place type to be searched
				String types = "types=geocode";

				// Sensor enabled
				String sensor = "sensor=false";

				// Building the parameters to the web service
				String parameters = input + "&" + types + "&" + sensor + "&"
						+ Constant.API_KEY;

				// Output format
				String output = "json";

				// Building the url to the web service
				String url = Constant.URL_AUTOCOMPLETE + output + "?"
						+ parameters;

				JSONObject jObject = new JSONObject();
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);

				nameValuePairs.add(new BasicNameValuePair("", ""));

				jObject = parser.makeHttpRequest(url, "POST", nameValuePairs);
				// Getting the parsed data as a List construct
				places = parseAutocompleteJSonObject(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}

			return places;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			System.out
					.println("HostOtherProfileFragment.ParserAutoCompleteTask.onPostExecute()");
			if (result != null) {
				String[] colorStrings = new String[result.size()];

				for (int i = 0; i < result.size(); i++) {

					colorStrings[i] = result.get(i).get("description");

				}

				if (isAdded()) {
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getActivity().getApplicationContext(),
							android.R.layout.simple_list_item_1, colorStrings);

					// set adapter for the auto complete fields
					location.setAdapter(adapter);

				}
				// specify the minimum type of characters before drop-down list
				// is
				// shown
				location.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						// LoadAddressComponent loadAddressComponent = new
						// LoadAddressComponent();
						// loadAddressComponent.execute((String) arg0
						// .getItemAtPosition(arg2));

						Address address;

						try {
							address = getCoordinate((String) arg0
									.getItemAtPosition(arg2));

							locality = address.getLocality();

							// result.get(i).get("locality_short_name");
							administrative_area_level_1 = address
									.getAdminArea();

							// result.get(i).get(
							// "administrative_area_level_1_short_name");

							administrative_area_level_2 = address
									.getSubAdminArea();
							// result.get(i).get(
							// "administrative_area_level_2_short_name");
							country = address.getCountryCode();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});

			}
		}
	}

	private class LoadAddressComponent extends
			AsyncTask<String, Void, List<HashMap<String, String>>> {
		List<HashMap<String, String>> addressComponents = null;

		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... addresses) {
			try {
				// Obtain browser key from https://code.google.com/apis/console

				String address = "address="
						+ URLEncoder.encode(addresses[0], "utf-8");

				// Sensor enabled
				String sensor = "sensor=false";

				// Building the parameters to the web service
				String parameters = address + "&" + sensor + "&";

				// Output format
				String output = "json";

				// Building the url to the web service
				String url = Constant.URL_ADDRESS_COMPONENT + output + "?"
						+ parameters;

				JSONObject jObject = new JSONObject();
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);

				nameValuePairs.add(new BasicNameValuePair("", ""));

				jObject = parser.makeHttpRequest(url, "POST", nameValuePairs);
				// Getting the parsed data as a List construct
				addressComponents = parseAddressComponentJSonObject(jObject);

			} catch (Exception exception) {
				Log.d("Exception", exception.toString());
				exception.printStackTrace();

			}
			return addressComponents;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			for (int i = 0; i < result.size(); i++) {

				locality = result.get(i).get("locality_short_name");
				administrative_area_level_1 = result.get(i).get(
						"administrative_area_level_1_short_name");
				administrative_area_level_2 = result.get(i).get(
						"administrative_area_level_2_short_name");
				country = result.get(i).get("country_short_name");

			}

			super.onPostExecute(result);
		}
	}

	private class LoadEditLocationResult extends
			AsyncTask<String, Void, JSONObject> {

		String updatedLocation = "";

		@Override
		protected JSONObject doInBackground(String... location) {

			JSONObject jsonObject = null;
			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						6);

				nameValuePairs.add(new BasicNameValuePair("token", session
						.getUserDetails().get(SessionManager.KEY_TOKEN)));
				nameValuePairs.add(new BasicNameValuePair("country",
						location[3]));
				nameValuePairs.add(new BasicNameValuePair(
						"administrative_area_level_1", location[1]));
				nameValuePairs.add(new BasicNameValuePair(
						"administrative_area_level_2", location[2]));
				nameValuePairs.add(new BasicNameValuePair("locality",
						location[0]));
				nameValuePairs.add(new BasicNameValuePair("about_place",
						location[4]));

				if ("".equals(location[0]) || location[0] == null) {

					if ("".equals(location[1]) || location[1] == null) {

						if ("".equals(location[2]) || location[2] == null) {
							if ("".equals(location[3]) || location[3] == null) {
								updatedLocation = "undefined";

							} else {
								updatedLocation = location[3];

							}

						} else {
							updatedLocation = location[2];

						}

					} else {
						updatedLocation = location[1];
					}

				} else {
					updatedLocation = location[0];
				}

				jsonObject = parser.makeHttpRequest(Constant.SERVER_HOST
						+ "edit_host_location", "POST", nameValuePairs);
				// Getting the parsed data as a List construct

			} catch (Exception e) {
				Log.d("Exception", e.toString());

				return null;
			}
			return jsonObject;
		}

		@Override
		protected void onPostExecute(JSONObject result) {

			if (null != result) {
				try {
					if ("ok".equals(result.getString("authen_status"))) {

						if ("ok".equals(result.getString("status"))) {

							txtLocation.setText(updatedLocation);
							editLocationDialog.dismiss();

						} else if ("fail".equals(result.getString("status"))) {

						} else if ("error".equals(result.getString("status"))) {

						}

					} else if ("fail".equals(result.getString("authen_status"))) {
						callLogin();
					} else if ("error"
							.equals(result.getString("authen_status"))) {

					}
				} catch (JSONException e) {
				}

			} else {

			}
		}
	}

	private class LoadEditStatusActivate extends
			AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... location) {

			JSONObject jsonObject = null;
			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);

				nameValuePairs.add(new BasicNameValuePair("token", session
						.getUserDetails().get(SessionManager.KEY_TOKEN)));

				jsonObject = parser.makeHttpRequest(Constant.SERVER_HOST
						+ "host_activate", "POST", nameValuePairs);
				// Getting the parsed data as a List construct

			} catch (Exception e) {
				Log.d("Exception", e.toString());

				return null;
			}
			return jsonObject;
		}

		@Override
		protected void onPostExecute(JSONObject result) {

			if (null != result) {
				try {
					if ("ok".equals(result.getString("authen_status"))) {

						if ("ok".equals(result.getString("status"))) {
							txtActiveStatus.setText("Activated");

						} else if ("fail".equals(result.getString("status"))) {

						} else if ("error".equals(result.getString("status"))) {

						}

					} else if ("fail".equals(result.getString("authen_status"))) {
						callLogin();
					} else if ("error"
							.equals(result.getString("authen_status"))) {

					}
				} catch (JSONException e) {

				}

			}
		}
	}

	private class LoadEditStatusDeactivate extends
			AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... location) {

			JSONObject jsonObject = null;
			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);

				nameValuePairs.add(new BasicNameValuePair("token", session
						.getUserDetails().get(SessionManager.KEY_TOKEN)));

				jsonObject = parser.makeHttpRequest(Constant.SERVER_HOST
						+ "host_deactivate", "POST", nameValuePairs);

			} catch (Exception e) {
				Log.d("Exception", e.toString());

				return null;
			}
			return jsonObject;
		}

		@Override
		protected void onPostExecute(JSONObject result) {

			if (null != result) {
				try {
					if ("ok".equals(result.getString("authen_status"))) {

						if ("ok".equals(result.getString("status"))) {
							txtActiveStatus.setText("Deactivated");

						} else if ("fail".equals(result.getString("status"))) {

						} else if ("error".equals(result.getString("status"))) {

						}

					} else if ("fail".equals(result.getString("authen_status"))) {
						callLogin();
					} else if ("error"
							.equals(result.getString("authen_status"))) {

					}
				} catch (JSONException e) {

				}

			} else {

			}
		}
	}

	private class LoadEditTime extends AsyncTask<String, Void, JSONObject> {

		String updatedTimeFrom = "";

		String updatedTimeTo = "";

		@Override
		protected JSONObject doInBackground(String... time) {

			JSONObject jsonObject = null;
			try {

				System.out
						.println("HostOtherProfileFragment.LoadEditTime.doInBackground()");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);

				nameValuePairs.add(new BasicNameValuePair("token", session
						.getUserDetails().get(SessionManager.KEY_TOKEN)));

				nameValuePairs.add(new BasicNameValuePair("active_start",
						time[0]));
				nameValuePairs
						.add(new BasicNameValuePair("active_end", time[1]));

				updatedTimeFrom = time[0] + " - ";

				updatedTimeTo = time[1];
				jsonObject = parser.makeHttpRequest(Constant.SERVER_HOST
						+ "edit_active_time", "POST", nameValuePairs);
				// Getting the parsed data as a List construct

			} catch (Exception e) {
				Log.d("Exception", e.toString());

				return null;
			}
			return jsonObject;
		}

		@Override
		protected void onPostExecute(JSONObject result) {

			if (null != result) {
				try {
					if ("ok".equals(result.getString("authen_status"))) {

						if ("ok".equals(result.getString("status"))) {

							txtFreeFrom.setText(updatedTimeFrom);
							txtFreeTo.setText(updatedTimeTo);

							editTimeDialog.dismiss();
						} else if ("fail".equals(result.getString("status"))) {
						} else if ("error".equals(result.getString("status"))) {
						}

					} else if ("fail".equals(result.getString("authen_status"))) {
						callLogin();
					} else if ("error"
							.equals(result.getString("authen_status"))) {

					}
				} catch (JSONException e) {

				}

			} else {

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

	public List<HashMap<String, String>> parseAutocompleteJSonObject(
			JSONObject jObject) {
		List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
		JSONArray jPlaces = null;

		try {
			/** Retrieves all the elements in the 'places' array */
			jPlaces = jObject.getJSONArray("predictions");
			int placesCount = jPlaces.length();

			/** Taking each place, parses and adds to list object */
			for (int i = 0; i < placesCount; i++) {
				HashMap<String, String> place = new HashMap<String, String>();

				/** Call getPlace with place JSON object to parse the place */
				String id = "";
				String reference = "";
				String description = "";
				String offset = "";
				JSONArray JAOffset = null;
				description = ((JSONObject) jPlaces.get(i))
						.getString("description");
				id = ((JSONObject) jPlaces.get(i)).getString("id");
				reference = ((JSONObject) jPlaces.get(i))
						.getString("reference");
				// offset = jPlace.getJSONObject("terms").getString("value");
				JAOffset = ((JSONObject) jPlaces.get(i)).getJSONArray("terms");
				offset = JAOffset.getJSONObject(0).getString("value");
				place.put("offsetValue", offset);
				place.put("description", description);
				place.put("_id", id);
				place.put("reference", reference);
				placesList.add(place);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return placesList;
	}

	public List<HashMap<String, String>> parseAddressComponentJSonObject(
			JSONObject jObject) {
		List<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
		JSONArray jPlaces = null;

		try {
			/** Retrieves all the elements in the 'results' array */
			jPlaces = jObject.getJSONArray("results");
			int placesCount = jPlaces.length();

			/** Taking each place, parses and adds to list object */
			for (int i = 0; i < placesCount; i++) {
				HashMap<String, String> address = new HashMap<String, String>();

				JSONObject object = (JSONObject) jPlaces.get(0);
				JSONArray array = object.getJSONArray("address_components");
				for (int j = 0; j < array.length(); j++) {
					if ("locality".equals(array.getJSONObject(j)
							.getJSONArray("types").get(0))) {
						address.put("locality_long_name", array
								.getJSONObject(j).getString("long_name"));
						address.put("locality_short_name",
								array.getJSONObject(j).getString("short_name"));
					} else if ("administrative_area_level_2".equals(array
							.getJSONObject(j).getJSONArray("types").get(0))) {
						address.put("administrative_area_level_2_long_name",
								array.getJSONObject(j).getString("long_name"));
						address.put("administrative_area_level_2_short_name",
								array.getJSONObject(j).getString("short_name"));
					} else if ("administrative_area_level_1".equals(array
							.getJSONObject(j).getJSONArray("types").get(0))) {
						address.put("administrative_area_level_1_long_name",
								array.getJSONObject(j).getString("long_name"));
						address.put("administrative_area_level_1_short_name",
								array.getJSONObject(j).getString("short_name"));
					} else if ("country".equals(array.getJSONObject(j)
							.getJSONArray("types").get(0))) {
						address.put("country_long_name", array.getJSONObject(j)
								.getString("long_name"));
						address.put("country_short_name", array
								.getJSONObject(j).getString("short_name"));
					}
				}
				resultList.add(address);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return resultList;
	}

	private void updateLabelStartTime() {
		dialogFreefrom.setText(fmtDateAndTime.format(dateAndTime.getTime()));
	}

	private void updateLabelEndTime() {
		dialogFreeto.setText(fmtDateAndTime.format(dateAndTime.getTime()));
	}

	Calendar dateAndTime = Calendar.getInstance();
	DatePickerDialog.OnDateSetListener fromTime = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			dateAndTime.set(Calendar.YEAR, year);
			dateAndTime.set(Calendar.MONTH, monthOfYear);
			dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateLabelStartTime();
		}
	};

	DatePickerDialog.OnDateSetListener toTime = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			dateAndTime.set(Calendar.YEAR, year);
			dateAndTime.set(Calendar.MONTH, monthOfYear);
			dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateLabelEndTime();
		}
	};

	public void chooseFromDate() {
		new DatePickerDialog(context1, fromTime,
				dateAndTime.get(Calendar.YEAR),
				dateAndTime.get(Calendar.MONTH),
				dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
	}

	public void chooseToDate() {
		new DatePickerDialog(context1, toTime, dateAndTime.get(Calendar.YEAR),
				dateAndTime.get(Calendar.MONTH),
				dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

		}
		// Checks whether a hardware keyboard is available
		if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {

		} else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {

		}
	}

	public String getAddress(double lat, double lon) throws IOException {
		Geocoder geocoder = new Geocoder(getActivity());
		String result;
		result = null;
		try {
			if (isAdded()) {
				List<Address> addressList = geocoder.getFromLocation(lat, lon,
						1);

				if (addressList != null && addressList.size() > 0) {
					Address resultAddress = addressList.get(0);
					StringBuilder addressString = new StringBuilder();
					for (int i = 0; i <= resultAddress.getMaxAddressLineIndex(); i++) {
						addressString.append(resultAddress.getAddressLine(i));
						if (i < resultAddress.getMaxAddressLineIndex()) {
							addressString.append(", ");
						}
					}
					result = addressString.toString();
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		return result;
	}

	public Address getCoordinate(String inputAddress) throws IOException {

		Address result = null;
		if (isAdded()) {

			List<Address> addresses = null;
			result = null;
			Geocoder geocoder = new Geocoder(getActivity());
			addresses = geocoder.getFromLocationName(inputAddress, 1);
			if (addresses.size() > 0) {
				result = addresses.get(0);
			}
		}

		return result;

	}

}
