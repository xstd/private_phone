package com.xstd.pirvatephone.activity;

import java.util.HashSet;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.module.IImage;
import com.xstd.pirvatephone.module.IImageList;
import com.xstd.pirvatephone.utils.ImageManager;
import com.xstd.privatephone.tools.ImageLoader;
import com.xstd.privatephone.view.GridViewSpecial;

public class ShowFolderMediaActivity extends BaseActivity implements
		View.OnClickListener, GridViewSpecial.Listener,
		GridViewSpecial.DrawAdapter {

	@ViewMapping(ID = R.id.ll_return_btn)
	public TextView ll_return_btn;

	@ViewMapping(ID = R.id.ll_title_text)
	public TextView ll_title_text;

	@ViewMapping(ID = R.id.gv)
	public GridViewSpecial gv;

	@ViewMapping(ID = R.id.move)
	public Button move;

	@ViewMapping(ID = R.id.all)
	public Button all;

	private ImageLoader mLoader;

	private int mInclusion;

	private SharedPreferences mPrefs;

	boolean mSortAscending = false;

	private boolean mPausing = true;

	private final Handler mHandler = new Handler();

	private BroadcastReceiver mReceiver = null;

	private IImageList mAllImages;

	private Dialog mMediaScanningDialog;

	private ImageManager.ImageListParam mParam;

	private int mSelectedIndex = GridViewSpecial.INDEX_NONE;

	private HashSet<IImage> mMultiSelected = null;

	private boolean mLayoutComplete;

	private float mScrollPosition = INVALID_POSITION;

	private static final float INVALID_POSITION = -1f;

	private Uri mCropResultUri;

	private boolean mConfigurationChanged = false;

	private static final String STATE_SCROLL_POSITION = "scroll_position";
	private static final String STATE_SELECTED_INDEX = "first_index";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		setContentView(R.layout.activity_show_folder_media);

		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		ll_return_btn.setOnClickListener(this);
		move.setOnClickListener(this);
		all.setOnClickListener(this);

		ll_title_text.setText(R.string.title_select_add_image);

		gv.setListener(this);

		setupInclusion();

		mLoader = new ImageLoader(getContentResolver(), mHandler);
	}

	@Override
	protected void onResume() {
		super.onResume();

		gv.setSizeChoice(Integer.parseInt(mPrefs.getString(
				"pref_gallery_size_key", "1")));
		gv.requestFocus();

		String sortOrder = mPrefs.getString("pref_gallery_sort_key", null);
		if (sortOrder != null) {
			mSortAscending = sortOrder.equals("ascending");
		}

		mPausing = false;

		// install an intent filter to receive SD card related events.
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		intentFilter.addDataScheme("file");

		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
					// SD card available
					// TODO put up a "please wait" message
					// TODO also listen for the media scanner finished message
				} else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
					// SD card unavailable
					rebake(true, false);
				} else if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {
					rebake(false, true);
				} else if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
					rebake(false, false);
				} else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
					rebake(true, false);
				}
			}
		};
		registerReceiver(mReceiver, intentFilter);
		rebake(false, ImageManager.isMediaScannerScanning(getContentResolver()));
		openMultiSelectMode();
	}

	private void openMultiSelectMode() {
		if (mMultiSelected != null)
			return;
		mMultiSelected = new HashSet<IImage>();
		gv.invalidate();
	}

	@Override
	public void onPause() {
		super.onPause();
		mPausing = true;

		mLoader.stop();

		gv.stop();

		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}

		// Now that we've paused the threads that are using the cursor it is
		// safe to close it.
		mAllImages.close();
		mAllImages = null;
	}

	@Override
	protected void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		state.putFloat(STATE_SCROLL_POSITION, mScrollPosition);
		state.putInt(STATE_SELECTED_INDEX, mSelectedIndex);
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		mScrollPosition = state.getFloat(STATE_SCROLL_POSITION,
				INVALID_POSITION);
		mSelectedIndex = state.getInt(STATE_SELECTED_INDEX, 0);
	}

	private void rebake(boolean unmounted, boolean scanning) {
		gv.stop();
		if (mAllImages != null) {
			mAllImages.close();
			mAllImages = null;
		}

		if (mMediaScanningDialog != null) {
			mMediaScanningDialog.cancel();
			mMediaScanningDialog = null;
		}

		if (scanning) {
			mMediaScanningDialog = ProgressDialog.show(this, null,
					getResources().getString(R.string.wait), true, true);
		}

		mParam = allImages(!unmounted && !scanning);
		mAllImages = ImageManager.makeImageList(getContentResolver(), mParam);

		gv.setImageList(mAllImages);
		gv.setDrawAdapter(this);
		gv.setLoader(mLoader);
		gv.start();
		// mNoImagesView.setVisibility(mAllImages.getCount() > 0 ? View.GONE
		// : View.VISIBLE);
	}

	// Returns the image list parameter which contains the subset of image/video
	// we want.
	private ImageManager.ImageListParam allImages(boolean storageAvailable) {
		if (!storageAvailable) {
			return ImageManager.getEmptyImageListParam();
		} else {
			Uri uri = getIntent().getData();
			return ImageManager.getImageListParam(
					ImageManager.DataLocation.EXTERNAL, mInclusion,
					mSortAscending ? ImageManager.SORT_ASCENDING
							: ImageManager.SORT_DESCENDING,
					(uri != null) ? uri.getQueryParameter("bucketId") : null);
		}
	}

	// According to the intent, setup what we include (image/video) in the
	// gallery and the title of the gallery.
	private void setupInclusion() {
		mInclusion = ImageManager.INCLUDE_IMAGES | ImageManager.INCLUDE_VIDEOS;

		Intent intent = getIntent();
		if (intent != null) {
			String type = intent.resolveType(this);
			if (type != null) {
				if (isImageType(type)) {
					mInclusion = ImageManager.INCLUDE_IMAGES;
				}
			}
			Bundle extras = intent.getExtras();

			if (extras != null) {
				mInclusion = (ImageManager.INCLUDE_IMAGES | ImageManager.INCLUDE_VIDEOS)
						& extras.getInt("mediaTypes", mInclusion);
			}

			if (extras != null && extras.getBoolean("pick-drm")) {
				mInclusion = ImageManager.INCLUDE_DRM_IMAGES;
			}
		}
	}

	private boolean isImageType(String type) {
		return type.equals("vnd.android.cursor.dir/image")
				|| type.equals("image/*");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_return_btn:
			finish();
			break;
		case R.id.move:
			moveSelectFiletoMissFolder();
			break;
		case R.id.all:
			selectAllOrNone();
			break;
		}
	}

	/**
	 * 全选按钮
	 */
	private void selectAllOrNone() {

	}

	/**
	 * 把选中的文件移入隐藏文件夹
	 */
	private void moveSelectFiletoMissFolder() {

	}

	private boolean isInMultiSelectMode() {
		return mMultiSelected != null;
	}

	private void toggleMultiSelected(IImage image) {
		int original = mMultiSelected.size();
		if (!mMultiSelected.add(image)) {
			mMultiSelected.remove(image);
		}
		gv.invalidate();
		// if (original == 0)
		// showFooter();
		// if (mMultiSelected.size() == 0)
		// hideFooter();
	}

	@Override
	public void onImageClicked(int index) {
	}

	@Override
	public void onImageTapped(int index) {
		if (isInMultiSelectMode()) {
			gv.setSelectedIndex(GridViewSpecial.INDEX_NONE);
			toggleMultiSelected(mAllImages.getImageAt(index));
		} else {
			onImageClicked(index);
		}
	}

	@Override
	public void onLayoutComplete(boolean changed) {
		mLayoutComplete = true;
		if (mCropResultUri != null) {
			IImage image = mAllImages.getImageForUri(mCropResultUri);
			mCropResultUri = null;
			if (image != null) {
				mSelectedIndex = mAllImages.getImageIndex(image);
			}
		}
		gv.setSelectedIndex(mSelectedIndex);
		if (mScrollPosition == INVALID_POSITION) {
			if (mSortAscending) {
				gv.scrollTo(0, gv.getHeight());
			} else {
				gv.scrollToImage(0);
			}
		} else if (mConfigurationChanged) {
			mConfigurationChanged = false;
			gv.scrollTo(mScrollPosition);
			if (gv.getCurrentSelection() != GridViewSpecial.INDEX_NONE) {
				gv.scrollToVisible(mSelectedIndex);
			}
		} else {
			gv.scrollTo(mScrollPosition);
		}
	}

	@Override
	public void onScroll(float scrollPosition) {
		mScrollPosition = scrollPosition;
	}

	// mSrcRect and mDstRect are only used in drawImage, but we put them as
	// instance variables to reduce the memory allocation overhead because
	// drawImage() is called a lot.
	private final Rect mSrcRect = new Rect();
	private final Rect mDstRect = new Rect();

	private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

	@Override
	public void drawImage(Canvas canvas, IImage image, Bitmap b, int xPos,
			int yPos, int w, int h) {
		if (b != null) {
			// if the image is close to the target size then crop,
			// otherwise scale both the bitmap and the view should be
			// square but I suppose that could change in the future.

			int bw = b.getWidth();
			int bh = b.getHeight();

			int deltaW = bw - w;
			int deltaH = bh - h;

			if (deltaW >= 0 && deltaW < 10 && deltaH >= 0 && deltaH < 10) {
				int halfDeltaW = deltaW / 2;
				int halfDeltaH = deltaH / 2;
				mSrcRect.set(0 + halfDeltaW, 0 + halfDeltaH, bw - halfDeltaW,
						bh - halfDeltaH);
				mDstRect.set(xPos, yPos, xPos + w, yPos + h);
				canvas.drawBitmap(b, mSrcRect, mDstRect, null);
			} else {
				mSrcRect.set(0, 0, bw, bh);
				mDstRect.set(xPos, yPos, xPos + w, yPos + h);
				canvas.drawBitmap(b, mSrcRect, mDstRect, mPaint);
			}
		} else {
			// If the thumbnail cannot be drawn, put up an error icon
			// instead
			Bitmap error = getErrorBitmap(image);
			int width = error.getWidth();
			int height = error.getHeight();
			mSrcRect.set(0, 0, width, height);
			int left = (w - width) / 2 + xPos;
			int top = (w - height) / 2 + yPos;
			mDstRect.set(left, top, left + width, top + height);
			canvas.drawBitmap(error, mSrcRect, mDstRect, null);
		}

	}

	private Bitmap mMissingImageThumbnailBitmap;
	private Bitmap mMissingVideoThumbnailBitmap;

	// Create this bitmap lazily, and only once for all the ImageBlocks to
	// use
	public Bitmap getErrorBitmap(IImage image) {
		if (ImageManager.isImage(image)) {
			if (mMissingImageThumbnailBitmap == null) {
				mMissingImageThumbnailBitmap = BitmapFactory
						.decodeResource(getResources(),
								R.drawable.ic_missing_thumbnail_picture);
			}
			return mMissingImageThumbnailBitmap;
		} else {
			if (mMissingVideoThumbnailBitmap == null) {
				mMissingVideoThumbnailBitmap = BitmapFactory.decodeResource(
						getResources(), R.drawable.ic_missing_thumbnail_video);
			}
			return mMissingVideoThumbnailBitmap;
		}
	}

	private Drawable mVideoOverlay;
	private Drawable mVideoMmsErrorOverlay;
	private Drawable mMultiSelectTrue;
	private Drawable mMultiSelectFalse;

	@Override
	public void drawDecoration(Canvas canvas, IImage image, int xPos, int yPos,
			int w, int h) {
		if (mMultiSelected != null) {
			initializeMultiSelectDrawables();

			Drawable checkBox = mMultiSelected.contains(image) ? mMultiSelectTrue
					: mMultiSelectFalse;
			int width = checkBox.getIntrinsicWidth();
			int height = checkBox.getIntrinsicHeight();
			int left = 5 + xPos;
			int top = h - height - 5 + yPos;
			mSrcRect.set(left, top, left + width, top + height);
			checkBox.setBounds(mSrcRect);
			checkBox.draw(canvas);
		}

	}

	private void initializeMultiSelectDrawables() {
		if (mMultiSelectTrue == null) {
			mMultiSelectTrue = getResources().getDrawable(
					R.drawable.btn_check_buttonless_on);
		}
		if (mMultiSelectFalse == null) {
			mMultiSelectFalse = getResources().getDrawable(
					R.drawable.btn_check_buttonless_off);
		}
	}

	@Override
	public boolean needsDecoration() {
		return (mMultiSelected != null);
	}

}
