package com.xstd.pirvatephone.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;

/**
 * Created with IntelliJ IDEA. User: michael Date: 13-9-9 Time: PM3:11 To change
 * this template use File | Settings | File Templates.
 */
public class DialActivity extends BaseActivity implements View.OnClickListener {

    @ViewMapping(ID=R.id.number)
    public TextView mNumber;

    @ViewMapping(ID=R.id.b1)
    public ImageView mB1;

    @ViewMapping(ID=R.id.b2)
    public ImageView mB2;

    @ViewMapping(ID=R.id.b3)
    public ImageView mB3;

    @ViewMapping(ID=R.id.b4)
    public ImageView mB4;

    @ViewMapping(ID=R.id.b5)
    public ImageView mB5;

    @ViewMapping(ID=R.id.b6)
    public ImageView mB6;

    @ViewMapping(ID=R.id.b7)
    public ImageView mB7;

    @ViewMapping(ID=R.id.b8)
    public ImageView mB8;

    @ViewMapping(ID=R.id.b9)
    public ImageView mB9;

    @ViewMapping(ID=R.id.b0)
    public ImageView mB0;

    @ViewMapping(ID=R.id.del)
    public ImageView mDel;

    private String mNumberStr = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.emergency_dialer);

        initUI();
    }

    private void initUI() {
        ViewMapUtil.viewMapping(this, this.getWindow());

        mNumber.setOnClickListener(this);
        mB1.setOnClickListener(this);
        mB2.setOnClickListener(this);
        mB3.setOnClickListener(this);
        mB4.setOnClickListener(this);
        mB5.setOnClickListener(this);
        mB6.setOnClickListener(this);
        mB7.setOnClickListener(this);
        mB8.setOnClickListener(this);
        mB9.setOnClickListener(this);
        mB0.setOnClickListener(this);
        mDel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int curPressData = -1;
        switch (view.getId()) {
            case R.id.b0:
                curPressData = 0;
                break;
            case R.id.b1:
                curPressData = 1;
                break;
            case R.id.b2:
                curPressData = 2;
                break;
            case R.id.b3:
                curPressData = 3;
                break;
            case R.id.b4:
                curPressData = 4;
                break;
            case R.id.b5:
                curPressData = 5;
                break;
            case R.id.b6:
                curPressData = 6;
                break;
            case R.id.b7:
                curPressData = 7;
                break;
            case R.id.b8:
                curPressData = 8;
                break;
            case R.id.b9:
                curPressData = 9;
                break;
            case R.id.del:
                curPressData = -2;
                break;
        }

        if (curPressData >= 0) {
            mNumberStr = mNumberStr + curPressData;
        } else if (curPressData == -1) {
            //TODO:
        } else if (curPressData == -2) {
            //del action
            if (mNumberStr.length() > 0) {
                mNumberStr = mNumberStr.substring(0, mNumberStr.length() - 1);
            }
        }

        mNumber.setText(mNumberStr);

        //Do action
        if (mNumberStr.equals("123456")) {
            Intent intent = new Intent(DialActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}