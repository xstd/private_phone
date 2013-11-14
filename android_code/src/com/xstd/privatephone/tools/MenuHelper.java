/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xstd.privatephone.tools;

import java.io.Closeable;

import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

import com.xstd.pirvatephone.module.IImage;

/**
 * A utility class to handle various kinds of menu operations.
 */
public class MenuHelper {
	private static final String TAG = "MenuHelper";

	public static final int INCLUDE_ALL = 0xFFFFFFFF;
	public static final int INCLUDE_VIEWPLAY_MENU = (1 << 0);
	public static final int INCLUDE_SHARE_MENU = (1 << 1);
	public static final int INCLUDE_SET_MENU = (1 << 2);
	public static final int INCLUDE_CROP_MENU = (1 << 3);
	public static final int INCLUDE_DELETE_MENU = (1 << 4);
	public static final int INCLUDE_ROTATE_MENU = (1 << 5);
	public static final int INCLUDE_DETAILS_MENU = (1 << 6);
	public static final int INCLUDE_SHOWMAP_MENU = (1 << 7);

	public static final int MENU_IMAGE_SHARE = 1;
	public static final int MENU_IMAGE_SHOWMAP = 2;

	public static final int POSITION_SWITCH_CAMERA_MODE = 1;
	public static final int POSITION_GOTO_GALLERY = 2;
	public static final int POSITION_VIEWPLAY = 3;
	public static final int POSITION_CAPTURE_PICTURE = 4;
	public static final int POSITION_CAPTURE_VIDEO = 5;
	public static final int POSITION_IMAGE_SHARE = 6;
	public static final int POSITION_IMAGE_ROTATE = 7;
	public static final int POSITION_IMAGE_TOSS = 8;
	public static final int POSITION_IMAGE_CROP = 9;
	public static final int POSITION_IMAGE_SET = 10;
	public static final int POSITION_DETAILS = 11;
	public static final int POSITION_SHOWMAP = 12;
	public static final int POSITION_SLIDESHOW = 13;
	public static final int POSITION_MULTISELECT = 14;
	public static final int POSITION_CAMERA_SETTING = 15;
	public static final int POSITION_GALLERY_SETTING = 16;

	public static final int NO_STORAGE_ERROR = -1;
	public static final int CANNOT_STAT_ERROR = -2;
	public static final String EMPTY_STRING = "";
	public static final String JPEG_MIME_TYPE = "image/jpeg";
	// valid range is -180f to +180f
	public static final float INVALID_LATLNG = 255f;

	/**
	 * Activity result code used to report crop results.
	 */
	public static final int RESULT_COMMON_MENU_CROP = 490;

	public interface MenuItemsResult {
		public void gettingReadyToOpen(Menu menu, IImage image);

		public void aboutToCall(MenuItem item, IImage image);
	}

	public interface MenuInvoker {
		public void run(MenuCallback r);
	}

	public interface MenuCallback {
		public void run(Uri uri, IImage image);
	}

	public static void closeSilently(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (Throwable e) {
				// ignore
			}
		}
	}

	public static long getImageFileSize(IImage image) {
		java.io.InputStream data = image.fullSizeImageData();
		if (data == null)
			return -1;
		try {
			return data.available();
		} catch (java.io.IOException ex) {
			return -1;
		} finally {
			closeSilently(data);
		}
	}

}
