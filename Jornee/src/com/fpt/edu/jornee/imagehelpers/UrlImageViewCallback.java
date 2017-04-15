/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: UrlImageViewCallback.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.imagehelpers;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Callback that is invoked with a success/failure after attempting to
 * load a drawable from an url.
 * Note: If an ImageView has multiple setUrlDrawable calls made on it, only the last callback
 * will be invoked. This scenario arises when using ListViews which recycle their views.
 * This is done to prevent callbacks from being erroneosly invoked on ImageViews that are no
 * longer interested in the url that was loaded.
 * To guarantee a callback is invoked, one can do the following:
 * First call loadUrlDrawable (with a callback), and then setUrlDrawable. Both loads just get queued into the same request,
 * so you don't need to worry about that being inefficient or that it is making two network calls.
 * @author koush
 *
 */
public interface UrlImageViewCallback {
    /**
     * 
     * @param imageView ImageView for the load request.
     * @param loadedBitmap The bitmap that was loaded by the request.
     *                          If the drawable failed to load, this will be null.
     * @param url The url that was loaded.
     * @param loadedFromCache This will indicate whether the load operation result came from cache, or was retrieved.
     */
    void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache);
}
