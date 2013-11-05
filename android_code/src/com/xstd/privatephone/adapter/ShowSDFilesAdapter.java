package com.xstd.privatephone.adapter;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.activity.ShowSDCardFilesActivity;

public class ShowSDFilesAdapter extends BaseAdapter {

	protected static final String TAG = "ShowSDFilesAdapter";
	private List<FileInfo> files;
	private DateFormat df;
	private Context mContext;

	public ShowSDFilesAdapter(Context context) {
		mContext = context;
		this.files = new ArrayList<FileInfo>();
		df = DateFormat.getDateTimeInstance();
		updateFiles(Environment.getExternalStorageDirectory().getAbsolutePath());
	}

	/**
	 * 更新文件夹
	 * 
	 * @param path
	 */
	public void updateFiles(String path) {
		File file = new File(path);
		if (file == null || file.isFile())
			return;
		File[] files = file.listFiles();
		if (files == null) {
			Toast.makeText(mContext, mContext.getResources().getString(R.string.privacy_permission_denied), Toast.LENGTH_SHORT).show();
		}
		this.files.clear();
		for (File f : files) {
			if (!f.isHidden()) {
				FileInfo fileInfo = new FileInfo();
				fileInfo.name = f.getName();
				fileInfo.absolutePath = f.getAbsolutePath();
				fileInfo.lasstModify = f.lastModified();
				fileInfo.isFolder = f.isDirectory();
				fileInfo.isChecked = false;
				this.files.add(fileInfo);
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return files.size();
	}

	@Override
	public Object getItem(int position) {
		return files.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.item_show_sdfile, null);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.fileName = (TextView) convertView.findViewById(R.id.filename);
			holder.lastModify = (TextView) convertView.findViewById(R.id.filemodify);
			holder.isSelect = (CheckBox) convertView.findViewById(R.id.cb);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final FileInfo fileInfo = files.get(position);
		holder.icon.setImageResource(fileInfo.isFolder ? R.drawable.ic_file_dir : R.drawable.ic_file);
		holder.fileName.setText(fileInfo.name);
		holder.lastModify.setText(df.format(new Date(fileInfo.lasstModify)));
		holder.isSelect.setChecked(fileInfo.isChecked);
		holder.isSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (fileInfo.isChecked) {
					fileInfo.isChecked = false;
				} else {
					fileInfo.isChecked = true;
				}
                ((ShowSDCardFilesActivity)mContext).checkIsSelectAll();
			}
		});
		return convertView;
	}

	class ViewHolder {
		ImageView icon;
		TextView fileName;
		TextView lastModify;
		CheckBox isSelect;
	}

	public class FileInfo {
		public String name;
		public String absolutePath;
		public long lasstModify;
		public boolean isChecked;
		public boolean isFolder;
	}

}
