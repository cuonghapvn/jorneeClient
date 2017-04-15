/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HomeListEntryFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fpt.edu.bean.Entry;
import com.fpt.edu.jornee.entry.CreateCheckinActivity;
import com.fpt.edu.jornee.entry.EditEntryActivity;
import com.fpt.edu.jornee.entry.ViewDetailEntryActivity;
import com.fpt.edu.jornee.sqllite.DatabaseHandler;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

public final class HomeListEntryFragment extends Fragment {
	GridView listView;
	AllEntriesImageAdapter entryGridAdapter;
	DatabaseHandler databaseHandler;
	ArrayList<Entry> entryList;
	boolean isFirst;

	public static Fragment newInstance() {
		HomeListEntryFragment fragment = new HomeListEntryFragment();

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater
				.inflate(R.layout.fragment_home_view_list_entry_layout,
						container, false);
		databaseHandler = new DatabaseHandler(this.getActivity());
		entryList = databaseHandler.getAllEntry();
		/*
		 * for (Entry entry : entryList) { tmpList.add(entry.getPath()); } try {
		 * imageUrls = tmpList.toArray(imageUrls); } catch (NullPointerException
		 * e) { imageUrls = null; }
		 */

		entryGridAdapter = new AllEntriesImageAdapter(getActivity(),
				R.layout.fragment_all_entry_item_grid_image, entryList);
		listView = (GridView) rootView.findViewById(R.id.allEntriesGridView);
		listView.setVerticalFadingEdgeEnabled(false);
		listView.setAdapter(entryGridAdapter);
		isFirst = true;

		ImageButton addFromCameraButt = (ImageButton) rootView
				.findViewById(R.id.addEntryCameraButt);
		ImageButton addFromGalleryButt = (ImageButton) rootView
				.findViewById(R.id.addEntryGalleryButt);
		ImageButton addTextButt = (ImageButton) rootView
				.findViewById(R.id.addEntryTextButt);
		ImageButton addCheckinButt = (ImageButton) rootView
				.findViewById(R.id.addEntryCheckinButt);

		// final Intent entryCreateIntent = new Intent(getActivity(),
		// CreateEntryActivity.class);
		final Intent entryEditIntent = new Intent(getActivity(),
				EditEntryActivity.class);

		addFromCameraButt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				entryEditIntent.putExtra("action", "camera");
				startActivity(entryEditIntent);
			}
		});

		addFromGalleryButt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				entryEditIntent.putExtra("action", "gallery");
				startActivity(entryEditIntent);
			}
		});

		addTextButt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				entryEditIntent.putExtra("action", "text");
				startActivity(entryEditIntent);
			}
		});

		final Intent entryDetailIntent = new Intent(getActivity(),
				ViewDetailEntryActivity.class);

		final Intent entryCheckinIntent = new Intent(getActivity(),
				CreateCheckinActivity.class);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				if ("checkin".equalsIgnoreCase(entryList.get(position)
						.getType())) {
					entryCheckinIntent.putExtra("action", "edit");
					entryCheckinIntent.putExtra("selectedEntry",
							entryList.get(position));
					startActivity(entryCheckinIntent);
				} else {
					entryDetailIntent.putExtra("selectedEntry",
							entryList.get(position));
					startActivity(entryDetailIntent);
				}

			}
		});

		addCheckinButt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				entryCheckinIntent.putExtra("action", "create");
				startActivity(entryCheckinIntent);
			}
		});

		return rootView;
	}

	public class AllEntriesImageAdapter extends ArrayAdapter<Entry> {
		ArrayList<Entry> entryItems;

		public AllEntriesImageAdapter(Context context, int resource,
				ArrayList<Entry> entryItems) {
			super(context, resource);
			this.entryItems = entryItems;
		}

		public void updateItems(ArrayList<Entry> items) {
			this.entryItems.clear();
			notifyDataSetChanged();
			this.entryItems.addAll(items);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return entryItems.size();

		}

		@Override
		public Entry getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View view = convertView;
			String type;

			if (view == null) {
				view = getActivity().getLayoutInflater().inflate(
						R.layout.fragment_all_entry_item_grid_image, parent,
						false);
				holder = new ViewHolder();
				assert view != null;
				holder.imageView = (ImageView) view
						.findViewById(R.id.entryImage);
				holder.progressBar = (ProgressBar) view
						.findViewById(R.id.entryProgress);
				holder.entryText = (TextView) view
						.findViewById(R.id.entryGridTextView);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			try {
				type = entryItems.get(position).getType();
				if ("image".equalsIgnoreCase(type)) {
					holder.entryText.setVisibility(View.GONE);
					holder.imageView.setVisibility(View.VISIBLE);
					UniversalImageHelper.loadThumbnail(getActivity()
							.getApplicationContext(), holder.imageView,
							entryItems.get(position).getPath());
				} else if ("text".equalsIgnoreCase(type)) {
					holder.imageView.setVisibility(View.GONE);
					holder.entryText.setVisibility(View.VISIBLE);
					holder.entryText
							.setText(entryItems.get(position).getText());

				} else if ("checkin".equalsIgnoreCase(type)) {
					holder.imageView.setVisibility(View.GONE);
					holder.entryText.setVisibility(View.VISIBLE);
					holder.entryText.setText(entryItems.get(position)
							.getVehicle());
					if (entryItems.get(position).getPath() != null
							&& !entryItems.get(position).getPath().isEmpty()) {
						holder.imageView.setVisibility(View.VISIBLE);
						UniversalImageHelper.loadThumbnail(getActivity()
								.getApplicationContext(), holder.imageView,
								entryItems.get(position).getPath());
					}
				}

			} catch (IndexOutOfBoundsException e) {

			}

			return view;
		}

		class ViewHolder {
			ImageView imageView;
			ProgressBar progressBar;
			TextView entryText;
		}

	}

	public void updateEntryGrid() {
		entryList.clear();
		entryList.addAll(databaseHandler.getAllEntry());
		entryGridAdapter.notifyDataSetChanged();
		// listView.invalidateViews();
	}

	@Override
	public void onResume() {
		updateEntryGrid();
		super.onResume();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

}
