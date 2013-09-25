package com.xstd.pirvatephone.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.plugin.common.utils.UtilsConfig;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.privacy.PrivacyDaoUtils;
import com.xstd.pirvatephone.dao.privacy.PrivacyFile;
import com.xstd.pirvatephone.dao.privacy.PrivacyFileDao;
import com.xstd.pirvatephone.utils.FileUtils;
import com.xstd.privatephone.adapter.ShowSDFilesAdapter;
import com.xstd.privatephone.adapter.ShowSDFilesAdapter.FileInfo;

public class ShowSDCardFilesActivity extends BaseActivity implements
		OnClickListener {

	protected static final String TAG = "ShowSDCardFilesActivity";
	private TextView return_btn;
	private TextView title_text;
	private TextView current_path;
	private ListView lv;
	private Button btn_move;
	private Button btn_add;
	private List<FileInfo> willMoves = new ArrayList<FileInfo>();
	private ShowSDFilesAdapter adapter;
	private int count = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_sdcardfile);
		privacy_type = getIntent().getIntExtra("privacy_type", 0);
		initViews();
		setListener();
	}

	private void setListener() {
		return_btn.setOnClickListener(this);
		btn_add.setOnClickListener(this);
		btn_move.setOnClickListener(this);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FileInfo fileInfo = (FileInfo) adapter.getItem(position);
				if (fileInfo.isFolder) {
					adapter.updateFiles(fileInfo.absolutePath);
					current_path.setText(fileInfo.absolutePath);
					count++;
				} else {
					fileInfo.isChecked = !fileInfo.isChecked;
					adapter.notifyDataSetChanged();
				}

			}
		});
	}

	private void initViews() {
		return_btn = (TextView) findViewById(R.id.ll_return_btn);
		title_text = (TextView) findViewById(R.id.ll_title_text);
		current_path = (TextView) findViewById(R.id.current_path);
		lv = (ListView) findViewById(R.id.lv);
		btn_move = (Button) findViewById(R.id.btn_move);
		btn_add = (Button) findViewById(R.id.btn_add);
		title_text.setText(getString(R.string.show_sdcardfile_title));
		current_path.setText(Environment.getExternalStorageDirectory()
				.getAbsolutePath());
		adapter = new ShowSDFilesAdapter(this);
		lv.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		if (v == return_btn) {
			if (count <= 0)
				finish();
			else
				exitsCurrentFolder();
		} else if (v == btn_add) {
			selectAll();
		} else if (v == btn_move) {
			missingFile();
		}
	}

	/**
	 * 返回上级目录
	 */
	private void exitsCurrentFolder() {
		String path = current_path.getText().toString().trim();
		String lastPath = path.substring(0, path.lastIndexOf(File.separator));
		adapter.updateFiles(lastPath);
		current_path.setText(lastPath);
		count--;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && count > 0) {
			exitsCurrentFolder();
			return true;// 终止返回按钮事件
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 全选
	 */
	private void selectAll() {
		for (int i = 0; i < adapter.getCount(); i++) {
			FileInfo fileInfo = (FileInfo) adapter.getItem(i);
			fileInfo.isChecked = true;
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 文件隐藏的位置
	 */
	public static final String PRIVACY_SAPCE_PATH = UtilsConfig.DISK_DIR_PATH
			+ "MISSFolder" + File.separator;
	ProgressDialog dialog;
	private int privacy_type;

	/**
	 * 移动文件
	 */
	private void missingFile() {
		for (int i = 0; i < adapter.getCount(); i++) {
			FileInfo fileInfo = (FileInfo) adapter.getItem(i);
			if (fileInfo.isChecked) {
				willMoves.add(fileInfo);
			}
		}
		new AsyncTask<Void, Void, Void>() {

			protected void onPreExecute() {
				dialog = new ProgressDialog(ShowSDCardFilesActivity.this);
				dialog.show();
			};

			@Override
			protected Void doInBackground(Void... params) {
				PrivacyFileDao dao = PrivacyDaoUtils
						.getFileDao(ShowSDCardFilesActivity.this);
				for (FileInfo fileInfo : willMoves) {
					// String destName = UUID.randomUUID().toString();
					com.plugin.common.utils.files.FileInfo info = new com.plugin.common.utils.files.FileInfo();
					info.fileName = fileInfo.name;
					info.filePath = fileInfo.absolutePath;
					// FileUtils.copyFile(info, PRIVACY_SAPCE_PATH);
					FileUtils.moveFile(fileInfo.absolutePath,
							PRIVACY_SAPCE_PATH + fileInfo.name);
					Log.w(TAG, privacy_type+"");
					dao.insert(new PrivacyFile(null, fileInfo.name,
							fileInfo.name, fileInfo.absolutePath, new Date(),
							privacy_type));
					FileUtils.DeleteFile(info);
				}

				return null;
			}

			protected void onPostExecute(Void result) {
				dialog.dismiss();
				finish();
			};

		}.execute();
	}
}
