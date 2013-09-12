package com.xstd.pirvatephone.activity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.privatephone.adapter.ShowSDFilesAdapter;
import com.xstd.privatephone.adapter.ShowSDFilesAdapter.FileInfo;

public class ShowSDCardFilesActivity extends BaseActivity implements
		OnClickListener {

	protected static final String TAG = "ShowSDCardFilesActivity";
	private TextView return_btn;
	private TextView title_text;
	private TextView current_path;
	private ListView lv;
	private Button btn_move;
	private Button btn_add;
	private ShowSDFilesAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_sdcardfile);
		initViews();
		setListener();
	}

	private void setListener() {
		return_btn.setOnClickListener(this);
		btn_add.setOnClickListener(this);
		btn_move.setOnClickListener(this);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FileInfo fileInfo = (FileInfo) adapter.getItem(position);
				if (!fileInfo.isFolder) {
					fileInfo.isChecked = !fileInfo.isChecked;
				}
				Log.v(TAG, fileInfo.isChecked+"");
				adapter.notifyDataSetChanged();
			}
		});
	}

	private void initViews() {
		return_btn = (TextView) findViewById(R.id.ll_return_btn);
		title_text = (TextView) findViewById(R.id.ll_title_text);
		current_path = (TextView) findViewById(R.id.current_path);
		lv = (ListView) findViewById(R.id.lv);
		btn_move = (Button) findViewById(R.id.btn_move);
		btn_add = (Button) findViewById(R.id.btn_add);
		title_text.setText(getString(R.string.show_sdcardfile_title));
		current_path.setText(Environment.getExternalStorageDirectory()
				.getAbsolutePath());
		adapter = new ShowSDFilesAdapter(this);
		lv.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		if (v == return_btn) {
			finish();
		} else if (v == btn_add) {

		} else if (v == btn_move) {

		}
	}
}
