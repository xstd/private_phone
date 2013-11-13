package com.xstd.pirvatephone.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.privacy.PrivacyDaoUtils;
import com.xstd.pirvatephone.dao.privacy.PrivacyFile;
import com.xstd.pirvatephone.dao.privacy.PrivacyFileDao;
import com.xstd.pirvatephone.module.IImage;
import com.xstd.pirvatephone.module.IImageList;
import com.xstd.pirvatephone.module.Item;
import com.xstd.pirvatephone.utils.BitmapManager;
import com.xstd.pirvatephone.utils.ImageManager;
import com.xstd.pirvatephone.utils.Util;
import com.xstd.privatephone.adapter.ShowSDCardMediaAdapter;

public class ShowSDCardMediaActivity extends BaseActivity implements
		View.OnClickListener {

	private static final String TAG = "ShowSDCardMediaActivity";

	@ViewMapping(ID = R.id.ll_return_btn)
	public TextView ll_return_btn;

	@ViewMapping(ID = R.id.ll_title_text)
	public TextView ll_title_text;

	@ViewMapping(ID = R.id.ll_toools)
	public ImageView ll_toools;

	@ViewMapping(ID = R.id.lv)
	public ListView lv;

	ShowSDCardMediaAdapter mAdapter; // mAdapter is only accessed in main
										// thread.
	Handler mHandler = new Handler(); // handler for the main thread
	Thread mWorkerThread;
	BroadcastReceiver mReceiver;
	ContentObserver mDbObserver;
	boolean mScanning;
	boolean mUnmounted;

	private static final String CAMERA_BUCKET = ImageManager.CAMERA_IMAGE_BUCKET_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_sd_media);

		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		ll_return_btn.setOnClickListener(this);
		ll_toools.setOnClickListener(this);

		ll_title_text.setText(R.string.title_select_add_image);
		ll_toools.setVisibility(View.VISIBLE);
		ll_toools
				.setBackgroundResource(R.drawable.selector_title_bar_snap_btn_bg);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

			}
		});

		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				onReceiveMediaBroadcast(intent);
			}
		};

		mDbObserver = new ContentObserver(mHandler) {
			@Override
			public void onChange(boolean selfChange) {
				rebake(false,
						ImageManager
								.isMediaScannerScanning(getContentResolver()));
			}
		};
	}

	@Override
	public void onStart() {
		super.onStart();

		mAdapter = new ShowSDCardMediaAdapter(getLayoutInflater());
		lv.setAdapter(mAdapter);

		// install an intent filter to receive SD card related events.
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		intentFilter.addDataScheme("file");

		registerReceiver(mReceiver, intentFilter);

		getContentResolver()
				.registerContentObserver(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true,
						mDbObserver);

		// Assume the storage is mounted and not scanning.
		mUnmounted = false;
		mScanning = false;
		startWorker();
	}

	// This is called when we receive media-related broadcast.
	private void onReceiveMediaBroadcast(Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
			// SD card available
			// TODO put up a "please wait" message
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

	// The storage status is changed, restart the worker or show "no images".
	private void rebake(boolean unmounted, boolean scanning) {
		if (unmounted == mUnmounted && scanning == mScanning)
			return;
		abortWorker();
		mUnmounted = unmounted;
		mScanning = scanning;
		// updateScanningDialog(mScanning);
		if (mUnmounted) {
			showNoImagesView();
		} else {
			hideNoImagesView();
			startWorker();
		}
	}

	// This is used to stop the worker thread.
	volatile boolean mAbort = false;

	// Create the worker thread.
	private void startWorker() {
		mAbort = false;
		mWorkerThread = new Thread("GalleryPicker Worker") {
			@Override
			public void run() {
				workerRun();
			}
		};
		BitmapManager.instance().allowThreadDecoding(mWorkerThread);
		mWorkerThread.start();
	}

	// This is run in the worker thread.
	private void workerRun() {
		// We collect items from checkImageList() and checkBucketIds() and
		// put them in allItems. Later we give allItems to checkThumbBitmap()
		// and generated thumbnail bitmaps for each item. We do this instead of
		// generating thumbnail bitmaps in checkImageList() and checkBucketIds()
		// because we want to show all the folders first, then update them with
		// the thumb bitmaps. (Generating thumbnail bitmaps takes some time.)
		ArrayList<Item> allItems = new ArrayList<Item>();

		checkScanning();
		if (mAbort)
			return;

		checkImageList(allItems);
		if (mAbort)
			return;

		checkBucketIds(allItems);
		if (mAbort)
			return;

		checkThumbBitmap(allItems);
		if (mAbort)
			return;

		checkLowStorage();
	}

	private static final long LOW_STORAGE_THRESHOLD = 1024 * 1024 * 2;

	// This is run in the worker thread.
	private void checkLowStorage() {
		// Check available space only if we are writable
		if (ImageManager.hasStorage()) {
			String storageDirectory = Environment.getExternalStorageDirectory()
					.toString();
			StatFs stat = new StatFs(storageDirectory);
			long remaining = (long) stat.getAvailableBlocks()
					* (long) stat.getBlockSize();
			if (remaining < LOW_STORAGE_THRESHOLD) {
				mHandler.post(new Runnable() {
					public void run() {
						checkLowStorageFinished();
					}
				});
			}
		}
	}

	// This is run in the main thread.
	// This is called only if the storage is low.
	private void checkLowStorageFinished() {
		Toast.makeText(this, R.string.not_enough_space, 5000).show();
	}

	// This is run in the worker thread.
	private void checkImageList(ArrayList<Item> allItems) {
		int length = IMAGE_LIST_DATA.length;
		IImageList[] lists = new IImageList[length];
		for (int i = 0; i < length; i++) {
			ImageListData data = IMAGE_LIST_DATA[i];
			lists[i] = createImageList(data.mInclude, data.mBucketId,
					getContentResolver());
			if (mAbort)
				return;
			Item item = null;

			if (lists[i].isEmpty())
				continue;

			// i >= 3 means we are looking at All Images/All Videos.
			// lists[i-3] is the corresponding Camera Images/Camera Videos.
			// We want to add the "All" list only if it's different from
			// the "Camera" list.
			if (i >= 3 && lists[i].getCount() == lists[i - 3].getCount()) {
				continue;
			}

			item = new Item(data.mType, data.mBucketId, getResources()
					.getString(data.mStringId), lists[i]);

			allItems.add(item);

			final Item finalItem = item;
			mHandler.post(new Runnable() {
				public void run() {
					updateItem(finalItem);
				}
			});
		}
	}

	private static final int THUMB_SIZE = 142;

	// This is run in the worker thread.
	private void checkThumbBitmap(ArrayList<Item> allItems) {
		for (Item item : allItems) {
			final Bitmap b = makeMiniThumbBitmap(THUMB_SIZE, THUMB_SIZE,
					item.mImageList);
			if (mAbort) {
				if (b != null)
					b.recycle();
				return;
			}

			final Item finalItem = item;
			mHandler.post(new Runnable() {
				public void run() {
					updateThumbBitmap(finalItem, b);
				}
			});
		}
	}

	// This is run in the main thread.
	private void updateThumbBitmap(Item item, Bitmap b) {
		item.setThumbBitmap(b);
		mAdapter.updateDisplay();
	}

	// This is run in worker thread.
	private Bitmap makeMiniThumbBitmap(int width, int height, IImageList images) {
		int count = images.getCount();
		// We draw three different version of the folder image depending on the
		// number of images in the folder.
		// For a single image, that image draws over the whole folder.
		// For two or three images, we draw the two most recent photos.
		// For four or more images, we draw four photos.
		final int padding = 4;
		int imageWidth = width;
		int imageHeight = height;
		int offsetWidth = 0;
		int offsetHeight = 0;

		imageWidth = (imageWidth - padding) / 2; // 2 here because we show two
													// images
		imageHeight = (imageHeight - padding) / 2; // per row and column

		final Paint p = new Paint();
		final Bitmap b = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		final Canvas c = new Canvas(b);
		final Matrix m = new Matrix();

		// draw the whole canvas as transparent
		p.setColor(0x00000000);
		c.drawPaint(p);

		// load the drawables
		loadDrawableIfNeeded();

		// draw the mask normally
		p.setColor(0xFFFFFFFF);
		mFrameGalleryMask.setBounds(0, 0, width, height);
		mFrameGalleryMask.draw(c);

		Paint pdpaint = new Paint();
		pdpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

		pdpaint.setStyle(Paint.Style.FILL);
		c.drawRect(0, 0, width, height, pdpaint);

		for (int i = 0; i < 4; i++) {
			if (mAbort) {
				return null;
			}

			Bitmap temp = null;
			IImage image = i < count ? images.getImageAt(i) : null;

			if (image != null) {
				temp = image.miniThumbBitmap();
			}

			if (temp != null) {
				if (ImageManager.isVideo(image)) {
					Bitmap newMap = temp.copy(temp.getConfig(), true);
					Canvas overlayCanvas = new Canvas(newMap);
					int overlayWidth = mVideoOverlay.getIntrinsicWidth();
					int overlayHeight = mVideoOverlay.getIntrinsicHeight();
					int left = (newMap.getWidth() - overlayWidth) / 2;
					int top = (newMap.getHeight() - overlayHeight) / 2;
					Rect newBounds = new Rect(left, top, left + overlayWidth,
							top + overlayHeight);
					mVideoOverlay.setBounds(newBounds);
					mVideoOverlay.draw(overlayCanvas);
					temp.recycle();
					temp = newMap;
				}

				temp = Util.transform(m, temp, imageWidth, imageHeight, true,
						Util.RECYCLE_INPUT);
			}

			Bitmap thumb = Bitmap.createBitmap(imageWidth, imageHeight,
					Bitmap.Config.ARGB_8888);
			Canvas tempCanvas = new Canvas(thumb);
			if (temp != null) {
				tempCanvas.drawBitmap(temp, new Matrix(), new Paint());
			}
			mCellOutline.setBounds(0, 0, imageWidth, imageHeight);
			mCellOutline.draw(tempCanvas);

			placeImage(thumb, c, pdpaint, imageWidth, padding, imageHeight,
					padding, offsetWidth, offsetHeight, i);

			thumb.recycle();

			if (temp != null) {
				temp.recycle();
			}
		}

		return b;
	}

	private static void placeImage(Bitmap image, Canvas c, Paint paint,
			int imageWidth, int widthPadding, int imageHeight,
			int heightPadding, int offsetX, int offsetY, int pos) {
		int row = pos / 2;
		int col = pos - (row * 2);

		int xPos = (col * (imageWidth + widthPadding)) - offsetX;
		int yPos = (row * (imageHeight + heightPadding)) - offsetY;

		c.drawBitmap(image, xPos, yPos, paint);
	}

	// These drawables are loaded on-demand.
	Drawable mFrameGalleryMask;
	Drawable mCellOutline;
	Drawable mVideoOverlay;

	private void loadDrawableIfNeeded() {
		if (mFrameGalleryMask != null)
			return; // already loaded
		Resources r = getResources();
		mFrameGalleryMask = r
				.getDrawable(R.drawable.frame_gallery_preview_album_mask);
		mCellOutline = r.getDrawable(android.R.drawable.gallery_thumb);
		mVideoOverlay = r.getDrawable(R.drawable.ic_gallery_video_overlay);
	}

	// This is run in the worker thread.
	private void checkBucketIds(ArrayList<Item> allItems) {
		final IImageList allImages;
		if (!mScanning && !mUnmounted) {
			allImages = ImageManager.makeImageList(getContentResolver(),
					ImageManager.DataLocation.ALL, ImageManager.INCLUDE_IMAGES
							| ImageManager.INCLUDE_VIDEOS,
					ImageManager.SORT_DESCENDING, null);
		} else {
			allImages = ImageManager.makeEmptyImageList();
		}

		if (mAbort) {
			allImages.close();
			return;
		}

		HashMap<String, String> hashMap = allImages.getBucketIds();
		allImages.close();
		if (mAbort)
			return;

		for (Map.Entry<String, String> entry : hashMap.entrySet()) {
			String key = entry.getKey();
			if (key == null) {
				continue;
			}
			if (!key.equals(CAMERA_BUCKET)) {
				IImageList list = createImageList(ImageManager.INCLUDE_IMAGES
						| ImageManager.INCLUDE_VIDEOS, key,
						getContentResolver());
				if (mAbort)
					return;

				Item item = new Item(Item.TYPE_NORMAL_FOLDERS, key,
						entry.getValue(), list);

				allItems.add(item);

				final Item finalItem = item;
				mHandler.post(new Runnable() {
					public void run() {
						updateItem(finalItem);
					}
				});
			}
		}

		mHandler.post(new Runnable() {
			public void run() {
				checkBucketIdsFinished();
			}
		});
	}

	// This is run in the main thread.
	private void checkBucketIdsFinished() {

		// If we just have one folder, open it.
		// If we have zero folder, show the "no images" icon.
		if (!mScanning) {
			int numItems = mAdapter.mItems.size();
			if (numItems == 0) {
				showNoImagesView();
			} else if (numItems == 1) {
				mAdapter.mItems.get(0).launch(this);
				finish();
				return;
			}
		}
	}

	// This is run in the main thread.
	private void updateItem(Item item) {
		// Hide NoImageView if we are going to add the first item
		if (mAdapter.getCount() == 0) {
			hideNoImagesView();
		}
		mAdapter.addItem(item);
		mAdapter.updateDisplay();
	}

	// This is run in the worker thread.
	private void checkScanning() {
		ContentResolver cr = getContentResolver();
		final boolean scanning = ImageManager.isMediaScannerScanning(cr);
		mHandler.post(new Runnable() {
			public void run() {
				checkScanningFinished(scanning);
			}
		});
	}

	// This is run in the main thread.
	private void checkScanningFinished(boolean scanning) {
		// updateScanningDialog(scanning);
	}

	private View mNoImagesView;

	// Show/Hide the "no images" icon and text. Load resources on demand.
	private void showNoImagesView() {
		// if (mNoImagesView == null) {
		// ViewGroup root = (ViewGroup) findViewById(R.id.root);
		// getLayoutInflater().inflate(R.layout.gallerypicker_no_images, root);
		// mNoImagesView = findViewById(R.id.no_images);
		// }
		// mNoImagesView.setVisibility(View.VISIBLE);
	}

	private void hideNoImagesView() {
		if (mNoImagesView != null) {
			mNoImagesView.setVisibility(View.GONE);
		}
	}

	private void abortWorker() {
		if (mWorkerThread != null) {
			BitmapManager.instance().cancelThreadDecoding(mWorkerThread,
					getContentResolver());
			mAbort = true;
			try {
				mWorkerThread.join();
			} catch (InterruptedException ex) {
				Log.e(TAG, "join interrupted");
			}
			mWorkerThread = null;
			// Remove all runnables in mHandler.
			// (We assume that the "what" field in the messages are 0
			// for runnables).
			mHandler.removeMessages(0);
			mAdapter.clear();
			mAdapter.updateDisplay();
			clearImageLists();
		}
	}

	// image lists created by createImageList() are collected in mAllLists.
	// They will be closed in clearImageList, so they don't hold open files
	// on SD card. We will be killed if we don't close files when the SD card
	// is unmounted.
	ArrayList<IImageList> mAllLists = new ArrayList<IImageList>();

	private IImageList createImageList(int mediaTypes, String bucketId,
			ContentResolver cr) {
		IImageList list = ImageManager.makeImageList(cr,
				ImageManager.DataLocation.ALL, mediaTypes,
				ImageManager.SORT_DESCENDING, bucketId);
		mAllLists.add(list);
		return list;
	}

	private void clearImageLists() {
		for (IImageList list : mAllLists) {
			list.close();
		}
		mAllLists.clear();
	}

	private Uri mOutPutFileUri;
	private String fileName;
	private static final int REQUEST_CAMERA_CODE = 1;

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.ll_return_btn:
			finish();
			break;
		case R.id.ll_toools:
			intent = new Intent();
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			File path = new File(ShowSDCardFilesActivity.PRIVACY_SAPCE_PATH);
			if (!path.exists()) {
				path.mkdirs();
			}
			fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
			File file = new File(path, fileName);
			mOutPutFileUri = Uri.fromFile(file);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
			startActivityForResult(intent, REQUEST_CAMERA_CODE);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUEST_CAMERA_CODE == requestCode && RESULT_OK == resultCode) {
			PrivacyFileDao dao = PrivacyDaoUtils.getFileDao(this);
			com.plugin.common.utils.files.FileInfo info = new com.plugin.common.utils.files.FileInfo();
			info.fileName = fileName;
			info.filePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "DCIM" + File.separator + "Camera";

			dao.insert(new PrivacyFile(null, fileName, fileName, info.filePath,
					new Date(), 0, Long.valueOf(-1)));
		}
	}

	// IMAGE_LIST_DATA stores the parameters for the four image lists
	// we are interested in. The order of the IMAGE_LIST_DATA array is
	// significant (See the implementation of GalleryPickerAdapter.init).
	private static final class ImageListData {
		ImageListData(int type, int include, String bucketId, int stringId) {
			mType = type;
			mInclude = include;
			mBucketId = bucketId;
			mStringId = stringId;
		}

		int mType;
		int mInclude;
		String mBucketId;
		int mStringId;
	}

	private static final ImageListData[] IMAGE_LIST_DATA = {
			// Camera Images
			new ImageListData(Item.TYPE_CAMERA_IMAGES,
					ImageManager.INCLUDE_IMAGES,
					ImageManager.CAMERA_IMAGE_BUCKET_ID,
					R.string.gallery_camera_bucket_name),
			// Camera Videos
			new ImageListData(Item.TYPE_CAMERA_VIDEOS,
					ImageManager.INCLUDE_VIDEOS,
					ImageManager.CAMERA_IMAGE_BUCKET_ID,
					R.string.gallery_camera_videos_bucket_name),

			// Camera Medias
			new ImageListData(Item.TYPE_CAMERA_MEDIAS,
					ImageManager.INCLUDE_VIDEOS | ImageManager.INCLUDE_IMAGES,
					ImageManager.CAMERA_IMAGE_BUCKET_ID,
					R.string.gallery_camera_media_bucket_name),

			// All Images
			new ImageListData(Item.TYPE_ALL_IMAGES,
					ImageManager.INCLUDE_IMAGES, null, R.string.all_images),

			// All Videos
			new ImageListData(Item.TYPE_ALL_VIDEOS,
					ImageManager.INCLUDE_VIDEOS, null, R.string.all_videos), };

}
