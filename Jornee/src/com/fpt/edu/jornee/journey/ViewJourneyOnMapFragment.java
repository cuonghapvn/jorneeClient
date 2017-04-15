/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: ViewJourneyOnMapFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.journey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.edu.bean.Entry;
import com.fpt.edu.bean.Journey;
import com.fpt.edu.jornee.MainActivity;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.imagehelpers.UrlImageViewHelper;
import com.fpt.edu.jornee.sqllite.DatabaseHandler;
import com.fpt.edu.jornee.utils.Constant;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

public class ViewJourneyOnMapFragment extends Fragment {
	private static final int INTERVAL = 100;
	private float oldZoom = 0;
	public final static String JOURNEY_ID = "journeyID";
	private GoogleMap googleMap;
	String journeyID;
	DatabaseHandler databaseHandler;
	ArrayList<Entry> listEntries;
	private static View rootView;

	public ViewJourneyOnMapFragment() {
		// Empty constructor required for fragment subclasses
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView != null) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null)
				parent.removeView(rootView);
		}
		try {
			rootView = inflater.inflate(R.layout.fragment_journey_detail_onmap,
					container, false);
		} catch (InflateException e) {
		}
		Bundle bundle = getArguments();
		listEntries = new ArrayList<Entry>();
		databaseHandler = new DatabaseHandler(getActivity()
				.getApplicationContext());
		if (bundle != null) {
			if (bundle.containsKey(JOURNEY_ID)) {
				journeyID = bundle.getString(JOURNEY_ID);
				System.out.println("Journey ID: " + journeyID);
				if (journeyID != null) {
					Journey journey = databaseHandler
							.getJourneyInfor(journeyID);
					new LoadData(journey).execute();
				}
			}
		}
		setHasOptionsMenu(true);
		return rootView;
	}

	public void zoomToBounds(LatLngBounds bounds, int padding) {
		googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,
				padding));
	}

	public static int getBoundsZoomLevel(LatLng northeast, LatLng southwest,
			int width, int height) {
		final int GLOBE_WIDTH = 256; // a constant in Google's map projection
		final int ZOOM_MAX = 21;
		double latFraction = (latRad(northeast.latitude) - latRad(southwest.latitude))
				/ Math.PI;
		double lngDiff = northeast.longitude - southwest.longitude;
		double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
		double latZoom = zoom(height, GLOBE_WIDTH, latFraction);
		double lngZoom = zoom(width, GLOBE_WIDTH, lngFraction);
		double zoom = Math.min(Math.min(latZoom, lngZoom), ZOOM_MAX);
		return (int) (zoom);
	}

	private static double latRad(double lat) {
		double sin = Math.sin(lat * Math.PI / 180);
		double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
		return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
	}

	private static double zoom(double mapPx, double worldPx, double fraction) {
		final double LN2 = .693147180559945309417;
		return (Math.log(mapPx / worldPx / fraction) / LN2);
	}

	public Entry getEntryFromList(String entryID) {
		Entry result = null;
		for (Entry item : listEntries) {
			if (entryID.equals(item.getEntryID())) {
				result = item;
			}
		}
		return result;
	}

	class LoadData extends AsyncTask<Void, Void, Void> {
		Journey journey;
		ArrayList<Entry> resultList;

		public LoadData(Journey journey) {
			this.journey = journey;
		}

		@Override
		protected Void doInBackground(Void... params) {
			resultList = new ArrayList<Entry>();
			StringTokenizer tokenizer = new StringTokenizer(
					journey.getEntriesID(), ",");
			Entry fromDatabase;
			while (tokenizer.hasMoreElements()) {
				String id = (String) tokenizer.nextElement();
				fromDatabase = new Entry();
				fromDatabase = databaseHandler.getEntryInfor(id);
				if (fromDatabase != null) {
					if (fromDatabase.getPath() != null) {
						fromDatabase
								.setImage(cropSquare(fromDatabase.getPath()));
					}
					if (fromDatabase.getPosition() != null) {
						resultList.add(fromDatabase);
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			listEntries = resultList;
			Collections.sort(listEntries);
			initilizeMap();
		}

	}

	public static Bitmap cropSquare(String url) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Config.RGB_565;
		options.inDither = true;
		options.inSampleSize = 8;
		Bitmap srcBmp = BitmapFactory.decodeFile(url, options);
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
		dstBmp = Bitmap.createScaledBitmap(dstBmp, 100, 100, false);
		return dstBmp;
	}

	public OnCameraChangeListener getCameraChangeListener() {
		return new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				System.out.println("Zoom level: " + position.zoom);
				if (position.zoom != oldZoom) {
					try {
						MarkersClusterizerCustomize.clusterMarkers(googleMap,
								listEntries, INTERVAL, getActivity()
										.getApplicationContext());
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				oldZoom = position.zoom;
			}
		};
	}

	private void initilizeMap() {
		try {

			if (googleMap != null) {
				googleMap.clear();
			}
			googleMap = ((SupportMapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			googleMap.setMyLocationEnabled(true);
			googleMap.setOnCameraChangeListener(getCameraChangeListener());

			if (googleMap == null) {
				Toast.makeText(getActivity().getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
			MarkersClusterizerCustomize.clusterMarkers(googleMap,
					listEntries, INTERVAL, getActivity()
							.getApplicationContext());
			if (listEntries.size() > 0) {
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						listEntries.get(0).getPosition(), 5));
			}
			googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

				@SuppressWarnings("deprecation")
				@Override
				public View getInfoWindow(Marker marker) {
					View v = new View(getActivity());
					String snipt = marker.getSnippet();
					if (snipt != null) {
						int numOfItem = Integer.parseInt(snipt);

						if (numOfItem == 1) {
							v = getActivity().getLayoutInflater().inflate(
									R.layout.map_marker_info_layout, null);
							TextView txtMarkerInfoTitle = (TextView) v
									.findViewById(R.id.txtMarkerInfoTitle);
							TextView txtMarkerInfoStatus = (TextView) v
									.findViewById(R.id.txtMarkerInfoStatus);
							ImageView imgMarketInfoConver = (ImageView) v
									.findViewById(R.id.imgMarketInfoConver);
							Entry thisEntry = getEntryFromList(marker
									.getTitle());
							if (thisEntry != null) {
								if (thisEntry.getText() != null
										&& thisEntry.getText().trim().length() > 0) {
									txtMarkerInfoStatus.setText(thisEntry
											.getText());
								} else {
									txtMarkerInfoStatus
											.setText("Empty description");
								}
								if (thisEntry.getPath() != null) {
									imgMarketInfoConver.setRotation(ImageRotateHelper.findRotate(thisEntry.getPath()));
									UrlImageViewHelper.setUrlDrawable(
											imgMarketInfoConver,
											thisEntry.getPath());
								} else {
									imgMarketInfoConver.setBackgroundResource(R.drawable.ic_no_image);
								}
							}

						} else {
							v = getActivity()
									.getLayoutInflater()
									.inflate(
											R.layout.map_multi_marker_info_layout,
											null);
							TextView txtMarkerInfoTitle = (TextView) v
									.findViewById(R.id.txtMarkerInfoTitle);
							txtMarkerInfoTitle.setText("Total: " + numOfItem
									+ " entry(s)");
						}
					}
					return v;
				}

				@Override
				public View getInfoContents(Marker arg0) {
					return null;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(),
					"Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		initilizeMap();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.journey_maps, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.detailsview:
			Fragment fragment = new ViewJourneyDetailFragment();
			Bundle bundle = new Bundle();
			bundle.putString(Constant.FRAGMENT_TITLE_BUNDLE, "Home");
			bundle.putString(CreateJourneyFragment.JOURNEY_ID, journeyID);
			fragment.setArguments(bundle);
			MainActivity activity = (MainActivity) getActivity();
			activity.replaceFragmentAndDeleteExisting(fragment);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
