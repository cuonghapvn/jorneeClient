/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: CreateCheckinActivity.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.entry;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fpt.edu.bean.Entry;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.sqllite.DatabaseHandler;
import com.fpt.edu.jornee.utils.DateTimeHelper;
import com.fpt.edu.jornee.utils.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

@SuppressLint("SimpleDateFormat")
public class CreateCheckinActivity extends FragmentActivity {
	GPSTracker gps;
	double latitude;
	double longitude;
	double startLatitude;
	double startLongitude;
	boolean isAlerted = false;
	String action;
	String mapURL;
	String address;
	String addressStart;

	Dialog datetimeDialog;
	DatePicker datePicker;
	TimePicker timePicker;
	Spinner vehicleList;

	Date parsed;

	TextView datetimeInp;
	Spinner startpointInp;
	EditText descriptionInp;
	final SimpleDateFormat displayDateTimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	DatabaseHandler databasehandler;

	GoogleMap googleMap;
	Entry checkin;
	Entry lastCheckin;

	File mapImg;
	String path;
	MarkerOptions marker;
	MarkerOptions markerStart;
	ArrayList<Entry> listCheckin;
	ArrayList<MarkerOptions> listMarkerStart;

	final String[] startPointOptions = { "Current location",
			"Location of last check in" };

	public static final int progress_bar_type = 0;
	private ProgressDialog pDialog;

