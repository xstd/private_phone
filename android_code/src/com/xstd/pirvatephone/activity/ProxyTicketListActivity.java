package com.xstd.pirvatephone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;

public class ProxyTicketListActivity extends BaseActivity implements View.OnClickListener {

	@ViewMapping(ID = R.id.ll_return_btn)
	public TextView return_bt;

	@ViewMapping(ID = R.id.ll_title_text)
	public TextView title_text;

	@ViewMapping(ID = R.id.lv)
	public ListView lv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_proxy_talk_list);

		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		return_bt.setOnClickListener(this);
		title_text.setText(R.string.service_jipiao);
	}

	@Override
	public void onClick(View v) {
		if (v == return_bt) {
			finish();
		}
	}

	/**
	 * 新建服务按钮
	 * 
	 * @param view
	 */
	public void addService(View view) {
		Intent intent = new Intent(this, AddProxyTicketActivity.class);
		startActivity(intent);
	}
}
