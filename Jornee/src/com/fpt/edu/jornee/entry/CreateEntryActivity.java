/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: CreateEntryActivity.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.entry;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.InputType;
import android.util.Log;
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
import com.fpt.edu.jornee.utils.UniversalImageHelper;

@SuppressLint("SimpleDateFormat")
public class CreateEntryActivity extends Activity {
	static int ACTION_TAKE_PICTURE = 1;
	static int ACTION_PICK_PICTURE = 2;
	Uri outputFileUri;
	DatabaseHandler databaseHandler;

	Dialog datetimeDialog;
	DatePicker datePicker;
	TimePicker timePicker;
	Button done;

	Date parsed;
	Double imgLatitude = null;
	Double imgLongitude = null;
	String path;

	SimpleDateFormat utcFormat;
	String utcDatetime;

	FrameLayout imgFrm;

	Bitmap bmp;

	TextView descriptionInp;
	TextView datetimeInp;
	TextView locationInp;

	final SimpleDateFormat displayDateTimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_entry_layout);
		// Show the Up button in the action bar.
		setupActionBar();
		this.setTitle("Create entry");
		final Bundle bundle = getIntent().getExtras();
		String getAction = bundle.getString("action");

		descriptionInp = (TextView) findViewById(R.id.descriptionLabel);
		datetimeInp = (TextView) findViewById(R.id.dateTimeInf);
		locationInp = (TextView) findViewById(R.id.locationInf);

		databaseHandler = new DatabaseHandler(this);

		done = (Button) findViewById(R.id.createEntryButt);
		done.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Entry entry = new Entry();
				entry.setDateTime(DateTimeHelper
						.convertLocalDateToServerString(parsed));

				entry.setModifiedDate(DateTimeHelper
						.convertLocalDateToServerString(new Date()));
				entry.setPlaceName(locationInp.getText().toString());
				entry.setText(descriptionInp.getText().toString());
				if (path != null) {
					entry.setType("image");
				} else {
					entry.setType("text");
				}
				entry.setPath(path);
				entry.setCoordinate("" + imgLatitude + "; " + imgLongitude);
				entry.setType("image");
				databaseHandler.addEntry(entry);
				Toast.makeText(CreateEntryActivity.this, "Entry added",
						Toast.LENGTH_LONG).show();
				Intent data = new Intent();
				data.putExtra("fragmentID", bundle.getInt("fragmentID"));
				if (getParent() == null) {
					setResult(Activity.RESULT_OK, data);
				} else {
					getParent().setResult(Activity.RESULT_OK, data);
				}
				finish();
			}
		});

		imgFrm = (FrameLayout) findViewById(R.id.newEntryImgFrm);
		if (getAction.equals("gallery")) {
			Intent intentGallery = new Intent();
			intentGallery.setType("image/*");
			intentGallery.setAction(Intent.ACTION_PICK);
			startActivityForResult(
					Intent.createChooser(intentGallery, "Select Picture"),
					ACTION_PICK_PICTURE);
		} else {
			Intent cameraIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			File tmpFile = getOutputMediaFile();
			path = tmpFile.getAbsolutePath();
			outputFileUri = Uri.fromFile(tmpFile);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			startActivityForResult(cameraIntent, ACTION_TAKE_PICTURE);
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

				TextView datetimeInp = (TextView) findViewById(R.id.dateTimeInf);
				datetimeInp.setText(displayDateTimeFormat.format(parsed));

				datetimeDialog.dismiss();
			}
		});

		datetimeDialog.show();

	}

	// Location changing handle
	public void onClickLocation(View v) {
		AlertDialog.Builder editalert = new AlertDialog.Builder(this);

		editalert.setTitle("Change location");
		editalert.setMessage("Please enter your location below");

		final EditText input = new EditText(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		input.setLayoutParams(lp);
		editalert.setView(input);
		editalert.setPositiveButton("Done",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						TextView locationInp = (TextView) findViewById(R.id.locationInf);
						if (input.getText().length() > 0) {
							locationInp.setText(input.getText());
						}
					}
				});

		editalert.show();
	}

	// Description changing handle
	public void onClickDescription(View v) {
		AlertDialog.Builder editalert = new AlertDialog.Builder(this);

		editalert.setTitle("Change description");
		editalert.setMessage("Please enter your description below");

		final EditText input = new EditText(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		input.setLayoutParams(lp);
		input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		input.setHorizontalScrollBarEnabled(false);
		input.setVerticalScrollBarEnabled(true);
		input.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		input.setMaxLines(4);
		editalert.setView(input);
		editalert.setPositiveButton("Done",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						TextView descriptionInp = (TextView) findViewById(R.id.descriptionLabel);
						if (input.getText().length() > 0) {
							descriptionInp.setText(input.getText());
						}
					}
				});

		editalert.show();
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

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		// imgFrm = (FrameLayout) findViewById(R.id.newEntryImgFrm);

		String imgDateTime = null;

		String imgLatitudeExif = null;
		String imgLatitudeExifRef = null;

		String imgLongitudeExif = null;
		String imgLongitudeExifRef = null;

		Uri imageURI;
		if (resultCode == RESULT_OK) {
			if (requestCode == ACTION_PICK_PICTURE) {
				imageURI = data.getData();
				path = getRealPathFromURI(imageURI);
			}

			// bmp = BitmapFactory.decodeFile(path);

			// double bmpRate = bmp.getWidth() / bmp.getHeight();
			// double imgFrmRate = imgFrm.getMeasuredWidth()
			// / imgFrm.getMeasuredHeight();
			// bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
			// if (bmpRate > bmpRate) {
			// bmp = Bitmap.createScaledBitmap(
			// bmp,
			// imgFrm.getMeasuredWidth(),
			// imgFrm.getMeasuredWidth() / bmp.getWidth()
			// * imgFrm.getMeasuredHeight(), true);
			// } else {
			// bmp = Bitmap.createScaledBitmap(bmp, imgFrm.getMeasuredHeight()
			// / bmp.getHeight() * imgFrm.getMeasuredWidth(),
			// imgFrm.getMeasuredHeight(), true);
			// }
			/*
			 * float origHeight = bmp.getHeight(); float origWidth =
			 * bmp.getWidth(); float aspectRatio = origWidth / origHeight;
			 * 
			 * bmp = Bitmap.createScaledBitmap(bmp, 500, (int) (500 /
			 * aspectRatio), true);
			 */

			// Create ImageView for the new image
			ImageView newImageView = new ImageView(this);
			newImageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			// newImageView.setImageBitmap(bmp);
			imgFrm.addView(newImageView);
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
				Toast.makeText(CreateEntryActivity.this, e.toString(),
						Toast.LENGTH_LONG).show();
			}

			// Set text for the views
			if (imgLatitude != null && imgLongitude != null) {
				locationInp.setText("" + imgLatitude + "; " + imgLongitude);
			}
		}

	}

	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = Images.Media.insertImage(inContext.getContentResolver(),
				inImage, "Title", null);
		return Uri.parse(path);
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outputFileUri != null) {
			outState.putString("cameraImageUri", outputFileUri.toString());
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("cameraImageUri")) {
			outputFileUri = Uri.parse(savedInstanceState
					.getString("cameraImageUri"));
		}
	}
}
