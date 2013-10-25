package com.xstd.pirvatephone.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.privacy.PrivacyDaoUtils;
import com.xstd.pirvatephone.dao.privacy.PrivacyFile;
import com.xstd.pirvatephone.dao.privacy.PrivacyFileDao;
import com.xstd.pirvatephone.module.MediaModule;
import com.xstd.pirvatephone.utils.FileUtils;
import com.xstd.pirvatephone.utils.MediaUtils;
import com.xstd.privatephone.adapter.AddFileAdapter;

import java.util.ArrayList;
import java.util.Date;
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

    private long ref_id;//用来标识图片是哪个图库的。

    private boolean isEdit = false;

    private Map<String, ArrayList<MediaModule>> data = new HashMap<String, ArrayList<MediaModule>>();

    private List<String> mapKeys = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.activity_add_file);

        privacy_type = getIntent().getIntExtra("privacy_type", 0);

        ref_id = getIntent().getLongExtra("ref_id",-1);

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
        } else if (privacy_type == 0) {
            ll_title_text.setText(R.string.title_select_add_image);
        }
        new QueryMediaTask().execute(privacy_type);
    }

    @Override
    public void onClick(View v) {
        if (v == ll_return_btn) {
            if (isEdit) {
                isEdit = false;
                ll_edit.setVisibility(View.GONE);
                adapter.changeData(data, -1);
            } else finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isEdit) {
            MediaModule module = (MediaModule) adapter.getItem(position);
            module.setSelect(!module.isSelect());
            adapter.notifyDataSetChanged();
        } else {
            isEdit = true;
            ll_edit.setVisibility(View.VISIBLE);
            adapter.changeData(data, position);
        }
    }

    private class QueryMediaTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            data = MediaUtils.getMediaParentFolder(getApplicationContext(), mapKeys, params[0]);
            Log.w(TAG,data.size()+"");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.changeData(data, -1);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isEdit) {
            isEdit = false;
            ll_edit.setVisibility(View.GONE);
            adapter.changeData(data, -1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private List<MediaModule> willMoves = new ArrayList<MediaModule>();

    ProgressDialog dialog;

    /**
     * 隐藏按钮
     *
     * @param view Button对象
     */
    public void move(View view) {
        for (int i = 0; i < adapter.getCount(); i++) {
            MediaModule module = (MediaModule) adapter.getItem(i);
            if (module.isSelect()) {
                willMoves.add(module);
            }
        }
        new AsyncTask<Void, Void, Void>() {

            protected void onPreExecute() {
                dialog = new ProgressDialog(AddFileActivity.this);
                dialog.show();
            };

            @Override
            protected Void doInBackground(Void... params) {
                PrivacyFileDao dao = PrivacyDaoUtils.getFileDao(AddFileActivity.this);
                for (MediaModule module : willMoves) {
                    com.plugin.common.utils.files.FileInfo info = new com.plugin.common.utils.files.FileInfo();
                    info.fileName = module.getDisplay_name();
                    info.filePath = module.getPath();
                    FileUtils.moveFile(module.getPath(), ShowSDCardFilesActivity.PRIVACY_SAPCE_PATH + module.getDisplay_name());
                    dao.insert(new PrivacyFile(null, module.getDisplay_name(), module.getDisplay_name(), module.getPath(), new Date(), privacy_type,ref_id));
                    FileUtils.DeleteFile(info);
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                dialog.dismiss();
                finish();
            };

        }.execute();
    }

    /**
     * 全选按钮
     *
     * @param view Button对象
     */
    public void selectAll(View view) {
        for (int i = 0; i < adapter.getCount(); i++) {
            MediaModule module = (MediaModule) adapter.getItem(i);
            module.setSelect(true);
        }
        adapter.notifyDataSetChanged();
    }
}
