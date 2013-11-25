package com.xstd.pirvatephone.activity;

import java.io.File;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.privacy.PrivacyDaoUtils;
import com.xstd.pirvatephone.dao.privacy.PrivacyFile;
import com.xstd.pirvatephone.dao.privacy.PrivacyFileDao;
import com.xstd.pirvatephone.dao.privacy.PrivacyPic;
import com.xstd.pirvatephone.dao.privacy.PrivacyPicDao;
import com.xstd.privatephone.adapter.AddPrivacyPicAdapter;
import com.xstd.privatephone.tools.Toasts;

public class AddPrivacyPictureActivity extends BaseActivity implements
		OnClickListener, AdapterView.OnItemClickListener,
		AdapterView.OnItemLongClickListener {

	private static final int REQUEST_CAMERA_CODE = 1;

	@ViewMapping(ID = R.id.ll_return_btn)
	public ImageButton ll_return_btn;

	@ViewMapping(ID = R.id.ll_title_text)
	public TextView ll_title_text;

	@ViewMapping(ID = R.id.ll_toools)
	public ImageView ll_toools;

	@ViewMapping(ID = R.id.inbox_divider)
	public TextView inbox_divider;

	@ViewMapping(ID = R.id.add_pic)
	public ViewGroup add_pic;

	@ViewMapping(ID = R.id.gridview)
	public GridView gridview;

	private AddPrivacyPicAdapter adapter;

	Uri mOutPutFileUri;
	String fileName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_privacy_picture);
		
		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		ll_return_btn.setOnClickListener(this);
		ll_toools.setOnClickListener(this);
		add_pic.setOnClickListener(this);

		ll_title_text.setText(R.string.privacy_picture);
		ll_toools.setVisibility(View.VISIBLE);
		ll_toools
				.setBackgroundResource(R.drawable.selector_title_bar_snap_btn_bg);
		PrivacyPicDao dao = PrivacyDaoUtils
				.geThumbDao(AddPrivacyPictureActivity.this);
		Cursor cursor = dao.getDatabase().query(dao.getTablename(),
				dao.getAllColumns(), null, null, null, null, null);
		refershFolderCount(cursor);
		adapter = new AddPrivacyPicAdapter(getApplicationContext(), cursor,
				true);
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(this);
		gridview.setOnItemLongClickListener(this);
	}

	private void refershFolderCount(Cursor cursor) {
		cursor.requery();
		String str = getString(R.string.strongbox_image_inbox_divider);
		inbox_divider.setText(String.format(str, cursor.getCount() - 1));
	}

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
		case R.id.add_pic:
			intent = new Intent(this, ShowSDCardMediaActivity.class);
			startActivity(intent);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == adapter.getCount() - 1) {
			showAddDialog();
			return;
		}
		Intent intent = new Intent(this, PrivacyShowActivity.class);
		intent.putExtra("privacy_type", 0);
		adapter.getCursor().moveToPosition(position);
		intent.putExtra("ref_id", adapter.getCursor().getLong(0));
		startActivity(intent);
	}

	private void showAddDialog() {
		final EditText et = new EditText(this);
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.rs_create_album)
				.setView(et)
				.setPositiveButton(R.string.rs_create,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String name = et.getText().toString().trim();
								if (TextUtils.isEmpty(name)) {
									Toasts.getInstance(
											AddPrivacyPictureActivity.this)
											.show(R.string.rs_error_empty_thumb,
													0);
									return;
								}
								PrivacyDaoUtils.geThumbDao(
										AddPrivacyPictureActivity.this).insert(
										new PrivacyPic(null, name));
								refershFolderCount(adapter.getCursor());
							}
						}).setNegativeButton(android.R.string.cancel, null)
				.create();
		dialog.show();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (position == 0 || position == adapter.getCount() - 1) {
			return true;
		}
		showDeleteDialog(position);
		return false;
	}

	private void showDeleteDialog(final int position) {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.rs_delete_thumb)
				.setMessage(R.string.warning_msg_for_delete_single_image_album)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Cursor cursor = adapter.getCursor();
								cursor.moveToPosition(position);
								PrivacyPic item = new PrivacyPic(cursor
										.getLong(0), cursor.getString(1));
								PrivacyDaoUtils.geThumbDao(
										AddPrivacyPictureActivity.this).delete(
										item);
								refershFolderCount(adapter.getCursor());
							}
						}).setNegativeButton(android.R.string.cancel, null)
				.create();
		dialog.show();
	}
}
