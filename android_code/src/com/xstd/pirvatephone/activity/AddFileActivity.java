package com.xstd.pirvatephone.activity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.module.MediaModule;
import com.xstd.privatephone.adapter.AddFileAdapter;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Chrain
 * Date: 13-10-21
 * Time: 上午10:59
 * To change this template use File | Settings | File Templates.
 */
public class AddFileActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "AddFileActivity";
    @ViewMapping(ID = R.id.lv)
    public ListView lv;

    @ViewMapping(ID = R.id.ll_return_btn)
    public TextView ll_return_btn;

    @ViewMapping(ID = R.id.ll_title_text)
    public TextView ll_title_text;

    AddFileAdapter adapter;

    private int privacy_type;

    private ArrayList<MediaModule> datas = new ArrayList<MediaModule>();

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
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
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

    private class QueryMediaTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            datas.clear();
            Cursor cursor = null;
            if (params[0] == 1) {
                Log.w(TAG, "audio");
                cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        MediaModule module = new MediaModule();
                        module.setDisplay_name(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                        module.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
                        module.setDate_modified(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)));
                        module.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                        datas.add(module);
                    }
                }
            } else if (params[0] == 2) {
                Log.w(TAG, "vedio");
                cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
                if (cursor != null && cursor.getCount() > 0) {
                    Log.w(TAG, "vedio有数据");
                    while (cursor.moveToNext()) {
                        MediaModule module = new MediaModule();
                        module.setDisplay_name(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
                        module.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
                        module.setDate_modified(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)));
                        module.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inDither = false;
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        module.setThumb(MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)), MediaStore.Images.Thumbnails.MICRO_KIND, options));
                        if(module.getThumb()!=null)
                            Log.w(TAG,module.getThumb().toString());
                        datas.add(module);
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);    //To change body of overridden methods use File | Settings | File Templates.
            adapter.changeData(datas);
        }
    }
}
