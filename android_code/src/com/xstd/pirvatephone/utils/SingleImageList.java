package com.xstd.pirvatephone.utils;

import java.util.HashMap;

import android.content.ContentResolver;
import android.net.Uri;

import com.xstd.pirvatephone.module.IImage;
import com.xstd.pirvatephone.module.IImageList;
import com.xstd.pirvatephone.module.UriImage;

public class SingleImageList implements IImageList {

	private IImage mSingleImage;
	private Uri mUri;

	public SingleImageList(ContentResolver resolver, Uri uri) {
		mUri = uri;
		mSingleImage = new UriImage(this, resolver, uri);
	}

	public HashMap<String, String> getBucketIds() {
		throw new UnsupportedOperationException();
	}

	public int getCount() {
		return 1;
	}

	public boolean isEmpty() {
		return false;
	}

	public int getImageIndex(IImage image) {
		return image == mSingleImage ? 0 : -1;
	}

	public IImage getImageAt(int i) {
		return i == 0 ? mSingleImage : null;
	}

	public boolean removeImage(IImage image) {
		return false;
	}

	public boolean removeImageAt(int index) {
		return false;
	}

	public IImage getImageForUri(Uri uri) {
		return uri.equals(mUri) ? mSingleImage : null;
	}

	public void close() {
		mSingleImage = null;
		mUri = null;
	}

}
