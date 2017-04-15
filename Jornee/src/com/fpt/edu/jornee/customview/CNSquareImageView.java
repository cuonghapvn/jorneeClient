/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: CNSquareImageView.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.customview;

import android.content.Context;
import android.util.AttributeSet;

public class CNSquareImageView extends CNImageView{
	public CNSquareImageView(Context paramContext) {
		super(paramContext);
	}

	public CNSquareImageView(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public CNSquareImageView(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	protected void onMeasure(int paramInt1, int paramInt2) {
		int i = getDefaultSize(getSuggestedMinimumWidth(), paramInt1);
		setMeasuredDimension(i, i);
	}

	protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3,
			int paramInt4) {
		super.onSizeChanged(paramInt1, paramInt1, paramInt3, paramInt4);
	}
}
