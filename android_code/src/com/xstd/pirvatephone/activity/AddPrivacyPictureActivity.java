package com.xstd.pirvatephone.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.privacy.PrivacyDaoUtils;
import com.xstd.pirvatephone.dao.privacy.PrivacyPicDao;
import com.xstd.privatephone.adapter.AddPrivacyPicAdapter;

public class AddPrivacyPictureActivity extends BaseActivity implements OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "AddPrivacyPictureActivity";
    @ViewMapping(ID = R.id.left_line)
    public ViewGroup left_line;

    @ViewMapping(ID = R.id.title_bar_title)
    public TextView title_bar_title;

    @ViewMapping(ID = R.id.right_line)
    public ViewGroup right_line;

    @ViewMapping(ID = R.id.inbox_divider)
    public TextView inbox_divider;

    @ViewMapping(ID = R.id.add_strongbox)
    public ViewGroup add_strongbox;

    @ViewMapping(ID = R.id.gridview)
    public GridView gridview;

    @ViewMapping(ID = R.id.float_edit_btn)
    public Button float_edit_btn;

    @ViewMapping(ID = R.id.float_done_btn)
    public Button float_done_btn;

    @ViewMapping(ID = R.id.text)
    public TextView text;

    private List<String> img_name = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_privacy_picture);

        initUI();
    }

    private void initUI() {
        ViewMapUtil.viewMapping(this, getWindow());

        left_line.setOnClickListener(this);
        right_line.setVisibility(View.VISIBLE);
        right_line.setOnClickListener(this);
        add_strongbox.setOnClickListener(this);
        float_edit_btn.setOnClickListener(this);
        float_done_btn.setOnClickListener(this);
        text.setText(R.string.privacy_picture);
        AddPrivacyPicAdapter adapter = new AddPrivacyPicAdapter(getApplicationContext(), img_name);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryImageFolderCount();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_line:
                finish();
                break;
            case R.id.right_line:
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                File path = new File(ShowSDCardFilesActivity.PRIVACY_SAPCE_PATH);
                if (!path.exists()) {
                    path.mkdirs();
                }
                File file = new File(path, "IMG_" + System.currentTimeMillis() + ".jpg");
                Uri mOutPutFileUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
                startActivityForResult(intent, 1);
                break;
            case R.id.add_strongbox:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);    //To change body of overridden methods use File | Settings | File Templates.
    }

    private void queryImageFolderCount() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                img_name.clear();
            }

            @Override
            protected Void doInBackground(Void... params) {
                PrivacyPicDao dao = PrivacyDaoUtils.geThumbDao(AddPrivacyPictureActivity.this);
                Cursor cursor = dao.getDatabase().query(dao.getTablename(), dao.getAllColumns(), null, null, null, null, null);
                img_name.add(getString(R.string.rs_my_img));
                while (cursor.moveToNext()) {
                    img_name.add(cursor.getString(cursor.getColumnIndex(PrivacyPicDao.Properties.Name.columnName)));
                }
                img_name.add("添加相册");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                String srcStr = getResources().getString(R.string.strongbox_image_inbox_divider);
                String destStr = String.format(srcStr, img_name.size());
                inbox_divider.setText(destStr);
            }
        }.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == img_name.size() - 1) {
            showAddDialog();
            return;
        }
        Intent intent = new Intent();
    }

    private void showAddDialog() {
        EditText et = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.rs_create_album).setView(et).setPositiveButton(R.string.rs_create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                PrivacyDaoUtils.geThumbDao(AddPrivacyPictureActivity.this).insert();
            }
        }).setNegativeButton(android.R.string.cancel, null).create();
        dialog.show();
    }
}
