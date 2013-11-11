package com.xstd.pirvatephone.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;

/**
 * Created by Chrain on 13-11-11.
 */
public class AddAdvanceSimulatePhoneActivity extends BaseActivity implements View.OnClickListener {

    @ViewMapping(ID = R.id.ll_return_btn)
    public TextView ll_return_btn;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advance_phone);
    }

    @Override
    public void onClick(View v) {

    }
}