	GetAddressTask getAddress;
	DownloadImageTask downloadTask;
	CountDownTimer timeout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_checkin_layout);
		this.setTitle("Check in");
		Bundle bundle = getIntent().getExtras();
		action = bundle.getString("action");
		databasehandler = new DatabaseHandler(this);
		datetimeInp = (TextView) findViewById(R.id.checkinDatetimeText);
		startpointInp = (Spinner) findViewById(R.id.checkinStartpointText);
		descriptionInp = (EditText) findViewById(R.id.checkinDescriptionText);
		vehicleList = (Spinner) findViewById(R.id.checkinVehicleList);
		vehicleList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.create_checkin_spinner_item, getResources()
						.getStringArray(R.array.vehicleList)));

		listCheckin = databasehandler.getAllCheckin();
		listMarkerStart = new ArrayList<MarkerOptions>();
		StringTokenizer tokens;
		String first;
		String second;
		double markerLatitude;
		double markerLongtitude;

		for (int i = 0; i < (listCheckin.size() - 1); i++) {
			markerLatitude = listCheckin.get(i).getPosition().latitude;
			markerLongtitude = listCheckin.get(i).getPosition().longitude;

			listMarkerStart.add(new MarkerOptions().position(
					new LatLng(markerLatitude, markerLongtitude)).title(
					displayDateTimeFormat.format(DateTimeHelper
							.convertStringServerTimeToLocalDate(listCheckin
									.get(i).getDateTime()))));

			if (listCheckin.get(i + 1) != null
					&& !listCheckin
							.get(i)
							.getStartPoint()
							.equalsIgnoreCase(
									listCheckin.get(i + 1).getCoordinate())) {
				break;
			}
		}

		startpointInp.setAdapter(new ArrayAdapter<String>(this,
				R.layout.create_checkin_spinner_item, startPointOptions));

		startpointInp
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						switch (position) {
						case 0:
							googleMap.clear();
							startLatitude = latitude;
							startLongitude = longitude;
							break;

						case 1:
							if (!listMarkerStart.isEmpty()
									&& listMarkerStart != null) {
								PolylineOptions rectOptions = new PolylineOptions()
										.width(5).color(Color.BLUE)
										.geodesic(true);
								googleMap.clear();
								for (int i = listMarkerStart.size() - 1; i >= 0; i--) {
									googleMap.addMarker(listMarkerStart.get(i));
									rectOptions.add(listMarkerStart.get(i)
											.getPosition());
								}
								rectOptions
										.add(new LatLng(latitude, longitude));
								googleMap.addPolyline(rectOptions);
								startLatitude = listMarkerStart.get(0)
										.getPosition().latitude;
								startLongitude = listMarkerStart.get(0)
										.getPosition().longitude;
							} else {
								googleMap.clear();
								startLatitude = latitude;
								startLongitude = longitude;
							}
							break;
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		if ("create".equalsIgnoreCase(action)) {
			checkin = new Entry();
			gps = new GPSTracker(this);
			if (gps.canGetLocation()) {
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
			} else {
				isAlerted = true;
				gps.showSettingsAlert();
			}

			initMap();
			parsed = new Date();

		} else if ("edit".equalsIgnoreCase(action)) {
			startpointInp.setEnabled(false);
			checkin = (Entry) bundle.getSerializable("selectedEntry");
			lastCheckin = (Entry) bundle.getSerializable("selectedEntry");
			String coordinate = checkin.getCoordinate();

			latitude = checkin.getPosition().latitude;
			longitude = checkin.getPosition().longitude;

			coordinate = checkin.getStartPoint();
			tokens = new StringTokenizer(coordinate, ";");
			first = tokens.nextToken().trim();
			second = tokens.nextToken().trim();

			startLatitude = Double.parseDouble(first);
			startLongitude = Double.parseDouble(second);

			initMap();

			markerStart = new MarkerOptions().position(
					new LatLng(startLatitude, startLongitude)).title(
					"You start from here");

			// adding marker
			googleMap.addMarker(markerStart);

			parsed = DateTimeHelper.convertStringServerTimeToLocalDate(checkin
					.getDateTime());

			String[] vehicles = getResources().getStringArray(
					R.array.vehicleList);

			datetimeInp.setText(displayDateTimeFormat.format(parsed));
			descriptionInp.setText(checkin.getText());
			String lastVehicle = checkin.getVehicle();
			int vehicleID = 0;

			for (int i = 0; i < vehicles.length; i++) {
				if (lastVehicle.equalsIgnoreCase(vehicles[i])) {
					vehicleID = i;
					break;
				}
			}

			vehicleList.setSelection(vehicleID);

			Button deleteCheckinButt = new Button(this);
			deleteCheckinButt.setText("Delete");
			LinearLayout buttonBar = (LinearLayout) findViewById(R.id.createCheckinButtBar);
			buttonBar.addView(deleteCheckinButt);

			deleteCheckinButt.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							CreateCheckinActivity.this);

					// Setting Dialog Title
					alertDialog.setTitle("Confirm Delete...");

					// Setting Dialog Message
					alertDialog
							.setMessage("Are you sure you want delete this?");

					// Setting Icon to Dialog
					alertDialog.setIcon(R.drawable.ic_action_delete);

					// Setting Positive "Yes" Button
					alertDialog.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									databasehandler.deleteEntry(checkin);
									finish();
								}
							});

					// Setting Negative "NO" Button
					alertDialog.setNegativeButton("NO",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// Write your code here to invoke NO event
									dialog.cancel();
								}
							});

					// Showing Alert Message
					alertDialog.show();

				}
			});
		}

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(latitude, longitude)).zoom(16).build();

		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));

		datetimeInp.setText(displayDateTimeFormat.format(parsed));

		if (!isNetworkConnected()) {
			Toast.makeText(this, "Please connect to internet to use map",
					Toast.LENGTH_SHORT).show();
		} else {
			getAddress = new GetAddressTask(this, latitude, longitude);
			getAddress.execute();
		}

	}

	public void onClickCreateCheckin(View v) {

		path = null;
		if (!isNetworkConnected()) {
			Toast.makeText(this, "No network connection, no image to save",
					Toast.LENGTH_SHORT).show();
			saveAndCloseCheckin();
		} else {
			mapImg = getOutputMediaFile();
			mapURL = "http://maps.google.com/maps/api/staticmap?center="
					+ latitude + "," + longitude
					+ "&zoom=17&size=600x600&markers=color:green|label:C|"
					+ latitude + "," + longitude + "&sensor=false";

			downloadTask = new DownloadImageTask(mapURL, this);

			downloadTask.execute(mapURL);
			timeout = new CountDownTimer(30000, 1000) {

				public void onTick(long millisUntilFinished) {
				}

				public void onFinish() {
					if (pDialog.isShowing()) {
						pDialog.dismiss();
					}
					if (downloadTask != null && !downloadTask.isCancelled()) {
						downloadTask.cancel(true);
					}
					Toast.makeText(getApplicationContext(),
							"Timeout, no image to save", Toast.LENGTH_SHORT)
							.show();
					saveAndCloseCheckin();
				}
			}.start();
		}

	}

	private void saveAndCloseCheckin() {
		Entry saveCheckin = null;
		if ("create".equalsIgnoreCase(action)) {
			saveCheckin = new Entry();

		} else if ("edit".equalsIgnoreCase(action)) {
			saveCheckin = checkin;
		}
		if (isNetworkConnected()) {
			saveCheckin.setPath(path);
		}
		saveCheckin.setCoordinate("" + latitude + "; " + longitude);
		saveCheckin.setDateTime(DateTimeHelper
				.convertLocalDateToServerString(parsed));
		saveCheckin.setText(descriptionInp.getText().toString());
		saveCheckin.setStartPoint("" + startLatitude + "; " + startLongitude);
		saveCheckin.setType("checkin");
		saveCheckin.setModifiedDate(DateTimeHelper
				.convertLocalDateToServerString(new Date()));

		if (vehicleList.getSelectedItem() != null) {
			saveCheckin.setVehicle(vehicleList.getSelectedItem().toString());
		} else {
			saveCheckin.setVehicle(vehicleList.getItemAtPosition(0).toString());
		}

		if ("create".equalsIgnoreCase(action)) {
			databasehandler.addEntry(saveCheckin);

		} else if ("edit".equalsIgnoreCase(action)) {
			String compare = checkin.compareEdit(lastCheckin);
			if ("text".equalsIgnoreCase(compare)
					|| "both".equalsIgnoreCase(compare)) {
				saveCheckin.setChangedElement("text");
			}
			databasehandler.updateEntry(saveCheckin);
		}
		Toast.makeText(this, "Checkin added", Toast.LENGTH_SHORT).show();
		if (getAddress != null && !getAddress.isCancelled()) {
			getAddress.cancel(true);
		}
		finish();
	}

	private void initMap() {

		try {

			if (googleMap != null) {
				googleMap.clear();
			}

			googleMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.checkinMapView)).getMap();

			PolylineOptions rectOptions = new PolylineOptions();
			for (MarkerOptions markerItem : listMarkerStart) {
				googleMap.addMarker(markerItem);
				rectOptions.add(markerItem.getPosition());
			}
			googleMap.addPolyline(rectOptions);
			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setZoomControlsEnabled(false);
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);
			googleMap.getUiSettings().setRotateGesturesEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Sorry! unable to create maps",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected void onResume() {
		super.onResume();
		initMap();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (isAlerted) {
			if (hasFocus) {
				if (gps.canGetLocation()) {
					latitude = gps.getLatitude();
					longitude = gps.getLongitude();
				} else {
					finish();
				}
			}
		}
	}

	// Datetime changing handle
	@SuppressWarnings("deprecation")
	public void onClickDatetime(View v) {
		datetimeDialog = new Dialog(this);
		datetimeDialog.setContentView(R.layout.dialog_datetime_picker);
		datetimeDialog.setTitle("Choose date and time");

		datePicker = (DatePicker) datetimeDialog
				.findViewById(R.id.datePickerEntry);
		timePicker = (TimePicker) datetimeDialog
				.findViewById(R.id.timePickerEntry);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parsed);
		datePicker.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				new DatePicker.OnDateChangedListener() {

					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						parsed.setDate(dayOfMonth);
						parsed.setMonth(monthOfYear);
						parsed.setYear(year - 1900);
					}
				});

		/*
		 * datePicker.updateDate(parsed.getYear(), parsed.getMonth(),
		 * parsed.getDate());
		 */
		timePicker.setCurrentHour(parsed.getHours());
		timePicker.setCurrentMinute(parsed.getMinutes());

		Button datetimePickButt = (Button) datetimeDialog
				.findViewById(R.id.datetimePButt);

		datetimePickButt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * parsed.setDate(datePicker.getDayOfMonth());
				 * parsed.setMonth(datePicker.getMonth());
				 * parsed.setYear(datePicker.getYear());
				 */
				parsed.setHours(timePicker.getCurrentHour());
				parsed.setMinutes(timePicker.getCurrentMinute());

				datetimeInp.setText(displayDateTimeFormat.format(parsed));
				datetimeDialog.dismiss();
			}
		});

		datetimeDialog.show();
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CreateCheckinActivity.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

	private class GetAddressTask extends AsyncTask<Void, Void, String> {
		Activity context;
		double taskLatitute;
		double taskLongitute;
		Address resultAddress;

		public GetAddressTask(Activity context, double taskLatitute,
				double taskLongitute) {
			this.context = context;
			this.taskLatitute = taskLatitute;
			this.taskLongitute = taskLongitute;
		}

		@Override
		protected String doInBackground(Void... params) {
			Geocoder geocoder = new Geocoder(context);
			String result;
			result = null;
			try {
				List<Address> addressList = geocoder.getFromLocation(
						taskLatitute, taskLongitute, 1);

				if (addressList != null && addressList.size() > 0) {
					resultAddress = addressList.get(0);
					StringBuilder addressString = new StringBuilder();
					for (int i = 0; i <= resultAddress.getMaxAddressLineIndex(); i++) {
						addressString.append(resultAddress.getAddressLine(i));
						if (i < resultAddress.getMaxAddressLineIndex()) {
							addressString.append(", ");
						}
					}
					result = addressString.toString();
				}
			} catch (IllegalArgumentException e) {
				Toast.makeText(CreateCheckinActivity.this, "Wrong information",
						Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				Toast.makeText(CreateCheckinActivity.this,
						"Service is unavailable", Toast.LENGTH_LONG).show();
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			marker = new MarkerOptions().position(new LatLng(taskLatitute,
					taskLongitute));
			address = result;
			marker.title(result);
			try {
				checkin.setStreet_number(resultAddress.getSubThoroughfare());
				checkin.setRoute(resultAddress.getThoroughfare());
				checkin.setLocality(resultAddress.getLocality());
				checkin.setSubLocality(resultAddress.getSubLocality());
				checkin.setAdministrative_area_level_2(resultAddress
						.getSubAdminArea());
				checkin.setAdministrative_area_level_1(resultAddress
						.getAdminArea());
				checkin.setCountry(resultAddress.getCountryCode());
			} catch (NullPointerException e) {

			}

			googleMap.addMarker(marker);
		}

	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		Activity context;
		String url;

		public DownloadImageTask(String url, Activity context) {
			this.url = url;
			this.context = context;
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			context.showDialog(progress_bar_type);
		}

		protected Bitmap doInBackground(String... urls) {
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(url).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);

			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(mapImg);
				final BufferedOutputStream bos = new BufferedOutputStream(fos,
						1024);
				result.compress(CompressFormat.JPEG, 100, fos);
				bos.flush();
				bos.close();
				fos.close();
			} catch (FileNotFoundException e) {
				Toast.makeText(context, "No file to save", Toast.LENGTH_SHORT)
						.show();
			} catch (IOException e) {
				Toast.makeText(context, "Cannot save", Toast.LENGTH_SHORT)
						.show();
			}
			pDialog.dismiss();
			path = mapImg.getAbsolutePath();
			if (timeout != null) {
				timeout.cancel();
			}
			Toast.makeText(getApplicationContext(), "Download completed",
					Toast.LENGTH_SHORT).show();
			saveAndCloseCheckin();
		}
	}

	private static File getOutputMediaFile() {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Jornee");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("Jornee", "Oops! Failed create " + "Jornee"
						+ " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "CHK_" + timeStamp + ".jpg");
		return mediaFile;

	}

	/**
	 * Showing Dialog
	 * */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case progress_bar_type: // we set this to 0
			pDialog = new ProgressDialog(this);
			pDialog.setMessage("Downloading map image. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setCancelable(false);
			pDialog.show();
			return pDialog;
		default:
			return null;
		}
	}

}
