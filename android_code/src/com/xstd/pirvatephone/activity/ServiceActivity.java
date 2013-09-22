package com.xstd.pirvatephone.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.privatephone.adapter.ServiceAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-9-22
 * Time: PM2:14
 * To change this template use File | Settings | File Templates.
 */
public class ServiceActivity extends BaseActivity implements View.OnClickListener {

    @ViewMapping(ID = R.id.service_list)
    public ListView mListView;

    @ViewMapping(ID = R.id.ll_return_btn)
    public TextView mReturn;

    @ViewMapping(ID = R.id.ll_title_text)
    public TextView mTitle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service);
        init();
    }

    private void init() {
        ViewMapUtil.viewMapping(this, this.getWindow());

        mReturn.setOnClickListener(this);
        mTitle.setText(R.string.service_title);

        mListView.setAdapter(new ServiceAdapter(this.getApplicationContext()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_return_btn:
                finish();
                break;
        }
    }
}