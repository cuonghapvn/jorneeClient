/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: UrlDownloader.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.imagehelpers;

import java.io.InputStream;

import android.content.Context;

public interface UrlDownloader {
    public static interface UrlDownloaderCallback {
        public void onDownloadComplete(UrlDownloader downloader, InputStream in, String filename);
    }
    
    public void download(Context context, String url, String filename, UrlDownloaderCallback callback, Runnable completion);
    public boolean allowCache();
    public boolean canDownloadUrl(String url);
}
