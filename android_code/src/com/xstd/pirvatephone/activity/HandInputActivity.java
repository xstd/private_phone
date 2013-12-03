package com.xstd.pirvatephone.activity;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfo;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.utils.RecordToUsUtils;
import com.xstd.pirvatephone.utils.WriteContactUtils;
import com.xstd.privatephone.tools.Tools;

public class HandInputActivity extends BaseActivity {

	private static final int SHOW_TOAST = 0;
	private static final int REMOVE_FINISH = 1;

	private RelativeLayout btn_cancle;
	private RelativeLayout btn_sure;
	private Button btn_back;
	private Button btn_edit;
	private EditText et_name;
	private EditText et_phone;

	/** 选取转换为隐私联系人的号码 **/
	private ContactInfo contactInfo = new ContactInfo();
	private TextView tv_title;
	private RadioGroup myRadioGroup;
	private RadioButton myRadioButton1;
	private RadioButton myRadioButton2;
	private ContactInfoDao contactInfoDao;

	private TextView recover_tv_progress;
	private TextView recover_tv_progress_detail;
	private ProgressBar recover_progress;

	private AlertDialog progressDialog;
	private boolean flags_delete = false;
	private String selectNumber;

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case SHOW_TOAST:
				Toast.makeText(HandInputActivity.this, "已经存在该号码，请从新添加",
						Toast.LENGTH_SHORT).show();
				break;

			case REMOVE_FINISH:
				progressDialog.dismiss();
				finish();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hand_input);

		contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(HandInputActivity.this);

		initView();
	}

	private void initView() {
		// title
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_edit = (Button) findViewById(R.id.btn_edit);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("新建隐私联系人");
		btn_edit.setVisibility(View.GONE);

		// content
		et_name = (EditText) findViewById(R.id.put_et_name);
		et_phone = (EditText) findViewById(R.id.put_et_phone);
		// radioGroup
		myRadioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
		myRadioGroup.check(R.id.myRadioButton1);
		myRadioButton1 = (RadioButton) findViewById(R.id.myRadioButton1);
		myRadioButton2 = (RadioButton) findViewById(R.id.myRadioButton2);
		// bottom
		btn_sure = (RelativeLayout) findViewById(R.id.btn_sure);
		btn_cancle = (RelativeLayout) findViewById(R.id.btn_cancle);

		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btn_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 若为空
				String name = et_name.getText().toString().trim();
				String phone = et_phone.getText().toString().trim();
				Tools.logSh("name===" + name + "    phone==" + phone);
				if (TextUtils.isEmpty(phone)) {
					Toast.makeText(getApplicationContext(), "请填写正确的电话",
							Toast.LENGTH_SHORT).show();
				} else {// 不为空,添加到我们数据库。

					if (name == null || "".equals(name)) {
						name = phone;
					}
					contactInfo.setPhone_number(phone);
					contactInfo.setDisplay_name(name);
					// type:0-正常接听，1-立即挂断
					if (myRadioButton1.isChecked()) {
						Tools.logSh("type===" + 0);
						contactInfo.setType(0);
					} else {
						Tools.logSh("type===" + 1);
						contactInfo.setType(1);
					}

					Tools.logSh("name===" + name + "    phone==" + phone);
					WriteContactUtils mWriteContactUtils = new WriteContactUtils(
							getApplicationContext());
					// 仅选择一个时：判断私密通信联系人是否已有该联系人
					boolean b = mWriteContactUtils
							.isPrivateContact(new String[] { phone });
					if (!b) {
						contactInfoDao.insert(contactInfo);
					}

					Toast.makeText(getApplicationContext(), "想联系人数据库中添加了一条新数据",
							Toast.LENGTH_SHORT).show();
					selectNumber = phone;
					showRemoveDialog(selectNumber);

				}
			}
		});

		btn_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void showRemoveDialog(final String selectPhone) {
		final Builder builder = new AlertDialog.Builder(this);
		builder.setItems(new String[] { "移动联系人同时删除手机数据库", "仅添加联系人" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						switch (which) {
						case 0:
							flags_delete = true;

							// 删除系统库中的联系人的相关信息,移动相关的通信信息
							RemoveRecordTast tast = new RemoveRecordTast(
									HandInputActivity.this, selectPhone);

							tast.execute();

							break;
						case 1:
							flags_delete = false;

							// 不删除系统库中的联系人,移动相关的通信信息
							RemoveRecordTast tast2 = new RemoveRecordTast(
									HandInputActivity.this, selectPhone);
							tast2.execute();
							break;
						}
					}
				});
		AlertDialog removeDialog = builder.create();
		removeDialog.setCanceledOnTouchOutside(false);
		removeDialog.show();

	}

	public void newInstance(Context ctx) {
		AlertDialog.Builder builder = new Builder(ctx);
		View dialogView = LayoutInflater.from(ctx).inflate(
				R.layout.private_comm_recover_progress_dialog, null, true);
		recover_tv_progress = (TextView) dialogView
				.findViewById(R.id.recover_tv_progress);
		recover_tv_progress_detail = (TextView) dialogView
				.findViewById(R.id.recover_tv_progress_detail);
		recover_progress = (ProgressBar) findViewById(R.id.recover_progress);
		builder.setView(dialogView);
		progressDialog = builder.create();
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	private class RemoveRecordTast extends AsyncTask<Void, Integer, Integer> {

		private Context mContext;
		private String mPhone;

		public RemoveRecordTast(Context context, String phone) {
			this.mContext = context;
			mPhone = phone;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		public void onPreExecute() {
			newInstance(mContext);
			Toast.makeText(mContext, "开始执行", Toast.LENGTH_SHORT).show();
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected Integer doInBackground(Void... params) {

			remove(new String[] { mPhone });
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		public void onPostExecute(Integer integer) {
			Toast.makeText(mContext, "执行完毕", Toast.LENGTH_SHORT).show();
			Message msg = new Message();
			msg.what = REMOVE_FINISH;
			handler.sendMessage(msg);
		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		public void onProgressUpdate(Integer... values) {

		}
	}

	private void remove(String[] numbers) {

		if (flags_delete) {
			Tools.logSh("执行删除系统数据库信息");

			RecordToUsUtils recordToUsUtils = new RecordToUsUtils(
					HandInputActivity.this);
			recordToUsUtils.removeContactRecord(numbers, flags_delete);

		} else {
			Tools.logSh("仅仅移动联系人");

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hand_input, menu);
		return true;
	}

}
