/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: ViewFollowFragment.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.account;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fpt.edu.bean.OutsideSearchFriendResult;
import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.utils.Constant;
import com.viewpagerindicator.TabPageIndicator;

public class ViewFollowFragment extends Fragment {

	public static final String SELECTED_TAB = "selectTabs";
	public static final String USERNAME = "username";
	PagerAdapter mSectionsPagerAdapter;
	TabPageIndicator indicator;
	ArrayList<String> tabs;
	ArrayList<OutsideSearchFriendResult> listFollowing;
	ArrayList<OutsideSearchFriendResult> listFollowers;
	ViewPager mViewPager;
	ProgressDialog progressDialog;
	String authen_status = null, status = null;
	JSONArray arrayFollowerResult = null;
	JSONArray arrayFollowingResult = null;
	String username;

	public ViewFollowFragment() {
		// Empty constructor required for fragment subclasses
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if(savedInstanceState!=null){
			bundle = savedInstanceState;
		}
		if (bundle != null
				&& bundle.containsKey(Constant.FRAGMENT_TITLE_BUNDLE)) {
			getActivity().setTitle(
					bundle.getString(Constant.FRAGMENT_TITLE_BUNDLE));
		}
		String selectTabs = getArguments().getString(SELECTED_TAB);
		username = getArguments().getString(USERNAME);
		listFollowing = new ArrayList<OutsideSearchFriendResult>();
		listFollowers = new ArrayList<OutsideSearchFriendResult>();
		View rootView = inflater.inflate(R.layout.fragment_viewfollow_layout,
				container, false);
		setHasOptionsMenu(true);
		tabs = new ArrayList<String>();
		tabs.add("Following");
		tabs.add("Followers");
		mViewPager = (ViewPager) rootView.findViewById(R.id.pagerViewFollow);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), tabs);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		TabPageIndicator indicator = (TabPageIndicator) rootView.findViewById(R.id.indicator);
		indicator.setViewPager(mViewPager);
		ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}

			@Override
			public void onPageSelected(int arg0) {
				
			}
			
		};
		indicator.setOnPageChangeListener(mPageChangeListener);
		if("Following".equals(selectTabs)){
			mViewPager.setCurrentItem(0);
		} else {
			mViewPager.setCurrentItem(1);
		}
		return rootView;

	}
	
	
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		inflater.inflate(R.menu.message, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
		
		ArrayList<String> tabs;

		public SectionsPagerAdapter(FragmentManager fm,
				ArrayList<String> tabsInput) {
			super(fm);
			tabs = tabsInput;
		}

		public Fragment getItem(int position) {
			Fragment fragment = new ViewFollowListFragment();
			Bundle args = new Bundle();
			args.putString("tabsName", tabs.get(position));
			args.putString("username", username);
			fragment.setArguments(args);
			return fragment;
		}

		public int getCount() {
			return tabs.size();
		}
		
		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}
		
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return "Following".toUpperCase(l);
			case 1:
				return "Followers".toUpperCase(l);
			}
			return null;
		}
	}

}
