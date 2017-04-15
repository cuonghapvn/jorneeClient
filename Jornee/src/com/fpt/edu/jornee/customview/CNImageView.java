/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: CNImageView.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.ImageView;

public class CNImageView extends ImageView {
	protected Animation b;

	public CNImageView(Context paramContext) {
		super(paramContext);
	}

	public CNImageView(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public CNImageView(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	@Deprecated
	public void setImageBitmap(Bitmap paramBitmap) {
		super.setImageBitmap(paramBitmap);
	}

}
