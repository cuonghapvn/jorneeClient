/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: LocationFragmentInterestingPlaceMap.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.location;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.fpt.edu.bean.Coordinate;
import com.fpt.edu.bean.InterestingPlace;
import com.fpt.edu.bean.InterestingPlaceEntry;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.customview.CNImageView;
import com.fpt.edu.jornee.utils.Constant;
import com.fpt.edu.jornee.utils.UniversalImageHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class LocationFragmentInterestingPlaceMap extends Fragment implements
		OnMarkerClickListener, OnInfoWindowClickListener, OnMarkerDragListener,
		OnSeekBarChangeListener {

	final int RQS_GooglePlayServices = 1;
	private GoogleMap mMap;
	public SlidingUpPanelLayout slidingUpPanel;
	private RelativeLayout upPanel;
	private ListView listEntryInterestPlace;
	List<Overlay> mapOverlays;
	AddItemizedOverlay itemizedOverlay;
	OverlayItem overlayitem;
	ArrayList<InterestingPlace> interestingPlaces;
	ImageView ivSource;
	Marker mMarker;
	String interestPlaceResult;
	private final List<Marker> listMarkers = new ArrayList<Marker>();

	ImageButton btnSlideDown;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(
				R.layout.fragment_location_interesting_place_maps, container,
				false);

		setHasOptionsMenu(true);
		Bundle extras = getArguments();
		if (extras != null) {
			interestPlaceResult = extras.getString("interest_place_result");
			getActivity().setTitle(
					extras.getString(Constant.FRAGMENT_TITLE_BUNDLE));			
		}
		try {
			interestingPlaces = parseInterestTingPlaceResult(interestPlaceResult);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (isAdded()) {
			mMap = ((SupportMapFragment) getActivity()
					.getSupportFragmentManager().findFragmentById(
							R.id.location_interest_place_map)).getMap();
		}
		setUpMap();

		slidingUpPanel = (SlidingUpPanelLayout) rootView
				.findViewById(R.id.map_interesting_place_sliding_layout);
		btnSlideDown = (ImageButton) rootView.findViewById(R.id.btnSlideDown);
		btnSlideDown.setVisibility(View.GONE);
		btnSlideDown.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				btnSlideDown.setVisibility(View.GONE);

				slidePanelUp();
			}
		});
		upPanel = (RelativeLayout) rootView
				.findViewById(R.id.upPanel_interest_place);
		listEntryInterestPlace = (ListView) rootView
				.findViewById(R.id.entry_interest_place);

		LayoutParams params = upPanel.getLayoutParams();
		slidingUpPanel.setShadowDrawable(getResources().getDrawable(
				R.drawable.above_shadow));
		slidingUpPanel.setPanelHeight(params.height);
		slidingUpPanel.setAnchorPoint(0.6f);
		slidingUpPanel.setSlidingEnabled(false);
		slidingUpPanel.setDragView(listEntryInterestPlace);
		slidingUpPanel.setEnableDragViewTouchEvents(true);
		mMap.setOnMarkerClickListener(this);
		mMap.setOnInfoWindowClickListener(this);
		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onResume() {
		super.onResume();
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getActivity()
						.getApplicationContext());
		if (resultCode == ConnectionResult.SUCCESS) {

		} else {
			GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
					RQS_GooglePlayServices);
		}

	}

	View insertPhoto(String path) {
		LinearLayout layout = new LinearLayout(getActivity()
				.getApplicationContext());

		layout.setLayoutParams(new LayoutParams(110, 110));
		layout.setGravity(Gravity.CENTER);

		ImageView imageView = new ImageView(getActivity()
				.getApplicationContext());

		imageView.setLayoutParams(new LayoutParams(100, 100));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

		if ("eating".equals(path)) {
			imageView.setImageDrawable(getResources().getDrawable(
					R.drawable.icon_eating));
		} else if ("drinking".equals(path)) {
			imageView.setImageDrawable(getResources().getDrawable(
					R.drawable.icon_drinking));
		} else if ("visiting".equals(path)) {
			imageView.setImageDrawable(getResources().getDrawable(
					R.drawable.icon_visiting));
		} else if ("entertainment".equals(path)) {
			imageView.setImageDrawable(getResources().getDrawable(
					R.drawable.icon_entertaiment));
		} else if ("staying".equals(path)) {
			imageView.setImageDrawable(getResources().getDrawable(
					R.drawable.icon_staying));
		} else if ("emergency".equals(path)) {
			imageView.setImageDrawable(getResources().getDrawable(
					R.drawable.icon_emegercy));
		}
		layout.addView(imageView);
		return layout;
	}

	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}

	class CustomInfoWindowAdapter implements InfoWindowAdapter {

		private final View mWindow;
		private final View mContents;
		ArrayList<String> categories;

		CustomInfoWindowAdapter(ArrayList<String> cates) {
			categories = cates;
			mWindow = getActivity().getLayoutInflater().inflate(
					R.layout.custom_info_window_listview, null);
			mContents = getActivity().getLayoutInflater().inflate(
					R.layout.custom_info_contents, null);
		}

		@Override
		public View getInfoWindow(Marker marker) {
			render(marker, mWindow);
			return mWindow;
		}

		@Override
		public View getInfoContents(Marker marker) {
			render(marker, mContents);
			return mContents;
		}

		private void render(Marker marker, View view) {
			LinearLayout myGallery = (LinearLayout) view
					.findViewById(R.id.mygallery);
			myGallery.removeAllViews();

			for (int j = 0; j < categories.size(); j++) {

				myGallery.addView(insertPhoto(categories.get(j)));

			}
		}
	}

	private void setUpMap() {
		for (int i = 0; i < interestingPlaces.size(); i++) {
			InterestingPlace interestingPlace = interestingPlaces.get(i);
			mMarker = mMap.addMarker(new MarkerOptions()
					.position(
							new LatLng(interestingPlace.getCoordinate().lat,
									interestingPlace.getCoordinate().lng))
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.iplace_show_map))
					.title(interestingPlace.getEntries().get(0).IPlaceName)
					.snippet(interestingPlace.getEntries().get(0).IPlaceName));
			listMarkers.add(mMarker);
			if (isAdded()) {
				int counter = 0;
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				for (Marker m : listMarkers) {
					builder.include(m.getPosition());
					counter++;
				}
				if (counter > 0) {
					LatLngBounds bounds = builder.build();
					int padding = 0;
					WindowManager wm = (WindowManager) getActivity()
							.getSystemService(Context.WINDOW_SERVICE);
					Display display = wm.getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);
					CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(
							bounds, size.x + 10, size.y + 10, padding);
					mMap.moveCamera(cu);
					mMap.animateCamera(cu);
				}
			}
		}
	}

	@Override
	public boolean onMarkerClick(Marker mk) {

		for (int i = 0; i < listMarkers.size(); i++) {
			if (listMarkers.get(i).equals(mk)) {
				slidePanelUp();
				btnSlideDown.setVisibility(View.VISIBLE);

				final ArrayList<String> categories = interestingPlaces.get(i)
						.getCategories();

				mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(
						categories));
				final ArrayList<InterestingPlaceEntry> interestingPlaceEntries = interestingPlaces
						.get(i).getEntries();

				BaseAdapter adapterEntry = new BaseAdapter() {

					@Override
					public int getCount() {
						return interestingPlaceEntries.size();
					}

					@Override
					public Object getItem(int position) {
						return interestingPlaceEntries.get(position);
					}

					@Override
					public long getItemId(int position) {
						return 0;
					}

					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						View vi = LayoutInflater
								.from(parent.getContext())
								.inflate(
										R.layout.location_interestingplace_entry_horizontal_list_item,
										null);

						CNImageView imageView = (CNImageView) vi
								.findViewById(R.id.location_list_entry_interesting_place);

						TextView placeName = (TextView) vi
								.findViewById(R.id.textView_location_list_entry_interesting_place);
						placeName
								.setText(interestingPlaceEntries.get(position).IPlaceName);

						if (isAdded()) {
							UniversalImageHelper.loadImage(
									getActivity(),
									imageView,
									Constant.SERVER_HOST
											+ "medium_"
											+ interestingPlaceEntries
													.get(position).path);
						}
						return vi;
					}

				};
				listEntryInterestPlace.setAdapter(adapterEntry);
				listEntryInterestPlace
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {

								if (isAdded()) {

									Intent i = new Intent(
											getActivity()
													.getApplicationContext(),
											LocationIplaceTransparentImageActivity.class);
									if (interestingPlaceEntries != null) {
										i.putExtra("interestingPlaceEntries",
												interestingPlaceEntries);
										startActivity(i);
									}
								}
							}
						});

			}
		}
		return false;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onMarkerDrag(Marker arg0) {

	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {

	}

	@Override
	public void onMarkerDragStart(Marker arg0) {

	}

	@Override
	public void onInfoWindowClick(Marker arg0) {

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Fragment fragment = (getFragmentManager()
				.findFragmentById(R.id.location_interest_place_map));
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		ft.remove(fragment);
		ft.commit();
	}

	ArrayList<InterestingPlace> parseInterestTingPlaceResult(String JSONresult)
			throws JSONException {
		JSONObject result = new JSONObject(JSONresult);
		JSONArray array = result.getJSONArray("places");
		ArrayList<InterestingPlace> interestingPlaces = new ArrayList<InterestingPlace>();
		ArrayList<String> coordinateStrings = null;
		ArrayList<String> categoryStrings = null;
		ArrayList<InterestingPlaceEntry> interestingPlaceEntries = null;

		for (int i = 0; i < array.length(); i++) {
			InterestingPlace interestingPlace = new InterestingPlace();

			JSONArray list_entries = array.getJSONObject(i).getJSONArray(
					"list_entries");
			JSONArray coordinates = array.getJSONObject(i).getJSONArray(
					"coordinate");
			JSONArray category = array.getJSONObject(i)
					.getJSONArray("category");

			coordinateStrings = new ArrayList<String>(coordinates.length());
			for (int j = 0; j < coordinates.length(); j++) {
				coordinateStrings.add(j, coordinates.get(j).toString());
			}

			categoryStrings = new ArrayList<String>(category.length());
			for (int j = 0; j < category.length(); j++) {
				categoryStrings.add(j, category.get(j).toString());
			}

			interestingPlaceEntries = new ArrayList<InterestingPlaceEntry>(
					list_entries.length());
			for (int j = 0; j < list_entries.length(); j++) {
				InterestingPlaceEntry entry = new InterestingPlaceEntry(
						list_entries.getJSONObject(j).getString("path"),
						list_entries.getJSONObject(j).getString("iplace_name"));
				interestingPlaceEntries.add(entry);
			}

			interestingPlace.setCategories(categoryStrings);
			interestingPlace.setEntries(interestingPlaceEntries);
			interestingPlace.setCoordinate(new Coordinate(Double
					.parseDouble(coordinateStrings.get(0)), Double
					.parseDouble(coordinateStrings.get(1))));
			interestingPlaces.add(interestingPlace);

		}

		return interestingPlaces;

	}

	public void slidePanelUp() {
		if (slidingUpPanel.isExpanded() || slidingUpPanel.isAnchored()) {
			slidingUpPanel.collapsePane();
		} else {
			slidingUpPanel.expandPane(slidingUpPanel.getAnchorPoint());
		}
	}

	public void slidePanelDown() {
		slidingUpPanel.expandPane(slidingUpPanel.getAnchorPoint());

	}

}
