/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: HomeFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.fpt.edu.jornee.customview.NonSwipeableViewPager;
import com.fpt.edu.jornee.journey.HomeListJourneyFragment;
import com.fpt.edu.jornee.utils.JSONParser;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class HomeFragment extends Fragment {

	public static NonSwipeableViewPager pager;
	public static FragmentStatePagerAdapter adapter;
	TabPageIndicator indicator;
	private static final String[] CONTENT = new String[] { "Entries",
			"Journeys" };
	Context context;

	JSONParser parser;

	public HomeFragment() {
		// Empty constructor required for fragment subclasses
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home_layout,
				container, false);
		getActivity().setTitle("Home");

		context = getActivity().getApplicationContext();
		setHasOptionsMenu(true);
		parser = new JSONParser(context);

		context = rootView.getContext();
		Bundle bundle = getArguments();

		pager = (NonSwipeableViewPager) rootView.findViewById(R.id.home_pager);
		indicator = (TabPageIndicator) rootView
				.findViewById(R.id.home_indicator);
		adapter = new TapPageAdapter(getChildFragmentManager());
		pager.setAdapter(adapter);
		indicator.setViewPager(pager);
		if (bundle != null && bundle.containsKey("defaultTab")) {
			int tabIndex = bundle.getInt("defaultTab");
			pager.setCurrentItem(tabIndex);
		}

		return rootView;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	class TapPageAdapter extends FragmentStatePagerAdapter implements
			IconPagerAdapter {

		public TapPageAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length].toUpperCase();
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {

			switch (position) {
			case 0:
				return HomeListEntryFragment.newInstance();
			case 1:
				return HomeListJourneyFragment.newInstance();
			}
			return null;

		}

		@Override
		public int getIconResId(int index) {
			return 0;
		}
	}

}
