/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: OutsideActivityListView.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.outside;

import java.util.List;
import com.fpt.edu.bean.OutsideActivityBean;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

public class OutsideActivityListView extends ListView implements OnScrollListener{
	private View footer;
	private boolean isLoading;
	private EndlessListener listener;
	private OutsideActivityListAdapter adapter;

	public OutsideActivityListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setOnScrollListener(this);
	}

	public OutsideActivityListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnScrollListener(this);
	}

	public OutsideActivityListView(Context context) {
		super(context);
		this.setOnScrollListener(this);
	}

	public void setListener(EndlessListener listener) {
		this.listener = listener;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		if (getAdapter() == null)
			return;

		if (getAdapter().getCount() == 0)
			return;

		int l = visibleItemCount + firstVisibleItem;
		if (l >= totalItemCount && !isLoading) {
			// It is time to add new data. We call the listener
			this.addFooterView(footer);
			isLoading = true;
			listener.loadData();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	public void setLoadingView(int resId) {
		LayoutInflater inflater = (LayoutInflater) super.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		footer = (View) inflater.inflate(resId, null);
		this.addFooterView(footer);

	}

	public void setAdapter(OutsideActivityListAdapter adapter) {
		super.setAdapter(adapter);
		this.adapter = adapter;
		this.removeFooterView(footer);
	}

	public void addNewData(List<OutsideActivityBean> data) {
		this.removeFooterView(footer);
		adapter.addAll(data);
		adapter.notifyDataSetChanged();
		isLoading = false;
	}
	
	public void resetView(){
		this.removeAllViews();
	}

	public EndlessListener setListener() {
		return listener;
	}

	public static interface EndlessListener {
		public void loadData();

	}
}
