/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: ViewDetailEntryActivity.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.entry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fpt.edu.bean.Entry;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.sqllite.DatabaseHandler;
import com.fpt.edu.jornee.utils.DateTimeHelper;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

@SuppressLint("SimpleDateFormat")
public class ViewDetailEntryActivity extends Activity {
	public static int ACTION_EDIT_ENTRY = 31;
	static int ACTION_DELETE_ENTRY = 32;
	static int ACTION_SHARE_ENTRY = 33;
	Date parsed;
	Calendar entryDatetime;
	Entry entry;
	String path;
	Bitmap entryImg;
	String entryType;
	Button datetimePickButt;
	Button done;

	TextView descriptionInp;
	TextView datetimeInp;
	TextView locationInp;

	ImageView detailImageView;

	DatabaseHandler databaseHandler;

	final SimpleDateFormat displayDateTimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_entry_view_details_layout);
		this.setTitle("Entry detail");

		Bundle bundle = getIntent().getExtras();

		entry = (Entry) bundle.getSerializable("selectedEntry");

		descriptionInp = (TextView) findViewById(R.id.entryDetailDescription);
		datetimeInp = (TextView) findViewById(R.id.entryDetailDatetimeText);
		locationInp = (TextView) findViewById(R.id.entryDetailLocationText);

		path = entry.getPath();
		entryType = entry.getType();

		parsed = DateTimeHelper.convertStringServerTimeToLocalDate(entry
				.getDateTime());
		entryDatetime = Calendar.getInstance();
		entryDatetime.setTime(parsed);

		// Create ImageView for the new image
		detailImageView = (ImageView) findViewById(R.id.entryDetailImgView);
		detailImageView.setVisibility(View.VISIBLE);
		if (path != null) {
			UniversalImageHelper.loadImage(this, detailImageView, path);
		}
		datetimeInp.setText(displayDateTimeFormat.format(parsed));

		descriptionInp.setText(entry.getText());
		descriptionInp.setVisibility(View.VISIBLE);

		if (entry.getPlaceName() != null && !entry.getPlaceName().isEmpty()) {
			locationInp.setText(entry.getPlaceName());
		} else {
			locationInp.setText(entry.getCoordinate());
		}
		databaseHandler = new DatabaseHandler(this);

		detailImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (path != null && !path.isEmpty()) {
					Intent fullsreenView = new Intent(
							ViewDetailEntryActivity.this,
							FullScreenViewActivity.class);
					fullsreenView.putExtra("path", path);
					startActivity(fullsreenView);
				}

			}
		});

	}

	public void onClickEntryDetailEdit(View v) {
		Intent editEntryIntent = new Intent(this, EditEntryActivity.class);
		editEntryIntent.putExtra("action", "edit");
		editEntryIntent.putExtra("selectedEntry", entry);
		startActivityForResult(editEntryIntent, ACTION_EDIT_ENTRY);
	}

	public void onClickEntryDetailDelete(View v) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				ViewDetailEntryActivity.this);

		// Setting Dialog Title
		alertDialog.setTitle("Confirm Delete...");

		// Setting Dialog Message
		alertDialog.setMessage("Are you sure you want delete this?");

		// Setting Icon to Dialog
		alertDialog.setIcon(R.drawable.ic_action_delete);

		// Setting Positive "Yes" Button
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						databaseHandler.deleteEntry(entry);
						finish();
					}
				});

		// Setting Negative "NO" Button
		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to invoke NO event
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();

	}

	public void onClickEntryDetailShare(View v) {
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("image/jpeg");
		sharingIntent
				.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == ACTION_EDIT_ENTRY
				&& resultCode == Activity.RESULT_OK) {
			entry = databaseHandler.getEntryInfor(entry.getEntryID());
			path = entry.getPath();
			entryType = entry.getType();

			parsed = DateTimeHelper.convertStringServerTimeToLocalDate(entry
					.getDateTime());
			entryDatetime = Calendar.getInstance();
			entryDatetime.setTime(parsed);

			if (path != null) {
				UniversalImageHelper.loadImage(this, detailImageView, path);
			}

			datetimeInp.setText(displayDateTimeFormat.format(parsed));

			descriptionInp.setText(entry.getText());
			if (entry.getPlaceName() != null && !entry.getPlaceName().isEmpty()) {
				locationInp.setText(entry.getPlaceName());
			} else {
				locationInp.setText(entry.getCoordinate());
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

}
