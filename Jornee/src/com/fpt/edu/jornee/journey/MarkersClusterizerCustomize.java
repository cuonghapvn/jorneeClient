/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: MarkersClusterizerCustomize.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.journey;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.fpt.edu.bean.Entry;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.customview.CNImageView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkersClusterizerCustomize {
	private static GoogleMap map;
	private static int interval;
	private static final int DEFAULT_INTERVAL = 25;
	private static Context context;

	public static LinkedHashMap<Point, ArrayList<Entry>> clusterMarkers(
			GoogleMap googleMap, ArrayList<Entry> markers, Context context)
			throws ExecutionException, InterruptedException {
		return clusterMarkers(googleMap, markers, DEFAULT_INTERVAL, context);
	}

	@SuppressWarnings("unchecked")
	public static LinkedHashMap<Point, ArrayList<Entry>> clusterMarkers(
			GoogleMap googleMap, ArrayList<Entry> markers, int i, Context cntxt)
			throws ExecutionException, InterruptedException {
		context = cntxt;
		map = googleMap;
		interval = i;
		Projection projection = map.getProjection();
		LinkedHashMap<Entry, Point> points = new LinkedHashMap<Entry, Point>();
		for (Entry markerOptions : markers) {
			points.put(markerOptions,
					projection.toScreenLocation(markerOptions.getPosition()));
		}
		map.clear();

		CheckMarkersTask checkMarkersTask = new CheckMarkersTask();
		checkMarkersTask.execute(points);
		return checkMarkersTask.get();
	}

	private static class CheckMarkersTask
			extends
			AsyncTask<LinkedHashMap<Entry, Point>, Void, LinkedHashMap<Point, ArrayList<Entry>>> {

		private double findDistance(float x1, float y1, float x2, float y2) {
			return Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
		}

		@Override
		protected LinkedHashMap<Point, ArrayList<Entry>> doInBackground(
				LinkedHashMap<Entry, Point>... params) {
			LinkedHashMap<Point, ArrayList<Entry>> clusters = new LinkedHashMap<Point, ArrayList<Entry>>();
			LinkedHashMap<Entry, Point> points = params[0];
			for (Entry markerOptions : points.keySet()) {
				Point point = points.get(markerOptions);
				double minDistance = -1;
				Point nearestPoint = null;
				double currentDistance;
				for (Point existingPoint : clusters.keySet()) {
					currentDistance = findDistance(point.x, point.y,
							existingPoint.x, existingPoint.y);
					if ((currentDistance <= interval)
							&& ((currentDistance < minDistance) || (minDistance == -1))) {
						minDistance = currentDistance;
						nearestPoint = existingPoint;
					}
				}
				if (nearestPoint != null) {
					clusters.get(nearestPoint).add(markerOptions);
				} else {
					ArrayList<Entry> markersForPoint = new ArrayList<Entry>();
					markersForPoint.add(markerOptions);
					clusters.put(point, markersForPoint);
				}
			}
			return clusters;
		}

		@Override
		protected void onPostExecute(
				LinkedHashMap<Point, ArrayList<Entry>> clusters) {
			for (Point point : clusters.keySet()) {
				ArrayList<Entry> markersForPoint = clusters.get(point);
				Entry mainMarker = markersForPoint.get(0);
				MarkerOptions markerOptions = new MarkerOptions();
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				StringBuilder sbuilder = new StringBuilder();
				if (markersForPoint.size() > 1) {
					for (int i = 0; i < markersForPoint.size(); i++) {
						builder.include(markersForPoint.get(i).getPosition());
						sbuilder.append(markersForPoint.get(i).getEntryID());
						if (i < (markersForPoint.size() - 1)) {
							sbuilder.append(";");
						}
					}
					LatLngBounds bounds = builder.build();
					LatLng ne = bounds.northeast;
					LatLng sw = bounds.southwest;
					LatLng center = new LatLng((ne.latitude + sw.latitude) / 2,
							(ne.longitude + sw.longitude) / 2);
					markerOptions.title(sbuilder.toString());
					markerOptions.position(center);
				} else {
					markerOptions.title(mainMarker.getEntryID());
					markerOptions.position(mainMarker.getPosition());
				}
				markerOptions.snippet("" + markersForPoint.size());
				markerOptions.icon(BitmapDescriptorFactory
						.fromBitmap(createDrawableFromView(
								mainMarker, markersForPoint.size())));
				map.addMarker(markerOptions);
			}
		}
	}

	public static ArrayList<Integer> listImageFromSnippet(String snippet) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		StringTokenizer st = new StringTokenizer(snippet, ";");
		while (st.hasMoreElements()) {
			result.add(Integer.parseInt(st.nextElement().toString()));
		}
		return result;
	}

	public static Bitmap getCroppedBitmap(Bitmap bitmap, int count) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawBitmap(bitmap, rect, rect, paint);

		if (count > 1) {
			Paint textPaint = new Paint();
			textPaint.setAntiAlias(true);
			textPaint.setTextAlign(Paint.Align.CENTER);
			textPaint.setTypeface(Typeface.DEFAULT_BOLD);
			textPaint.setColor(Color.MAGENTA);
			float density = context.getResources().getDisplayMetrics().density;
			textPaint.setTextSize(16 * density);

			Paint circlePaint = new Paint();
			circlePaint.setColor(Color.WHITE);
			canvas.drawCircle(bitmap.getWidth() - 10, -10, 5, circlePaint);

			canvas.drawText(String.valueOf(count), bitmap.getWidth() / 2,
					bitmap.getHeight() / 2 + paint.getTextSize() / 3, textPaint);
		}
		return output;
	}

	// Convert a view to bitmap
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static Bitmap createDrawableFromView(Entry mainMarker, int numberOfMarker) {
		View marker = ((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.marker_layout, null);
		TextView numTxt = (TextView) marker.findViewById(R.id.tvNumberOfMarker);
		CNImageView imageView = (CNImageView) marker.findViewById(R.id.ivImage);
		if(mainMarker.getPath()!=null){
			imageView.setRotation(ImageRotateHelper
					.findRotate(mainMarker.getPath()));
			imageView.setImageBitmap(mainMarker.getImage());
		} else {
			imageView.setBackgroundResource(R.drawable.ic_no_image);
		}
		if (numberOfMarker == 1) {
			numTxt.setVisibility(View.INVISIBLE);
		}
		numTxt.setText("" + numberOfMarker);
		marker.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		Bitmap b = Bitmap.createBitmap(marker.getMeasuredWidth(),
				marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		marker.layout(0, 0, marker.getMeasuredWidth(),
				marker.getMeasuredHeight());
		marker.draw(c);
		return b;
	}

	public static Bitmap cropSquare(String url) {
		Bitmap srcBmp = BitmapFactory.decodeFile(url);
		Bitmap dstBmp;
		if (srcBmp.getWidth() >= srcBmp.getHeight()) {

			dstBmp = Bitmap.createBitmap(srcBmp,
					srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2, 0,
					srcBmp.getHeight(), srcBmp.getHeight());

		} else {

			dstBmp = Bitmap.createBitmap(srcBmp, 0, srcBmp.getHeight() / 2
					- srcBmp.getWidth() / 2, srcBmp.getWidth(),
					srcBmp.getWidth());
		}
		dstBmp = Bitmap.createScaledBitmap(dstBmp, 120, 120, false);
		return dstBmp;
	}

}
