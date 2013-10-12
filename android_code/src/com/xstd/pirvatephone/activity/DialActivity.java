package com.xstd.pirvatephone.activity;

import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.plugin.common.utils.StringUtils;
import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.setting.SettingManager;
import com.xstd.privatephone.tools.Toasts;

/**
 * Created with IntelliJ IDEA. User: michael Date: 13-9-9 Time: PM3:11 To change
 * this template use File | Settings | File Templates.
 */
public class DialActivity extends BaseActivity implements View.OnClickListener {

    @ViewMapping(ID = R.id.number)
    public TextView mNumber;

    @ViewMapping(ID = R.id.b1)
    public ImageView mB1;

    @ViewMapping(ID = R.id.b2)
    public ImageView mB2;

    @ViewMapping(ID = R.id.b3)
    public ImageView mB3;

    @ViewMapping(ID = R.id.b4)
    public ImageView mB4;

    @ViewMapping(ID = R.id.b5)
    public ImageView mB5;

    @ViewMapping(ID = R.id.b6)
    public ImageView mB6;

    @ViewMapping(ID = R.id.b7)
    public ImageView mB7;

    @ViewMapping(ID = R.id.b8)
    public ImageView mB8;

    @ViewMapping(ID = R.id.b9)
    public ImageView mB9;

    @ViewMapping(ID = R.id.b0)
    public ImageView mB0;

    @ViewMapping(ID = R.id.star)
    public ImageView mBStar;

    @ViewMapping(ID = R.id.bj)
    public ImageView mBj;

    @ViewMapping(ID = R.id.del)
    public ImageView mDel;

    @ViewMapping(ID = R.id.dial)
    public ImageView mDial;

    @ViewMapping(ID = R.id.tabCall)
    public Button mCallBtn;

    @ViewMapping(ID = R.id.tabContact)
    public Button mContactBtn;

    private String mNumberStr = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.emergency_dialer);

        initUI();
        checkEnterPassword();
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
        mDial.setOnClickListener(this);
        mCallBtn.setOnClickListener(this);
        mContactBtn.setOnClickListener(this);
        mBStar.setOnClickListener(this);
        mBj.setOnClickListener(this);
    }

    private void checkEnterPassword() {
        if (TextUtils.isEmpty(SettingManager.getInstance().getKeyEnterPassword())) {
            View v = getLayoutInflater().inflate(R.layout.enter_password_dialog, null);
            final EditText passwd = (EditText) v.findViewById(R.id.password);
            final EditText passwdCheck = (EditText) v.findViewById(R.id.password_confirm);
            AlertDialog dialog = new AlertDialog.Builder(this)
                                        .setTitle(R.string.enter_no_password_tips)
                                        .setView(v)
                                        .setPositiveButton(R.string.enter_btn_ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String passwdStr = passwd.getEditableText().toString();
                                                String passwdCheckStr = passwdCheck.getEditableText().toString();
                                                if (TextUtils.isEmpty(passwdStr) || TextUtils.isEmpty(passwdCheckStr)) {
                                                    Toasts.getInstance(DialActivity.this).show(R.string.enter_tips_password_empty,
                                                                                                  Toast.LENGTH_LONG);
                                                    checkEnterPassword();
                                                    return;
                                                }
                                                if (!passwdStr.equals(passwdCheckStr)) {
                                                    Toasts.getInstance(DialActivity.this).show(R.string.enter_tips_password_not_same,
                                                                                                  Toast.LENGTH_LONG);
                                                    checkEnterPassword();
                                                    return;
                                                }

                                                if (!isNumeric(passwdStr)) {
                                                    Toasts.getInstance(DialActivity.this).show(R.string.enter_tips_password_number_only,
                                                                                                  Toast.LENGTH_LONG);
                                                    checkEnterPassword();
                                                    return;
                                                }

                                                if (passwdStr.length() < 6) {
                                                    Toasts.getInstance(DialActivity.this).show(R.string.enter_tips_password_too_short,
                                                                                                  Toast.LENGTH_LONG);
                                                    checkEnterPassword();
                                                    return;
                                                }

                                                String md5 = StringUtils.MD5Encode(passwdCheckStr);
                                                if (!TextUtils.isEmpty(md5)) {
                                                    SettingManager.getInstance().setKeyEnterPassword(md5);
                                                }
                                            }
                                        })
                                        .setNegativeButton(R.string.enter_btn_cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                            }
                                        })
                                        .create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    @Override
    public void onClick(View view) {
        String curPressData = "-1";
        switch (view.getId()) {
            case R.id.b0:
                curPressData = "0";
                break;
            case R.id.b1:
                curPressData = "1";
                break;
            case R.id.b2:
                curPressData = "2";
                break;
            case R.id.b3:
                curPressData = "3";
                break;
            case R.id.b4:
                curPressData = "4";
                break;
            case R.id.b5:
                curPressData = "5";
                break;
            case R.id.b6:
                curPressData = "6";
                break;
            case R.id.b7:
                curPressData = "7";
                break;
            case R.id.b8:
                curPressData = "8";
                break;
            case R.id.b9:
                curPressData = "9";
                break;
            case R.id.star:
                curPressData = "*";
                break;
            case R.id.bj:
                curPressData = "#";
                break;
            case R.id.del:
                curPressData = "-2";
                break;
            case R.id.dial:
                if (!TextUtils.isEmpty(mNumberStr)) {
                    Intent myIntentDial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mNumberStr));
                    startActivity(myIntentDial);
                    return;
                }
                break;
            case R.id.tabCall:
                Intent localIntent3 = new Intent("android.intent.action.CALL_BUTTON");
                localIntent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(localIntent3);
                finish();
                return;
            case R.id.tabContact:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(ContactsContract.Contacts.CONTENT_URI);
                startActivity(intent);
                return;
        }

        if (!curPressData.equals("-1") && !curPressData.equals("-2")) {
            mNumberStr = mNumberStr + curPressData;
        } else if (curPressData.equals("-1")) {
            //TODO:
        } else if (curPressData.equals("-2")) {
            //del action
            if (mNumberStr.length() > 0) {
                mNumberStr = mNumberStr.substring(0, mNumberStr.length() - 1);
            }
        }

        mNumber.setText(mNumberStr);

        //Do action
        if (!TextUtils.isEmpty(mNumberStr)) {
            String check = StringUtils.MD5Encode(mNumberStr);
            if (check.equals(SettingManager.getInstance().getKeyEnterPassword())) {
                Intent intent = new Intent(DialActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}