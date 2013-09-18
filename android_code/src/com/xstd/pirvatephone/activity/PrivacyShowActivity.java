package com.xstd.pirvatephone.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.privacy.PrivacyDaoUtils;
import com.xstd.pirvatephone.dao.privacy.SrcToDestMapping;
import com.xstd.pirvatephone.dao.privacy.SrcToDestMappingDao;
import com.xstd.pirvatephone.utils.FileUtils;
import com.xstd.privatephone.adapter.PrivacyFileAdapter;

public class PrivacyShowActivity extends BaseActivity {

	protected static final String TAG = "PrivacyShowActivity";
	/**
	 * 请求隐藏文件的请求码
	 */
	protected static final int REQUEST_GET_FILE_CODE = 1;
	private TextView return_bt;
	private TextView title_text;
	/**
	 * 上个页面传来的隐藏文件类型
	 */
	private int privacy_type;
	private PrivacyFileAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy_show);
		item_pic = getResources().getStringArray(R.array.privacy_pic);
		item_audio = getResources().getStringArray(R.array.privacy_audio);
		item_vedio = getResources().getStringArray(R.array.privacy_vedio);
		privacy_type = getIntent().getIntExtra("privacy_type", 0);
		initViews();
		setListener();
	}

	@Override
	protected void onResume() {
		new QueryPrivacyFile().execute();//界面每次获得焦点就刷新数据
		super.onResume();
	}

	private void setListener() {
		return_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initViews() {
		return_bt = (TextView) findViewById(R.id.ll_return_btn);
		title_text = (TextView) findViewById(R.id.ll_title_text);
		title_text.setText(getString(R.string.privacy)
				+ PrivacySpaceActivity.home_privacy_title[privacy_type]);
		add_privacy = (Button) findViewById(R.id.add_privacy);
		empty_view = (RelativeLayout) findViewById(R.id.empty_view);
		lv = (ListView) findViewById(R.id.lv);
		add_privacy.setText(getString(R.string.privacy_add_msg)
				+ PrivacySpaceActivity.home_privacy_title[privacy_type]);
	}

	CharSequence[] item_pic;
	CharSequence[] item_audio;
	CharSequence[] item_vedio;
	private Button add_privacy;
	private RelativeLayout empty_view;
	private ListView lv;

	/**
	 * 下方添加按钮
	 * 
	 * @param view
	 */
	public void add(View view) {
		CharSequence[] items = null;
		switch (privacy_type) {
		case 0:
			items = item_pic;
			break;
		case 1:
			items = item_audio;
			break;
		case 2:
			items = item_vedio;
			break;
		case 3:
			//如果选择的文件隐藏，直接跳转到选择文件界面，而不是弹出dialog
			Intent intent = new Intent(PrivacyShowActivity.this,
					ShowSDCardFilesActivity.class);
			startActivity(intent);
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//如果点击的调图是手动选择文件，直接跳到选择文件界面。
				if (which == 2) {
					Intent intent = new Intent(PrivacyShowActivity.this,
							ShowSDCardFilesActivity.class);
					intent.putExtra("privacy_type", privacy_type);
					startActivity(intent);
					return;
				}
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				switch (privacy_type) {
				case 0:
					intent.setType("image/*");
					break;
				case 1:
					intent.setType("audio/*");
					break;
				case 2:
					intent.setType("video/*");
					break;
				}
				startActivityForResult(intent, REQUEST_GET_FILE_CODE);
			}
		});
		builder.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_GET_FILE_CODE && resultCode == RESULT_OK) {
			// 获取uri
			Uri uri = data.getData();

			// 查询，返回cursor
			Cursor cursor = getContentResolver().query(uri, null, null, null,
					null);

			// 第一行第二列保存路径strRingPath
			cursor.moveToFirst();
			String name = "";
			if (privacy_type == 0) {
				name = cursor.getString(3);
			} else if (privacy_type == 1) {
				name = cursor.getString(2);
			}
			String path = cursor.getString(1);
			cursor.close();
			hideFile(name, path);
		}
	};

	/**
	 * 文件隐藏
	 * @param fileName 要隐藏文件的名字
	 * @param filePath 要隐藏文件的路径
	 */
	private void hideFile(String fileName, String filePath) {
		new AsyncTask<String, Void, Void>() {

			protected void onPreExecute() {
				dialog = new ProgressDialog(PrivacyShowActivity.this);
				dialog.show();
			};

			@Override
			protected Void doInBackground(String... params) {
				SrcToDestMappingDao dao = PrivacyDaoUtils
						.getPrivacyDao(PrivacyShowActivity.this);
				// String destName = UUID.randomUUID().toString();
				com.plugin.common.utils.files.FileInfo info = new com.plugin.common.utils.files.FileInfo();
				info.fileName = params[0];
				info.filePath = params[1];
				// FileUtils.copyFile(info, PRIVACY_SAPCE_PATH);
				FileUtils.moveFile(params[1],
						ShowSDCardFilesActivity.PRIVACY_SAPCE_PATH + params[0]);
				dao.insert(new SrcToDestMapping(null, params[0], params[0],
						params[1], new Date(), privacy_type));
				FileUtils.DeleteFile(info);

				return null;
			}

			protected void onPostExecute(Void result) {
				dialog.dismiss();
			};

		}.execute(fileName, filePath);
	}

	ProgressDialog dialog;

	/**
	 * 异步查询隐藏文件
	 * 
	 * @author Chrain
	 * 
	 */
	private class QueryPrivacyFile extends
			AsyncTask<Void, Void, List<SrcToDestMapping>> {

		@Override
		protected List<SrcToDestMapping> doInBackground(Void... params) {
			SrcToDestMappingDao dao = PrivacyDaoUtils
					.getPrivacyDao(PrivacyShowActivity.this);
			String type = String.valueOf(privacy_type);
			String orderBy = SrcToDestMappingDao.Properties.SrcName.columnName
					+ " COLLATE LOCALIZED ASC";
			Cursor cursor = dao.getDatabase().query(dao.getTablename(),
					dao.getAllColumns(), "type=?", new String[] { type }, null,
					null, orderBy);
			List<SrcToDestMapping> result = new ArrayList<SrcToDestMapping>();
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					SrcToDestMapping mapping = new SrcToDestMapping();
					mapping.setId(cursor.getLong(cursor
							.getColumnIndex(SrcToDestMappingDao.Properties.Id.columnName)));
					mapping.setSrcName(cursor.getString(cursor
							.getColumnIndex(SrcToDestMappingDao.Properties.SrcName.columnName)));
					mapping.setSrcPath(cursor.getString(cursor
							.getColumnIndex(SrcToDestMappingDao.Properties.SrcPath.columnName)));
					mapping.setDestName(cursor.getString(cursor
							.getColumnIndex(SrcToDestMappingDao.Properties.DestName.columnName)));
					mapping.setMisstime(new Date(
							cursor.getLong(cursor
									.getColumnIndex(SrcToDestMappingDao.Properties.Misstime.columnName))));
					mapping.setType(cursor.getInt(cursor
							.getColumnIndex(SrcToDestMappingDao.Properties.Type.columnName)));
					result.add(mapping);
				}
			}
			cursor.close();
			return result;
		}

		@Override
		protected void onPostExecute(List<SrcToDestMapping> result) {
			if (result == null || result.size() < 1) {
				empty_view.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
				return;
			}
			empty_view.setVisibility(View.GONE);
			lv.setVisibility(View.VISIBLE);
			adapter = new PrivacyFileAdapter(PrivacyShowActivity.this, result);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					View pop = View.inflate(PrivacyShowActivity.this,
							R.layout.pop_setting, null);
					final SrcToDestMapping mapping = (SrcToDestMapping) adapter
							.getItem(position);
					final PopupWindow window = new PopupWindow(pop,
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT, true);
					Button btn1 = (Button) pop.findViewById(R.id.btn1);
					btn1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							boolean success = FileUtils.moveFile(
									ShowSDCardFilesActivity.PRIVACY_SAPCE_PATH
											+ mapping.getDestName(),
									mapping.getSrcPath());
							if (success) {
								SrcToDestMappingDao dao = PrivacyDaoUtils
										.getPrivacyDao(PrivacyShowActivity.this);
								dao.delete(mapping);
								new QueryPrivacyFile().execute();
								Toast.makeText(PrivacyShowActivity.this,
										R.string.privacy_backfile_success_msg,
										Toast.LENGTH_SHORT).show();

							} else {
								Toast.makeText(PrivacyShowActivity.this,
										R.string.privacy_backfile_failed_msg,
										Toast.LENGTH_SHORT).show();
							}
							window.dismiss();
						}
					});
					Button btn2 = (Button) pop.findViewById(R.id.btn2);
					btn2.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							com.plugin.common.utils.files.FileInfo f = new com.plugin.common.utils.files.FileInfo();
							f.filePath = ShowSDCardFilesActivity.PRIVACY_SAPCE_PATH
									+ mapping.getDestName();
							FileUtils.DeleteFile(f);
							SrcToDestMappingDao dao = PrivacyDaoUtils
									.getPrivacyDao(PrivacyShowActivity.this);
							dao.delete(mapping);
							new QueryPrivacyFile().execute();
							window.dismiss();
						}
					});
					Button btn3 = (Button) pop.findViewById(R.id.btn3);
					btn3.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Log.w(TAG, "按钮3");
							window.dismiss();
						}
					});
					ColorDrawable cd = new ColorDrawable(0xFFFFFF);
					window.setBackgroundDrawable(cd);
					window.showAsDropDown(view);
				}
			});
		}

	}

}
