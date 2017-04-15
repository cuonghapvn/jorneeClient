/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: LoginFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.account;

import static com.fpt.edu.jornee.utils.Constant.SERVER_HOST;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.customview.CNRectangleImageView;
import com.fpt.edu.jornee.exception.JorneeEmptyInputException;
import com.fpt.edu.jornee.exception.JorneeException;
import com.fpt.edu.jornee.exception.JorneeInvalidEmailException;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.JSONParser;
import com.fpt.edu.jornee.utils.Validator;

public class LoginFragment extends Fragment {

	EditText username;
	EditText password;
	TextView linkRegister;
	TextView errorText;
	TextView lostPassword;
	LinearLayout loginButton;
	TextView textViewLogin;
	SessionManager session;
	Context context;
	Validator validator;
	CNRectangleImageView logo;
	JSONParser parser;

	public LoginFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_login_layout,
				container, false);

		Bundle bundle = getArguments();
		if (bundle != null
				&& bundle.containsKey(Constant.FRAGMENT_TITLE_BUNDLE)) {
			getActivity().setTitle(
					bundle.getString(Constant.FRAGMENT_TITLE_BUNDLE));
		}
		context = getActivity().getApplicationContext();
		session = new SessionManager(getActivity().getApplicationContext());

		username = (EditText) rootView.findViewById(R.id.txtUsername_Login);
		password = (EditText) rootView.findViewById(R.id.txt_Password_Login);
		errorText = (TextView) rootView.findViewById(R.id.ErrorLogin);
		logo = (CNRectangleImageView) rootView.findViewById(R.id.logo);
		// logo.setImageResource(R.drawable.jornee_logo_white);
		// UniversalImageHelper.loadImage(context, logo,"drawable://" +
		// R.drawable.jornee_logo_white);
		parser = new JSONParser(context);
		validator = new Validator(context);

		lostPassword = (TextView) rootView.findViewById(R.id.lost_password);
		lostPassword.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				lostPassword();
			}
		});

		loginButton = (LinearLayout) rootView.findViewById(R.id.btnLogin);
		textViewLogin = (TextView) rootView.findViewById(R.id.textViewLogin);
		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				InputMethodManager imm = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(username.getWindowToken(), 0);

				textViewLogin.setText(Html.fromHtml("<b>Loging in...</b>"));

				LoadResultLogin loadResultLogin = new LoadResultLogin();
				loadResultLogin.execute();

			}
		});
		linkRegister = (TextView) rootView
				.findViewById(R.id.link_to_register_login);

		linkRegister.setText(Html
				.fromHtml("Don\'t have an account ?<b>Sign up !</b>"));
		linkRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isAdded()) {
					Fragment fragment0 = new RegisterFragment();
					
					Bundle bundle = new Bundle();
					bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE,"Register");
					fragment0.setArguments(bundle);

					
					
					MainActivity activity = (MainActivity) getActivity();
					activity.replaceFragment(fragment0);
				}
			}
		});

		setHasOptionsMenu(true);
		return rootView;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// inflater.inflate(R.menu.outside, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	public void callLogin() {

		if (isAdded()) {
			Fragment fragment0 = new LoginFragment();

			MainActivity activity = (MainActivity) getActivity();
			activity.replaceFragment(fragment0);
		}
	}

	private class LoadResultLogin extends AsyncTask<String, Void, JSONObject> {
		String error;

		@Override
		protected JSONObject doInBackground(String... str) {

			JSONObject jsonObject = null;
			try {
				jsonObject = login();

			} catch (JorneeException jorneeException) {
				error = jorneeException.getMessage();

				return null;
			} catch (JorneeEmptyInputException emptyInputException) {
				error = emptyInputException.getMessage();
				return null;
			}

			catch (Exception e) {
				error = context.getResources().getString(
						R.string.error_general_error_message);
				return null;
			}

			return jsonObject;

		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// { status: "ok"||"fail"||"error", token: "ttsdfsadwq" }

			try {

				textViewLogin.setText(Html.fromHtml("<b>Login</b>"));

				if (null != result) {
					if (result.getString("status").equals("fail")) {
						errorText.setText(context.getResources().getString(
								R.string.error_login_fail_message));
					} else if (result.getString("status").equals("error")) {
						errorText.setText(context.getResources().getString(
								R.string.error_general_error_message));
					} else {
						if (result.getString("token") != null) {

							session.createLoginSession(result
									.getString("token"), username.getText()
									.toString(), result.getString("avatar"),
									result.getString("is_host"));

							// session.logoutUser(manager);
							getActivity().finish();
							getActivity().startActivity(
									getActivity().getIntent());
						}
						// callProfileFRagment();

					}
				} else {
					errorText.setText(error);

				}

			} catch (JSONException exception) {
				errorText.setText(context.getResources().getString(
						R.string.error_general_error_message));
			}
		}

	}

	private JSONObject login() throws JorneeException,
			JorneeEmptyInputException {

		String strUsername = username.getText().toString();
		String strPassword = password.getText().toString();

		if (validator.validateEmptyInput(strUsername)
				&& validator.validateEmptyInput(strPassword)) {

			JSONObject jsonObject = null;

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

			nameValuePairs.add(new BasicNameValuePair("username", strUsername));
			nameValuePairs.add(new BasicNameValuePair("password", strPassword));

			jsonObject = parser.makeHttpRequest(SERVER_HOST + "login", "POST",
					nameValuePairs);

			return jsonObject;
		}
		return null;

	}

	private void lostPassword() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("Lost Password");
		alert.setMessage("Please Input your email . We will send back your password");

		// Set an EditText view to get user input
		final EditText input = new EditText(context);

		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				String value = input.getText().toString();

				try {
					if (validator.validateEmptyInput(value)) {

						if (validator.validateEmail(value)) {
							LoadResultResetPassword loadResultResetPassword = new LoadResultResetPassword();
							loadResultResetPassword.execute(value);

						}

					}
				} catch (JorneeEmptyInputException e) {
					Toast.makeText(
							context,
							context.getResources().getString(
									R.string.error_required_field_message),
							Toast.LENGTH_LONG).show();
				} catch (JorneeInvalidEmailException e) {
					Toast.makeText(
							context,
							context.getResources().getString(
									R.string.error_invalid_email_message),
							Toast.LENGTH_LONG).show();
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

	private class LoadResultResetPassword extends
			AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... email) {
			String email1 = email[0];

			JSONObject jsonObject = null;

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

			nameValuePairs.add(new BasicNameValuePair("email", email1));

			try {
				jsonObject = parser.makeHttpRequest(SERVER_HOST
						+ "reset_password", "POST", nameValuePairs);
			} catch (JorneeException e) {
				return null;
			}

			return jsonObject;

		}

		@Override
		protected void onPostExecute(JSONObject result) {

			if (null != result) {
				try {
					if ("ok".equals(result.getString("status"))) {

						errorText.setText(context.getResources().getString(
								R.string.message_reset_passwors_success));
					} else if ("fail".equals(result.getString("status"))) {
						errorText.setText(context.getResources().getString(
								R.string.error_invalid_email_message));

					} else if ("error".equals(result.getString("status"))) {
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
