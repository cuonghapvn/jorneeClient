/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: AssetUrlDownloader.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.imagehelpers;

import java.io.InputStream;

import android.content.Context;
import android.os.AsyncTask;

public class AssetUrlDownloader implements UrlDownloader {
    @Override
    public void download(final Context context, final String url, final String filename,
            final UrlDownloaderCallback callback, final Runnable completion) {
        final AsyncTask<Void, Void, Void> downloader = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                try {
                    String relativePath = url.replaceFirst("file:///android_asset/", "");
                    InputStream is = context.getAssets().open(relativePath);
                    callback.onDownloadComplete(AssetUrlDownloader.this, is, null);
                    return null;
                }
                catch (final Throwable e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final Void result) {
                completion.run();
            }
        };

        UrlImageViewHelper.executeTask(downloader);
    }

    @Override
    public boolean allowCache() {
        return false;
    }

    @Override
    public boolean canDownloadUrl(String url) {
        return url.startsWith("file:///android_asset/");
    }
}
