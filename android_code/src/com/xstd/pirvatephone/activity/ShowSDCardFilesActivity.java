package com.xstd.pirvatephone.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.plugin.common.utils.UtilsConfig;
import com.plugin.common.utils.files.FileOperatorHelper;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.privacy.PrivacyDaoUtils;
import com.xstd.pirvatephone.dao.privacy.SrcToDestMapping;
import com.xstd.pirvatephone.dao.privacy.SrcToDestMappingDao;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_sdcardfile);
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
			finish();
		} else if (v == btn_add) {
			selectAll();
		} else if (v == btn_move) {
			missingFile();
		}
	}

	private void selectAll() {
		Log.i(TAG, "全选按钮，");
		com.plugin.common.utils.files.FileInfo fileInfo = new com.plugin.common.utils.files.FileInfo();
		File file = new File("/sdcard/abc");
		fileInfo.filePath = file.getAbsolutePath();
		fileInfo.fileName = file.getName();
		Log.i(TAG, fileInfo.fileName);
		FileOperatorHelper.copyFile(fileInfo, "/sdcard/def");
		Log.i(TAG, "全选按钮jieshu，");
	}

	public static final String PRIVACY_SAPCE_PATH = UtilsConfig.DISK_DIR_PATH
			+ "MISSFolder" + File.separator;
	ProgressDialog dialog;

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
				SrcToDestMappingDao dao = PrivacyDaoUtils
						.getPrivacyDao(ShowSDCardFilesActivity.this);
				for (FileInfo fileInfo : willMoves) {
					long currentTimeMillis = System.currentTimeMillis();
					String destName = UUID.randomUUID().toString();
					com.plugin.common.utils.files.FileInfo info = new com.plugin.common.utils.files.FileInfo();
					info.fileName = fileInfo.name;
					info.filePath = fileInfo.absolutePath;
					FileOperatorHelper.copyFile(info, PRIVACY_SAPCE_PATH
							+ destName);
					Log.i(TAG, "begin insert ------");
					dao.insert(new SrcToDestMapping(fileInfo.name, destName,
							fileInfo.absolutePath, currentTimeMillis));
					FileOperatorHelper.DeleteFile(info);
				}

				return null;
			}

			protected void onPostExecute(Void result) {
				dialog.dismiss();
			};

		}.execute();
	}
}
