package com.xstd.pirvatephone.module;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.util.Log;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.activity.ShowFolderMediaActivity;
import com.xstd.pirvatephone.activity.ShowSDCardMediaActivity;
import com.xstd.pirvatephone.utils.ImageManager;

public class Item {

	public static final int TYPE_NONE = -1;
	public static final int TYPE_ALL_IMAGES = 0;
	public static final int TYPE_ALL_VIDEOS = 1;
	public static final int TYPE_CAMERA_IMAGES = 2;
	public static final int TYPE_CAMERA_VIDEOS = 3;
	public static final int TYPE_CAMERA_MEDIAS = 4;
	public static final int TYPE_NORMAL_FOLDERS = 5;
	private static final String TAG = "Item";

	public final int mType;
	public final String mBucketId;
	public final String mName;
	public final IImageList mImageList;
	public final int mCount;
	public final Uri mFirstImageUri; // could be null if the list is empty

	// The thumbnail bitmap is set by setThumbBitmap() later because we want
	// to let the user sees the folder icon as soon as possible (and possibly
	// select them), then present more detailed information when we have it.
	public Bitmap mThumbBitmap; // the thumbnail bitmap for the image list

	public Item(int type, String bucketId, String name, IImageList list) {
		mType = type;
		mBucketId = bucketId;
		mName = name;
		mImageList = list;
		mCount = list.getCount();
		if (mCount > 0) {
			mFirstImageUri = list.getImageAt(0).fullSizeImageUri();
		} else {
			mFirstImageUri = null;
		}
	}

	public void setThumbBitmap(Bitmap thumbBitmap) {
		mThumbBitmap = thumbBitmap;
	}

	public boolean needsBucketId() {
		return mType >= TYPE_CAMERA_IMAGES;
	}

	public void launch(Activity activity) {
		Uri uri = Images.Media.INTERNAL_CONTENT_URI;
		if (needsBucketId()) {
			uri = uri.buildUpon().appendQueryParameter("bucketId", mBucketId)
					.build();
		}
		// Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		// intent.putExtra("windowTitle", mName);
		// intent.putExtra("mediaTypes", getIncludeMediaTypes());
		Intent intent = new Intent(activity, ShowFolderMediaActivity.class);
		int privacy_type = activity.getIntent().getIntExtra("privacy_type", 0);
		long ref_id = activity.getIntent().getLongExtra("ref_id", -1);
		intent.putExtra("privacy_type", privacy_type);
		intent.putExtra("ref_id", ref_id);
		intent.setData(uri);
		activity.startActivityForResult(intent,
				ShowSDCardMediaActivity.REQUEST_FINISH_CODE);
	}

	public int getIncludeMediaTypes() {
		return convertItemTypeToIncludedMediaType(mType);
	}

	public static int convertItemTypeToIncludedMediaType(int itemType) {
		switch (itemType) {
		case TYPE_ALL_IMAGES:
		case TYPE_CAMERA_IMAGES:
			return ImageManager.INCLUDE_IMAGES;
		case TYPE_ALL_VIDEOS:
		case TYPE_CAMERA_VIDEOS:
			return ImageManager.INCLUDE_VIDEOS;
		case TYPE_NORMAL_FOLDERS:
		case TYPE_CAMERA_MEDIAS:
		default:
			return ImageManager.INCLUDE_IMAGES | ImageManager.INCLUDE_VIDEOS;
		}
	}

	public int getOverlay() {
		switch (mType) {
		case TYPE_ALL_IMAGES:
		case TYPE_CAMERA_IMAGES:
			return R.drawable.frame_overlay_gallery_camera;
		case TYPE_ALL_VIDEOS:
		case TYPE_CAMERA_VIDEOS:
		case TYPE_CAMERA_MEDIAS:
			return R.drawable.frame_overlay_gallery_video;
		case TYPE_NORMAL_FOLDERS:
		default:
			return R.drawable.frame_overlay_gallery_folder;
		}
	}

}
