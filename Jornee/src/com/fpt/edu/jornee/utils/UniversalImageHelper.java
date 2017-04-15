/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: UniversalImageHelper.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.utils;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fpt.edu.jornee.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class UniversalImageHelper {
	static Context context;

	public static ImageLoader imageLoader = ImageLoader.getInstance();
	static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.thumbnail_default)
			.showImageForEmptyUri(R.drawable.ic_no_image)
			.showImageOnFail(R.drawable.thumbnail_default)
			.resetViewBeforeLoading(true).cacheInMemory(true).cacheOnDisc(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
			.build();
	public static ImageLoader imageThumbnailLoader = ImageLoader.getInstance();
	static ImageLoadingListener imageLoadingListener = new ImageLoadingListener() {

		@Override
		public void onLoadingStarted(String imageUri, View view) {

		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {

		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
		}
	};

	public static void pause() {
		if (imageLoader.isInited()) {
			imageLoader.clearMemoryCache();
		}
	}

	public static void loadImage(Context context, String url,
			ImageView imageView) {
		try {
			if (!url.startsWith("http") && !url.contains("file://")) {
				url.replaceAll("\\s+", "%20");
				url = "file://" + url;
			}
			if (!imageLoader.isInited()) {
				UniversalImageHelper.context = context;
				File cacheDir = new File(context.getCacheDir(), "Jornee");
				if (!cacheDir.exists())
					cacheDir.mkdir();
				imageLoader.init(new ImageLoaderConfiguration.Builder(context)
						.denyCacheImageMultipleSizesInMemory()
						.threadPoolSize(1)
						.threadPriority(Thread.MIN_PRIORITY + 5)
						.discCache(new UnlimitedDiscCache(cacheDir))
						.memoryCache(new WeakMemoryCache())
						.tasksProcessingOrder(QueueProcessingType.LIFO)
						.defaultDisplayImageOptions(options).writeDebugLogs()
						.build());
			}
			imageLoader.displayImage(url, imageView, options,
					imageLoadingListener);
		} catch (Exception e) {
			Toast.makeText(context, "Fail to load image!", Toast.LENGTH_SHORT)
					.show();
		}

	}

	public static void loadThumbnail(Context context, ImageView imageView,
			String url) {
		try {
			if (!url.startsWith("http") && !url.contains("file://")) {
				url.replaceAll("\\s+", "%20");
				// imageView.setRotation(ImageRotateHelper.findRotate(url));
				url = "file://" + url;

			}
			if (!imageThumbnailLoader.isInited()) {
				File cacheDir = new File(context.getCacheDir(), "Jornee");
				if (!cacheDir.exists())
					cacheDir.mkdir();
				imageThumbnailLoader.init(new ImageLoaderConfiguration.Builder(
						context)
						.threadPoolSize(5)
						.threadPriority(Thread.MIN_PRIORITY + 5)
						.discCache(new UnlimitedDiscCache(cacheDir))
						// .discCacheExtraOptions(200, 120, CompressFormat.JPEG,
						// 0, null)
						.tasksProcessingOrder(QueueProcessingType.FIFO)
						.defaultDisplayImageOptions(options).build());
			}
			// UrlImageViewHelper.setUrlDrawable(imageView, url);
			imageThumbnailLoader.displayImage(url, imageView, options,
					imageLoadingListener);
		} catch (Exception e) {
			Toast.makeText(context, "Fail to load image!", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public static void loadImage(Context context, ImageView imageView,
			String url) {
		loadImage(context, url, imageView);
	}
}
