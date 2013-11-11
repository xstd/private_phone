package com.xstd.pirvatephone.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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
import com.xstd.pirvatephone.dao.privacy.PrivacyPic;
import com.xstd.pirvatephone.dao.privacy.PrivacyPicDao;
import com.xstd.privatephone.adapter.AddPrivacyPicAdapter;
import com.xstd.privatephone.tools.Toasts;

public class AddPrivacyPictureActivity extends BaseActivity implements OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = "AddPrivacyPictureActivity";
	private static final int REQUEST_CAMERA_CODE = 1;
    @ViewMapping(ID = R.id.left_line)
    public ViewGroup left_line;

    @ViewMapping(ID = R.id.title_bar_title)
    public TextView title_bar_title;

    @ViewMapping(ID = R.id.right_line)
    public ViewGroup right_line;

    @ViewMapping(ID = R.id.inbox_divider)
    public TextView inbox_divider;

    @ViewMapping(ID = R.id.add_pic)
    public ViewGroup add_pic;

    @ViewMapping(ID = R.id.gridview)
    public GridView gridview;

    @ViewMapping(ID = R.id.float_edit_btn)
    public Button float_edit_btn;

    @ViewMapping(ID = R.id.float_done_btn)
    public Button float_done_btn;

    @ViewMapping(ID = R.id.text)
    public TextView text;

    private List<PrivacyPic> img_name = new ArrayList<PrivacyPic>();

    private AddPrivacyPicAdapter adapter;

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
        add_pic.setOnClickListener(this);
        float_edit_btn.setOnClickListener(this);
        float_done_btn.setOnClickListener(this);
        text.setText(R.string.s_hide_image_add);
        adapter = new AddPrivacyPicAdapter(getApplicationContext(), img_name);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(this);
        gridview.setOnItemLongClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryImageFolderCount();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.left_line:
                finish();
                break;
            case R.id.right_line:
                intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                File path = new File(ShowSDCardFilesActivity.PRIVACY_SAPCE_PATH);
                if (!path.exists()) {
                    path.mkdirs();
                }
                File file = new File(path, "IMG_" + System.currentTimeMillis() + ".jpg");
                Uri mOutPutFileUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
                startActivityForResult(intent, REQUEST_CAMERA_CODE);
                break;
            case R.id.add_pic:
                Log.w(TAG,"您按了添加按钮");
                intent = new Intent(this,AddFileActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(REQUEST_CAMERA_CODE == requestCode && RESULT_OK == resultCode) {
    		Log.w(TAG, data.toString());
    	}
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
                img_name.add(new PrivacyPic(Long.valueOf(-1), getString(R.string.rs_my_img)));
                while (cursor.moveToNext()) {
                    img_name.add(new PrivacyPic(cursor.getLong(cursor.getColumnIndex(PrivacyPicDao.Properties.Id.columnName)), cursor.getString(cursor.getColumnIndex(PrivacyPicDao.Properties.Name.columnName))));
                }
                img_name.add(new PrivacyPic(Long.valueOf(-1), "添加相册"));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                String srcStr = getResources().getString(R.string.strongbox_image_inbox_divider);
                String destStr = String.format(srcStr, img_name.size() - 1);
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
        Intent intent = new Intent(this,PrivacyShowActivity.class);
        intent.putExtra("privacy_type",0);
        intent.putExtra("ref_id",((PrivacyPic)adapter.getItem(position)).getId());
        startActivity(intent);
    }

    private void showAddDialog() {
        final EditText et = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.rs_create_album).setView(et).setPositiveButton(R.string.rs_create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = et.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toasts.getInstance(AddPrivacyPictureActivity.this).show(R.string.rs_error_empty_thumb, 0);
                    return;
                }
                PrivacyDaoUtils.geThumbDao(AddPrivacyPictureActivity.this).insert(new PrivacyPic(null, name));
                queryImageFolderCount();
                adapter.notifyDataSetChanged();
            }
        }).setNegativeButton(android.R.string.cancel, null).create();
        dialog.show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0 || position == img_name.size() - 1) {
            return true;
        }
        showDeleteDialog(adapter.getItem(position));
        return false;
    }

    private void showDeleteDialog(final Object item) {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.rs_delete_thumb).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PrivacyDaoUtils.geThumbDao(AddPrivacyPictureActivity.this).delete((PrivacyPic) item);
                queryImageFolderCount();
                adapter.notifyDataSetChanged();
            }
        }).setNegativeButton(android.R.string.cancel, null).create();
        dialog.show();
    }
}
