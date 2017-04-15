/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: RegisterFragment.java
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
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.customview.CNRectangleImageView;
import com.fpt.edu.jornee.exception.JorneeContainSpecialCharacterException;
import com.fpt.edu.jornee.exception.JorneeEmptyInputException;
import com.fpt.edu.jornee.exception.JorneeException;
import com.fpt.edu.jornee.exception.JorneeInputLengthException;
import com.fpt.edu.jornee.exception.JorneeInvalidEmailException;
import com.fpt.edu.jornee.exception.JorneeNotMatchException;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.JSONParser;
import com.fpt.edu.jornee.utils.Validator;

public class RegisterFragment extends Fragment {

	Button registerBtn;
	EditText userName;
	EditText password;
	EditText confirmpassword;
	EditText email;
	Validator validator;
	TextView loginLink;
	TextView errorText;
	JSONParser parser;
	Context context;
	String strUsername;
	String strPassword;
	String strEmail;
	CNRectangleImageView logoregister;
	String strConfirm;

	public RegisterFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_register_layout,
				container, false);

		Bundle bundle = getArguments();
		if (bundle != null
				&& bundle.containsKey(Constant.FRAGMENT_TITLE_BUNDLE)) {
			getActivity().setTitle(
					bundle.getString(Constant.FRAGMENT_TITLE_BUNDLE));
		}

		context = getActivity().getApplicationContext();
		parser = new JSONParser(context);
		validator = new Validator(context);
		userName = (EditText) rootView.findViewById(R.id.reg_fullname);
		password = (EditText) rootView.findViewById(R.id.reg_password);
		email = (EditText) rootView.findViewById(R.id.reg_email);
		logoregister = (CNRectangleImageView) rootView
				.findViewById(R.id.logoregister);

		confirmpassword = (EditText) rootView
				.findViewById(R.id.confirmpassword);
		errorText = (TextView) rootView.findViewById(R.id.ErrorRegister);
		registerBtn = (Button) rootView.findViewById(R.id.btnRegister);
		registerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				InputMethodManager imm = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(confirmpassword.getWindowToken(), 0);
				userName.setFocusable(false);
				email.setFocusable(false);
				confirmpassword.setFocusable(false);
				password.setFocusable(false);

				LoadResultRegister loadResultRegister = new LoadResultRegister();
				loadResultRegister.execute();
			}
		});

		loginLink = (TextView) rootView.findViewById(R.id.link_to_login);

		loginLink.setText(Html
				.fromHtml("Already have an account ! <b>Sign in !</b>"));
		loginLink.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				callLogin();

			}

		});
		setHasOptionsMenu(true);
		return rootView;

	}

	public void callLogin() {

		if (isAdded()) {
			Fragment fragment0 = new LoginFragment();

			MainActivity activity = (MainActivity) getActivity();
			activity.replaceFragment(fragment0);
		}
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

	private JSONObject register() throws JorneeEmptyInputException,
			JorneeInvalidEmailException, JorneeException,
			JorneeNotMatchException, NotFoundException,
			JorneeContainSpecialCharacterException, JorneeInputLengthException {

		strUsername = userName.getText().toString();
		strPassword = password.getText().toString();
		strEmail = email.getText().toString();

		strConfirm = confirmpassword.getText().toString();
		if (validator.validateEmptyInput(strUsername)
				&& validator.validateEmptyInput(strPassword)
				&& validator.validateEmptyInput(strEmail)
				&& validator.validateEmail(strEmail)
				&& validator.validateEmptyInput(strConfirm)
				&& validator.validateMatch(strPassword, strConfirm)
				&& validator.validateContainsSpecialCharacter(strPassword)
				&& validator.validateContainsSpecialCharacter(strUsername)
				&& validator.validateLength(strUsername)
				&& validator.validateLength(strPassword)) {

			JSONObject jsonObject = null;

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

			nameValuePairs.add(new BasicNameValuePair("id", userName.getText()
					.toString()));
			nameValuePairs.add(new BasicNameValuePair("pass", password
					.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("email", email.getText()
					.toString()));

			jsonObject = parser.makeHttpRequest(SERVER_HOST + "register",
					"POST", nameValuePairs);

			return jsonObject;
		}
		return null;

	}

	private class LoadResultRegister extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... place) {

			JSONObject jsonObject = null;
			String result = null;
			try {
				jsonObject = register();
				result = jsonObject.getString("status");
			} catch (Exception e) {
				return e.getMessage();
			}
			return result;

		}

		@Override
		protected void onPostExecute(String result) {

			userName.setFocusableInTouchMode(true);
			email.setFocusableInTouchMode(true);
			confirmpassword.setFocusableInTouchMode(true);
			password.setFocusableInTouchMode(true);
			if (context.getResources()
					.getString(R.string.error_general_error_message)
					.equals(result)) {
				errorText.setText(context.getResources().getString(
						R.string.error_general_error_message));
			} else if (context.getResources()
					.getString(R.string.error_required_field_message)
					.equals(result)) {
				errorText.setText(context.getResources().getString(
						R.string.error_required_field_message));
			} else if (context.getResources()
					.getString(R.string.error_invalid_email_message)
					.equals(result)) {
				errorText.setText(context.getResources().getString(
						R.string.error_invalid_email_message));
			} else if ("id".equals(result)) {
				errorText.setText(context.getResources().getString(
						R.string.error_username_taken_message));

			} else if ("email".equals(result)) {
				errorText.setText(context.getResources().getString(
						R.string.error_email_taken_message));

			} else if ("false".equals(result)) {
				errorText.setText(context.getResources().getString(
						R.string.error_general_error_message));
			} else if (context.getResources()
					.getString(R.string.error_required_field_message)
					.equals(result)) {
				errorText.setText(context.getResources().getString(
						R.string.error_required_field_message));

			} else if (context.getResources()
					.getString(R.string.error_invalid_email_message)
					.equals(result)) {
				errorText.setText(context.getResources().getString(
						R.string.error_invalid_email_message));

			} else if (context.getResources()
					.getString(R.string.error_invalid_not_match).equals(result)) {
				errorText.setText(context.getResources().getString(
						R.string.error_invalid_not_match));

			} else if (context.getResources()
					.getString(R.string.error_input_length).equals(result)) {
				errorText.setText(context.getResources().getString(
						R.string.error_input_length));

			} else if (context.getResources()
					.getString(R.string.error_contain_special_character)
					.equals(result)) {
				errorText.setText(context.getResources().getString(
						R.string.error_contain_special_character));
			}

			else if ("true".equals(result)) {

				callLogin();
				Toast.makeText(
						getActivity(),
						context.getResources().getString(
								R.string.message_register_success),
						Toast.LENGTH_SHORT).show();
				errorText.setText("");
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

}
