package com.xstd.pirvatephone.activity;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.R.layout;
import com.xstd.pirvatephone.R.menu;
import com.xstd.pirvatephone.dao.contextmodel.ContextModel;
import com.xstd.pirvatephone.dao.contextmodel.ContextModelDao;
import com.xstd.pirvatephone.dao.contextmodel.ContextModelDaoUtils;
import com.xstd.pirvatephone.dao.model.Model;
import com.xstd.pirvatephone.dao.model.ModelDao;
import com.xstd.pirvatephone.dao.model.ModelDaoUtils;
import com.xstd.privatephone.tools.Tools;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NewContextModelActivity extends Activity {

	private EditText model_name;
	private Button add_notinterept;
	private Button add_interept;
	private Button btn_cancle;
	private Button btn_sure;
	private ContextModelDao contextModelDao;
	private ModelDao modelDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_context_model);

		createModel();
		initView();

	}

	private void createModel() {
		modelDao = ModelDaoUtils.getModelDao(getApplicationContext());
		
	}

	private void initView() {
		model_name = (EditText) findViewById(R.id.et_model_name);

		add_notinterept = (Button) findViewById(R.id.btn_add_notinterept);
		add_interept = (Button) findViewById(R.id.btn_add_interept);
		
		btn_cancle = (Button) findViewById(R.id.btn_cancle);
		btn_sure = (Button) findViewById(R.id.btn_sure);

		add_notinterept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NewContextModelActivity.this,NotIntereptActivity.class);
				startActivity(intent);
			}
		});

		add_interept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NewContextModelActivity.this,IntereptActivity.class);
				startActivity(intent);
			}
		});

		btn_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btn_sure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String modelName = model_name.getText().toString().trim();
				if(!TextUtils.isEmpty(modelName)){
					//增加一种情景模式
					
					Model model = new Model();
					model.setModel_name(modelName);
					model.setModel_type(0);
					modelDao.insert(model);
					Tools.logSh("新建了一种情景模式");
				}
				
				finish();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_context_model, menu);
		return true;
	}

}
