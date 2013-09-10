package com.xstd.pirvatephone.activity;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.R.layout;
import com.xstd.pirvatephone.R.menu;
import com.xstd.privatephone.view.MyScrollView;
import com.xstd.privatephone.view.MyScrollView.IMyScrollListener;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PrivateCommActivity extends BaseActivity {

	private MyScrollView myScrollView;

	private LinearLayout body_layout;

	private Button btn_sms;
	private Button btn_phone;
	private Button btn_contacts;
	
	private boolean isEidting = false;

	private int currentView = 0;

	private Button ib_back;

	private Button edit;

	private RelativeLayout sms_rl_select;
	private RelativeLayout contact_rl_select;
	private RelativeLayout dial_rl_select;
	
	private LinearLayout sms_ll_remove;
	private LinearLayout dial_ll_lv;
	private LinearLayout contact_ll_lv;


	private ListView sms_lv_cont;

	private TextView sms_tv_empty;

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_private_comm);

		initView();

	}

	private void initView() {
		
		//1��title��
		edit = (Button) findViewById(R.id.edit);
		ib_back = (Button) findViewById(R.id.ib_back);

		btn_sms = (Button) findViewById(R.id.btn_sms);
		btn_phone = (Button) findViewById(R.id.btn_phone);
		btn_contacts = (Button) findViewById(R.id.btn_contacts);

		//2�� scrollview��
		myScrollView = new MyScrollView(this);

		View smsView = getLayoutInflater().inflate(R.layout.spac_sms, null);
		myScrollView.addView(smsView, 0);

		View dialView = getLayoutInflater().inflate(R.layout.spac_dial, null);
		myScrollView.addView(dialView, 1);

		View contaceView = getLayoutInflater().inflate(R.layout.spac_contact,
				null);
		myScrollView.addView(contaceView, 2);

		body_layout = (LinearLayout) findViewById(R.id.body_layout);
		body_layout.addView(myScrollView);

		//ListView
		sms_lv_cont = (ListView) findViewById(R.id.sms_lv_cont);
		sms_tv_empty = (TextView) findViewById(R.id.sms_tv_empty);
		sms_lv_cont.setEmptyView(sms_tv_empty);
		
		ib_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// ������ҳ������ҳ��ʱ����ʼ��ʱ��ʾsms
		initSpacButton(btn_sms);

		btn_sms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(currentView!=0){
				initSpacButton(btn_sms);
				myScrollView.moveToDest(0);
				}
			}
		});
		btn_phone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(currentView!=1){
				initSpacButton(btn_phone);
				myScrollView.moveToDest(1);
				}
			}
		});
		btn_contacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(currentView!=2){
				initSpacButton(btn_contacts);
				myScrollView.moveToDest(2);
				}
			}
		});

		//viewpager����ʱ
		myScrollView.setMyScrollListener(new IMyScrollListener() {

			@Override
			public void moveToDest(int destId) {
				switch (destId) {
				case 0:
					initSpacButton(btn_sms);
					initEdit();
					break;
				case 1:
					initSpacButton(btn_phone);
					initEdit();
					break;
				case 2:
					initSpacButton(btn_contacts);
					initEdit();
					break;
				}
			}
		});
		
		//�༭ѡ��(��)
		sms_rl_select = (RelativeLayout) findViewById(R.id.sms_rl_select);
		contact_rl_select = (RelativeLayout) findViewById(R.id.contact_rl_select);
		dial_rl_select = (RelativeLayout) findViewById(R.id.dial_rl_select);
		//�༭ѡ��(��)
		sms_ll_remove = (LinearLayout) findViewById(R.id.sms_ll_remove);
		dial_ll_lv = (LinearLayout) findViewById(R.id.dial_ll_remove);
		contact_ll_lv = (LinearLayout) findViewById(R.id.contact_ll_add);
		
		
		// ����༭ʱ��
		edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//��һ�ε��ʱ��isEidting=false
				isEidting = !isEidting;//isEidting=true
				showEdit(isEidting);
				
			}
		});
		
		initEdit();
	}
	
	/**
	 * ʹ�༭�����ʼ��״̬
	 */
	private void initEdit(){
		sms_rl_select.setVisibility(View.GONE);
		sms_ll_remove.setVisibility(View.GONE);
		dial_rl_select.setVisibility(View.GONE);
		dial_ll_lv.setVisibility(View.GONE);
		contact_rl_select.setVisibility(View.GONE);
		contact_ll_lv.setVisibility(View.GONE);
	}
	
	
	/**
	 * ����spac��ť�Ƿ���Ե��,viewpager�Ƿ���Ի���
	 * @param b
	 */
	private void controlSpacButton(boolean b){
		btn_sms.setClickable(b);
		btn_phone.setClickable(b);
		btn_contacts.setClickable(b);
		
	}
	
	/**
	 * ��ʾ�༭��,ÿ���л�����󶼽����ʼ��״̬
	 * @param isEidting
	 */
	private void showEdit(boolean isEidting) {
		
		
		if(currentView==0){

			if(isEidting){//��δ�༭-���༭
				sms_rl_select.setVisibility(View.VISIBLE);
				sms_ll_remove.setVisibility(View.VISIBLE);
				//�����û����й����ͻ�����Ļ����
				
			}else{
				sms_rl_select.setVisibility(View.GONE);
				sms_ll_remove.setVisibility(View.GONE);
				//���û����й����ͻ�����Ļ����
			}
		}else if(currentView ==1){
			
			if(!isEidting){//��δ�༭-���༭
				dial_rl_select.setVisibility(View.VISIBLE);
				dial_ll_lv.setVisibility(View.VISIBLE);
				//�����û����й����ͻ�����Ļ����
			}else{
				dial_rl_select.setVisibility(View.GONE);
				dial_ll_lv.setVisibility(View.GONE);
				//���û����й����ͻ�����Ļ����
			}
			
		}else if(currentView ==2){
			if(!isEidting){//��δ�༭-���༭
				contact_rl_select.setVisibility(View.VISIBLE);
				contact_ll_lv.setVisibility(View.VISIBLE);
				//�����û����й����ͻ�����Ļ����
			}else{
				contact_rl_select.setVisibility(View.GONE);
				contact_ll_lv.setVisibility(View.GONE);
				//���û����й����ͻ�����Ļ����
			}
		}
	}
	
	//sms,dial,contact��ʾ������
	private void initSpacButton(Button btn){
		btn_sms.setSelected(false);
		btn_phone.setSelected(false);
		btn_contacts.setSelected(false);
		
		btn_sms.setTextColor(getResources().getColor(R.color.sms_text_content_unseleted));
		btn_phone.setTextColor(getResources().getColor(R.color.sms_text_content_unseleted));
		btn_contacts.setTextColor(getResources().getColor(R.color.sms_text_content_unseleted));
		
		btn.setSelected(true);
		btn.setTextColor(getResources().getColor(R.color.sms_text_content_seleted));
		
		//��¼��ǰ��ʾҳ��
		if(btn.equals(btn_sms)){
			currentView = 0;
		}else if(btn.equals(btn_phone)){
			currentView = 1;
		}else if(btn.equals(btn_contacts)){
			currentView = 2;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.private_comm, menu);
		return true;
	}

}
