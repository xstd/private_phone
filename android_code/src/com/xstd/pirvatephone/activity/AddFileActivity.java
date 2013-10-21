package com.xstd.pirvatephone.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;import com.xstd.privatephone.adapter.AddFileAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: Chrain
 * Date: 13-10-21
 * Time: 上午10:59
 * To change this template use File | Settings | File Templates.
 */
public class AddFileActivity extends BaseActivity implements View.OnClickListener {

    @ViewMapping(ID = R.id.lv)
    public ListView lv;

    @ViewMapping(ID = R.id.ll_return_btn)
    public TextView ll_return_btn;

    @ViewMapping(ID = R.id.ll_title_text)
    public TextView ll_title_text;

    private int privacy_type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.activity_add_file);

        privacy_type = getIntent().getIntExtra("privacy_type", 0);

        initUI();
    }

    private void initUI() {
        ViewMapUtil.viewMapping(this,getWindow());

        ll_return_btn.setOnClickListener(this);
        lv.setAdapter(new AddFileAdapter(getApplicationContext()));
    }

    @Override
    public void onClick(View v) {
        //To change body of implemented methods use File | Settings | File Templates.
        if (v == ll_return_btn) {
            finish();
        }
    }
}
