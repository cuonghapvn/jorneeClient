/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: CNEditText.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.customview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class CNEditText extends EditText {
	private Drawable drawble;
	private Rect rect;
	private int bfb = 100;

	public CNEditText(Context paramContext) {
		super(paramContext);
	}

	public CNEditText(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public CNEditText(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	protected void finalize() throws Throwable {
		this.drawble = null;
		this.rect = null;
		super.finalize();
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		if ((paramMotionEvent.getAction() == 0) && (this.drawble != null)) {
			this.rect = this.drawble.getBounds();
			int i = (int) paramMotionEvent.getX();
			int j = (int) paramMotionEvent.getY();
			this.bfb = (2 * this.rect.width());
			if ((i >= getRight() - this.rect.width() - this.bfb)
					&& (i <= getRight() - getPaddingRight() + this.bfb)
					&& (j >= getPaddingTop() - this.bfb)
					&& (j <= getHeight() - getPaddingBottom() + this.bfb)) {
				setText("");
				paramMotionEvent.setAction(3);
			}
		}
		return super.onTouchEvent(paramMotionEvent);
	}

	public void setCompoundDrawables(Drawable paramDrawable1,
			Drawable paramDrawable2, Drawable paramDrawable3,
			Drawable paramDrawable4) {
		if (paramDrawable3 != null)
			this.drawble = paramDrawable3;
		super.setCompoundDrawables(paramDrawable1, paramDrawable2,
				paramDrawable3, paramDrawable4);
	}
}
