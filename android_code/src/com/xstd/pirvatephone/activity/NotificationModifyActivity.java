package com.xstd.pirvatephone.activity;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.R.layout;
import com.xstd.pirvatephone.R.menu;
import com.xstd.privatephone.adapter.NotificationModifyAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class NotificationModifyActivity extends Activity {
	@ViewMapping(ID = R.id.back)
	public ImageView back;

	@ViewMapping(ID = R.id.settingList)
	public ListView listView;

	private NotificationModifyAdapter notificationAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification_modify);

		initView();
	}

	private void initView() {
		ViewMapUtil.viewMapping(this, getWindow());
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		notificationAdapter = new NotificationModifyAdapter(this);
		listView.setAdapter(notificationAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// view.findViewById(R.id.checkBox);

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notification_modify, menu);
		return true;
	}

}
