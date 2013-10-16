package com.xstd.pirvatephone.activity;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;

public class CopyContactsListMultiple extends ListActivity implements OnClickListener {

	private static final String TAG = "CopyContactsListMultiple";

	@ViewMapping(ID = R.id.ll_return_btn)
	public TextView return_bt;

	@ViewMapping(ID = R.id.ll_title_text)
	public TextView title_text;

	private final int UPDATE_LIST = 1;
	ArrayList<String> contactsList; // 得到的所有联系人
	ArrayList<String> getcontactsList; // 选择得到联系人

	@ViewMapping(ID = R.id.contacts_done_button)
	public Button okbtn;
	private ProgressDialog proDialog;

	Thread getcontacts;
	Handler updateListHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case UPDATE_LIST:
				if (proDialog != null) {
					proDialog.dismiss();
				}
				updateList();
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contactslist);

		ViewMapUtil.viewMapping(this, getWindow());

		contactsList = new ArrayList<String>();
		getcontactsList = new ArrayList<String>();

		return_bt.setOnClickListener(this);
		title_text.setText("选择联系人");
		ListView listView = getListView();
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		okbtn.setOnClickListener(this);

		getcontacts = new Thread(new GetContacts());
		getcontacts.start();
		proDialog = ProgressDialog.show(this, "loading", "loading", true, true);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	void updateList() {
		if (contactsList != null)
			setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, contactsList));

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (((CheckedTextView) v).isChecked()) {

			CharSequence num = ((CheckedTextView) v).getText();
			getcontactsList.add(num.toString());
		}
		if (!((CheckedTextView) v).isChecked()) {
			CharSequence num = ((CheckedTextView) v).getText();
			if ((num.toString()).indexOf("[") > 0) {
				String phoneNum = num.toString().substring(0, (num.toString()).indexOf("\n"));
				getcontactsList.remove(phoneNum);
			} else {
				getcontactsList.remove(num.toString());
			}
		}
		super.onListItemClick(l, v, position, id);
	}

	class GetContacts implements Runnable {
		@Override
		public void run() {
			String sortOrder = ContactInfoDao.Properties.Display_name.columnName + " COLLATE LOCALIZED ASC";

			ContactInfoDao contactInfoDao = ContactInfoDaoUtils.getContactInfoDao(getApplicationContext());
			SQLiteDatabase db = contactInfoDao.getDatabase();
			Cursor cursor = db.query(ContactInfoDao.TABLENAME, null, null, null, null, null, sortOrder);

			while (cursor.moveToNext()) {
				contactsList.add(cursor.getString(cursor.getColumnIndex(ContactInfoDao.Properties.Phone_number.columnName)) + "\n" + cursor.getString(cursor.getColumnIndex(ContactInfoDao.Properties.Display_name.columnName)));
			}
			cursor.close();
			Message msg1 = new Message();
			msg1.what = UPDATE_LIST;
			updateListHandler.sendMessage(msg1);

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		contactsList.clear();
		getcontactsList.clear();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.contacts_done_button:
			returnContacts();
			break;
		case R.id.ll_return_btn:
			setResult(RESULT_CANCELED);
			break;
		}
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_CANCELED);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void returnContacts() {
		Intent i = new Intent();
		if (getcontactsList != null && getcontactsList.size() > 0) {
			i.putStringArrayListExtra("GET_CONTACT", getcontactsList);
		}
		setResult(RESULT_OK, i);
	}
}
