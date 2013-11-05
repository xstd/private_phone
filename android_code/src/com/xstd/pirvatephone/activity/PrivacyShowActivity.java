package com.xstd.pirvatephone.activity;

import java.io.File;
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
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.privacy.PrivacyDaoUtils;
import com.xstd.pirvatephone.dao.privacy.PrivacyFile;
import com.xstd.pirvatephone.dao.privacy.PrivacyFileDao;
import com.xstd.pirvatephone.dao.privacy.PrivacyPwd;
import com.xstd.pirvatephone.dao.privacy.PrivacyPwdDao;
import com.xstd.pirvatephone.utils.FileUtils;
import com.xstd.privatephone.adapter.PrivacyFileAdapter;
import com.xstd.privatephone.adapter.PrivacyPwdAdapter;

public class PrivacyShowActivity extends BaseActivity {

	protected static final String TAG = "PrivacyShowActivity";
	/**
	 * 请求隐藏文件的请求码
	 */
	private static final int REQUEST_RECORDER_CODE = 2;
	private TextView return_bt;
	/**
	 * 上个页面传来的隐藏文件类型int
	 */
	private int privacy_type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy_show);
		privacy_type = getIntent().getIntExtra("privacy_type", 0);
		initViews();
		setListener();
	}

	@Override
	protected void onResume() {
		if (privacy_type == 4) {
			new QueryPrivacyPwd().execute();
		} else {
			new QueryPrivacyFile().execute();// 界面每次获得焦点就刷新数据
		}
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
		TextView title_text = (TextView) findViewById(R.id.ll_title_text);
		title_text.setText(getString(R.string.privacy)
				+ PrivacySpaceActivity.home_privacy_title[privacy_type]);
		Button add_privacy = (Button) findViewById(R.id.add_privacy);
		empty_view = (RelativeLayout) findViewById(R.id.empty_view);
		lv = (ListView) findViewById(R.id.lv);
		add_privacy.setText(getString(R.string.privacy_add_msg)
				+ PrivacySpaceActivity.home_privacy_title[privacy_type]);
	}

	private RelativeLayout empty_view;
	private ListView lv;

	/**
	 * 下方添加按钮
	 * 
	 * @param view
	 */
	public void add(View view) {
		Intent intent;
		switch (privacy_type) {
		case 0:
			intent = new Intent(PrivacyShowActivity.this, AddFileActivity.class);
			intent.putExtra("privacy_type", privacy_type);
			intent.putExtra("ref_id", getIntent().getLongExtra("ref_id", -1));
			startActivity(intent);
			break;
		case 1:
			showAudioSelectDialog();
			break;
		case 2:
			intent = new Intent(PrivacyShowActivity.this, AddFileActivity.class);
			intent.putExtra("privacy_type", privacy_type);
			startActivity(intent);
			break;
		case 3:
			// 如果选择的文件隐藏，直接跳转到选择文件界面，而不是弹出dialog
			intent = new Intent(PrivacyShowActivity.this,
					ShowSDCardFilesActivity.class);
			intent.putExtra("privacy_type", privacy_type);
			startActivity(intent);
			break;
		case 4:
			showAddPwdDialog();
			break;
		}
	}

	/**
	 * 选择音频对话框
	 */
	private void showAudioSelectDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(R.array.privacy_audio,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.w(TAG, which + "");
						Intent intent = new Intent();
						switch (which) {
						case 0:
							intent.setAction(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
							startActivityForResult(intent,
									REQUEST_RECORDER_CODE);
							break;
						case 1:
							intent = new Intent(PrivacyShowActivity.this,
									AddFileActivity.class);
							intent.putExtra("privacy_type", privacy_type);
							startActivity(intent);
							break;
						}
					}
				});
		builder.show();
	}

	/**
	 * 添加密码本对话框
	 */
	private void showAddPwdDialog() {
		View view = View.inflate(this, R.layout.dialog_add_pwd, null);
		final EditText et_name = (EditText) view.findViewById(R.id.et_pwd_name);
		final EditText et_site = (EditText) view.findViewById(R.id.et_pwd_site);
		final EditText et_num = (EditText) view
				.findViewById(R.id.et_pwd_number);
		final EditText et_pwd = (EditText) view.findViewById(R.id.et_pwd_pwd);
		AlertDialog builder = new AlertDialog.Builder(this)
				.setTitle(R.string.add_pwd_title)
				.setView(view)
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String name = et_name.getText().toString()
										.trim();
								String pwd = et_pwd.getText().toString().trim();
								String num = et_num.getText().toString().trim();
								String site = et_site.getText().toString()
										.trim();
								if (TextUtils.isEmpty(name)
										|| TextUtils.isEmpty(pwd)) {
									Toast.makeText(PrivacyShowActivity.this,
											R.string.error_empty_name,
											Toast.LENGTH_SHORT).show();
									showAddPwdDialog();
								} else {
									PrivacyPwdDao dao = PrivacyDaoUtils
											.getPwdDao(PrivacyShowActivity.this);
									dao.insert(new PrivacyPwd(null, name, site,
											num, pwd));
									new QueryPrivacyPwd().execute();
									Toast.makeText(PrivacyShowActivity.this,
											R.string.success_empty_name,
											Toast.LENGTH_SHORT).show();
								}
							}
						}).create();
		builder.setCanceledOnTouchOutside(false);
		builder.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_RECORDER_CODE) {
			Uri uri = data.getData();

			Cursor cursor = getContentResolver().query(uri, null, null, null,
					null);
			cursor.moveToFirst();
			String path = cursor.getString(1);
			String name = cursor.getString(2);
			cursor.close();

			hideFile(name, path);

		}
	}

	/**
	 * 文件隐藏
	 * 
	 * @param fileName
	 *            要隐藏文件的名字
	 * @param filePath
	 *            要隐藏文件的路径
	 */
	private void hideFile(String fileName, String filePath) {
		new AsyncTask<String, Void, Void>() {

			protected void onPreExecute() {
				dialog = new ProgressDialog(PrivacyShowActivity.this);
				dialog.show();
			}

			@Override
			protected Void doInBackground(String... params) {
				PrivacyFileDao dao = PrivacyDaoUtils
						.getFileDao(PrivacyShowActivity.this);
				// String destName = UUID.randomUUID().toString();
				com.plugin.common.utils.files.FileInfo info = new com.plugin.common.utils.files.FileInfo();
				info.fileName = params[0];
				info.filePath = params[1];
				FileUtils.moveFile(params[1],
						ShowSDCardFilesActivity.PRIVACY_SAPCE_PATH + params[0]);
				dao.insert(new PrivacyFile(null, params[0], params[0],
						params[1], new Date(), privacy_type, null));
				FileUtils.DeleteFile(info);
				return null;
			}

			protected void onPostExecute(Void result) {
                new QueryPrivacyFile().execute();
                dialog.dismiss();
			}

		}.execute(fileName, filePath);
	}

	ProgressDialog dialog;

	/**
	 * 异步查询隐藏文件
	 * 
	 * @author Chrain
	 */
	private class QueryPrivacyFile extends
			AsyncTask<Void, Void, List<PrivacyFile>> {

		@Override
		protected List<PrivacyFile> doInBackground(Void... params) {
			PrivacyFileDao dao = PrivacyDaoUtils
					.getFileDao(PrivacyShowActivity.this);
			String type = String.valueOf(privacy_type);
			String ref_id = String.valueOf(getIntent().getLongExtra("ref_id",
					-1));
			String orderBy = PrivacyFileDao.Properties.SrcName.columnName
					+ " COLLATE LOCALIZED ASC";
			Cursor cursor = null;

			if (privacy_type == 0) {
				cursor = dao.getDatabase().query(dao.getTablename(),
						dao.getAllColumns(), "type=? and ref_id=?",
						new String[] { type, ref_id }, null, null, orderBy);
			} else {
				cursor = dao.getDatabase().query(dao.getTablename(),
						dao.getAllColumns(), "type=?", new String[] { type },
						null, null, orderBy);
			}

			List<PrivacyFile> result = new ArrayList<PrivacyFile>();
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					PrivacyFile mapping = new PrivacyFile();
					mapping.setId(cursor.getLong(cursor
							.getColumnIndex(PrivacyFileDao.Properties.Id.columnName)));
					mapping.setSrcName(cursor.getString(cursor
							.getColumnIndex(PrivacyFileDao.Properties.SrcName.columnName)));
					mapping.setSrcPath(cursor.getString(cursor
							.getColumnIndex(PrivacyFileDao.Properties.SrcPath.columnName)));
					mapping.setDestName(cursor.getString(cursor
							.getColumnIndex(PrivacyFileDao.Properties.DestName.columnName)));
					mapping.setMisstime(new Date(
							cursor.getLong(cursor
									.getColumnIndex(PrivacyFileDao.Properties.Misstime.columnName))));
					mapping.setType(cursor.getInt(cursor
							.getColumnIndex(PrivacyFileDao.Properties.Type.columnName)));
					result.add(mapping);
				}
			}
			if (cursor != null) {
				cursor.close();
			}
			return result;
		}

		@Override
		protected void onPostExecute(List<PrivacyFile> result) {
			FileUtils.updateSystemFile(PrivacyShowActivity.this);
			if (result == null || result.size() < 1) {
				empty_view.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
				return;
			}
			empty_view.setVisibility(View.GONE);
			lv.setVisibility(View.VISIBLE);
			final PrivacyFileAdapter adapter = new PrivacyFileAdapter(
					PrivacyShowActivity.this, result);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					View pop = View.inflate(PrivacyShowActivity.this,
							R.layout.pop_setting, null);
					final PrivacyFile mapping = (PrivacyFile) adapter
							.getItem(position);
					final PopupWindow window = new PopupWindow(pop,
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT, true);
					Button btn1 = (Button) pop.findViewById(R.id.btn1);
					btn1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							new AsyncTask<Void, Void, Boolean>() {

								@Override
								protected Boolean doInBackground(Void... params) {
									return FileUtils
											.moveFile(
													ShowSDCardFilesActivity.PRIVACY_SAPCE_PATH
															+ mapping
																	.getDestName(),
													mapping.getSrcPath());
								}

								protected void onPostExecute(Boolean result) {
									if (result) {
										PrivacyFileDao dao = PrivacyDaoUtils
												.getFileDao(PrivacyShowActivity.this);
										dao.delete(mapping);
										new QueryPrivacyFile().execute();
										Toast.makeText(
												PrivacyShowActivity.this,
												R.string.privacy_backfile_success_msg,
												Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(
												PrivacyShowActivity.this,
												R.string.privacy_backfile_failed_msg,
												Toast.LENGTH_SHORT).show();
									}
									 FileUtils.updateSystemFile(PrivacyShowActivity.this);
									window.dismiss();
								}

							}.execute();
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
							PrivacyFileDao dao = PrivacyDaoUtils
									.getFileDao(PrivacyShowActivity.this);
							dao.delete(mapping);
							new QueryPrivacyFile().execute();
							window.dismiss();
						}
					});
					Button btn3 = (Button) pop.findViewById(R.id.btn3);
					btn3.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							String type = "";
							if (privacy_type == 0) {
								type = "image/*";
							} else if (privacy_type == 1) {
								type = "audio/*";
							} else if (privacy_type == 2) {
								type = "video/*";
							}
							intent.setDataAndType(Uri.fromFile(new File(
									ShowSDCardFilesActivity.PRIVACY_SAPCE_PATH
											+ mapping.getDestName())), type);
							startActivity(intent);
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

	private class QueryPrivacyPwd extends
			AsyncTask<Void, Void, List<PrivacyPwd>> {

		@Override
		protected List<PrivacyPwd> doInBackground(Void... params) {
			PrivacyPwdDao dao = PrivacyDaoUtils
					.getPwdDao(PrivacyShowActivity.this);
			String orderBy = PrivacyPwdDao.Properties.Name.columnName
					+ " COLLATE LOCALIZED ASC";
			Cursor cursor = dao.getDatabase().query(dao.getTablename(),
					dao.getAllColumns(), null, null, null, null, orderBy);
			List<PrivacyPwd> result = new ArrayList<PrivacyPwd>();
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					PrivacyPwd mapping = new PrivacyPwd();
					mapping.setId(cursor.getLong(cursor
							.getColumnIndex(PrivacyPwdDao.Properties.Id.columnName)));
					mapping.setName(cursor.getString(cursor
							.getColumnIndex(PrivacyPwdDao.Properties.Name.columnName)));
					mapping.setNumber(cursor.getString(cursor
							.getColumnIndex(PrivacyPwdDao.Properties.Number.columnName)));
					mapping.setPassword(cursor.getString(cursor
							.getColumnIndex(PrivacyPwdDao.Properties.Password.columnName)));
					mapping.setSite(cursor.getString(cursor
							.getColumnIndex(PrivacyPwdDao.Properties.Site.columnName)));
					result.add(mapping);
				}
			}
			if (cursor != null) {
				cursor.close();
			}
			return result;
		}

		@Override
		protected void onPostExecute(List<PrivacyPwd> result) {
			if (result == null || result.size() < 1) {
				empty_view.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
				return;
			}
			empty_view.setVisibility(View.GONE);
			lv.setVisibility(View.VISIBLE);
			final PrivacyPwdAdapter adapter = new PrivacyPwdAdapter(
					PrivacyShowActivity.this, result);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					final PrivacyPwd privacyPwd = (PrivacyPwd) adapter
							.getItem(position);
					AlertDialog dialog = new AlertDialog.Builder(
							PrivacyShowActivity.this).setItems(
							R.array.privacy_pwd_setting,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										showDetailInfo(privacyPwd);
										break;
									case 1:
										modifyInfo(privacyPwd, true);
										break;
									case 2:
										PrivacyPwdDao dao = PrivacyDaoUtils
												.getPwdDao(PrivacyShowActivity.this);
										dao.delete(privacyPwd);
										new QueryPrivacyPwd().execute();
										break;
									}
									dialog.dismiss();
								}

							}).create();
					dialog.show();
				}
			});
		}

		/**
		 * 显示详细信息
		 * 
		 * @param privacyPwd
		 */
		private void showDetailInfo(PrivacyPwd privacyPwd) {
			modifyInfo(privacyPwd, false);
		}

		/**
		 * 修改
		 * 
		 * @param privacyPwd
		 */
		private void modifyInfo(final PrivacyPwd privacyPwd, final boolean flag) {
			View view = View.inflate(PrivacyShowActivity.this,
					R.layout.dialog_add_pwd, null);
			final EditText et_name = (EditText) view
					.findViewById(R.id.et_pwd_name);
			et_name.setText(privacyPwd.getName());
			final EditText et_site = (EditText) view
					.findViewById(R.id.et_pwd_site);
			et_site.setText(privacyPwd.getSite());
			final EditText et_num = (EditText) view
					.findViewById(R.id.et_pwd_number);
			et_num.setText(privacyPwd.getNumber());
			final EditText et_pwd = (EditText) view
					.findViewById(R.id.et_pwd_pwd);
			et_pwd.setText(privacyPwd.getPassword());
			if (!flag) {
				et_name.setEnabled(false);
				et_name.setFocusable(false);
				et_site.setEnabled(false);
				et_site.setFocusable(false);
				et_num.setEnabled(false);
				et_num.setFocusable(false);
				et_pwd.setEnabled(false);
				et_pwd.setFocusable(false);
			}
			AlertDialog builder = new AlertDialog.Builder(
					PrivacyShowActivity.this)
					.setTitle(R.string.add_pwd_title)
					.setView(view)
					.setNegativeButton(android.R.string.cancel, null)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (!flag)
										return;
									String name = et_name.getText().toString()
											.trim();
									String pwd = et_pwd.getText().toString()
											.trim();
									String num = et_num.getText().toString()
											.trim();
									String site = et_site.getText().toString()
											.trim();
									if (TextUtils.isEmpty(name)
											|| TextUtils.isEmpty(pwd)) {
										Toast.makeText(
												PrivacyShowActivity.this,
												R.string.error_empty_name,
												Toast.LENGTH_LONG).show();
									} else {
										PrivacyPwdDao dao = PrivacyDaoUtils
												.getPwdDao(PrivacyShowActivity.this);
										privacyPwd.setName(name);
										privacyPwd.setSite(site);
										privacyPwd.setNumber(num);
										privacyPwd.setPassword(pwd);
										dao.update(privacyPwd);
										new QueryPrivacyPwd().execute();
										Toast.makeText(
												PrivacyShowActivity.this,
												R.string.success_modify,
												Toast.LENGTH_SHORT).show();
									}
								}
							}).create();
			builder.setCanceledOnTouchOutside(false);
			builder.show();
		}
	}
}
