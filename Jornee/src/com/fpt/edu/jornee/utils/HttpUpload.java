/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HttpUpload.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.account.UserProfileFragment;
import com.fpt.edu.jornee.message.MessagingFragment;
import com.fpt.edu.jornee.session.SessionManager;
import com.fpt.edu.jornee.utils.MyMultipartEntity.ProgressListener;
/***
 * 
 * @author tuan
 *
 *Class use for upload image to server using multipart upload
 */
public class HttpUpload extends AsyncTask<Void, Integer, Void> {
	int serverResponseCode = 0;

	private Context context;
	private String imgPath;

	private HttpClient client;

	SessionManager sessionManager;
	private ProgressDialog pd;
	private long totalSize;
	private FragmentActivity activity;
	// /upload_avatar

	private static final String url = Constant.SERVER_HOST + "upload_avatar";

	public HttpUpload(Context context, String imgPath, FragmentActivity activity) {
		super();
		this.activity = activity;
		this.context = context;
		this.imgPath = imgPath;
		sessionManager = new SessionManager(context);
	}

	@Override
	protected void onPreExecute() {
		// Set timeout parameters
		int timeout = 1000000;
		// HttpParams httpParameters = new BasicHttpParams();
		// HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);
		// HttpConnectionParams.setSoTimeout(httpParameters, timeout);

		client = new DefaultHttpClient();
		client.getParams().setParameter("Connection", "Keep-Alive");
		client.getParams().setParameter("Content-Type", "multipart/form-data;");
		client.getParams().setParameter("http.socket.timeout", timeout);
		client.getParams().setParameter("http.connection.timeout", timeout);

		// We'll use the DefaultHttpClient

		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("Uploading Picture...");
		pd.setCancelable(false);
		pd.show();
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			File file = new File(imgPath);
			HttpPost post = new HttpPost(url);
			MultipartEntity entity = new MyMultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE,
					new ProgressListener() {
						@Override
						public void transferred(long num) {
							publishProgress((int) ((num / (float) totalSize) * 100));
						}
					});
			FormBodyPart bodyPart = new FormBodyPart("token", new StringBody(
					sessionManager.getUserDetails().get(
							SessionManager.KEY_TOKEN)));
			ContentBody cbFile = new FileBody(file, "image/jpeg");
			entity.addPart("upload", cbFile);
			entity.addPart(bodyPart);
			totalSize = entity.getContentLength();
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				String fullRes = EntityUtils.toString(response.getEntity());
				Log.d("DEBUG", fullRes);
			} else {
				Log.d("DEBUG", "HTTP Fail, Response Code: " + statusCode);
			}

		} catch (ConnectTimeoutException e) {
			Log.e("Time out", "Timeout", e);
		} catch (SocketTimeoutException e) {
			Log.e("Time out", " Socket timeout", e);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		// Set the pertange done in the progress dialog
		pd.setProgress((int) (progress[0]));
	}

	@Override
	protected void onPostExecute(Void result) {
		// Dismiss progress dialog
		pd.dismiss();

		Fragment fragment = new UserProfileFragment();
		MainActivity mainActivity = (MainActivity) activity;
		Bundle bundle = new Bundle();
		bundle.putString(Constant.USERNAME_BUNDLE, sessionManager
				.getUserDetails().get(SessionManager.KEY_USERNAME));
		bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, sessionManager
				.getUserDetails().get(SessionManager.KEY_USERNAME));
		bundle.putString("imagePath", imgPath);
		fragment.setArguments(bundle);
		mainActivity.replaceFragment(fragment);

		sessionManager.setOfflineAvatar(imgPath);
	}

}
