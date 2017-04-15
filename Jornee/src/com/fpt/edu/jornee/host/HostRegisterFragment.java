/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HostRegisterFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.host;

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
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fpt.edu.bean.TipsQuestion;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.account.LoginFragment;
import com.fpt.edu.jornee.customview.CNImageView;
import com.fpt.edu.jornee.exception.JorneeEmptyInputException;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.ImageLoader;
import com.fpt.edu.jornee.utils.JSONParser;
import com.fpt.edu.jornee.utils.UniversalImageHelper;
import com.fpt.edu.jornee.utils.Validator;

public class HostRegisterFragment extends Fragment {

	Button btnHostRegister;
	Context context;
	CNImageView userAvatar;
	TextView username;
	TextView userfullname;
	AutoCompleteTextView location;
	String country = "";
	String administrative_area_level_1 = "";
	String administrative_area_level_2 = "";
	String locality = "";

	Validator validator;
	String location_lat = "";
	String location_lng = "";
	String[] descriptions;
	TextWatcher textWatcher;
	TextView freefrom;
	TextView freeto;
	EditText phone;
	EditText aboutyourself;
	EditText aboutyourplace;
	TextView errorText;
	ImageLoader imageLoader;
	RelativeLayout relativeLayout;
	HashMap<String, String> user;
	DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();
	// SimpleDateFormat fmtDateAndTime = new
	// SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
	JSONParser parser;
	SessionManager session;
	Calendar dateAndTime = Calendar.getInstance();

	private Handler mHandler = new Handler();
	String str;
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

	private void updateLabelStartTime() {
		freefrom.setText(fmtDateAndTime.format(dateAndTime.getTime()));
	}

	private void updateLabelEndTime() {
		freeto.setText(fmtDateAndTime.format(dateAndTime.getTime()));
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_host_register_layout, container, false);
		Bundle bundle = getArguments();
		if (bundle != null
				&& bundle.containsKey(Constant.FRAGMENT_TITLE_BUNDLE)) {
			getActivity().setTitle(
					bundle.getString(Constant.FRAGMENT_TITLE_BUNDLE));
		}
		setHasOptionsMenu(true);
		context = rootView.getContext();
		imageLoader = new ImageLoader(context);
		if (isAdded()) {
			session = new SessionManager(getActivity().getApplicationContext());
			validator = new Validator(getActivity().getApplicationContext());
		}
		user = session.getUserDetails();
		parser = new JSONParser(getActivity().getApplicationContext());
		btnHostRegister = (Button) rootView.findViewById(R.id.btnHostRegister);
		userAvatar = (CNImageView) rootView.findViewById(R.id.userAvatar);
		if (isAdded()) {
			UniversalImageHelper.loadImage(getActivity()
					.getApplicationContext(), userAvatar, Constant.SERVER_HOST
					+ "thumbnail_" + user.get(SessionManager.KEY_AVATAR));
		}
		username = (TextView) rootView.findViewById(R.id.username);
		username.setText(user.get(SessionManager.KEY_USERNAME));
		userfullname = (TextView) rootView.findViewById(R.id.userfullname);
		relativeLayout = (RelativeLayout) rootView
				.findViewById(R.id.controllRelativeLayout);
		relativeLayout.bringToFront();

		errorText = (TextView) rootView
				.findViewById(R.id.errorTextViewHostRegister);
		location = (AutoCompleteTextView) rootView.findViewById(R.id.location);
		final Runnable mFilterTask = new Runnable() {

			@Override
			public void run() {

				ParserAutoCompleteTask parserTask = new ParserAutoCompleteTask();
				parserTask.execute(str.toString());
			}
		};
		textWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

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

