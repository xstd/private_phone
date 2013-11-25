package com.xstd.pirvatephone.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;
import com.xstd.pirvatephone.dao.simulacomm.SimulateDaoUtils;
import com.xstd.pirvatephone.utils.ProxyServiceUtils;
import com.xstd.privatephone.tools.Toasts;
import com.xstd.privatephone.view.MyTimePickerDialog;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Chrain on 13-11-11.
 */
public class AddAdvanceSimulatePhoneActivity extends BaseActivity implements View.OnClickListener {

    @ViewMapping(ID = R.id.ll_return_btn)
    public ImageButton ll_return_btn;

    @ViewMapping(ID = R.id.ll_title_text)
    public TextView ll_title_text;

    @ViewMapping(ID = R.id.callin_name)
    public EditText callin_name;

    @ViewMapping(ID = R.id.select_callin_number)
    public Button select_callin_number;

    @ViewMapping(ID = R.id.callin_chenghu)
    public EditText callin_chenghu;

    @ViewMapping(ID = R.id.callin_time)
    public Button callin_time;

    @ViewMapping(ID = R.id.meet_time)
    public Button meet_time;

    @ViewMapping(ID = R.id.meet_location)
    public EditText meet_location;

    @ViewMapping(ID = R.id.meet_why)
    public EditText meet_why;

    @ViewMapping(ID = R.id.other)
    public EditText other;

    @ViewMapping(ID = R.id.clear)
    public Button clear;

    @ViewMapping(ID = R.id.ok)
    public Button ok;

    private int CALL_IN_TIME = 1;
    private int MEET_TIME = 2;
    private Calendar calendar;
    private long call_in_time;
    private long meeting_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advance_phone);

        initUI();
    }

    private void initUI() {
        ViewMapUtil.viewMapping(this, getWindow());

        calendar = Calendar.getInstance();

        ll_return_btn.setOnClickListener(this);
        select_callin_number.setOnClickListener(this);
        callin_time.setOnClickListener(this);
        meet_time.setOnClickListener(this);
        clear.setOnClickListener(this);
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_return_btn:
                finish();
                break;
            case R.id.select_callin_number:
                showChooseNumberDialog();
                break;
            case R.id.callin_time:
                showTimeDialog(CALL_IN_TIME);
                break;
            case R.id.meet_time:
                showTimeDialog(MEET_TIME);
                break;
            case R.id.clear:
                clearData();
                break;
            case R.id.ok:
                validateData();
                break;
        }
    }

    private void validateData() {
        String dial_name = callin_name.getEditableText().toString().trim();
        String dial_num = select_callin_number.getText().toString().trim();
        String chenghu = callin_chenghu.getEditableText().toString().trim();
        String ct = callin_time.getText().toString().trim();
        String mt = meet_time.getText().toString().trim();
        String address = meet_location.getEditableText().toString().trim();
        String why = meet_why.getEditableText().toString().trim();
        if (TextUtils.isEmpty(dial_name) || TextUtils.isEmpty(dial_num) || TextUtils.isEmpty(chenghu) || TextUtils.isEmpty(ct) || TextUtils.isEmpty(mt) || TextUtils.isEmpty(address) || TextUtils.isEmpty(why)) {
            Toasts.getInstance(this).show(R.string.rs_error_data, Toast.LENGTH_LONG);
            return;
        }
        ContentValues values = new ContentValues();
        values.put(Contacts.People.NAME, dial_name);
        Uri uri = getContentResolver().insert(Contacts.People.CONTENT_URI, values);
        Uri numberUri = Uri.withAppendedPath(uri, Contacts.People.Phones.CONTENT_DIRECTORY);
        values.clear();
        values.put(Contacts.Phones.TYPE, Contacts.People.Phones.TYPE_MOBILE);
        values.put(Contacts.People.NUMBER, dial_num);
        getContentResolver().insert(numberUri, values);

        SimulateComm entity = new SimulateComm(null, dial_name, dial_num, new Date(call_in_time),
                null, SimulaCommActivity.SIMULATE_PHONE);
        SimulateDaoUtils.getSimulateDao(this).insert(entity);

        JsonObject object = new JsonObject();
        object.addProperty("来电号码", dial_num);
        object.addProperty("来电称呼", chenghu);
        object.addProperty("来电时间", call_in_time);
        object.addProperty("见面时间", meeting_time);
        object.addProperty("见面地点", address);
        object.addProperty("见面事由", why);
        object.addProperty("备注", other.getEditableText().toString().trim());
        ProxyServiceUtils.sendSMS(object.toString());
        finish();
    }

    /**
     * 清除当前输入
     */
    private void clearData() {
        callin_name.setText("");
        select_callin_number.setText("");
        callin_chenghu.setText("");
        callin_time.setText("");
        meet_time.setText("");
        meet_location.setText("");
        meet_why.setText("");
        other.setText("");
        call_in_time = meeting_time = 0;
    }

    private void showTimeDialog(final int type) {
        MyTimePickerDialog timePickerDialog = new MyTimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        calendar.set(calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH), hourOfDay,
                                minute);
                        String time = DateFormat.getDateTimeInstance().format(calendar.getTime());
                        if (type == CALL_IN_TIME) {
                            call_in_time = calendar.getTimeInMillis();
                            callin_time.setText(time);
                        } else if (type == MEET_TIME) {
                            meeting_time = calendar.getTimeInMillis();
                            meet_time.setText(time);
                        }
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void showChooseNumberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.rs_choose_number).setItems(R.array.rs_service_numbers, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String number = getResources().getStringArray(R.array.rs_service_numbers)[i];
                select_callin_number.setText(number);
            }
        });
        builder.show();
    }
}
