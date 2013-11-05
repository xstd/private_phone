package com.xstd.pirvatephone.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;

import com.xstd.pirvatephone.module.MediaModule;

import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chrain
 * Date: 13-10-23
 * Time: 上午10:09
 * To change this template use File | Settings | File Templates.
 */
public class MediaUtils {

    private static final String TAG = "MediaUtils";

    /**
     * 给一个List 查询系统数据库，并把结果装到List中
     *
     * @param ctx  上下文
     * @param type 1代表音频，2代表视频
     */
    public static List<MediaModule> getMediaModule(Context ctx, int type) {
        List<MediaModule> datas = new ArrayList<MediaModule>();
        Cursor cursor = null;
        if (type == 1) {
            cursor = ctx.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
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
        } else if (type == 2) {
            cursor = ctx.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    MediaModule module = new MediaModule();
                    module.setDisplay_name(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
                    module.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
                    module.setDate_modified(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)));
                    module.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inDither = false;
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    module.setThumb(MediaStore.Video.Thumbnails.getThumbnail(ctx.getContentResolver(), cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)), MediaStore.Images.Thumbnails.MICRO_KIND, options));
                    datas.add(module);
                }
            }
        } else if (type == 0) {
            cursor = ctx.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,null,null,MediaStore.Images.Media.DEFAULT_SORT_ORDER);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    MediaModule module = new MediaModule();
                    module.setDisplay_name(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
                    module.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)));
                    module.setDate_modified(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
                    module.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inDither = false;
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    module.setThumb(MediaStore.Images.Thumbnails.getThumbnail(ctx.getContentResolver(), cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)), MediaStore.Images.Thumbnails.MICRO_KIND, options));
                    datas.add(module);
                }
            }

        }
        if (cursor != null) {
            cursor.close();
        }
        return datas;
    }

    /**
     * 以父目录为Map的key，value为一个List，List里装Key路径的媒体文件
     *
     * @param ctx  上下文对象
     * @param type privacy_type
     * @return key:父目录 value:这个目录下的文件
     */
    public static HashMap<String, ArrayList<MediaModule>> getMediaParentFolder(Context ctx, List<String> mapKeys, int type) {
        List<MediaModule> data = getMediaModule(ctx, type);
        HashMap<String, ArrayList<MediaModule>> typeData = new HashMap<String, ArrayList<MediaModule>>();
        for (MediaModule module : data) {
            String path = module.getPath();
            String folderPath = path.substring(0, path.lastIndexOf("/"));
            boolean contains = typeData.containsKey(folderPath);
            if (contains) {
                typeData.get(folderPath).add(module);
            } else {
                ArrayList<MediaModule> typeModules = new ArrayList<MediaModule>();
                typeModules.add(module);
                mapKeys.add(folderPath);
                typeData.put(folderPath, typeModules);
            }
        }
        return typeData;
    }

}
