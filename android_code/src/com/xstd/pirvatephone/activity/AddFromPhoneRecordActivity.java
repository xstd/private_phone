package com.xstd.pirvatephone.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.utils.ArrayUtils;
import com.xstd.pirvatephone.utils.RecordToUsUtils;
import com.xstd.pirvatephone.utils.WriteContactUtils;
import com.xstd.privatephone.adapter.AddFromPhoneRecordAdapter;
import com.xstd.privatephone.tools.Tools;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddFromPhoneRecordActivity extends Activity implements
		View.OnClickListener {

	private static final int UPDATE = 0;
	private static final int UPDATE_UI = 1;
	private static final int FINISH = 2;

	@ViewMapping(ID = R.id.btn_sure)
	public RelativeLayout btn_sure;

	@ViewMapping(ID = R.id.btn_cancle)
	public RelativeLayout btn_cancle;

	@ViewMapping(ID = R.id.lv_contact)
	public ListView lv_contact;

	@ViewMapping(ID = R.id.tv_title)
	public TextView tv_title;

	@ViewMapping(ID = R.id.btn_edit)
	public Button btn_edit;

	@ViewMapping(ID = R.id.btn_back)
	public RelativeLayout btn_back;

	@ViewMapping(ID = R.id.img_inside)
	public ImageView img_inside;
	@ViewMapping(ID = R.id.img_outside)
	public ImageView img_outside;

	@ViewMapping(ID = R.id.rl_loading)
	public LinearLayout rl_loading;

	public AddFromPhoneRecordAdapter recordAdapter;
	private Cursor recordCursor;
	private long end_time;
	private Long start_time;
	private Long duration;
	private ArrayList<Integer> ids = new ArrayList<Integer>();
	private ArrayList<String> names = new ArrayList<String>();
	private ArrayList<String> numbers = new ArrayList<String>();

	private boolean flags_delete = false;

	private TextView recover_tv_progress;
	private TextView recover_tv_progress_detail;
	private ProgressBar recover_progress;
	private AlertDialog progressDialog;

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case UPDATE:
				end_time = System.currentTimeMillis();
				duration = end_time - start_time;
				if (duration < 1000) {
					TimerTask task = new TimerTask() {
						public void run() {
							Message msg = new Message();
							msg.what = UPDATE_UI;
							handler.sendMessage(msg);
						}
					};
					Timer timer = new Timer();
					timer.schedule(task, 1000 - duration);
				} else {
					Message msg2 = new Message();
					msg2.what = UPDATE_UI;
					handler.sendMessage(msg2);
				}

				break;

			case UPDATE_UI:
				img_outside.clearAnimation();

				rl_loading.setVisibility(View.GONE);
				recordAdapter = new AddFromPhoneRecordAdapter(
						AddFromPhoneRecordActivity.this, recordCursor);
				lv_contact.setAdapter(recordAdapter);
				lv_contact.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						CheckBox checkbox = (CheckBox) view
								.findViewById(R.id.checkbox);
						checkbox.setChecked(!checkbox.isChecked());
						TextView tv_id = (TextView) view
								.findViewById(R.id.tv_id);
						TextView tv_name = (TextView) view
								.findViewById(R.id.tv_name);
						Integer _id = Integer.valueOf(tv_id.getText().toString()
								.trim());
						String name = tv_name.getText().toString().trim();
						if (checkbox.isChecked()) {
							if (!ids.contains(_id)) {
								ids.add(_id);
								names.add(name);
							}
						} else {
							if (ids.contains(_id)) {
								ids.remove(_id);
								names.remove(name);
							}
						}
						recordAdapter.notifyChange(ids);
						Tools.logSh("ids===" + ids);

					}
				});
				break;

			case FINISH:
				progressDialog.dismiss();
				Toast.makeText(AddFromPhoneRecordActivity.this, "增加了联系人",
						Toast.LENGTH_SHORT).show();
				finish();
				break;

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_from_phone_record);

		initView();
		initData();
	}

	private void initData() {
		// 加载动画
		Animation loadingAnimation = AnimationUtils.loadAnimation(this,
				R.anim.loading_animation);
		// 使用ImageView显示动画
		img_outside.startAnimation(loadingAnimation);

		start_time = System.currentTimeMillis();
		GetSystemPhoneRecordTast tast = new GetSystemPhoneRecordTast(this);
		tast.execute();

	}

	private void initView() {

		ViewMapUtil.viewMapping(this, getWindow());

		tv_title.setText(getString(R.string.private_comm_add_from_phonerecord));
		btn_sure.setOnClickListener(this);
		btn_cancle.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_edit.setVisibility(View.GONE);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_from_phone_record, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		if (recordCursor != null) {
			recordCursor.close();
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_sure:

			if (ids != null && ids.size() > 0) {
				// Tools.logSh("selectPhones中个数为：" + selectPhones.length);
				// 通过Id寻找号码
				numbers = getPhoneNumberById(ids);

				showRemoveDialog();
			} else {
				Toast.makeText(AddFromPhoneRecordActivity.this, "选择联系人不能为空！！",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btn_cancle:
			finish();
			break;
		}
	}

	private ArrayList<String> getPhoneNumberById(ArrayList<Integer> phoneIds) {
		ArrayList<String> phoneNumbers = new ArrayList<String>();
		if (phoneIds == null || phoneIds.size() == 0) {
			return null;
		}
		for (int i = 0; i < phoneIds.size(); i++) {
			Integer id = phoneIds.get(i);
			// 获取详细通话记录
			ContentResolver resolver = getContentResolver();
			Cursor phoneCallCursor = resolver.query(CallLog.Calls.CONTENT_URI,
					null, CallLog.Calls._ID + "=?", new String[] { id + "" },
					null);
			if (phoneCallCursor != null && phoneCallCursor.getCount() > 0) {
				while (phoneCallCursor.moveToNext()) {
					String number = phoneCallCursor.getString(phoneCallCursor
							.getColumnIndex("number"));
					phoneNumbers.add(number);
				}
				phoneCallCursor.close();
			}
		}
		return phoneNumbers;
	}

	public void showRemoveDialog() {
		
		final AddPhoneRecordTast addPhoneRecordTast = new AddPhoneRecordTast(
				AddFromPhoneRecordActivity.this);
		final WriteContactUtils mWriteContactUtils = new WriteContactUtils(
				AddFromPhoneRecordActivity.this);

		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_private_remove_select, null);
		final LinearLayout ll_remove_and_delete = (LinearLayout) view
				.findViewById(R.id.ll_remove_and_delete);
		final LinearLayout ll_remove_only = (LinearLayout) view
				.findViewById(R.id.ll_remove_only);
		ll_remove_and_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				flags_delete = true;
				mWriteContactUtils
						.writeContactByPhoneRecord(ArrayUtils
								.listToArray(numbers));
				addPhoneRecordTast.execute();
				// 删除系统库中的联系人的相关信息,移动相关的通信信息
			}
		});

		ll_remove_only.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				flags_delete = false;
				mWriteContactUtils
						.writeContactByPhoneRecord(ArrayUtils
								.listToArray(numbers));
				// 不删除系统库中的联系人,移动相关的通信信息
				addPhoneRecordTast.execute();
			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

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

	private class AddPhoneRecordTast extends AsyncTask<Void, Integer, Integer> {

		private Context mContext;

		public AddPhoneRecordTast(Context context) {
			this.mContext = context;
		}

		@Override
		public void onPreExecute() {
			newInstance(mContext);
			Toast.makeText(mContext, "开始执行", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Integer doInBackground(Void... params) {

			RecordToUsUtils recordToUsUtils = new RecordToUsUtils(
					AddFromPhoneRecordActivity.this);
			recordToUsUtils.removeContactRecord(
					ArrayUtils.listToArray(numbers), flags_delete);
			return null;
		}

		@Override
		public void onPostExecute(Integer integer) {
			Message msg = new Message();
			msg.what = FINISH;
			handler.sendMessage(msg);

		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		public void onProgressUpdate(Integer... values) {

		}
	}

	private class GetSystemPhoneRecordTast extends
			AsyncTask<Void, Integer, Integer> {

		private Context mContext;

		public GetSystemPhoneRecordTast(Context context) {
			this.mContext = context;
		}

		@Override
		public void onPreExecute() {
			Toast.makeText(mContext, "开始执行", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			return null;
		}

		@Override
		public void onPostExecute(Integer integer) {

			recordCursor = getContentResolver().query(
					CallLog.Calls.CONTENT_URI, null, null, null, null);

			Message msg = new Message();
			msg.what = UPDATE;
			handler.sendMessage(msg);

		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		public void onProgressUpdate(Integer... values) {

		}
	}

}