		freefrom = (TextView) rootView.findViewById(R.id.free_from);
		freefrom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chooseFromDate();
			}
		});

		freeto = (TextView) rootView.findViewById(R.id.free_to);
		freeto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				chooseToDate();
			}
		});
		phone = (EditText) rootView.findViewById(R.id.phone);
		aboutyourself = (EditText) rootView.findViewById(R.id.about_yourself);
		aboutyourplace = (EditText) rootView
				.findViewById(R.id.about_your_place);
		aboutyourplace.setVisibility(View.GONE);

		btnHostRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				try {
					if (validateDateTime(freefrom.getText().toString(), freeto
							.getText().toString())) {
						if (validator.validateStartEnd(freefrom.getText()
								.toString(), freeto.getText().toString())) {

							HashMap<String, String> user = session
									.getUserDetails();
							String token = user.get(SessionManager.KEY_TOKEN);

							String[] arrayOfInput = new String[12];

							arrayOfInput[0] = token;
							arrayOfInput[1] = country;
							arrayOfInput[2] = administrative_area_level_1;
							arrayOfInput[3] = administrative_area_level_2;
							arrayOfInput[4] = locality;

							arrayOfInput[5] = freefrom.getText().toString();
							arrayOfInput[6] = freeto.getText().toString();
							arrayOfInput[7] = phone.getText().toString();
							arrayOfInput[8] = aboutyourself.getText()
									.toString();
							arrayOfInput[9] = aboutyourplace.getText()
									.toString();

							arrayOfInput[10] = location_lat;
							arrayOfInput[11] = location_lng;

							LoadResultHostRegistration resultChangePassword = new LoadResultHostRegistration();
							resultChangePassword.execute(arrayOfInput);

						} else {
							errorText
									.setText("Free from must be smaller than free to");

						}

					} else {
						errorText.setText("Please select your free time ");
					}
				} catch (Exception e) {
					errorText.setText("Free from must be smaller than free to");

				}

			}
		});

		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (isAdded()) {
			getActivity().getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();

		location.addTextChangedListener(textWatcher);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	public void chooseFromDate() {
		new DatePickerDialog(context, fromTime, dateAndTime.get(Calendar.YEAR),
				dateAndTime.get(Calendar.MONTH),
				dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
	}

	public void chooseToDate() {
		new DatePickerDialog(context, toTime, dateAndTime.get(Calendar.YEAR),
				dateAndTime.get(Calendar.MONTH),
				dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
	}

	public boolean validateLocationField(String country, String lv1,
			String locality) throws JorneeEmptyInputException {
		if (validator.validateEmptyInput(country)
				&& validator.validateEmptyInput(lv1)
				&& validator.validateEmptyInput(locality)) {

			return true;

		}

		return false;
	}

	public boolean validateDateTime(String start, String end) {

		if ("Click to set".equals(start) && "Click to set".equals(end)) {
			return false;

		}
		return true;
	}

	private class LoadResultHostRegistration extends
			AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... strs) {

			JSONObject jObject = null;
			try {
				jObject = new JSONObject();

				// Building the url to the web service
				String url = Constant.SERVER_HOST + "host_register";

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						10);

				nameValuePairs.add(new BasicNameValuePair("token", strs[0]));
				nameValuePairs.add(new BasicNameValuePair("country", strs[1]));
				nameValuePairs.add(new BasicNameValuePair(
						"administrative_area_level_1", strs[2]));
				nameValuePairs.add(new BasicNameValuePair(
						"administrative_area_level_2", strs[3]));
				nameValuePairs.add(new BasicNameValuePair("locality", strs[4]));
				nameValuePairs.add(new BasicNameValuePair("active_start",
						strs[5]));
				nameValuePairs
						.add(new BasicNameValuePair("active_end", strs[6]));
				nameValuePairs.add(new BasicNameValuePair("phone", strs[7]));
				nameValuePairs.add(new BasicNameValuePair("description",
						strs[8]));
				nameValuePairs.add(new BasicNameValuePair("place_about",
						strs[9]));

				jObject = parser.makeHttpRequest(url, "POST", nameValuePairs);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			return jObject;
		}

		@Override
		protected void onPostExecute(JSONObject result) {

			if (null != result) {
				try {
					if ("ok".equals(result.getString("authen_status"))) {

						if ("ok".equals(result.getString("status"))) {

							JSONArray array = result
									.getJSONArray("tip_question");
							ArrayList<TipsQuestion> data = new ArrayList<TipsQuestion>();

							for (int i = 0; i < array.length(); i++) {

								TipsQuestion question = new TipsQuestion();
								question.setContent(((JSONObject) array.get(i))
										.getString("content"));

								data.add(question);
							}

							Bundle extras1 = new Bundle();
							extras1.putParcelableArrayList("arraylist", data);

							if (isAdded()) {
								Fragment fragment0 = new HostAddTipsFragment();

								extras1.putString(
										Constant.FRAGMENT_TITLE_BUNDLE,
										"Add Tips");
								fragment0.setArguments(extras1);

								MainActivity activity = (MainActivity) getActivity();
								activity.replaceFragment(fragment0);
							}

							session.changeIsHostStatus("true");

						} else if ("fail".equals(result.getString("status"))) {

							errorText.setText(context.getResources().getString(
									R.string.error_general_error_message));
						} else if ("error".equals(result.getString("status"))) {

							errorText.setText(context.getResources().getString(
									R.string.error_general_error_message));
						}

					} else if ("fail".equals(result.getString("authen_status"))) {
						callLogin();
						errorText.setText(context.getResources().getString(
								R.string.error_invalid_authentication));
					} else if ("error"
							.equals(result.getString("authen_status"))) {

						errorText.setText(context.getResources().getString(
								R.string.error_general_error_message));
					}
				} catch (JSONException e) {
					e.printStackTrace();
					errorText.setText(

					context.getResources().getString(
							R.string.error_general_error_message));

				}
			}
			super.onPostExecute(result);
		}
	}

	public void callLogin() {
		Fragment fragment0 = new LoginFragment();
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.content_frame, fragment0);
		fragmentTransaction.commit();
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

				locality = result.get(0).get("locality_short_name");
				administrative_area_level_1 = result.get(0).get(
						"administrative_area_level_1_short_name");
				administrative_area_level_2 = result.get(0).get(
						"administrative_area_level_2_short_name");
				country = result.get(0).get("country_short_name");

				location_lat = result.get(0).get("location_lat");
				location_lng = result.get(0).get("location_lng");
			}

			super.onPostExecute(result);
		}

	}

	private class ParserAutoCompleteTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		@Override
		protected List<HashMap<String, String>> doInBackground(String... place) {

			List<HashMap<String, String>> places = null;

			try {

				// Obtain browser key from https://code.google.com/apis/console

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
						3);

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
			if (result != null) {
				descriptions = new String[result.size()];

				for (int i = 0; i < result.size(); i++) {

					descriptions[i] = result.get(i).get("description");

				}

				if (isAdded()) {
					AutocompleteAdapter autocompleteAdapter = new AutocompleteAdapter(
							getActivity(), 0, descriptions);
					autocompleteAdapter.notifyDataSetChanged();

					location.setAdapter(autocompleteAdapter);
				}
				location.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						aboutyourplace.setVisibility(View.VISIBLE);
						Address address;
						try {
							address = getCoordinate((String) arg0
									.getItemAtPosition(arg2));

							locality = address.getLocality();
							administrative_area_level_1 = address
									.getAdminArea();

							administrative_area_level_2 = address
									.getSubAdminArea();
							country = address.getCountryCode();
							location_lat = address.getLatitude() + "";
							location_lng = address.getLongitude() + "";
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				});

			}
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

				address.put("location_lat", object.getJSONObject("geometry")
						.getJSONObject("location").getString("lat"));
				address.put("location_lng", object.getJSONObject("geometry")
						.getJSONObject("location").getString("lng"));
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

	class AutocompleteAdapter extends ArrayAdapter<String> {

		public AutocompleteAdapter(Context context, int resource,
				String[] objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount() {
			return descriptions.length;
		}

		@Override
		public String getItem(int position) {
			return descriptions[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View vi = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.location_list_history_options, null);

			TextView textview = (TextView) vi
					.findViewById(R.id.location_list_history_options_textView);

			TextView textView01 = (TextView) vi
					.findViewById(R.id.TextView01History);
			TextView textView02 = (TextView) vi
					.findViewById(R.id.TextView02History);

			textview.setBackgroundColor(Color.parseColor("#1f90b7"));
			textView01.setBackgroundColor(Color.parseColor("#1f90b7"));
			textView02.setBackgroundColor(Color.parseColor("#1f90b7"));

			textview.setTextColor(Color.parseColor("#FFFFFF"));
			textview.setText(descriptions[position]);
			return vi;
		}

	}
}
