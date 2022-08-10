package net.oschina.gitapp.share;

import android.app.Activity;
import android.graphics.Bitmap;

/**
 * base share
 * Created by huanghaibin on 2017/6/12.
 */

public abstract class BaseShare {
    protected Activity mActivity;
    protected String title;
    protected String content;
    protected int resId;
    protected String url;
    protected String imageUrl;
    protected Bitmap bitmap;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
