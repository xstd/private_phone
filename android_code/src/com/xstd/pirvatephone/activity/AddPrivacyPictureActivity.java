package com.xstd.pirvatephone.activity;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class AddPrivacyPictureActivity extends BaseActivity implements OnClickListener {

	@ViewMapping(ID = R.id.ll_return_btn)
	public TextView return_bt;

	@ViewMapping(ID = R.id.ll_title_text)
	public TextView title_text;

	@ViewMapping(ID = R.id.ll_toools)
	public ImageView toools;

	@ViewMapping(ID = R.id.count)
	public TextView count;

//	@ViewMapping(ID = R.id.gv)
//	public GridView gv;
//
//	@ViewMapping(ID = R.id.btn_add_picture)
//	public Button btn_add;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_privacy_picture);

//		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		return_bt.setOnClickListener(this);
		title_text.setText(R.string.privacy_picture);
		toools.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

	}
}
