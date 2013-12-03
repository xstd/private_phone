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
import com.xstd.privatephone.adapter.AddFromSmsRecordAdapter;
import com.xstd.privatephone.tools.Tools;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AddFromSmsRecordActivity extends Activity implements
		OnClickListener {

	@ViewMapping(ID = R.id.bt_sure)
	public RelativeLayout bt_sure;

	@ViewMapping(ID = R.id.bt_cancle)
	public RelativeLayout bt_cancle;

	@ViewMapping(ID = R.id.lv_contact)
	public ListView lv_contact;

	@ViewMapping(ID = R.id.tv_title)
	public TextView tv_title;

	@ViewMapping(ID = R.id.btn_edit)
	public Button btn_edit;

	@ViewMapping(ID = R.id.btn_back)
	public Button btn_back;

	@ViewMapping(ID = R.id.img_inside)
	public ImageView img_inside;
	@ViewMapping(ID = R.id.img_outside)
	public ImageView img_outside;

	@ViewMapping(ID = R.id.rl_loading)
	public LinearLayout rl_loading;

	private long end_time;
	private Long start_time;
	private Long duration;

	private static final int UPDATE = 0;
	private static final int UPDATE_UI = 1;
	private static final int FINISH = 2;
	private static final int NULL = 5;

	private Cursor recordCursor;
	private AddFromSmsRecordAdapter recordAdapter;

	private ArrayList<String> numbers = new ArrayList<String>();
	private ArrayList<Integer> _ids = new ArrayList<Integer>();

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
				showUpdateUI();
				
				break;

			case FINISH:
				progressDialog.dismiss();
				Toast.makeText(AddFromSmsRecordActivity.this, "增加了联系人",
						Toast.LENGTH_SHORT).show();
				finish();
				break;

			case NULL:
				Toast.makeText(AddFromSmsRecordActivity.this, "请选择联系人",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	private void showUpdateUI() {
		img_outside.clearAnimation();

		rl_loading.setVisibility(View.GONE);
		recordAdapter = new AddFromSmsRecordAdapter(
				AddFromSmsRecordActivity.this, recordCursor);
		lv_contact.setAdapter(recordAdapter);
		lv_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CheckBox checkbox = (CheckBox) view
						.findViewById(R.id.checkbox);
				checkbox.setChecked(!checkbox.isChecked());
				if (checkbox.isChecked()) {
					TextView tv_id = (TextView) view
							.findViewById(R.id.tv_id);
					Integer _id = Integer.valueOf(tv_id.getText().toString()
							.trim());
					if (checkbox.isChecked()) {
						if (!_ids.contains(_id)) {
							Tools.logSh("增加了number===" + _id);
							_ids.add(_id);
						}
					} else {
						if (_ids.contains(_id)) {
							Tools.logSh("移除了number===" + _id);
							_ids.remove(_id);
						}
					}
					recordAdapter.notifyChange(_ids);
					Tools.logSh("numbers===" + numbers);
				}
			}
		});
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_from_sms_record);

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
		GetSystemSmsRecordTast tast = new GetSystemSmsRecordTast(this);
		tast.execute();

	}

	private void initView() {
		ViewMapUtil.viewMapping(this, getWindow());

		tv_title.setText(getString(R.string.private_comm_add_from_smsrecord));

		bt_sure.setOnClickListener(this);
		bt_cancle.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_edit.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_from_sms_record, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.bt_sure:
			if (_ids != null && _ids.size() > 0) {
				Tools.logSh("selectPhones中个数为：" + numbers.size());
				// 显示选择对话框
				numbers = getPhoneNumberById(_ids);
				showRemoveDialog();
			} else {
				Toast.makeText(AddFromSmsRecordActivity.this, "选择联系人不能为空！！",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.bt_cancle:
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
			Cursor phoneCallCursor = resolver.query(Uri.parse("content://sms/"),
					null,  "_id=?", new String[] { id + "" },
					null);
			if (phoneCallCursor != null && phoneCallCursor.getCount() > 0) {
				while (phoneCallCursor.moveToNext()) {
					String address = phoneCallCursor.getString(phoneCallCursor
							.getColumnIndex("address"));
					phoneNumbers.add(address);
				}
				phoneCallCursor.close();
			}
		}
		return phoneNumbers;

	}

	public void showRemoveDialog() {
		final Builder builder = new AlertDialog.Builder(this);
		builder.setItems(new String[] { "移动联系人同时删除手机数据库", "仅添加联系人" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Tools.logSh("numbers=="+numbers+"   数组元素="+ArrayUtils.listToArray(numbers).length);
						AddSmsRecordTast task = new AddSmsRecordTast(
								AddFromSmsRecordActivity.this);
						WriteContactUtils mWriteContactUtils = new WriteContactUtils(
								AddFromSmsRecordActivity.this);
						switch (which) {
						case 0:
							Tools.logSh("0被点击了");
							flags_delete = true;
							mWriteContactUtils
									.writeContactBySmsRecord(ArrayUtils.listToArray(numbers));
							// 删除系统库中的联系人的相关信息,移动相关的通信信息
							task.execute();
							break;
						case 1:
							Tools.logSh("1被点击了");
							flags_delete = false;
							mWriteContactUtils
									.writeContactBySmsRecord(ArrayUtils.listToArray(numbers));
							finish();
							// 不删除系统库中的联系人,移动相关的通信信息
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

	private class AddSmsRecordTast extends AsyncTask<Void, Integer, Integer> {

		private Context mContext;

		public AddSmsRecordTast(Context context) {
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
					AddFromSmsRecordActivity.this);
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

	private class GetSystemSmsRecordTast extends
			AsyncTask<Void, Integer, Integer> {

		private Context mContext;

		public GetSystemSmsRecordTast(Context context) {
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
					Uri.parse("content://sms/"), null, null, null, null);

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
