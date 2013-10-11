package com.xstd.pirvatephone.activity;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.R.layout;
import com.xstd.pirvatephone.R.menu;
import com.xstd.pirvatephone.dao.contextmodel.ContextModelDao;
import com.xstd.pirvatephone.dao.contextmodel.ContextModelDaoUtils;
import com.xstd.pirvatephone.dao.model.ModelDao;
import com.xstd.pirvatephone.dao.model.ModelDaoUtils;
import com.xstd.privatephone.adapter.ModelAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class ContextModelActivity extends Activity {

	private ListView model_lv;
	private Button add_btn;
	private ContextModelDao contextModelDao;
	private Cursor modelCursor;
	private ModelAdapter modelAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_context_model);
		
		initView();
		
		showModel();
	}

	private void showModel() {
		//查询情景模式表。若不存在表，创建该表，存在则查询数据，展示数据
		ModelDao modelDao = ModelDaoUtils.getModelDao(getApplicationContext());
		SQLiteDatabase ModelDatabase = modelDao.getDatabase();
		modelCursor = ModelDatabase.query(ModelDao.TABLENAME, null, null, null, null, null, null);
		modelAdapter = new ModelAdapter(getApplicationContext(),modelCursor);
		model_lv.setAdapter(modelAdapter);
	}

	private void initView() {
		model_lv = (ListView) findViewById(R.id.model_lv);
		add_btn = (Button) findViewById(R.id.add_btn);
		
		add_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ContextModelActivity.this,NewContextModelActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.context_model, menu);
		return true;
	}

	@Override
	protected void onResume() {
		modelCursor.requery();
		modelAdapter.notifyDataSetChanged();
		super.onResume();
	}
}
