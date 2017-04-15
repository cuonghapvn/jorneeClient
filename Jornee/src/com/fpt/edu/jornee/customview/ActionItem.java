/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: ActionItem.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.customview;

import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;

public class ActionItem 
{
	private Drawable icon;
	private Bitmap thumb;
	private String title;
	private int actionId = -1;
    private boolean selected;
    private boolean sticky;

    public ActionItem(int actionId, String title, Drawable icon) 
    {
        this.title = title;
        this.icon = icon;
        this.actionId = actionId;
    }

    public ActionItem()
    {
        this(-1, null, null);
    }

    public ActionItem(int actionId, String title) 
    {
        this(actionId, title, null);
    }

    public ActionItem(Drawable icon)
    {
        this(-1, null, icon);
    }

    public ActionItem(int actionId, Drawable icon)
    {
        this(actionId, null, icon);
    }

	public void setTitle(String title) 
	{
		this.title = title;
	}
	
	public String getTitle()
	{
		return this.title;
	}

	public void setIcon(Drawable icon) 
	{
		this.icon = icon;
	}

	public Drawable getIcon()
	{
		return this.icon;
	}

    public void setActionId(int actionId) 
    {
        this.actionId = actionId;
    }

    public int getActionId()
    {
        return actionId;
    }

    public void setSticky(boolean sticky)
    {
        this.sticky = sticky;
    }

    public boolean isSticky()
    {
        return sticky;
    }

	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}

	public boolean isSelected()
	{
		return this.selected;
	}

	public void setThumb(Bitmap thumb) 
	{
		this.thumb = thumb;
	}

	public Bitmap getThumb()
	{
		return this.thumb;
	}
}
