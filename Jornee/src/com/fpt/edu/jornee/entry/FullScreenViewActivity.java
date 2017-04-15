/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: FullScreenViewActivity.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.entry;

import com.fpt.edu.jornee.R;
import com.fpt.edu.jornee.utils.TouchImageView;
import com.fpt.edu.jornee.utils.UniversalImageHelper;

import android.app.Activity;
import android.os.Bundle;

public class FullScreenViewActivity extends Activity {
	String path;
	TouchImageView imgView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen_image_view);
		Bundle bundle = getIntent().getExtras();
		path = bundle.getString("path");

		imgView = (TouchImageView) findViewById(R.id.fullScreenImgView);
		UniversalImageHelper.loadImage(this, imgView, path);

	}

}
