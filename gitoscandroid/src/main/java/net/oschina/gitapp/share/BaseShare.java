package net.oschina.gitapp.share;

import android.app.Activity;
import android.graphics.Bitmap;

/**
 * base share
 * Created by huanghaibin on 2017/6/12.
 */
@SuppressWarnings("unused")
public abstract class BaseShare {
    Builder mBuilder;

    BaseShare(Builder mBuilder) {
        this.mBuilder = mBuilder;
    }

    public abstract boolean share();

    public static final class Builder{
        private Activity mActivity;
        private String title;
        private String content;
        private int resId;
        private String url;
        private String imageUrl;
        private Bitmap bitmap;
        private boolean isShareApp;

        private int itemIcon;//显示的分享项图标
        private String itemTitle;//显示的分享项名称

        public Builder(Activity mActivity) {
            this.mActivity = mActivity;
        }

        public Builder resId(int resId){
            this.resId = resId;
            return this;
        }

        public Builder itemIcon(int itemIcon){
            this.itemIcon = itemIcon;
            return this;
        }

        public Builder itemTitle(String itemTitle){
            this.itemTitle = itemTitle;
            return this;
        }

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder url(String url){
            this.url = url;
            return this;
        }

        public Builder imageUrl(String imageUrl){
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public Builder bitmap(Bitmap bitmap){
            this.bitmap = bitmap;
            return this;
        }

        public Builder isShareApp(boolean isShareApp){
            this.isShareApp = isShareApp;
            return this;
        }
    }
}
