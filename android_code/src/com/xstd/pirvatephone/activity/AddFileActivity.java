package com.xstd.pirvatephone.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.module.MediaModule;
import com.xstd.pirvatephone.utils.MediaUtils;
import com.xstd.privatephone.adapter.AddFileAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Chrain
 * Date: 13-10-21
 * Time: 上午10:59
 * To change this template use File | Settings | File Templates.
 */
public class AddFileActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "AddFileActivity";
    @ViewMapping(ID = R.id.lv)
    public ListView lv;

    @ViewMapping(ID = R.id.ll_return_btn)
    public TextView ll_return_btn;

    @ViewMapping(ID = R.id.ll_title_text)
    public TextView ll_title_text;

    @ViewMapping(ID = R.id.ll_edit)
    public ViewGroup ll_edit;

    AddFileAdapter adapter;

    private int privacy_type;

    private boolean isEdit = false;

    private Map<String, ArrayList<MediaModule>> data = new HashMap<String, ArrayList<MediaModule>>();

    private List<String> mapKeys = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.activity_add_file);

        privacy_type = getIntent().getIntExtra("privacy_type", 0);

        initUI();
    }

    private void initUI() {
        ViewMapUtil.viewMapping(this, getWindow());

        ll_return_btn.setOnClickListener(this);
        adapter = new AddFileAdapter(getApplicationContext());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (privacy_type == 1) {
            ll_title_text.setText(R.string.title_select_add_audio);
        } else if (privacy_type == 2) {
            ll_title_text.setText(R.string.title_select_add_vedio);
        }
        new QueryMediaTask().execute(privacy_type);
    }

    @Override
    public void onClick(View v) {
        if (v == ll_return_btn) {
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        isEdit = true;
        adapter.showEdit(data.get(mapKeys.get(position)));
        ll_edit.setVisibility(View.VISIBLE);
        //TODO 显示内容
    }

    private class QueryMediaTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            data = MediaUtils.getMediaParentFolder(getApplicationContext(), mapKeys, params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.changeData(data);
        }
    }

    /**
     * 隐藏按钮
     *
     * @param view Button对象
     */
    public void move(View view) {

    }

    /**
     * 全选按钮
     *
     * @param view Button对象
     */
    public void selectAll(View view) {

    }
}
