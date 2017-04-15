/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: EditProfileFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.account;

import static com.fpt.edu.jornee.utils.Constant.SERVER_HOST;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.exception.JorneeEmptyInputException;
import com.fpt.edu.jornee.exception.JorneeException;
import com.fpt.edu.jornee.exception.JorneeInvalidEmailException;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.JSONParser;
import com.fpt.edu.jornee.utils.Validator;

public class EditProfileFragment extends Fragment {

	Validator validator;
	Context context;
	RadioButton rdMale;
	TextView changePassword;

	RadioButton rdFemale;
	RadioButton rdOther;
	RadioGroup rdGroup;
	Button btnSubmit;
	Button btnCancel;
	String sex;
	EditText email;
	TextView birthday;
	TextView errorText;
	String strEmail = "";
	String strGender = "";
	String strDob = "";
	DateFormat fmtDateAndTime = DateFormat.getDateInstance();
	JSONParser parser;
	SessionManager session;
	Calendar dateAndTime = Calendar.getInstance();
	DatePickerDialog.OnDateSetListener birthdayPicker = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			dateAndTime.set(Calendar.YEAR, year);
			dateAndTime.set(Calendar.MONTH, monthOfYear);
			dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateLabelBirthDay();
		}
	};

	public EditProfileFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.fragment_account_edit_profile_layout,
						container, false);

		context = rootView.getContext();
		parser = new JSONParser(context);
		validator = new Validator(context);
		rdGroup = (RadioGroup) rootView.findViewById(R.id.radioSex);
		email = (EditText) rootView.findViewById(R.id.edit_profile_email);
		email.setText(strEmail);
		birthday = (TextView) rootView.findViewById(R.id.txtEditProfileDOB);
		changePassword = (TextView) rootView.findViewById(R.id.change_password);
		rdMale = (RadioButton) rootView.findViewById(R.id.radioMale);
		rdFemale = (RadioButton) rootView.findViewById(R.id.radioFemale);
		rdOther = (RadioButton) rootView.findViewById(R.id.radioLGBT);

		Bundle extras = getArguments();
		if (extras != null && extras.containsKey("email")) {
			strEmail = extras.getString("email");
			email.setText(strEmail);

			strGender = extras.getString("gender");
			System.out.println(strGender);
			if ("Male".equals(strGender)) {
				rdGroup.check(R.id.radioMale);

			} else if ("Female".equals(strGender)) {

				rdGroup.check(R.id.radioFemale);
			} else if ("LGBT".equals(strGender)) {

				rdGroup.check(R.id.radioLGBT);
			}
			strDob = extras.getString("dob");
			birthday.setText(strDob);

		}
		changePassword.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				changePassword();

			}
		});
		btnSubmit = (Button) rootView.findViewById(R.id.btnSubmitEditProfile);
		btnCancel = (Button) rootView.findViewById(R.id.btnCancelEditProfile);
		btnCancel.setOnClickListener(new View.OnClickListener() {

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

		btnSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (rdGroup.getCheckedRadioButtonId()) {
				case R.id.radioMale:
					sex = "Male";
					break;
				case R.id.radioFemale:
					sex = "Female";
					break;
				case R.id.radioLGBT:
					sex = "LGBT";
					break;

				default:
					break;
				}
				try {
					if (validator
							.validateEmptyInput(email.getText().toString())
							&& validator.validateEmail(email.getText()
									.toString())) {

						if (!"Click to set".equals(birthday.getText()
								.toString())) {

							LoadResultEditProfile editProfile = new LoadResultEditProfile();
							editProfile.execute();
						} else {
							errorText.setText("Please choose your birthday !");
						}
					}
				} catch (JorneeEmptyInputException e) {
					errorText.setText(e.getMessage());
				} catch (JorneeInvalidEmailException e) {
					errorText.setText(e.getMessage());
				}

			}
		});
		errorText = (TextView) rootView.findViewById(R.id.ErrorEditProfile);
		birthday.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				chooseBirthday();
			}
		});
		session = new SessionManager(context);

		setHasOptionsMenu(true);
		return rootView;

	}

	public void callLogin() {

		Fragment fragment0 = new LoginFragment();

		MainActivity activity = (MainActivity) getActivity();
		activity.replaceFragment(fragment0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

	private class LoadResultEditProfile extends
			AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... input) {

			JSONObject jsonObject = null;

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("token", session
					.getUserDetails().get(SessionManager.KEY_TOKEN)));
			nameValuePairs.add(new BasicNameValuePair("gender", sex));
			nameValuePairs.add(new BasicNameValuePair("email", email.getText()
					.toString()));
			nameValuePairs.add(new BasicNameValuePair("birthday", birthday
					.getText().toString()));

			try {
				jsonObject = parser.makeHttpRequest(SERVER_HOST
						+ "edit_profile", "POST", nameValuePairs);
			} catch (JorneeException e) {
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

							Fragment fragment0 = new UserProfileFragment();
							Bundle bundle = new Bundle();
							bundle.putString(
									Constant.USERNAME_BUNDLE,
									session.getUserDetails().get(
											SessionManager.KEY_USERNAME));
							bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,
									"User Profile");
							fragment0.setArguments(bundle);
							MainActivity activity = (MainActivity) getActivity();
							activity.replaceFragment(fragment0);
						} else if ("fail".equals(result.getString("status"))) {

						} else if ("error".equals(result.getString("status"))) {

							errorText.setText(context.getResources().getString(
									R.string.error_general_error_message));
						}

					} else if ("fail".equals(result.getString("authen_status"))) {
						callLogin();
					} else if ("error"
							.equals(result.getString("authen_status"))) {

						errorText.setText(context.getResources().getString(
								R.string.error_general_error_message));

					}
				} catch (JSONException e) {

					errorText.setText(context.getResources().getString(
							R.string.error_general_error_message));

				}
			}
		}
	}

	private void updateLabelBirthDay() {
		birthday.setText(fmtDateAndTime.format(dateAndTime.getTime()));
	}

	public void chooseBirthday() {
		new DatePickerDialog(context, birthdayPicker,
				dateAndTime.get(Calendar.YEAR),
				dateAndTime.get(Calendar.MONTH),
				dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
	}

	private void changePassword() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle(context.getResources().getString(
				R.string.title_change_password));
		alert.setMessage(context.getResources().getString(
				R.string.message_input_new_password));

		final LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);

		// Set an EditText view to get user input
		final EditText newPassword = new EditText(context);
		final EditText confirmNewPassword = new EditText(context);
		final EditText oldPassword = new EditText(context);

		newPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		confirmNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		oldPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

		final TextView oldPasswordTextView = new TextView(context);
		final TextView newPasswordTextView = new TextView(context);
		final TextView confirmNewPasswordTextView = new TextView(context);

		oldPasswordTextView.setText(context.getResources().getString(
				R.string.hint_old_password));
		newPasswordTextView.setText(context.getResources().getString(
				R.string.hint_new_password));
		confirmNewPasswordTextView.setText(context.getResources().getString(
				R.string.hint_confirm));

		layout.addView(oldPasswordTextView);
		layout.addView(oldPassword);

		layout.addView(newPasswordTextView);
		layout.addView(newPassword);

		layout.addView(confirmNewPasswordTextView);
		layout.addView(confirmNewPassword);

		alert.setView(layout);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				String newPasswordText = newPassword.getText().toString();
				String confirmNewPasswordText = confirmNewPassword.getText()
						.toString();
				String oldPasswordText = oldPassword.getText().toString();

				try {
					if (validator.validateEmptyInput(newPasswordText)
							&& validator
									.validateEmptyInput(confirmNewPasswordText)) {

						if (!(newPasswordText).equals(confirmNewPasswordText)) {
							errorText
									.setText(context
											.getResources()
											.getString(
													R.string.error_password_confirmination_not_match));

						} else {
							HashMap<String, String> user = session
									.getUserDetails();
							String token = user.get(SessionManager.KEY_TOKEN);

							String[] arrayOfInput = new String[4];
							arrayOfInput[0] = token;
							arrayOfInput[1] = oldPasswordText;
							arrayOfInput[2] = newPasswordText;
							arrayOfInput[3] = confirmNewPasswordText;

							LoadResultChangePassword resultChangePassword = new LoadResultChangePassword();
							resultChangePassword.execute(arrayOfInput);

						}

					} else {
						Toast.makeText(
								context,
								context.getResources().getString(
										R.string.error_required_field_message),
								Toast.LENGTH_LONG).show();
					}
				} catch (JorneeEmptyInputException e) {
					errorText.setText(context.getResources().getString(
							R.string.error_required_field_message));
					
				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
	}

	private class LoadResultChangePassword extends
			AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... input) {
			String token = input[0];
			String oldpass = input[1];
			String newpass = input[2];
			String confirm = input[3];

			JSONObject jsonObject = null;

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("token", token));
			nameValuePairs.add(new BasicNameValuePair("old_pass", oldpass));
			nameValuePairs.add(new BasicNameValuePair("new_pass", newpass));

			try {
				jsonObject = parser.makeHttpRequest(SERVER_HOST
						+ "change_password", "POST", nameValuePairs);
			} catch (JorneeException e) {
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

							errorText.setText(context.getResources().getString(
									R.string.message_change_password_success));

						} else if ("fail".equals(result.getString("status"))) {

							errorText
									.setText("Invalid Old-Password. Please try again !!!");
						} else if ("error".equals(result.getString("status"))) {

							errorText.setText(context.getResources().getString(
									R.string.error_general_error_message));
						}

					} else if ("fail".equals(result.getString("authen_status"))) {
						callLogin();
					} else if ("error"
							.equals(result.getString("authen_status"))) {

						errorText.setText(context.getResources().getString(
								R.string.error_general_error_message));

					}
				} catch (JSONException e) {

					errorText.setText(context.getResources().getString(
							R.string.error_general_error_message));

				}

			} else {

				errorText.setText(context.getResources().getString(
						R.string.error_general_error_message));

			}
		}
	}
}
