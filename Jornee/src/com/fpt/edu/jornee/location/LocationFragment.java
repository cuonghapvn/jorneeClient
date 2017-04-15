/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: LocationFragment.java
 * Copyright � 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.location;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.bean.HostInLocation;
import com.fpt.edu.bean.Location;
import com.fpt.edu.bean.TipsQuestion;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.account.LoginFragment;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.JSONParser;

public class LocationFragment extends Fragment {

	AutoCompleteTextView location;
	String country;
	String administrative_area_level_1;
	String administrative_area_level_2;
	String locality;
	String location_lat;
	String location_lng;
	JSONParser parser;
	Context context;
	SessionManager session;
	String[] locations;
	ProgressDialog progressDialog;
	Location prcLocation;
	TextWatcher textWatcher;
	Dialog dialogSelectLanguage;
	String str;

	// TextView btnSearch;
	private Handler mHandler = new Handler();
	String[] descriptions;
	// ArrayAdapter<String> adapter;
	ListView listViewprediction;
	TextView txtSearch;
	ImageButton locationBtnIplace;
	ImageButton locationBtnHost;
	ImageButton locationBtnTip;
	ImageButton locationBtnHistory;
	ProgressBar progressBar1Locationhistory;

	public LocationFragment() {

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if (bundle != null
				&& bundle.containsKey(Constant.FRAGMENT_TITLE_BUNDLE)) {
			getActivity().setTitle(
					bundle.getString(Constant.FRAGMENT_TITLE_BUNDLE));
		}
		View rootView = inflater.inflate(R.layout.fragment_location_layout,
				container, false);

		setHasOptionsMenu(true);

		progressBar1Locationhistory = (ProgressBar) rootView
				.findViewById(R.id.progressBar1Locationhistory);
		progressBar1Locationhistory.setVisibility(View.GONE);
		locationBtnHistory = (ImageButton) rootView
				.findViewById(R.id.locationBtnHistory);
		locationBtnHost = (ImageButton) rootView
				.findViewById(R.id.locationBtnHost);
		locationBtnIplace = (ImageButton) rootView
				.findViewById(R.id.locationBtnIplace);
		locationBtnTip = (ImageButton) rootView
				.findViewById(R.id.locationBtnTip);

		locationBtnHistory.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				location.setFocusable(true);
				location.setFocusableInTouchMode(true);
				location.requestFocus();
				if (prcLocation != null && locations.length != 0) {

					dialogSelectLanguage = new Dialog(getActivity());
					dialogSelectLanguage
							.setContentView(R.layout.dialog_location_select_language);

					dialogSelectLanguage.getWindow().setBackgroundDrawable(
							new ColorDrawable(
									android.graphics.Color.TRANSPARENT));
					final Spinner spinner = (Spinner) dialogSelectLanguage
							.findViewById(R.id.location_select_language_spinner);
					TextView btnSubmit = (TextView) dialogSelectLanguage
							.findViewById(R.id.location_select_language_btnSubmit);

					spinner.setBackgroundColor(getActivity().getResources()
							.getColor(android.R.color.background_light));
					final List<String> list = new ArrayList<String>();
					list.add("Tiếng Việt");
					list.add("English");
					BaseAdapter adapter = new BaseAdapter() {

						@Override
						public int getCount() {
							return list.size();
						}

						@Override
						public String getItem(int position) {
							if ("Tiếng Việt".equals(list.get(position))) {
								return "vi";
							} else {
								if ("English".equals(list.get(position))) {
									return "en";
								} else {
									return "";
								}
							}
						}

						@Override
						public long getItemId(int position) {
							return 0;
						}

						@Override
						public View getView(int position, View convertView,
								ViewGroup parent) {
							View vi = LayoutInflater
									.from(parent.getContext())
									.inflate(
											R.layout.location_list_history_options,
											null);

							TextView textview = (TextView) vi
									.findViewById(R.id.location_list_history_options_textView);

							textview.setText(list.get(position));
							return vi;
						}

					};
					if (isAdded()) {
						spinner.setAdapter(adapter);

					}
					btnSubmit.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							LoadResultListHistory loadResultListHistory = new LoadResultListHistory();

							locations[6] = String.valueOf(spinner
									.getSelectedItem());

							loadResultListHistory.execute(locations);

						}
					});

					dialogSelectLanguage.show();

				} else {
					Toast.makeText(getActivity(), "Please select location ",
							Toast.LENGTH_LONG).show();
				}

			}
		});
		locationBtnHost.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				location.setFocusable(true);
				location.setFocusableInTouchMode(true);
				location.requestFocus();
				if (prcLocation != null && locations.length != 0) {

					LoadResultListHost loadResultListHost = new LoadResultListHost();
					loadResultListHost.execute(locations);
				}

			}
		});
		locationBtnIplace.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				location.setFocusable(true);
				location.setFocusableInTouchMode(true);
				location.requestFocus();
				LoadResultInterestingPlace interestingPlace = new LoadResultInterestingPlace();
				interestingPlace.execute(locations);

			}
		});
		locationBtnTip.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				location.setFocusable(true);
				location.setFocusableInTouchMode(true);
				location.requestFocus();
				if (prcLocation != null && locations.length != 0) {
					LoadResultListQuestionTips listQuestionTips = new LoadResultListQuestionTips();
					listQuestionTips.execute(locations);

				}

			}
		});

		context = getActivity().getApplicationContext();
		parser = new JSONParser(context);
		session = new SessionManager(context);
		location = (AutoCompleteTextView) rootView
				.findViewById(R.id.autocomplete_location_search);
		locations = new String[9];

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
				mHandler.postDelayed(mFilterTask, 1000);
			}
		};
		location.setThreshold(2);

		location.addTextChangedListener(textWatcher);

		txtSearch = (TextView) rootView.findViewById(R.id.txtSearch);
		txtSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				ParserAutoCompleteTask parserTask = new ParserAutoCompleteTask();
				parserTask.execute(location.getText().toString());
			}
		});
		listViewprediction = (ListView) rootView
				.findViewById(R.id.listViewprediction);

		locationBtnIplace.setVisibility(View.GONE);
		locationBtnHost.setVisibility(View.GONE);
		locationBtnTip.setVisibility(View.GONE);
		locationBtnHistory.setVisibility(View.GONE);

		return rootView;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {

		location.addTextChangedListener(textWatcher);
		super.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.outside, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	private class ParserAutoCompleteTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		@Override
		protected List<HashMap<String, String>> doInBackground(String... place) {

			List<HashMap<String, String>> places = null;

			try {
				String input = "input=" + URLEncoder.encode(place[0], "utf-8");

				// place type to be searched
				String types = "types=(regions)";

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
					listViewprediction.setAdapter(autocompleteAdapter);
					listViewprediction
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {

									InputMethodManager imm = (InputMethodManager) context
											.getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(
											location.getWindowToken(), 0);
									location.setText(arg0.getItemAtPosition(
											arg2).toString());
									location.setFocusable(false);

									locationBtnIplace
											.setVisibility(View.VISIBLE);
									locationBtnHost.setVisibility(View.VISIBLE);
									locationBtnTip.setVisibility(View.VISIBLE);
									locationBtnHistory
											.setVisibility(View.VISIBLE);

									try {
										Address address = getCoordinate((String) arg0
												.getItemAtPosition(arg2));

										locality = address.getLocality();

										administrative_area_level_1 = address
												.getAdminArea();

										administrative_area_level_2 = address
												.getSubAdminArea();
										country = address.getCountryCode();
										location_lat = address.getLatitude()
												+ "";

										location_lng = address.getLongitude()
												+ "";

										locations[0] = locality;
										locations[1] = administrative_area_level_1;
										locations[2] = administrative_area_level_2;
										locations[3] = country;
										locations[4] = location_lat;
										locations[5] = location_lng;
										prcLocation = new Location();
										prcLocation
												.setAdministrative_area_level_1(administrative_area_level_1);
										prcLocation
												.setAdministrative_area_level_2(administrative_area_level_2);
										prcLocation.setCountry(country);
										prcLocation.setLocality(locality);
										prcLocation
												.setLocation_lat(location_lat);
										prcLocation
												.setLocation_lng(location_lng);

									} catch (IOException e) {
										e.printStackTrace();
									}

								}
							});

				}
				// specify the minimum type of characters before drop-down list
				// is
				// shown
				location.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						location.setFocusable(false);

						try {
							Address address = getCoordinate((String) arg0
									.getItemAtPosition(arg2));

							locality = address.getLocality();

							administrative_area_level_1 = address
									.getAdminArea();

							administrative_area_level_2 = address
									.getSubAdminArea();
							country = address.getCountryCode();
							location_lat = address.getLatitude() + "";

							location_lng = address.getLongitude() + "";

							locations[0] = locality;
							locations[1] = administrative_area_level_1;
							locations[2] = administrative_area_level_2;
							locations[3] = country;
							locations[4] = location_lat;
							locations[5] = location_lng;
							prcLocation = new Location();
							prcLocation
									.setAdministrative_area_level_1(administrative_area_level_1);
							prcLocation
									.setAdministrative_area_level_2(administrative_area_level_2);
							prcLocation.setCountry(country);
							prcLocation.setLocality(locality);
							prcLocation.setLocation_lat(location_lat);
							prcLocation.setLocation_lng(location_lng);

							// btnInterestPlace.setEnabled(true);
							// btnHistory.setEnabled(true);
							// btnHost.setEnabled(true);
							// btnTips.setEnabled(true);
						} catch (IOException e) {
							// TODO Auto-generated catch block
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
				address.put("formatted_address",
						object.getString("formatted_address"));

				address.put("location_lat", object.getJSONObject("geometry")
						.getJSONObject("location").getString("lat"));
				address.put("location_lng", object.getJSONObject("geometry")
						.getJSONObject("location").getString("lng"));

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

	public void callLogin() {
		Fragment fragment = new LoginFragment();

		MainActivity activity = (MainActivity) getActivity();
		activity.replaceFragment(fragment);
	}

	private class LoadResultListQuestionTips extends
			AsyncTask<String, Void, JSONObject> {

		@Override
		protected void onPreExecute() {

			progressBar1Locationhistory.setVisibility(View.VISIBLE);
			location.setVisibility(View.GONE);
			txtSearch.setVisibility(View.GONE);
			listViewprediction.setVisibility(View.GONE);
			locationBtnHistory.setVisibility(View.GONE);
			locationBtnHost.setVisibility(View.GONE);
			locationBtnIplace.setVisibility(View.GONE);
			locationBtnTip.setVisibility(View.GONE);
			super.onPreExecute();
		}

		@Override
		protected JSONObject doInBackground(String... location) {

			JSONObject jsonObject = null;

			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);

				nameValuePairs.add(new BasicNameValuePair("token", session
						.getUserDetails().get(SessionManager.KEY_TOKEN)));

				jsonObject = parser.makeHttpRequest(Constant.SERVER_HOST
						+ "all_questions", "POST", nameValuePairs);
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

							progressBar1Locationhistory
									.setVisibility(View.GONE);
							JSONArray array = result.getJSONArray("questions");

							ArrayList<TipsQuestion> questions = new ArrayList<TipsQuestion>();

							for (int i = 0; i < array.length(); i++) {

								JSONObject object = array.getJSONObject(i);
								TipsQuestion question = new TipsQuestion();
								question.setQuestionId(object.getString("_id"));
								question.setContent(object.getString("content"));
								questions.add(question);
							}

							Bundle extras1 = new Bundle();
							extras1.putParcelableArrayList("all_questions",
									questions);
							extras1.putParcelable("location_detail",
									prcLocation);
							extras1.putString(Constant.FRAGMENT_TITLE_BUNDLE,
									"All Tips");

							Fragment fragment = new LocationListAllQuestionTips();

							fragment.setArguments(extras1);
							MainActivity activity = (MainActivity) getActivity();
							activity.replaceFragment(fragment);

						} else if ("fail".equals(result.getString("status"))) {

							Toast.makeText(context,
									"This location currently have no host !",
									Toast.LENGTH_LONG).show();

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

	private class LoadResultListHost extends
			AsyncTask<String, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			progressBar1Locationhistory.setVisibility(View.VISIBLE);
			location.setVisibility(View.GONE);
			txtSearch.setVisibility(View.GONE);
			listViewprediction.setVisibility(View.GONE);
			locationBtnHistory.setVisibility(View.GONE);
			locationBtnHost.setVisibility(View.GONE);
			locationBtnIplace.setVisibility(View.GONE);
			locationBtnTip.setVisibility(View.GONE);

			super.onPreExecute();
		}

		@Override
		protected JSONObject doInBackground(String... location) {

			JSONObject jsonObject = null;

			if (location != null) {

				try {

					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							5);

					nameValuePairs.add(new BasicNameValuePair("token", session
							.getUserDetails().get(SessionManager.KEY_TOKEN)));

					nameValuePairs.add(new BasicNameValuePair("country",
							location[3]));
					nameValuePairs.add(new BasicNameValuePair(
							"administrative_area_level_1", location[1]));

					nameValuePairs.add(new BasicNameValuePair("locality",
							location[0]));
					nameValuePairs.add(new BasicNameValuePair("location_lat",
							location[4]));
					nameValuePairs.add(new BasicNameValuePair("location_lng",
							location[5]));

					jsonObject = parser.makeHttpRequest(Constant.SERVER_HOST
							+ "all_hosts", "POST", nameValuePairs);
					// Getting the parsed data as a List construct

				} catch (Exception e) {
					Log.d("Exception", e.toString());

					return null;
				}
			}
			return jsonObject;
		}

		@Override
		protected void onPostExecute(JSONObject result) {

			if (null != result) {
				try {
					if ("ok".equals(result.getString("authen_status"))) {

						if ("ok".equals(result.getString("status"))) {

							progressBar1Locationhistory
									.setVisibility(View.GONE);
							JSONArray array = result
									.getJSONArray("exact_hosts");
							ArrayList<HostInLocation> data = new ArrayList<HostInLocation>();

							for (int i = 0; i < array.length(); i++) {

								HostInLocation host = new HostInLocation();

								host.setStatus(((JSONObject) array.get(i))
										.getString("status"));

								host.setUsername(((JSONObject) array.get(i))
										.getString("username"));

								host.setRegis_location(((JSONObject) array
										.get(i)).getString("regis_location"));
								host.setAvatar(((JSONObject) array.get(i))
										.getString("avatar"));
								data.add(host);
							}

							Fragment fragment = new LocationListAllHost();

							Bundle extras1 = new Bundle();
							extras1.putParcelableArrayList("all_host", data);
							extras1.putString(Constant.FRAGMENT_TITLE_BUNDLE,
									"All Host");

							fragment.setArguments(extras1);

							MainActivity activity = (MainActivity) getActivity();
							activity.replaceFragment(fragment);

						} else if ("fail".equals(result.getString("status"))) {

							Toast.makeText(context,
									"This location currently have no host !",
									Toast.LENGTH_LONG).show();

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

	private class LoadResultListHistory extends
			AsyncTask<String, Void, JSONObject> {

		@Override
		protected void onPreExecute() {

			if (dialogSelectLanguage.isShowing()) {
				dialogSelectLanguage.dismiss();
			}
			progressBar1Locationhistory.setVisibility(View.VISIBLE);
			location.setVisibility(View.GONE);
			txtSearch.setVisibility(View.GONE);
			listViewprediction.setVisibility(View.GONE);
			locationBtnHistory.setVisibility(View.GONE);
			locationBtnHost.setVisibility(View.GONE);
			locationBtnIplace.setVisibility(View.GONE);
			locationBtnTip.setVisibility(View.GONE);

			super.onPreExecute();
		}

		@Override
		protected JSONObject doInBackground(String... location) {

			JSONObject jsonObject = null;

			if (location != null) {

				try {

					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							5);

					nameValuePairs.add(new BasicNameValuePair("token", session
							.getUserDetails().get(SessionManager.KEY_TOKEN)));
					nameValuePairs.add(new BasicNameValuePair("country",
							location[3]));
					nameValuePairs.add(new BasicNameValuePair(
							"administrative_area_level_1", location[1]));

					nameValuePairs.add(new BasicNameValuePair("locality",
							location[0]));
					nameValuePairs.add(new BasicNameValuePair("language",
							location[6]));

					jsonObject = parser.makeHttpRequest(Constant.SERVER_HOST
							+ "place_information_list", "POST", nameValuePairs);
					// Getting the parsed data as a List construct

				} catch (Exception e) {
					Log.d("Exception", e.toString());

					return null;
				}
			}
			return jsonObject;
		}

		@Override
		protected void onPostExecute(JSONObject result) {

			if (null != result) {
				try {
					if ("ok".equals(result.getString("authen_status"))) {

						if ("ok".equals(result.getString("status"))) {

							JSONArray array = result.getJSONArray("results");
							ArrayList<String> arrayStringResult = new ArrayList<String>();
							for (int i = 0; i < array.length(); i++) {

								arrayStringResult.add(i, array.getString(i)
										.toString());

								// TODO load history
							}

							Bundle extras1 = new Bundle();
							extras1.putStringArrayList("string_results",
									arrayStringResult);
							extras1.putString("language", locations[6]);

							extras1.putString(Constant.FRAGMENT_TITLE_BUNDLE,
									"Facts");

							Fragment fragment0 = new LocationListAllHistory();

							fragment0.setArguments(extras1);

							MainActivity activity = (MainActivity) getActivity();
							activity.replaceFragment(fragment0);

						} else if ("fail".equals(result.getString("status"))) {

							Toast.makeText(context,
									"This location currently have no host !",
									Toast.LENGTH_LONG).show();

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

	private class LoadResultInterestingPlace extends
			AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			progressBar1Locationhistory.setVisibility(View.VISIBLE);
			location.setVisibility(View.GONE);
			txtSearch.setVisibility(View.GONE);
			listViewprediction.setVisibility(View.GONE);
			locationBtnHistory.setVisibility(View.GONE);
			locationBtnHost.setVisibility(View.GONE);
			locationBtnIplace.setVisibility(View.GONE);
			locationBtnTip.setVisibility(View.GONE);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... location) {
			JSONObject jsonObject = null;
			if (location != null) {

				try {

					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							5);

					nameValuePairs.add(new BasicNameValuePair("token", session
							.getUserDetails().get(SessionManager.KEY_TOKEN)));

					nameValuePairs.add(new BasicNameValuePair("country",
							location[3]));
					nameValuePairs.add(new BasicNameValuePair(
							"administrative_area_level_1", location[1]));

					nameValuePairs.add(new BasicNameValuePair("locality",
							location[0]));

					jsonObject = parser.makeHttpRequest(Constant.SERVER_HOST
							+ "interesting_places", "POST", nameValuePairs);
					// Getting the parsed data as a List construct

				} catch (Exception e) {
					Log.d("Exception", e.toString());

					return null;
				}
			}
			return jsonObject.toString();
		}

		@Override
		protected void onPostExecute(String str) {

			if (null != str) {

				try {
					JSONObject result = new JSONObject(str);

					if ("ok".equals(result.getString("authen_status"))) {

						if ("ok".equals(result.getString("status"))) {

							progressBar1Locationhistory
									.setVisibility(View.GONE);

							Fragment fragment = new LocationFragmentInterestingPlaceMap();

							Bundle bundle = new Bundle();

							bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,
									"Interesting Place");
							bundle.putString("interest_place_result", str);
							fragment.setArguments(bundle);
							MainActivity activity = (MainActivity) getActivity();
							activity.replaceFragment(fragment);

						} else if ("fail".equals(result.getString("status"))) {

							Toast.makeText(context,
									"This location currently have no host !",
									Toast.LENGTH_LONG).show();

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

		@SuppressLint("ResourceAsColor")
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
