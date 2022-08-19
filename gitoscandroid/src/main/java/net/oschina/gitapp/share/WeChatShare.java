package net.oschina.gitapp.share;

import android.app.Activity;

/**
 * WeChatShare
 * Created by huanghaibin on 2017/6/12.
 */

public class WeChatShare extends BaseShare{
    private static final String APP_ID = "wx850b854f6aad6764";

    public WeChatShare(Builder mBuilder) {
        super(mBuilder);
    }

    @Override
    public boolean share() {
        return false;
    }
}
