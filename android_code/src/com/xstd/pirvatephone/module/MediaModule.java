package com.xstd.pirvatephone.module;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Chrain
 * Date: 13-10-22
 * Time: 上午11:04
 * To change this template use File | Settings | File Templates.
 */
public class MediaModule implements Serializable {

    private String display_name;
    private Long size;
    private Long date_modified;
    private String path;
    private Bitmap thumb;

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(Long date_modified) {
        this.date_modified = date_modified;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
