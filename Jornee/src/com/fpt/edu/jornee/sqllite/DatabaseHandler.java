/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: DatabaseHandler.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.sqllite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fpt.edu.bean.Category;
import com.fpt.edu.bean.Comment;
import com.fpt.edu.bean.Entry;
import com.fpt.edu.bean.Host;
import com.fpt.edu.bean.Journey;
import com.fpt.edu.bean.Conversation;
import com.fpt.edu.bean.TipsQuestion;
import com.fpt.edu.bean.Transport;
import com.fpt.edu.bean.User;
import com.fpt.edu.bean.Vehicle;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

@SuppressLint("SimpleDateFormat")
public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 6;

	// Database Name
	private static final String DATABASE_NAME = "Jornee";

	// table name
	private static final String TABLE_CATEGORY = "Category";
	private static final String TABLE_COMMENT = "Comment";
	private static final String TABLE_ENTRY = "Entry";
	private static final String TABLE_HOST = "Host";
	private static final String TABLE_JOURNEY = "Journey";
	private static final String TABLE_TIPS_QUESTION = "TipsQuestion";
	private static final String TABLE_TRANSPORT = "Transport";
	private static final String TABLE_USER = "User";
	private static final String TABLE_VEHICLE = "Vehicle";
	private static final String TABLE_MESSAGE = "Message";

	// Table Columns names
	private static final String TABLE_CATEGORY_COLLUM_ID = "categoryID";
	private static final String TABLE_CATEGORY_COLLUM_NAME = "categoryName";
	private static final String TABLE_CATEGORY_COLLUM_MODIFYDATE = "modifiedDate";

	private static final String TABLE_COMMENT_COLLUM_ID = "commentId";
	private static final String TABLE_COMMENT_COLLUM_TEXT = "text";
	private static final String TABLE_COMMENT_COLLUM_USERNAME = "username";
	private static final String TABLE_COMMENT_COLLUM_DATETIME = "dateTime";
	private static final String TABLE_COMMENT_COLLUM_MODIFIEDDATE = "modifiedDate";

	private static final String TABLE_ENTRY_COLLUM_ID = "entryID";
	private static final String TABLE_ENTRY_COLLUM_TYPE = "type";
	private static final String TABLE_ENTRY_COLLUM_COORDINATE = "coordinate";
	private static final String TABLE_ENTRY_COLLUM_TEXT = "text";
	private static final String TABLE_ENTRY_COLLUM_PATH = "path";
	private static final String TABLE_ENTRY_COLLUM_IPLACEID = "iplaceID";
	private static final String TABLE_ENTRY_COLLUM_JOURNEYID = "journeyID";
	private static final String TABLE_ENTRY_COLLUM_DATETIME = "dateTime";
	private static final String TABLE_ENTRY_COLLUM_MODIFIEDDATE = "modifiedDate";
	private static final String TABLE_ENTRY_COLLUM_PLACENAME = "placeName";
	private static final String TABLE_ENTRY_COLLUM_VEHICLE = "vehicle";
	private static final String TABLE_ENTRY_COLLUM_SYNCDATE = "syncDate";
	private static final String TABLE_ENTRY_COLLUM_CHANGEDELEMENT = "changedElement";
	private static final String TABLE_ENTRY_COLLUM_STARTPOINT = "startPoint";
	private static final String TABLE_ENTRY_COLLUM_ADDRESS = "address";
	private static final String TABLE_ENTRY_COLLUM_STREETNUMBER = "street_number";
	private static final String TABLE_ENTRY_COLLUM_ROUTE = "route";
	private static final String TABLE_ENTRY_COLLUM_SUBLOCALITY = "sub_locality";
	private static final String TABLE_ENTRY_COLLUM_LOCALITY = "locality";
	private static final String TABLE_ENTRY_COLLUM_AREALVL2 = "administrative_area_level_2";
	private static final String TABLE_ENTRY_COLLUM_AREALVL1 = "administrative_area_level_1";
	private static final String TABLE_ENTRY_COLLUM_COUNTRY = "country";
	private static final String TABLE_ENTRY_COLLUM_SERVERENTRYID = "serverEntryID";

	private static final String TABLE_HOST_COLLUM_ID = "hostID";
	private static final String TABLE_HOST_COLLUM_USERID = "userID";
	private static final String TABLE_HOST_COLLUM_AREAID = "areaID";
	private static final String TABLE_HOST_COLLUM_USERANDRATE = "userAndRate";
	private static final String TABLE_HOST_COLLUM_DESCRIPTION = "desctiprion";
	private static final String TABLE_HOST_COLLUM_STATUS = "status";
	private static final String TABLE_HOST_COLLUM_COMMENTID = "commentId";
	private static final String TABLE_HOST_COLLUM_ACTIVATETIME = "activeTime";
	private static final String TABLE_HOST_COLLUM_HOSTTIPS = "hostTips";
	private static final String TABLE_HOST_COLLUM_MODIFIEDDATE = "modifiedDate";

	private static final String TABLE_JOURNEY_COLLUM_ID = "journeyID";
	private static final String TABLE_JOURNEY_COLLUM_JOURNEYNAME = "journeyName";
	private static final String TABLE_JOURNEY_COLLUM_USERLIKED = "userLiked";
	private static final String TABLE_JOURNEY_COLLUM_COMMENTID = "commentID";
	private static final String TABLE_JOURNEY_COLLUM_USERID = "userID";
	private static final String TABLE_JOURNEY_COLLUM_ENTRIESID = "entriesID";
	private static final String TABLE_JOURNEY_COLLUM_SHARED = "shared";
	private static final String TABLE_JOURNEY_COLLUM_CREATEDDATE = "createdDate";
	private static final String TABLE_JOURNEY_COLLUM_MODIFIEDDATE = "modifiedDate";
	private static final String TABLE_JOURNEY_COLLUM_SYNCEDDATE = "syncedDate";
	private static final String TABLE_JOURNEY_COLLUM_SERVERJOURNEYID = "serverJourneyID";

	private static final String TABLE_TIPSQUESTION_COLLUM_ID = "questionId";
	private static final String TABLE_TIPSQUESTION_COLLUM_CONTENT = "content";
	private static final String TABLE_TIPSQUESTION_COLLUM_MODIFIEDDATE = "modifiedDate";

	private static final String TABLE_TRANSPORT_COLLUM_ID = "transportId";
	private static final String TABLE_TRANSPORT_COLLUM_STARTPOINT = "startPoint";
	private static final String TABLE_TRANSPORT_COLLUM_ENDPOINT = "endPoint";
	private static final String TABLE_TRANSPORT_COLLUM_VEHICLEID = "vehicleId";
	private static final String TABLE_TRANSPORT_COLLUM_MODIFIEDDATE = "modifiedDate";

	private static final String TABLE_USER_COLLUM_ID = "userID";
	private static final String TABLE_USER_COLLUM_USERNAME = "username";
	private static final String TABLE_USER_COLLUM_PASSWORD = "password";
	private static final String TABLE_USER_COLLUM_EMAIL = "email";
	private static final String TABLE_USER_COLLUM_DATEOFBIRTH = "dateOfBirth";
	private static final String TABLE_USER_COLLUM_GENDER = "gender";
	private static final String TABLE_USER_COLLUM_FOLLOWING = "following";
	private static final String TABLE_USER_COLLUM_FOLLOWER = "follower";
	private static final String TABLE_USER_COLLUM_FACEBOOKID = "facebookID";
	private static final String TABLE_USER_COLLUM_MODIFIEDDATE = "modifiedDate";

	private static final String TABLE_VEHICLE_COLLUM_ID = "vehicleId";
	private static final String TABLE_VEHICLE_COLLUM_NAME = "vehicleName";
	private static final String TABLE_VEHICLE_COLLUM_MODIFIEDDATE = "modifiedDate";

	private static final String TABLE_MESSAGE_COLLUM_ID = "conversationId";
	private static final String TABLE_MESSAGE_COLLUM_CURRENT_USER = "currentUser";
	private static final String TABLE_MESSAGE_COLLUM_CONTACT_USER = "contactUser";
	private static final String TABLE_MESSAGE_COLLUM_LAST_MESSAGE = "lastMessage";
	private static final String TABLE_MESSAGE_COLLUM_NUMBER_UNREAD = "numberUnread";
	private static final String TABLE_MESSAGE_COLLUM_MODIFIEDDATE = "modifiedDate";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {

		String createTableCategory = "CREATE TABLE " + TABLE_CATEGORY + " ( "
				+ TABLE_CATEGORY_COLLUM_ID + " NVARCHAR NOT NULL PRIMARY KEY, "
				+ TABLE_CATEGORY_COLLUM_NAME + " NVARCHAR, "
				+ TABLE_CATEGORY_COLLUM_MODIFYDATE + " NVARCHAR "

				+ ")";

		String createTableComment = "CREATE TABLE " + TABLE_COMMENT + " ( "
				+ TABLE_COMMENT_COLLUM_ID + " NVARCHAR NOT NULL PRIMARY KEY, "
				+ TABLE_COMMENT_COLLUM_TEXT + " NVARCHAR, "
				+ TABLE_COMMENT_COLLUM_USERNAME + " NVARCHAR, "
				+ TABLE_COMMENT_COLLUM_DATETIME + " NVARCHAR, "
				+ TABLE_COMMENT_COLLUM_MODIFIEDDATE + " NVARCHAR "

				+ ")";

		String createTableEntry = "CREATE TABLE " + TABLE_ENTRY + " ( "
				+ TABLE_ENTRY_COLLUM_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ TABLE_ENTRY_COLLUM_TYPE + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_COORDINATE + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_TEXT + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_PATH + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_IPLACEID + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_JOURNEYID + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_DATETIME + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_MODIFIEDDATE + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_PLACENAME + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_VEHICLE + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_STARTPOINT + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_SYNCDATE + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_CHANGEDELEMENT + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_ADDRESS + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_STREETNUMBER + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_ROUTE + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_SUBLOCALITY + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_LOCALITY + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_AREALVL2 + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_AREALVL1 + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_COUNTRY + " NVARCHAR, "
				+ TABLE_ENTRY_COLLUM_SERVERENTRYID + " NVARCHAR "

				+ ")";

		String createTableHost = "CREATE TABLE " + TABLE_HOST + " ( "
				+ TABLE_HOST_COLLUM_ID + " NVARCHAR NOT NULL PRIMARY KEY, "
				+ TABLE_HOST_COLLUM_USERID + " NVARCHAR , "
				+ TABLE_HOST_COLLUM_AREAID + " NVARCHAR , "
				+ TABLE_HOST_COLLUM_USERANDRATE + " NVARCHAR , "
				+ TABLE_HOST_COLLUM_DESCRIPTION + " NVARCHAR , "
				+ TABLE_HOST_COLLUM_STATUS + " NVARCHAR , "
				+ TABLE_HOST_COLLUM_COMMENTID + " NVARCHAR , "
				+ TABLE_HOST_COLLUM_ACTIVATETIME + " NVARCHAR , "
				+ TABLE_HOST_COLLUM_HOSTTIPS + " NVARCHAR , "
				+ TABLE_HOST_COLLUM_MODIFIEDDATE + " NVARCHAR "

				+ ")";
		String createTableJourney = "CREATE TABLE " + TABLE_JOURNEY + " ( "
				+ TABLE_JOURNEY_COLLUM_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ TABLE_JOURNEY_COLLUM_JOURNEYNAME + " NVARCHAR, "

				+ TABLE_JOURNEY_COLLUM_USERLIKED + " NVARCHAR, "
				+ TABLE_JOURNEY_COLLUM_COMMENTID + " NVARCHAR, "
				+ TABLE_JOURNEY_COLLUM_USERID + " NVARCHAR, "
				+ TABLE_JOURNEY_COLLUM_ENTRIESID + " NVARCHAR, "
				+ TABLE_JOURNEY_COLLUM_SHARED + " NVARCHAR, "
				+ TABLE_JOURNEY_COLLUM_CREATEDDATE + " NVARCHAR, "
				+ TABLE_JOURNEY_COLLUM_MODIFIEDDATE + " NVARCHAR, "
				+ TABLE_JOURNEY_COLLUM_SYNCEDDATE + " NVARCHAR, "
				+ TABLE_JOURNEY_COLLUM_SERVERJOURNEYID + " NVARCHAR "

				+ ")";
		String createTableTipsQuestion = "CREATE TABLE " + TABLE_TIPS_QUESTION
				+ " ( " + TABLE_TIPSQUESTION_COLLUM_ID
				+ " NVARCHAR NOT NULL PRIMARY KEY, "

				+ TABLE_TIPSQUESTION_COLLUM_CONTENT + " NVARCHAR, "

				+ TABLE_TIPSQUESTION_COLLUM_MODIFIEDDATE + " NVARCHAR "

				+ ")";
		String createTableTransport = "CREATE TABLE " + TABLE_TRANSPORT + " ( "
				+ TABLE_TRANSPORT_COLLUM_ID
				+ " NVARCHAR NOT NULL PRIMARY KEY, "

				+ TABLE_TRANSPORT_COLLUM_STARTPOINT + " NVARCHAR, "
				+ TABLE_TRANSPORT_COLLUM_ENDPOINT + " NVARCHAR, "
				+ TABLE_TRANSPORT_COLLUM_VEHICLEID + " NVARCHAR, "

				+ TABLE_TRANSPORT_COLLUM_MODIFIEDDATE + " NVARCHAR "

				+ ")";
		String createTableUser = "CREATE TABLE " + TABLE_USER + " ( "
				+ TABLE_USER_COLLUM_ID + " NVARCHAR NOT NULL PRIMARY KEY, "

				+ TABLE_USER_COLLUM_USERNAME + " NVARCHAR, "
				+ TABLE_USER_COLLUM_PASSWORD + " NVARCHAR, "
				+ TABLE_USER_COLLUM_EMAIL + " NVARCHAR, "
				+ TABLE_USER_COLLUM_DATEOFBIRTH + " NVARCHAR, "
				+ TABLE_USER_COLLUM_GENDER + " NVARCHAR, "
				+ TABLE_USER_COLLUM_FOLLOWING + " NVARCHAR, "
				+ TABLE_USER_COLLUM_FOLLOWER + " NVARCHAR, "
				+ TABLE_USER_COLLUM_FACEBOOKID + " NVARCHAR, "

				+ TABLE_USER_COLLUM_MODIFIEDDATE + " NVARCHAR "

				+ ")";

		String createTableVehicle = "CREATE TABLE " + TABLE_VEHICLE + " ( "
				+ TABLE_VEHICLE_COLLUM_ID + " NVARCHAR NOT NULL PRIMARY KEY, "
				+ TABLE_VEHICLE_COLLUM_NAME + " NVARCHAR, "

				+ TABLE_VEHICLE_COLLUM_MODIFIEDDATE + " NVARCHAR "

				+ ")";

		String createTableMessage = "CREATE TABLE " + TABLE_MESSAGE + " ( "
				+ TABLE_MESSAGE_COLLUM_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ TABLE_MESSAGE_COLLUM_CURRENT_USER + " NVARCHAR, "
				+ TABLE_MESSAGE_COLLUM_CONTACT_USER + " NVARCHAR, "
				+ TABLE_MESSAGE_COLLUM_LAST_MESSAGE + " NVARCHAR, "
				+ TABLE_MESSAGE_COLLUM_NUMBER_UNREAD + " NVARCHAR, "
				+ TABLE_MESSAGE_COLLUM_MODIFIEDDATE + " NVARCHAR " + ")";

		db.execSQL(createTableComment);
		db.execSQL(createTableCategory);
		db.execSQL(createTableEntry);
		db.execSQL(createTableHost);
		db.execSQL(createTableJourney);
		db.execSQL(createTableTipsQuestion);
		db.execSQL(createTableTransport);
		db.execSQL(createTableUser);
		db.execSQL(createTableVehicle);
		db.execSQL(createTableMessage);

	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOST);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURNEY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPS_QUESTION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSPORT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICLE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);

		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations of Category Table
	 */
	public void addCategory(Category category) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_CATEGORY_COLLUM_ID, category.getCategoryID());
		values.put(TABLE_CATEGORY_COLLUM_NAME, category.getCategoryName());

		values.put(TABLE_CATEGORY_COLLUM_MODIFYDATE, category.getModifiedDate());
		// Inserting Row
		db.insert(TABLE_CATEGORY, null, values);
		db.close(); // Closing database connection
	}

	// Getting single contact
	public Category getCategoryInfor(String categoryID) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CATEGORY, new String[] {
				TABLE_CATEGORY_COLLUM_ID, TABLE_CATEGORY_COLLUM_NAME,
				TABLE_CATEGORY_COLLUM_MODIFYDATE }, TABLE_CATEGORY_COLLUM_ID
				+ "=?", new String[] { categoryID }, null, null, null, null);
		Category category = null;
		if (cursor != null) {

			cursor.moveToFirst();
			if (cursor.moveToFirst()) {
				category = new Category(cursor.getString(0),
						cursor.getString(1), cursor.getString(2)

				);

			} else {
			}
		}
		// return contact
		return category;
	}

	// Getting All Contacts
	public List<Category> getAllCategory() {
		List<Category> categoryList = new ArrayList<Category>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {

				Category category = new Category(cursor.getString(0),
						cursor.getString(1), cursor.getString(2));

				// Adding to list
				categoryList.add(category);
			} while (cursor.moveToNext());
		}

		// return list
		return categoryList;
	}

	// Updating single
	public int updateCategory(Category category) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_CATEGORY_COLLUM_ID, category.getCategoryID());
		values.put(TABLE_CATEGORY_COLLUM_NAME, category.getCategoryName());
		values.put(TABLE_CATEGORY_COLLUM_MODIFYDATE, category.getModifiedDate());

		// updating row
		return db.update(TABLE_CATEGORY, values, TABLE_CATEGORY_COLLUM_ID
				+ " = ?", new String[] { category.getCategoryID() });
	}

	// Deleting single
	public void deleteCategory(Category category) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CATEGORY, TABLE_CATEGORY_COLLUM_ID + " = ?",
				new String[] { category.getCategoryID() });

		// db.close();
	}

	// Getting Count
	public int getCategoryCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CATEGORY;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		return cursor.getCount();
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations of Comment Table
	 */
	public void addComment(Comment comment) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_COMMENT_COLLUM_ID, comment.getCommentId());
		values.put(TABLE_COMMENT_COLLUM_TEXT, comment.getText());
		values.put(TABLE_COMMENT_COLLUM_USERNAME, comment.getUsername());
		values.put(TABLE_COMMENT_COLLUM_DATETIME, comment.getDateTime());
		values.put(TABLE_COMMENT_COLLUM_MODIFIEDDATE, comment.getModifiedDate());

		// Inserting Row
		db.insert(TABLE_COMMENT, null, values);
		db.close(); // Closing database connection
	}

	// Getting single contact
	public Comment getCommentInfor(String commentID) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_COMMENT, new String[] {
				TABLE_COMMENT_COLLUM_ID, TABLE_COMMENT_COLLUM_TEXT,
				TABLE_COMMENT_COLLUM_USERNAME, TABLE_COMMENT_COLLUM_DATETIME,
				TABLE_COMMENT_COLLUM_MODIFIEDDATE }, TABLE_COMMENT_COLLUM_ID
				+ "=?", new String[] { commentID }, null, null, null, null);
		Comment comment = null;
		if (cursor != null) {

			cursor.moveToFirst();
			if (cursor.moveToFirst()) {
				comment = new Comment(cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4));

			} else {
			}
		}
		// return contact
		return comment;
	}

	// Getting All Contacts
	public List<Comment> getAllComment() {
		List<Comment> comments = new ArrayList<Comment>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_COMMENT;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {

				Comment comment = new Comment(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4));

				// Adding to list
				comments.add(comment);
			} while (cursor.moveToNext());
		}

		// return list
		return comments;
	}

	// Updating single
	public int updateComment(Comment comment) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_COMMENT_COLLUM_ID, comment.getCommentId());
		values.put(TABLE_COMMENT_COLLUM_TEXT, comment.getText());
		values.put(TABLE_COMMENT_COLLUM_USERNAME, comment.getUsername());
		values.put(TABLE_COMMENT_COLLUM_DATETIME, comment.getDateTime());
		values.put(TABLE_COMMENT_COLLUM_MODIFIEDDATE, comment.getModifiedDate());

		// updating row
		return db.update(TABLE_COMMENT, values, TABLE_COMMENT_COLLUM_ID
				+ " = ?", new String[] { comment.getCommentId() });
	}

	// Deleting single
	public void deleteComment(Comment comment) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_COMMENT, TABLE_COMMENT_COLLUM_ID + " = ?",
				new String[] { comment.getCommentId() });

		// db.close();
	}

	// Getting Count
	public int getCommentCount() {
		String countQuery = "SELECT  * FROM " + TABLE_COMMENT;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		return cursor.getCount();
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations of Entry Table
	 */
	public void addEntry(Entry entry) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_ENTRY_COLLUM_ID, entry.getEntryID());
		values.put(TABLE_ENTRY_COLLUM_TYPE, entry.getType());
		values.put(TABLE_ENTRY_COLLUM_COORDINATE, entry.getCoordinate());
		values.put(TABLE_ENTRY_COLLUM_TEXT, entry.getText());
		values.put(TABLE_ENTRY_COLLUM_PATH, entry.getPath());
		values.put(TABLE_ENTRY_COLLUM_IPLACEID, entry.getIplaceID());
		values.put(TABLE_ENTRY_COLLUM_JOURNEYID, entry.getJourneyID());
		values.put(TABLE_ENTRY_COLLUM_DATETIME, entry.getDateTime());
		values.put(TABLE_ENTRY_COLLUM_MODIFIEDDATE, entry.getModifiedDate());
		values.put(TABLE_ENTRY_COLLUM_PLACENAME, entry.getPlaceName());
		values.put(TABLE_ENTRY_COLLUM_VEHICLE, entry.getVehicle());
		values.put(TABLE_ENTRY_COLLUM_STARTPOINT, entry.getStartPoint());
		values.put(TABLE_ENTRY_COLLUM_SYNCDATE, entry.getSyncDate());
		values.put(TABLE_ENTRY_COLLUM_CHANGEDELEMENT, entry.getChangedElement());
		values.put(TABLE_ENTRY_COLLUM_ADDRESS, entry.getAddress());
		values.put(TABLE_ENTRY_COLLUM_STREETNUMBER, entry.getStreet_number());
		values.put(TABLE_ENTRY_COLLUM_ROUTE, entry.getRoute());
		values.put(TABLE_ENTRY_COLLUM_SUBLOCALITY, entry.getSubLocality());
		values.put(TABLE_ENTRY_COLLUM_LOCALITY, entry.getLocality());
		values.put(TABLE_ENTRY_COLLUM_AREALVL2,
				entry.getAdministrative_area_level_2());
		values.put(TABLE_ENTRY_COLLUM_AREALVL1,
				entry.getAdministrative_area_level_1());
		values.put(TABLE_ENTRY_COLLUM_COUNTRY, entry.getCountry());
		values.put(TABLE_ENTRY_COLLUM_SERVERENTRYID, entry.getServerEntryID());

		// Inserting Row
		db.insert(TABLE_ENTRY, null, values);
		db.close(); // Closing database connection
	}

	// Getting single contact
	public Entry getEntryInfor(String entryID) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_ENTRY, new String[] {
				TABLE_ENTRY_COLLUM_ID, TABLE_ENTRY_COLLUM_TYPE,
				TABLE_ENTRY_COLLUM_COORDINATE, TABLE_ENTRY_COLLUM_TEXT,
				TABLE_ENTRY_COLLUM_PATH, TABLE_ENTRY_COLLUM_IPLACEID,
				TABLE_ENTRY_COLLUM_JOURNEYID, TABLE_ENTRY_COLLUM_DATETIME,
				TABLE_ENTRY_COLLUM_MODIFIEDDATE, TABLE_ENTRY_COLLUM_PLACENAME,
				TABLE_ENTRY_COLLUM_VEHICLE, TABLE_ENTRY_COLLUM_STARTPOINT,
				TABLE_ENTRY_COLLUM_SYNCDATE, TABLE_ENTRY_COLLUM_CHANGEDELEMENT,
				TABLE_ENTRY_COLLUM_ADDRESS, TABLE_ENTRY_COLLUM_STREETNUMBER,
				TABLE_ENTRY_COLLUM_ROUTE, TABLE_ENTRY_COLLUM_SUBLOCALITY,
				TABLE_ENTRY_COLLUM_LOCALITY, TABLE_ENTRY_COLLUM_AREALVL2,
				TABLE_ENTRY_COLLUM_AREALVL1, TABLE_ENTRY_COLLUM_COUNTRY,
				TABLE_ENTRY_COLLUM_SERVERENTRYID }, TABLE_ENTRY_COLLUM_ID
				+ "=?", new String[] { entryID }, null, null, null, null);
		Entry entry = null;
		if (cursor != null) {

			cursor.moveToFirst();
			if (cursor.moveToFirst()) {
				entry = new Entry(cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5),
						cursor.getString(6), cursor.getString(7),
						cursor.getString(8), cursor.getString(9),
						cursor.getString(10), cursor.getString(11),
						cursor.getString(12), cursor.getString(13),
						cursor.getString(14), cursor.getString(15),
						cursor.getString(16), cursor.getString(17),
						cursor.getString(18), cursor.getString(19),
						cursor.getString(20), cursor.getString(21),
						cursor.getString(22));

			} else {
			}
		}
		cursor.close();
		db.close();
		// return contact
		return entry;
	}

	// Getting All Entries
	public ArrayList<Entry> getAllEntry() {
		ArrayList<Entry> entries = new ArrayList<Entry>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_ENTRY;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Entry entry = new Entry(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4),
						cursor.getString(5), cursor.getString(6),
						cursor.getString(7), cursor.getString(8),
						cursor.getString(9), cursor.getString(10),
						cursor.getString(11), cursor.getString(12),
						cursor.getString(13), cursor.getString(14),
						cursor.getString(15), cursor.getString(16),
						cursor.getString(17), cursor.getString(18),
						cursor.getString(19), cursor.getString(20),
						cursor.getString(21), cursor.getString(22));

				// Adding to list
				entries.add(entry);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		// return list
		return entries;
	}

	// Getting All Checkin
	public ArrayList<Entry> getAllCheckin() {
		ArrayList<Entry> entries = new ArrayList<Entry>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_ENTRY + " WHERE "
				+ TABLE_ENTRY_COLLUM_TYPE + " = 'checkin' ORDER BY "
				+ TABLE_ENTRY_COLLUM_MODIFIEDDATE + " DESC";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Entry entry = new Entry(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4),
						cursor.getString(5), cursor.getString(6),
						cursor.getString(7), cursor.getString(8),
						cursor.getString(9), cursor.getString(10),
						cursor.getString(11), cursor.getString(12),
						cursor.getString(13), cursor.getString(14),
						cursor.getString(15), cursor.getString(16),
						cursor.getString(17), cursor.getString(18),
						cursor.getString(19), cursor.getString(20),
						cursor.getString(21), cursor.getString(22));

				// Adding to list
				entries.add(entry);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		// return list
		return entries;
	}

	// Updating single
	public int updateEntry(Entry entry) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_ENTRY_COLLUM_ID, entry.getEntryID());
		values.put(TABLE_ENTRY_COLLUM_TYPE, entry.getType());
		values.put(TABLE_ENTRY_COLLUM_COORDINATE, entry.getCoordinate());
		values.put(TABLE_ENTRY_COLLUM_TEXT, entry.getText());
		values.put(TABLE_ENTRY_COLLUM_PATH, entry.getPath());
		values.put(TABLE_ENTRY_COLLUM_IPLACEID, entry.getIplaceID());
		values.put(TABLE_ENTRY_COLLUM_JOURNEYID, entry.getJourneyID());
		values.put(TABLE_ENTRY_COLLUM_DATETIME, entry.getDateTime());
		values.put(TABLE_ENTRY_COLLUM_MODIFIEDDATE, entry.getModifiedDate());
		values.put(TABLE_ENTRY_COLLUM_PLACENAME, entry.getPlaceName());
		values.put(TABLE_ENTRY_COLLUM_VEHICLE, entry.getVehicle());
		values.put(TABLE_ENTRY_COLLUM_STARTPOINT, entry.getStartPoint());
		values.put(TABLE_ENTRY_COLLUM_SYNCDATE, entry.getSyncDate());
		values.put(TABLE_ENTRY_COLLUM_CHANGEDELEMENT, entry.getChangedElement());
		values.put(TABLE_ENTRY_COLLUM_ADDRESS, entry.getAddress());
		values.put(TABLE_ENTRY_COLLUM_STREETNUMBER, entry.getStreet_number());
		values.put(TABLE_ENTRY_COLLUM_ROUTE, entry.getRoute());
		values.put(TABLE_ENTRY_COLLUM_SUBLOCALITY, entry.getSubLocality());
		values.put(TABLE_ENTRY_COLLUM_LOCALITY, entry.getLocality());
		values.put(TABLE_ENTRY_COLLUM_AREALVL2,
				entry.getAdministrative_area_level_2());
		values.put(TABLE_ENTRY_COLLUM_AREALVL1,
				entry.getAdministrative_area_level_1());
		values.put(TABLE_ENTRY_COLLUM_COUNTRY, entry.getCountry());
		values.put(TABLE_ENTRY_COLLUM_SERVERENTRYID, entry.getServerEntryID());
		int result = db.update(TABLE_ENTRY, values, TABLE_ENTRY_COLLUM_ID
				+ " = ?", new String[] { entry.getEntryID() });
		db.close();
		// updating row
		return result;
	}

	// Deleting single
	public void deleteEntry(Entry entry) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ENTRY, TABLE_ENTRY_COLLUM_ID + " = ?",
				new String[] { entry.getEntryID() });

		db.close();
	}

	// Getting Count
	public int getEntryCount() {
		String countQuery = "SELECT  * FROM " + TABLE_ENTRY;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		db.close();

		return cursor.getCount();
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations of Host Table
	 */
	public void addHost(Host host) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_HOST_COLLUM_ID, host.getHostID());
		values.put(TABLE_HOST_COLLUM_USERID, host.getUserID());
		values.put(TABLE_HOST_COLLUM_AREAID, host.getAreaID());
		values.put(TABLE_HOST_COLLUM_USERANDRATE, host.getUserAndRate());
		values.put(TABLE_HOST_COLLUM_DESCRIPTION, host.getDesctiprion());
		values.put(TABLE_HOST_COLLUM_STATUS, host.getStatus());
		values.put(TABLE_HOST_COLLUM_COMMENTID, host.getCommentId());
		values.put(TABLE_HOST_COLLUM_ACTIVATETIME, host.getActiveTime());
		values.put(TABLE_HOST_COLLUM_HOSTTIPS, host.getHostTips());
		values.put(TABLE_HOST_COLLUM_MODIFIEDDATE, host.getModifiedDate());
		// Inserting Row
		db.insert(TABLE_HOST, null, values);
		db.close(); // Closing database connection
	}

	// Getting single contact
	public Host getHostInfor(String hostID) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_HOST, new String[] {
				TABLE_HOST_COLLUM_ID, TABLE_HOST_COLLUM_USERID,
				TABLE_HOST_COLLUM_AREAID, TABLE_HOST_COLLUM_USERANDRATE,
				TABLE_HOST_COLLUM_DESCRIPTION, TABLE_HOST_COLLUM_STATUS,
				TABLE_HOST_COLLUM_COMMENTID, TABLE_HOST_COLLUM_ACTIVATETIME,
				TABLE_HOST_COLLUM_HOSTTIPS, TABLE_HOST_COLLUM_HOSTTIPS },
				TABLE_HOST_COLLUM_ID + "=?", new String[] { hostID }, null,
				null, null, null);
		Host host = null;
		if (cursor != null) {

			cursor.moveToFirst();
			if (cursor.moveToFirst()) {
				host = new Host(cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5),
						cursor.getString(6), cursor.getString(7),
						cursor.getString(8), cursor.getString(9));

			} else {
			}
		}
		// return
		return host;
	}

	// Getting All
	public List<Host> getAllHost() {
		List<Host> hosts = new ArrayList<Host>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_HOST;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {

				Host host = new Host(cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5),
						cursor.getString(6), cursor.getString(7),
						cursor.getString(8), cursor.getString(9));

				// Adding to list
				hosts.add(host);
			} while (cursor.moveToNext());
		}

		// return list
		return hosts;
	}

	// Updating single
	public int updateHost(Host host) {
		int id = -1;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_HOST_COLLUM_ID, host.getHostID());
		values.put(TABLE_HOST_COLLUM_USERID, host.getUserID());
		values.put(TABLE_HOST_COLLUM_AREAID, host.getAreaID());
		values.put(TABLE_HOST_COLLUM_USERANDRATE, host.getUserAndRate());
		values.put(TABLE_HOST_COLLUM_DESCRIPTION, host.getDesctiprion());
		values.put(TABLE_HOST_COLLUM_STATUS, host.getStatus());
		values.put(TABLE_HOST_COLLUM_COMMENTID, host.getCommentId());
		values.put(TABLE_HOST_COLLUM_ACTIVATETIME, host.getActiveTime());
		values.put(TABLE_HOST_COLLUM_HOSTTIPS, host.getHostTips());
		values.put(TABLE_HOST_COLLUM_MODIFIEDDATE, host.getModifiedDate());

		// updating row
		id = db.update(TABLE_ENTRY, values, TABLE_ENTRY_COLLUM_ID + " = ?",
				new String[] { host.getHostID() });
		db.close();
		return id;
	}

	// Deleting single
	public void deleteHost(Host host) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_HOST, TABLE_HOST_COLLUM_ID + " = ?",
				new String[] { host.getHostID() });

		db.close();
	}

	// Getting Count
	public int getHostCount() {
		String countQuery = "SELECT  * FROM " + TABLE_HOST;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		db.close();

		return cursor.getCount();
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations of Journey Table
	 */
	public long addJourney(Journey journey) {
		long id = -1;
		SQLiteDatabase db = null;
		try {
			db = this.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put(TABLE_JOURNEY_COLLUM_ID, journey.getJourneyID());
			values.put(TABLE_JOURNEY_COLLUM_JOURNEYNAME,
					journey.getJourneyName());
			values.put(TABLE_JOURNEY_COLLUM_USERLIKED, journey.getUserLiked());
			values.put(TABLE_JOURNEY_COLLUM_COMMENTID, journey.getCommentID());
			values.put(TABLE_JOURNEY_COLLUM_USERID, journey.getUserID());
			values.put(TABLE_JOURNEY_COLLUM_ENTRIESID, journey.getEntriesID());
			values.put(TABLE_JOURNEY_COLLUM_SHARED, journey.getShared());
			values.put(TABLE_JOURNEY_COLLUM_CREATEDDATE,
					journey.getCreatedDate());
			values.put(TABLE_JOURNEY_COLLUM_MODIFIEDDATE,
					journey.getModifiedDate());
			values.put(TABLE_JOURNEY_COLLUM_SYNCEDDATE, journey.getSyncedDate());
			values.put(TABLE_JOURNEY_COLLUM_SERVERJOURNEYID,
					journey.getServerJourneyID());
			// Inserting Row
			id = db.insert(TABLE_JOURNEY, null, values);
		} catch (Exception e) {

		} finally {
			db.close();
		}
		return id;
	}

	// Getting single contact
	public Journey getJourneyInfor(String journeyID) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_JOURNEY, new String[] {
				TABLE_JOURNEY_COLLUM_ID, TABLE_JOURNEY_COLLUM_JOURNEYNAME,
				TABLE_JOURNEY_COLLUM_USERLIKED, TABLE_JOURNEY_COLLUM_COMMENTID,
				TABLE_JOURNEY_COLLUM_USERID, TABLE_JOURNEY_COLLUM_ENTRIESID,
				TABLE_JOURNEY_COLLUM_SHARED, TABLE_JOURNEY_COLLUM_CREATEDDATE,
				TABLE_JOURNEY_COLLUM_MODIFIEDDATE,
				TABLE_JOURNEY_COLLUM_SYNCEDDATE,
				TABLE_JOURNEY_COLLUM_SERVERJOURNEYID }, TABLE_JOURNEY_COLLUM_ID
				+ "=?", new String[] { journeyID }, null, null, null, null);
		Journey journey = null;
		if (cursor != null) {

			cursor.moveToFirst();
			if (cursor.moveToFirst()) {
				journey = new Journey(cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5),
						cursor.getString(6), cursor.getString(7),
						cursor.getString(8), cursor.getString(9),
						cursor.getString(10));

			} else {
			}
		}
		cursor.close();
		db.close();
		// return
		return journey;
	}

	public Journey getJourneyInforServerID(String journeyID) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_JOURNEY, new String[] {
				TABLE_JOURNEY_COLLUM_ID, TABLE_JOURNEY_COLLUM_JOURNEYNAME,
				TABLE_JOURNEY_COLLUM_USERLIKED, TABLE_JOURNEY_COLLUM_COMMENTID,
				TABLE_JOURNEY_COLLUM_USERID, TABLE_JOURNEY_COLLUM_ENTRIESID,
				TABLE_JOURNEY_COLLUM_SHARED, TABLE_JOURNEY_COLLUM_CREATEDDATE,
				TABLE_JOURNEY_COLLUM_MODIFIEDDATE,
				TABLE_JOURNEY_COLLUM_SYNCEDDATE,
				TABLE_JOURNEY_COLLUM_SERVERJOURNEYID },
				TABLE_JOURNEY_COLLUM_SERVERJOURNEYID + "=?",
				new String[] { journeyID }, null, null, null, null);
		Journey journey = null;
		if (cursor != null) {

			cursor.moveToFirst();
			if (cursor.moveToFirst()) {
				journey = new Journey(cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5),
						cursor.getString(6), cursor.getString(7),
						cursor.getString(8), cursor.getString(9),
						cursor.getString(10));

			} else {
			}
		}
		cursor.close();
		db.close();
		// return
		return journey;
	}

	// Getting All
	public ArrayList<Journey> getAllJourneys() {
		ArrayList<Journey> journeys = new ArrayList<Journey>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_JOURNEY;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Journey journey = new Journey(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4),
						cursor.getString(5), cursor.getString(6),
						cursor.getString(7), cursor.getString(8),
						cursor.getString(9), cursor.getString(10));

				// Adding to list
				journeys.add(journey);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		// return list
		return journeys;
	}

	// Updating single
	public int updateJourney(Journey journey) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_JOURNEY_COLLUM_ID, journey.getJourneyID());
		values.put(TABLE_JOURNEY_COLLUM_JOURNEYNAME, journey.getJourneyName());
		values.put(TABLE_JOURNEY_COLLUM_USERLIKED, journey.getUserLiked());
		values.put(TABLE_JOURNEY_COLLUM_COMMENTID, journey.getCommentID());
		values.put(TABLE_JOURNEY_COLLUM_USERID, journey.getUserID());
		values.put(TABLE_JOURNEY_COLLUM_ENTRIESID, journey.getEntriesID());
		values.put(TABLE_JOURNEY_COLLUM_SHARED, journey.getShared());
		values.put(TABLE_JOURNEY_COLLUM_CREATEDDATE, journey.getCreatedDate());
		values.put(TABLE_JOURNEY_COLLUM_MODIFIEDDATE, journey.getModifiedDate());
		values.put(TABLE_JOURNEY_COLLUM_SYNCEDDATE, journey.getSyncedDate());
		values.put(TABLE_JOURNEY_COLLUM_SERVERJOURNEYID,
				journey.getServerJourneyID());
		int result = db.update(TABLE_JOURNEY, values, TABLE_JOURNEY_COLLUM_ID
				+ " = ?", new String[] { journey.getJourneyID() });
		db.close();
		// updating row
		return result;
	}

	// Deleting single
	public void deleteJourney(Journey journey) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_JOURNEY, TABLE_JOURNEY_COLLUM_ID + " = ?",
				new String[] { journey.getJourneyID() });

		db.close();
	}

	// Getting Count
	public int getJourneyCount() {
		String countQuery = "SELECT  * FROM " + TABLE_JOURNEY;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		db.close();

		return cursor.getCount();
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations of TipsQuestion Table
	 */
	public void addTip(TipsQuestion tipsQuestion) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_TIPSQUESTION_COLLUM_ID, tipsQuestion.getQuestionId());
		values.put(TABLE_TIPSQUESTION_COLLUM_CONTENT, tipsQuestion.getContent());
		values.put(TABLE_TIPSQUESTION_COLLUM_MODIFIEDDATE,
				tipsQuestion.getModifiedDate());

		// Inserting Row
		db.insert(TABLE_TIPS_QUESTION, null, values);
		db.close(); // Closing database connection
	}

	// Getting single contact
	public TipsQuestion getTipsQuestionInfor(String tipsQuestionID) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_TIPS_QUESTION, new String[] {
				TABLE_TIPSQUESTION_COLLUM_ID,
				TABLE_TIPSQUESTION_COLLUM_CONTENT,
				TABLE_TIPSQUESTION_COLLUM_MODIFIEDDATE },
				TABLE_TIPSQUESTION_COLLUM_ID + "=?",
				new String[] { tipsQuestionID }, null, null, null, null);
		TipsQuestion tipsQuestion = null;
		if (cursor != null) {

			cursor.moveToFirst();
			if (cursor.moveToFirst()) {
				tipsQuestion = new TipsQuestion(cursor.getString(0),
						cursor.getString(1), cursor.getString(2));

			} else {

			}
		}
		// return
		return tipsQuestion;
	}

	// Getting All
	public List<TipsQuestion> getAllTipsQuestions() {
		List<TipsQuestion> tipsQuestions = new ArrayList<TipsQuestion>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TIPS_QUESTION;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {

				TipsQuestion tipsQuestion = new TipsQuestion(
						cursor.getString(0), cursor.getString(1),
						cursor.getString(2));

				// Adding to list
				tipsQuestions.add(tipsQuestion);
			} while (cursor.moveToNext());
		}

		// return list
		return tipsQuestions;
	}

	// Updating single
	public int updateTipsQuestion(TipsQuestion tipsQuestion) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_TIPSQUESTION_COLLUM_ID, tipsQuestion.getQuestionId());
		values.put(TABLE_TIPSQUESTION_COLLUM_CONTENT, tipsQuestion.getContent());
		values.put(TABLE_TIPSQUESTION_COLLUM_MODIFIEDDATE,
				tipsQuestion.getModifiedDate());

		// updating row
		return db.update(TABLE_TIPS_QUESTION, values,
				TABLE_TIPSQUESTION_COLLUM_ID + " = ?",
				new String[] { tipsQuestion.getQuestionId() });
	}

	// Deleting single
	public void deleteTipsQuestion(TipsQuestion tipsQuestion) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TIPS_QUESTION, TABLE_TIPSQUESTION_COLLUM_ID + " = ?",
				new String[] { tipsQuestion.getQuestionId() });

		// db.close();
	}

	// Getting Count
	public int getTipsQuestionCount() {
		String countQuery = "SELECT  * FROM " + TABLE_TIPS_QUESTION;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		return cursor.getCount();
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations of Transport Table
	 */
	public void addTransport(Transport transport) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_TRANSPORT_COLLUM_ID, transport.getTransportId());
		values.put(TABLE_TRANSPORT_COLLUM_STARTPOINT, transport.getStartPoint());
		values.put(TABLE_TRANSPORT_COLLUM_ENDPOINT, transport.getEndPoint());
		values.put(TABLE_TRANSPORT_COLLUM_VEHICLEID, transport.getVehicleId());
		values.put(TABLE_TRANSPORT_COLLUM_MODIFIEDDATE,
				transport.getModifiedDate());

		// Inserting Row
		db.insert(TABLE_TRANSPORT, null, values);
		db.close(); // Closing database connection
	}

	// Getting single contact
	public Transport getTransportInfor(String transportID) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_TRANSPORT, new String[] {
				TABLE_TRANSPORT_COLLUM_ID, TABLE_TRANSPORT_COLLUM_STARTPOINT,
				TABLE_TRANSPORT_COLLUM_ENDPOINT,
				TABLE_TRANSPORT_COLLUM_VEHICLEID,
				TABLE_TRANSPORT_COLLUM_MODIFIEDDATE },
				TABLE_TRANSPORT_COLLUM_ID + "=?", new String[] { transportID },
				null, null, null, null);
		Transport transport = null;
		if (cursor != null) {

			cursor.moveToFirst();
			if (cursor.moveToFirst()) {
				transport = new Transport(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4));

			} else {

			}
		}
		// return
		return transport;
	}

	// Getting All
	public List<Transport> getTransports() {
		List<Transport> transports = new ArrayList<Transport>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TRANSPORT;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Transport transport = new Transport(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4));

				// Adding to list

				transports.add(transport);
			} while (cursor.moveToNext());
		}

		// return list
		return transports;
	}

	// Updating single
	public int updateTransport(Transport transport) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_TRANSPORT_COLLUM_ID, transport.getTransportId());
		values.put(TABLE_TRANSPORT_COLLUM_STARTPOINT, transport.getStartPoint());
		values.put(TABLE_TRANSPORT_COLLUM_ENDPOINT, transport.getEndPoint());
		values.put(TABLE_TRANSPORT_COLLUM_VEHICLEID, transport.getVehicleId());
		values.put(TABLE_TRANSPORT_COLLUM_MODIFIEDDATE,
				transport.getModifiedDate());

		// updating row
		return db.update(TABLE_TRANSPORT, values, TABLE_TRANSPORT_COLLUM_ID
				+ " = ?", new String[] { transport.getTransportId() });
	}

	// Deleting single
	public void deleteTransport(Transport transport) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TRANSPORT, TABLE_TRANSPORT_COLLUM_ID + " = ?",
				new String[] { transport.getTransportId() });

		// db.close();
	}

	// Getting Count
	public int getTransportCount() {
		String countQuery = "SELECT  * FROM " + TABLE_TRANSPORT;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		return cursor.getCount();
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations of User Table
	 */
	public void addUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_USER_COLLUM_ID, user.getUserID());
		values.put(TABLE_USER_COLLUM_USERNAME, user.getUsername());
		values.put(TABLE_USER_COLLUM_PASSWORD, user.getPassword());
		values.put(TABLE_USER_COLLUM_EMAIL, user.getEmail());
		values.put(TABLE_USER_COLLUM_DATEOFBIRTH, user.getDateOfBirth());
		values.put(TABLE_USER_COLLUM_GENDER, user.getGender());
		values.put(TABLE_USER_COLLUM_FOLLOWING, user.getFollowing());
		values.put(TABLE_USER_COLLUM_FOLLOWER, user.getFollower());
		values.put(TABLE_USER_COLLUM_FACEBOOKID, user.getFacebookID());
		values.put(TABLE_USER_COLLUM_MODIFIEDDATE, user.getModifiedDate());
		// Inserting Row
		db.insert(TABLE_TRANSPORT, null, values);
		db.close(); // Closing database connection
	}

	// Getting single contact
	public User getUserInfor(String userID) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_USER, new String[] {
				TABLE_USER_COLLUM_ID, TABLE_USER_COLLUM_USERNAME,
				TABLE_USER_COLLUM_PASSWORD, TABLE_USER_COLLUM_EMAIL,
				TABLE_USER_COLLUM_DATEOFBIRTH, TABLE_USER_COLLUM_GENDER,
				TABLE_USER_COLLUM_FOLLOWING, TABLE_USER_COLLUM_FOLLOWER,
				TABLE_USER_COLLUM_FACEBOOKID, TABLE_USER_COLLUM_MODIFIEDDATE },
				TABLE_TRANSPORT_COLLUM_ID + "=?", new String[] { userID },
				null, null, null, null);
		User user = null;
		if (cursor != null) {

			cursor.moveToFirst();
			if (cursor.moveToFirst()) {
				user = new User(cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5),
						cursor.getString(6), cursor.getString(7),
						cursor.getString(8), cursor.getString(9));

			} else {

			}
		}
		// return
		return user;
	}

	// Getting All
	public List<User> getUsers() {
		List<User> users = new ArrayList<User>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_USER;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				User user = new User(cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5),
						cursor.getString(6), cursor.getString(7),
						cursor.getString(8), cursor.getString(9));

				// Adding to list

				users.add(user);
			} while (cursor.moveToNext());
		}

		// return list
		return users;
	}

	// Updating single
	public int updateUser(User user) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TABLE_USER_COLLUM_ID, user.getUserID());
		values.put(TABLE_USER_COLLUM_USERNAME, user.getUsername());
		values.put(TABLE_USER_COLLUM_PASSWORD, user.getPassword());
		values.put(TABLE_USER_COLLUM_EMAIL, user.getEmail());
		values.put(TABLE_USER_COLLUM_DATEOFBIRTH, user.getDateOfBirth());
		values.put(TABLE_USER_COLLUM_GENDER, user.getGender());
		values.put(TABLE_USER_COLLUM_FOLLOWING, user.getFollowing());
		values.put(TABLE_USER_COLLUM_FOLLOWER, user.getFollower());
		values.put(TABLE_USER_COLLUM_FACEBOOKID, user.getFacebookID());
		values.put(TABLE_USER_COLLUM_MODIFIEDDATE, user.getModifiedDate());

		// updating row
		return db.update(TABLE_USER, values, TABLE_USER_COLLUM_ID + " = ?",
				new String[] { user.getUserID() });
	}

	// Deleting single
	public void deleteUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_USER, TABLE_USER_COLLUM_ID + " = ?",
				new String[] { user.getUserID() });

		// db.close();
	}

	// Getting Count
	public int getUserCount() {
		String countQuery = "SELECT  * FROM " + TABLE_USER;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		return cursor.getCount();
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations of Message Table
	 */
	@SuppressLint("SimpleDateFormat")
	public long addMessage(Conversation conversation) {
		long id = -1;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_MESSAGE_COLLUM_CURRENT_USER,
				conversation.getCurrentUser());
		values.put(TABLE_MESSAGE_COLLUM_CONTACT_USER,
				conversation.getContactUser());
		values.put(TABLE_MESSAGE_COLLUM_LAST_MESSAGE,
				conversation.getLastMessage());
		values.put(TABLE_MESSAGE_COLLUM_NUMBER_UNREAD,
				conversation.getNumberUnread());
		String format = "yyyy-MM-dd'T'HH:mm:ss.SSS";
		String formattedfromGmt = new SimpleDateFormat(format)
				.format(conversation.getModifiedDate());
		values.put(TABLE_MESSAGE_COLLUM_MODIFIEDDATE, formattedfromGmt);

		// Inserting Row
		id = db.insert(TABLE_MESSAGE, null, values);
		db.close(); // Closing database connection
		return id;
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations of Vehicle Table
	 */
	public void addVehicle(Vehicle vehicle) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_VEHICLE_COLLUM_ID, vehicle.getVehicleId());
		values.put(TABLE_VEHICLE_COLLUM_NAME, vehicle.getVehicleName());
		values.put(TABLE_VEHICLE_COLLUM_MODIFIEDDATE, vehicle.getModifiedDate());

		// Inserting Row
		db.insert(TABLE_VEHICLE, null, values);
		db.close(); // Closing database connection
	}

	// Getting single contact
	public Vehicle getVehicleInfor(String vehicleID) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_VEHICLE, new String[] {
				TABLE_VEHICLE_COLLUM_ID, TABLE_VEHICLE_COLLUM_NAME,
				TABLE_VEHICLE_COLLUM_MODIFIEDDATE }, TABLE_VEHICLE_COLLUM_ID
				+ "=?", new String[] { vehicleID }, null, null, null, null);
		Vehicle vehicle = null;
		if (cursor != null) {

			cursor.moveToFirst();
			if (cursor.moveToFirst()) {
				vehicle = new Vehicle(cursor.getString(0), cursor.getString(1),
						cursor.getString(2));

			} else {

			}
		}
		// return
		return vehicle;
	}

	// Getting All
	public List<Vehicle> getVehicles() {
		List<Vehicle> users = new ArrayList<Vehicle>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_VEHICLE;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Vehicle vehicle = new Vehicle(cursor.getString(0),
						cursor.getString(1), cursor.getString(2));

				// Adding to list

				users.add(vehicle);
			} while (cursor.moveToNext());
		}

		// return list
		return users;
	}

	// Updating single
	public int updateVehicle(Vehicle vehicle) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TABLE_VEHICLE_COLLUM_ID, vehicle.getVehicleId());
		values.put(TABLE_VEHICLE_COLLUM_NAME, vehicle.getVehicleName());
		values.put(TABLE_VEHICLE_COLLUM_MODIFIEDDATE, vehicle.getModifiedDate());

		// updating row
		return db.update(TABLE_VEHICLE, values, TABLE_VEHICLE_COLLUM_ID
				+ " = ?", new String[] { vehicle.getVehicleId() });
	}

	// Deleting single
	public void deleteVehicle(Vehicle vehicle) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_VEHICLE, TABLE_VEHICLE_COLLUM_ID + " = ?",
				new String[] { vehicle.getVehicleId() });

		// db.close();
	}

	// Getting Count
	public int getVehicleCount() {
		String countQuery = "SELECT  * FROM " + TABLE_VEHICLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		return cursor.getCount();
	}
}
