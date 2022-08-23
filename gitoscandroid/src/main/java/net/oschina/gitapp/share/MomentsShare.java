package net.oschina.gitapp.share;

/**
 * WeChatShare
 * Created by huanghaibin on 2017/6/12.
 */

public class MomentsShare extends WeChatShare {
    private static final String APP_ID = "wx850b854f6aad6764";

    public MomentsShare(Builder mBuilder) {
        super(mBuilder);
    }

    @Override
    public boolean share() {
        wechatShare(1);
        return true;
    }
}
