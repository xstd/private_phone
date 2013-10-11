package com.xstd.pirvatephone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.privatephone.adapter.ProxyTalkListAdapter;

public class ProxyTalkListActivity extends BaseActivity implements
		View.OnClickListener {

	@ViewMapping(ID = R.id.ll_return_btn)
	public TextView return_bt;

	@ViewMapping(ID = R.id.ll_title_text)
	public TextView title_text;

	@ViewMapping(ID = R.id.lv)
	public ListView lv;

	private ProxyTalkListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_proxy_talk_list);

		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		return_bt.setOnClickListener(this);
		title_text.setText(R.string.service_dailiao);
		adapter = new ProxyTalkListAdapter(getApplicationContext());
		lv.setAdapter(adapter);
	}
	
	/**
	 * 更新界面数据
	 */
	private void updateData(){
		adapter.updateDatas();
	}
	
	@Override
	protected void onResume() {
		updateData();
		super.onResume();
	}
	

	/**
	 * 新建服务按钮
	 * 
	 * @param view
	 */
	public void addService(View view) {
		Intent intent = new Intent(this, ProxyTalkActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		if (v == return_bt) {
			finish();
		}
	}
}
