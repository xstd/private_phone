package com.xstd.pirvatephone.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.privatephone.view.MyTimePickerDialog;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-9-26
 * Time: PM2:29
 * To change this template use File | Settings | File Templates.
 */
public class ProxyTalkActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @ViewMapping(ID = R.id.ok)
    public Button mOK;

    @ViewMapping(ID = R.id.sms_check)
    public CheckBox mSMSCheck;

    @ViewMapping(ID = R.id.weixin_check)
    public CheckBox mWeixinCheck;

    @ViewMapping(ID = R.id.other_check)
    public CheckBox mOtherCheck;

    @ViewMapping(ID = R.id.self_check)
    public CheckBox mSelfCheck;

    @ViewMapping(ID = R.id.begin)
    public Button mBegin;

    @ViewMapping(ID = R.id.end)
    public Button mEnd;

    @ViewMapping(ID = R.id.default_text)
    public EditText mDefaultEditText;

    private boolean mIsSmsChecked;
    private boolean mIsWeixinChecked;
    private boolean mIsSelfChecked;
    private boolean mIsOtherChecked;

    private String mDefaultString;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_proxy_talk);
        init();
    }

    private void init() {
        ViewMapUtil.viewMapping(this, this.getWindow());

        mOK.setOnClickListener(this);
        mBegin.setOnClickListener(this);
        mEnd.setOnClickListener(this);

        mSMSCheck.setOnCheckedChangeListener(this);
        mWeixinCheck.setOnCheckedChangeListener(this);
        mOtherCheck.setOnCheckedChangeListener(this);
        mSelfCheck.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ok:
                mDefaultString = mDefaultEditText.getEditableText().toString();
                finish();
                break;
            case R.id.begin:
                showTimeDialog(mBegin);
                break;
            case R.id.end:
                showTimeDialog(mEnd);
                break;
        }
    }

    private void showTimeDialog(final Button bt) {
        long time = System.currentTimeMillis() + 60 * 1000;
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        MyTimePickerDialog timePickerDialog = new MyTimePickerDialog(this,
                                                                        new TimePickerDialog.OnTimeSetListener() {

                                                                            @Override
                                                                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                                                                  int minute) {
                                                                                calendar.set(calendar.get(Calendar.YEAR),
                                                                                                calendar.get(Calendar.MONTH),
                                                                                                calendar.get(Calendar.DAY_OF_MONTH), hourOfDay,
                                                                                                minute);
                                                                                bt.setText(getString(R.string.proxy_time_begin)
                                                                                         + DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                                                                            }
                                                                        }, calendar.get(Calendar.HOUR_OF_DAY),
                                                                        calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.sms_check:
                mIsSmsChecked = b;
                break;
            case R.id.weixin_check:
                mIsWeixinChecked = b;
                break;
            case R.id.self_check:
                mIsSelfChecked = b;
                if (mIsSelfChecked) {
                    mOtherCheck.setChecked(false);
                }
                break;
            case R.id.other_check:
                mIsOtherChecked = b;
                if (mIsOtherChecked) {
                    mSelfCheck.setChecked(false);
                }
                break;
        }
    }
}