/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: EditEntryActivity.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.entry;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fpt.edu.bean.Entry;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.sqllite.DatabaseHandler;
import com.fpt.edu.jornee.utils.DateTimeHelper;
import com.fpt.edu.jornee.utils.GPSTracker;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

@SuppressLint("SimpleDateFormat")
public class EditEntryActivity extends Activity {
	static int ACTION_TAKE_PICTURE = 1;
	static int ACTION_PICK_PICTURE = 2;
	private static Uri outputFileUri;

	String action;

	Date parsed;
	Entry entry;
	String path;
	Bitmap entryImg;
	String entryType;
	Button datetimePickButt;
	Button done;
	FrameLayout imgFrm;
	String address;
	Entry lastEntry;

	ImageView newImageView;

	EditText descriptionInp;
	TextView datetimeInp;
	EditText locationInp;
	TextView imgText;
	TextView addressInp;

	DatabaseHandler databaseHandler;

	Dialog datetimeDialog;
	DatePicker datePicker;
	TimePicker timePicker;

	Double imgLatitude = null;
	Double imgLongitude = null;

	GPSTracker gps;

	final SimpleDateFormat displayDateTimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	GetAddressTask getAddressTask;
	GetCoordinateTask getCoordinateTask;

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_create_entry_layout);

		descriptionInp = (EditText) findViewById(R.id.descriptionInput);
		datetimeInp = (TextView) findViewById(R.id.dateTimeInf);
		locationInp = (EditText) findViewById(R.id.locationInf);
		imgFrm = (FrameLayout) findViewById(R.id.newEntryImgFrm);
		done = (Button) findViewById(R.id.createEntryButt);
		addressInp = (TextView) findViewById(R.id.entryLocationInf);

		Bundle bundle = getIntent().getExtras();
		action = bundle.getString("action");
		databaseHandler = new DatabaseHandler(this);
		datetimeInp.setText(displayDateTimeFormat.format(new Date()));
		imgText = new TextView(this);
		path = null;
		imgText.setText("Tap here to add image");
		imgText.setGravity(Gravity.CENTER);
		imgFrm.addView(imgText);

		lastEntry = null;

		parsed = new Date();
		entry = new Entry();
		lastEntry = new Entry();
		gps = new GPSTracker(this);

		this.setTitle("Create entry");

		if ("edit".equalsIgnoreCase(action)) {

			this.setTitle("Edit entry");
			entry = (Entry) bundle.getSerializable("selectedEntry");

			lastEntry.clone(entry);

			path = entry.getPath();
			entryType = entry.getType();

			parsed = DateTimeHelper.convertStringServerTimeToLocalDate(entry
					.getDateTime());

			// Create ImageView for the new image
			newImageView = new ImageView(this);
			newImageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

			// newImageView.setImageBitmap(entryImg);
			imgFrm.addView(newImageView);
			newImageView.setVisibility(View.VISIBLE);
			imgText.setVisibility(View.GONE);
			address = entry.getAddress();

			addressInp.setText(address);

			String coordinate = entry.getCoordinate();
			if (coordinate != null && !coordinate.isEmpty()) {

				imgLatitude = entry.getPosition().latitude;
				imgLongitude = entry.getPosition().longitude;

				if ((entry.getAddress() == null || entry.getAddress().isEmpty())
						&& isNetworkConnected()) {
					getAddressTask = new GetAddressTask(this, imgLatitude,
							imgLongitude);
					getAddressTask.execute();
				}
			}

			UniversalImageHelper.loadImage(this, newImageView, path);

			datetimeInp.setText(displayDateTimeFormat.format(parsed));

			descriptionInp.setText(entry.getText());

			if (entry.getPlaceName() != null && !entry.getPlaceName().isEmpty()) {
				locationInp.setText(entry.getPlaceName());
			} else {
				locationInp.setText(entry.getAddress());
			}

		} else if ("gallery".equalsIgnoreCase(action)) {

			newImageView = new ImageView(this);
			newImageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			// newImageView.setImageBitmap(entryImg);
			imgFrm.addView(newImageView);

			Intent intentGallery = new Intent();
			intentGallery.setType("image/*");
			intentGallery.setAction(Intent.ACTION_PICK);
			startActivityForResult(
					Intent.createChooser(intentGallery, "Select Picture"),
					ACTION_PICK_PICTURE);
		} else if ("camera".equalsIgnoreCase(action)) {

			newImageView = new ImageView(this);
			newImageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			// newImageView.setImageBitmap(entryImg);
			imgFrm.addView(newImageView);

			Intent cameraIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			File tmpFile = getOutputMediaFile();
			path = tmpFile.getAbsolutePath();
			outputFileUri = Uri.fromFile(tmpFile);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			startActivityForResult(cameraIntent, ACTION_TAKE_PICTURE);
		} else if ("text".equalsIgnoreCase(action)) {

			if (gps.canGetLocation()) {
				imgLatitude = gps.getLatitude();
				imgLongitude = gps.getLongitude();
			} else {
				gps.showSettingsAlert();
			}
			if ((entry.getAddress() == null || entry.getAddress().isEmpty())
					&& isNetworkConnected()) {
				getAddressTask = new GetAddressTask(this, imgLatitude,
						imgLongitude);
				getAddressTask.execute();
			}

		}

		done.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String descript = descriptionInp.getText().toString();
				if ((path != null && !path.isEmpty())
						|| (descript != null && !descript.isEmpty())) {
					entry.setDateTime(DateTimeHelper
							.convertLocalDateToServerString(parsed));
					entry.setModifiedDate(DateTimeHelper
							.convertLocalDateToServerString(new Date()));
					if (imgLatitude != null && imgLongitude != null) {
						entry.setCoordinate("" + imgLatitude + "; "
								+ imgLongitude);
					} else {
						entry.setCoordinate(null);
					}
					entry.setPlaceName(locationInp.getText().toString());
					entry.setText(descript);
					entry.setAddress(address);

					if (path != null && !path.isEmpty()) {
						entry.setType("image");
					} else {
						entry.setType("text");
					}
					entry.setPath(path);
					if ("edit".equalsIgnoreCase(action)) {
						if (lastEntry != null) {
							entry.setChangedElement(entry
									.compareEdit(lastEntry));
						}
						databaseHandler.updateEntry(entry);
						Toast.makeText(EditEntryActivity.this, "Entry updated",
								Toast.LENGTH_LONG).show();
					} else {
						databaseHandler.addEntry(entry);
						Toast.makeText(EditEntryActivity.this, "Entry added",
								Toast.LENGTH_LONG).show();
					}
					try {
						if (getAddressTask != null
								&& !getAddressTask.isCancelled()) {
							getAddressTask.cancel(true);
						}
						if (getCoordinateTask != null
								&& !getCoordinateTask.isCancelled()) {
							getCoordinateTask.cancel(true);
						}
					} catch (NullPointerException e) {

					}
					setResult(RESULT_OK);
					finish();
				} else {
					Toast.makeText(EditEntryActivity.this,
							"Image or description must not be empty",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		imgFrm.setOnClickListener(new View.OnClickListener() {
			final CharSequence[] items = { "Gallery", "Camera" };

			@Override
			public void onClick(View v) {
				AlertDialog.Builder imgPickAlert = new AlertDialog.Builder(
						EditEntryActivity.this);
				imgPickAlert.setTitle("Choose where to get your picture")
						.setItems(items, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								if (which == 0) {
									Intent intentGallery = new Intent();
									intentGallery.setType("image/*");
									intentGallery.setAction(Intent.ACTION_PICK);
									startActivityForResult(Intent
											.createChooser(intentGallery,
													"Select Picture"),
											ACTION_PICK_PICTURE);
								}
								if (which == 1) {
									Intent cameraIntent = new Intent(
											android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
									File tmpFile = getOutputMediaFile();
									path = tmpFile.getAbsolutePath();
									outputFileUri = Uri.fromFile(tmpFile);
									cameraIntent.putExtra(
											MediaStore.EXTRA_OUTPUT,
											outputFileUri);
									startActivityForResult(cameraIntent,
											ACTION_TAKE_PICTURE);
								}

							}

						});

				imgPickAlert.show();

			}
		});
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

	public void onClickAddressInp(View v) {
		AlertDialog.Builder editalert = new AlertDialog.Builder(this);

		editalert.setTitle("Find coordinate");
		editalert.setMessage("Please enter your address below");

		final EditText input = new EditText(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		input.setLayoutParams(lp);
		editalert.setView(input);
		editalert.setPositiveButton("Done",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (input.getText().length() > 0) {
							getCoordinateTask = new GetCoordinateTask(
									getParent(), input.getText().toString());
							getCoordinateTask.execute();
						}
					}
				});

		editalert.show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			String imgDateTime = null;

			String imgLatitudeExif = null;
			String imgLatitudeExifRef = null;

			String imgLongitudeExif = null;
			String imgLongitudeExifRef = null;

			Uri imageURI = null;

			if (requestCode == ACTION_PICK_PICTURE && data != null) {
				imageURI = data.getData();
				path = getRealPathFromURI(imageURI);
			}
			if ("text".equalsIgnoreCase(action)) {
				imgFrm.addView(newImageView);
			}

			imgText.setVisibility(View.GONE);
			UniversalImageHelper.loadImage(this, newImageView, path);

			try {
				ExifInterface exifInterface = new ExifInterface(path);

				// Get Exif information from image
				imgDateTime = exifInterface
						.getAttribute(ExifInterface.TAG_DATETIME);
				imgLatitudeExif = exifInterface
						.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
				imgLatitudeExifRef = exifInterface
						.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
				imgLongitudeExif = exifInterface
						.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
				imgLongitudeExifRef = exifInterface
						.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

				// Format datetime

				try {
					parsed = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
							.parse(imgDateTime);
				} catch (Exception e) {
					parsed = new Date();
				}
				datetimeInp.setText(displayDateTimeFormat.format(parsed));

				// Convert exif coordinate to GPS coordinate
				if (imgLatitudeExif != null && imgLatitudeExifRef != null
						&& imgLongitudeExif != null
						&& imgLongitudeExifRef != null) {
					if (imgLatitudeExifRef.equalsIgnoreCase("N")) {
						imgLatitude = convertToDegree(imgLatitudeExif);
					} else {
						imgLatitude = 0 - convertToDegree(imgLatitudeExif);
					}

					if (imgLongitudeExifRef.equalsIgnoreCase("E")) {
						imgLongitude = convertToDegree(imgLongitudeExif);
					} else {
						imgLongitude = 0 - convertToDegree(imgLongitudeExif);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(EditEntryActivity.this,
						"Something went wrong, please retry", Toast.LENGTH_LONG)
						.show();
			}

			// Set text for the views
			if (imgLatitude != null && imgLongitude != null) {
				if (isNetworkConnected()) {
					if (getAddressTask != null && !getAddressTask.isCancelled()) {
						getAddressTask.cancel(true);
					}

					getAddressTask = new GetAddressTask(this, imgLatitude,
							imgLongitude);
					getAddressTask.execute();
				}
			} else {
				if (gps.canGetLocation()) {
					imgLatitude = gps.getLatitude();
					imgLongitude = gps.getLongitude();
				} else {
					gps.showSettingsAlert();
				}
				if (imgLatitude != null && imgLongitude != null) {
					if (isNetworkConnected()) {
						if (getAddressTask != null
								&& !getAddressTask.isCancelled()) {
							getAddressTask.cancel(true);
						}

						getAddressTask = new GetAddressTask(this, imgLatitude,
								imgLongitude);
						getAddressTask.execute();
					}
				}
			}

		}
	}

	public String getRealPathFromURI(Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = this.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	// Function for converting Exif coordinate to GPS degrees coordinate
	@SuppressLint("UseValueOf")
	private Double convertToDegree(String stringDMS) {
		Double result = null;
		String[] DMS = stringDMS.split(",", 3);

		String[] stringD = DMS[0].split("/", 2);
		Double D0 = new Double(stringD[0]);
		Double D1 = new Double(stringD[1]);
		Double FloatD = D0 / D1;

		String[] stringM = DMS[1].split("/", 2);
		Double M0 = new Double(stringM[0]);
		Double M1 = new Double(stringM[1]);
		Double FloatM = M0 / M1;

		String[] stringS = DMS[2].split("/", 2);
		Double S0 = new Double(stringS[0]);
		Double S1 = new Double(stringS[1]);
		Double FloatS = S0 / S1;

		result = new Double(FloatD + (FloatM / 60) + (FloatS / 3600));

		return result;
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
				+ "IMG_" + timeStamp + ".jpg");

		return mediaFile;
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

	public String getAddress(double lat, double lon) throws IOException {
		Geocoder geocoder = new Geocoder(this);
		String result;
		result = null;
		try {
			List<Address> addressList = geocoder.getFromLocation(lat, lon, 1);

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
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		return result;
	}

	public Address getCoordinate(String inputAddress) throws IOException {

		Address result;
		Geocoder geocoder = new Geocoder(this);
		List<Address> addresses = null;
		result = null;
		addresses = geocoder.getFromLocationName(inputAddress, 1);
		if (addresses.size() > 0) {
			result = addresses.get(0);
		}
		return result;
	}

	private class GetAddressTask extends AsyncTask<Void, Void, String> {
		Activity context;
		double latitute;
		double longitute;
		Address resultAddress;

		public GetAddressTask(Activity context, double latitute,
				double longitute) {
			super();
			this.context = context;
			this.latitute = latitute;
			this.longitute = longitute;
		}

		@Override
		protected String doInBackground(Void... params) {
			Geocoder geocoder = new Geocoder(context);
			String result;
			result = null;
			try {
				List<Address> addressList = geocoder.getFromLocation(latitute,
						longitute, 1);

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
				Toast.makeText(EditEntryActivity.this, "Wrong information",
						Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				Toast.makeText(EditEntryActivity.this,
						"Service is unavailable", Toast.LENGTH_LONG).show();
			}

			return result;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			addressInp.setHint("Finding address...");
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			address = result;
			addressInp.setHint("Tap here and enter address to find coordinate");
			addressInp.setText(address);

			entry.setStreet_number(resultAddress.getSubThoroughfare());
			entry.setRoute(resultAddress.getThoroughfare());
			entry.setSubLocality(resultAddress.getSubLocality());
			entry.setLocality(resultAddress.getLocality());
			entry.setAdministrative_area_level_2(resultAddress
					.getSubAdminArea());
			entry.setAdministrative_area_level_1(resultAddress.getAdminArea());
			entry.setCountry(resultAddress.getCountryCode());

			Toast.makeText(EditEntryActivity.this, "Address loaded",
					Toast.LENGTH_LONG).show();
		}

	}

	private class GetCoordinateTask extends AsyncTask<Void, Void, Address> {
		Activity context;
		String input;

		public GetCoordinateTask(Activity context, String input) {
			super();
			this.context = context;
			this.input = input;
		}

		@Override
		protected Address doInBackground(Void... params) {
			try {
				return getCoordinate(input);
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Address result) {
			super.onPostExecute(result);
			if (result == null) {
				Toast.makeText(EditEntryActivity.this,
						"Cannot get coordinate from this address",
						Toast.LENGTH_LONG).show();
			} else {
				imgLatitude = result.getLatitude();
				imgLongitude = result.getLongitude();

				StringBuilder addressString = new StringBuilder();
				for (int i = 0; i <= result.getMaxAddressLineIndex(); i++) {
					addressString.append(result.getAddressLine(i));
					if (i < result.getMaxAddressLineIndex()) {
						addressString.append(", ");
					}
				}

				entry.setStreet_number(result.getSubThoroughfare());
				entry.setRoute(result.getThoroughfare());
				entry.setSubLocality(result.getSubLocality());
				entry.setLocality(result.getLocality());
				entry.setAdministrative_area_level_2(result.getSubAdminArea());
				entry.setAdministrative_area_level_1(result.getAdminArea());
				entry.setCountry(result.getCountryCode());

				address = addressString.toString();
				addressInp.setText(address);
			}
		}

	}
}
