/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: CNRectangleImageView.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.customview;

import android.content.Context;
import android.util.AttributeSet;

public class CNRectangleImageView extends CNImageView{
	public CNRectangleImageView(Context paramContext) {
		super(paramContext);
	}

	public CNRectangleImageView(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public CNRectangleImageView(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	protected void onMeasure(int paramInt1, int paramInt2) {
		int i = getDefaultSize(getSuggestedMinimumWidth(), paramInt1);
		setMeasuredDimension(i, 3*i/5);
	}

	protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3,
			int paramInt4) {
		super.onSizeChanged(paramInt1, paramInt1, paramInt3, paramInt4);
	}
}
