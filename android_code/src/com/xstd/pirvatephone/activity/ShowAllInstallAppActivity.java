package com.xstd.pirvatephone.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.privatephone.adapter.ShowAllInstallAppAdapter;

public class ShowAllInstallAppActivity extends BaseActivity implements
		OnClickListener {

	@ViewMapping(ID = R.id.ll_return_btn)
	public TextView ll_return_btn;

	@ViewMapping(ID = R.id.ll_title_text)
	public TextView ll_title_text;

	@ViewMapping(ID = R.id.lv)
	public ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_all_install_app);

		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		ll_return_btn.setOnClickListener(this);
		ll_title_text.setText(R.string.rs_choose_jump_app);
		lv.setAdapter(new ShowAllInstallAppAdapter(getApplicationContext()));
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				showOkDialog((PackageInfo) parent.getAdapter()
						.getItem(position));
			}

		});
	}

	private void showOkDialog(PackageInfo packageInfo) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(
				R.string.rs_set_jump_app).setMessage(
				Html.fromHtml(String.format(getString(R.string.rs_set_sure),
						packageInfo.applicationInfo
								.loadLabel(getPackageManager()))));
		builder.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_all_install_app, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ll_return_btn:
			finish();
			break;

		default:
			break;
		}
	}

}
