/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: ImageRotateHelper.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.journey;

import java.io.IOException;

import android.media.ExifInterface;

public class ImageRotateHelper {

	public static float findRotate(String filePath) {
		float result = 0;
		try {
			ExifInterface exif = new ExifInterface(filePath);
			if (exif != null) {
				int orientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION, 1);
				switch (orientation) {
				case ExifInterface.ORIENTATION_NORMAL:
					result = 0;
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:
					result = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					result = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					result = 270;
					break;
				default:
					result = 0;
					break;
				}
			}
		} catch (IOException e) {
			return result;
		}
		return result;
	}

}
