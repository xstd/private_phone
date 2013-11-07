package com.xstd.pirvatephone.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.utils.ArrayUtils;
import com.xstd.pirvatephone.utils.WriteContactUtils;
import com.xstd.privatephone.adapter.AddFromSmsRecordAdapter;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AddFromSmsRecordActivity extends Activity implements
		OnClickListener {

	@ViewMapping(ID = R.id.bt_sure)
	public Button bt_sure;

	@ViewMapping(ID = R.id.bt_cancle)
	public Button bt_cancle;

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
	private Cursor recordCursor;
	private AddFromSmsRecordAdapter recordAdapter;
	private ArrayList<String> numbers = new ArrayList<String>();

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
							TextView tv_hidden = (TextView) view
									.findViewById(R.id.tv_hidden);
							String number = tv_hidden.getText().toString()
									.trim();
							if (!numbers.contains(number)) {
								numbers.add(number);
							} else {
								numbers.remove(number);
							}
						}
					}
				});
				break;
				
			case FINISH:
				Toast.makeText(AddFromSmsRecordActivity.this, "增加了联系人", Toast.LENGTH_SHORT).show();
				finish();
			break;
			}
		}
	};

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
			GetAddTast task = new GetAddTast(AddFromSmsRecordActivity.this);
			task.execute();
			break;
		case R.id.bt_cancle:
			finish();
			break;
		}

	}

	private class GetAddTast extends AsyncTask<Void, Integer, Integer> {

		private Context mContext;

		public GetAddTast(Context context) {
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

			if (numbers.size() > 0) {
				WriteContactUtils writeContactUtils = new WriteContactUtils(
						AddFromSmsRecordActivity.this);
				String[] array = new ArrayUtils().listToArray(numbers);
				writeContactUtils.removeRepeat(array);
				writeContactUtils.writeContact(array);
			} else {

			}

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
