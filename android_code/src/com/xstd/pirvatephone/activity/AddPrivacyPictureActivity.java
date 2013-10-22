package com.xstd.pirvatephone.activity;

import java.io.File;
import java.util.concurrent.Executor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.privatephone.adapter.AddPrivacyPicAdapter;

public class AddPrivacyPictureActivity extends BaseActivity implements OnClickListener {

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
        gridview.setAdapter(new AddPrivacyPicAdapter(getApplicationContext()));
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        String srcStr = getResources().getString(R.string.strongbox_image_inbox_divider);
        String destStr = String.format(srcStr,3);
        inbox_divider.setText(destStr);
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
}
