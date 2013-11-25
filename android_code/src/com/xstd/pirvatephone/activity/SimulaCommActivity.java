package com.xstd.pirvatephone.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;
import com.xstd.pirvatephone.dao.simulacomm.SimulateDaoUtils;
import com.xstd.pirvatephone.receiver.SimulateSendPhoneReceiver;
import com.xstd.pirvatephone.receiver.SimulateSendSMSReceiver;
import com.xstd.pirvatephone.utils.ContactsUtils;
import com.xstd.privatephone.adapter.AddCommonPhoneAdapter;
import com.xstd.privatephone.adapter.AddCommonSMSAdapter;
import com.xstd.privatephone.adapter.SimulateCommAdapter;

public class SimulaCommActivity extends BaseActivity implements
        OnItemLongClickListener {

    /**
     * 代表模拟短信的类型
     */
    public final static int SIMULATE_SMS = 1;

    /**
     * 代表模拟通话的类型
     */
    public final static int SIMULATE_PHONE = 2;
    private static final String TAG = "SimulaCommActivity";
    public static final int COMMON_SMS = 3;
    public static final int COMMON_PHONE = 4;

    @ViewMapping(ID = R.id.ll_return_btn)
    public ImageButton ll_return_btn;

    @ViewMapping(ID = R.id.ll_title_text)
    public TextView ll_title_text;

    @ViewMapping(ID = R.id.lv_phone)
    public ListView lv_phone;

    @ViewMapping(ID = R.id.lv_sms)
    public ListView lv_sms;

    @ViewMapping(ID = R.id.lv_com_sms)
    public ListView lv_common_sms;

    @ViewMapping(ID = R.id.lv_com_phone)
    public ListView lv_common_phone;

    @ViewMapping(ID = R.id.btn_add)
    public Button btn_add;

    @ViewMapping(ID = R.id.btn_add_advance)
    public Button btn_add_advance;

    @ViewMapping(ID = R.id.simulate_sms)
    public TextView simulate_sms;

    @ViewMapping(ID = R.id.simulate_phone)
    public TextView simulate_phone;

    @ViewMapping(ID = R.id.common_sms)
    public TextView common_sms;

    @ViewMapping(ID = R.id.common_phone)
    public TextView common_phone;

    private int type = SIMULATE_SMS;

    private SimulateCommAdapter adapter_phone;
    private SimulateCommAdapter adapter_sms;
    private AddCommonPhoneAdapter adapter_common_phone;
    private AddCommonSMSAdapter adapter_common_sms;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simula_comm);

        initUI();
    }

    private void initUI() {
        ViewMapUtil.viewMapping(this, getWindow());

        ll_return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ll_title_text.setText(R.string.s_menu_simulate);

        adapter_phone = new SimulateCommAdapter(this, SIMULATE_PHONE);
        lv_phone.setAdapter(adapter_phone);

        adapter_sms = new SimulateCommAdapter(this, SIMULATE_SMS);
        lv_sms.setAdapter(adapter_sms);

        adapter_common_phone = new AddCommonPhoneAdapter(getApplicationContext(), ContactsUtils.getCallLogByPeople(getApplicationContext()),true);
        lv_common_phone.setAdapter(adapter_common_phone);

        adapter_common_sms = new AddCommonSMSAdapter(getApplicationContext(), ContactsUtils.getSmsByPeople(getApplicationContext()),true);
        lv_common_sms.setAdapter(adapter_common_sms);

        lv_phone.setOnItemLongClickListener(this);
        lv_sms.setOnItemLongClickListener(this);
        lv_common_sms.setOnItemLongClickListener(this);
        lv_common_phone.setOnItemLongClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (type == SIMULATE_PHONE)
            adapter_phone.changeDatas();
        else if (type == SIMULATE_SMS)
            adapter_sms.changeDatas();
        displayWhich();
    }

    private void displayWhich() {
        btn_add_advance.setVisibility(View.GONE);
        if (type == SIMULATE_PHONE) {
            btn_add_advance.setVisibility(View.VISIBLE);
            lv_phone.setVisibility(View.VISIBLE);
            lv_sms.setVisibility(View.GONE);
            lv_common_phone.setVisibility(View.GONE);
            lv_common_sms.setVisibility(View.GONE);
            simulate_phone.setSelected(true);
            simulate_sms.setSelected(false);
            common_sms.setSelected(false);
            common_phone.setSelected(false);
        } else if (type == SIMULATE_SMS) {
            lv_phone.setVisibility(View.GONE);
            lv_sms.setVisibility(View.VISIBLE);
            lv_common_phone.setVisibility(View.GONE);
            lv_common_sms.setVisibility(View.GONE);
            simulate_phone.setSelected(false);
            simulate_sms.setSelected(true);
            common_sms.setSelected(false);
            common_phone.setSelected(false);
        } else if (type == COMMON_PHONE) {
            lv_phone.setVisibility(View.GONE);
            lv_sms.setVisibility(View.GONE);
            lv_common_phone.setVisibility(View.VISIBLE);
            lv_common_sms.setVisibility(View.GONE);
            simulate_phone.setSelected(false);
            simulate_sms.setSelected(false);
            common_sms.setSelected(false);
            common_phone.setSelected(true);
        } else if (type == COMMON_SMS) {
            lv_phone.setVisibility(View.GONE);
            lv_sms.setVisibility(View.GONE);
            lv_common_phone.setVisibility(View.GONE);
            lv_common_sms.setVisibility(View.VISIBLE);
            simulate_phone.setSelected(false);
            simulate_sms.setSelected(false);
            common_sms.setSelected(true);
            common_phone.setSelected(false);
        }
    }

    /**
     * 显示模拟短信记录按钮
     *
     * @param view
     */
    public void simulateSms(View view) {
        btn_add.setText(R.string.simula_add_sms);
        type = SIMULATE_SMS;
        adapter_sms.changeDatas();
        displayWhich();
    }

    /**
     * 显示模拟电话记录按钮
     *
     * @param view
     */
    public void simulatePhone(View view) {
        btn_add.setText(R.string.simula_add_phone);
        type = SIMULATE_PHONE;
        adapter_phone.changeDatas();
        displayWhich();
    }

    public void addSMS(View view) {
        btn_add.setText(R.string.add_common_sms);
        type = COMMON_SMS;
        adapter_phone.changeDatas();
        displayWhich();
    }

    public void addPhone(View view) {
        btn_add.setText(R.string.add_common_phone);
        type = COMMON_PHONE;
        adapter_phone.changeDatas();
        displayWhich();
    }

    /**
     * 添加一条模拟通讯
     *
     * @param view
     */
    public void addSimulate(View view) {
        Intent intent = new Intent();
        if (type == SIMULATE_SMS) {
            intent.setClass(this, AddSimulaSmsActivity.class);
        } else if (type == SIMULATE_PHONE) {
            intent.setClass(this, AddSimulaPhoneActivity.class);
        } else if (type == COMMON_SMS) {
            intent.setClass(this, AddCommonSmsActivity.class);
        } else if (type == COMMON_PHONE) {
            intent.setClass(this, AddCommonPhoneActivity.class);
        }
        startActivity(intent);
    }

    /**
     * 增加一条人工模拟电话
     * @param view
     */
    public void addAdvanceSimulate(View view) {
        Intent intent = new Intent(this,AddAdvanceSimulatePhoneActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        if (type == SIMULATE_PHONE || type == SIMULATE_SMS)
            showDialog((SimulateComm) parent.getAdapter().getItem(position));
        return false;
    }

    private void showDialog(final SimulateComm entity) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.s_del_title).setMessage(R.string.s_del_msg)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimulateDaoUtils
                                .getSimulateDao(getApplicationContext())
                                .delete(entity);
                        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                        Intent intent = new Intent();
                        if (type == SIMULATE_PHONE) {
                            adapter_phone.changeDatas();
                            intent.setClass(getApplicationContext(),
                                    SimulateSendPhoneReceiver.class);
                        } else if (type == SIMULATE_SMS) {
                            adapter_sms.changeDatas();
                            intent.setClass(getApplicationContext(),
                                    SimulateSendSMSReceiver.class);
                        }
                        intent.putExtra("simu", entity);
                        PendingIntent pendingIntent = PendingIntent
                                .getBroadcast(
                                        getApplicationContext(),
                                        AddSimulaPhoneActivity.REQUES_REVEIVER_CODE,
                                        intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);
                        am.cancel(pendingIntent);
                        Log.w(TAG, "删除记录:" + entity.toString());
                    }
                }).create();
        dialog.show();
    }

}