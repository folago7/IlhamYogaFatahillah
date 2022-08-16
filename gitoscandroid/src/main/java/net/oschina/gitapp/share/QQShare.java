package net.oschina.gitapp.share;

import com.tencent.tauth.Tencent;

/**
 * QZoneShare
 * Created by huanghaibin on 2017/6/12.
 */

public class QQShare extends BaseShare{

    private static final String APP_ID = "1101982202";
    private static final String APP_KEY = "ozSmyAyDw59prAw9";
    private Tencent tencent;

    public QQShare(Builder mBuilder) {
        super(mBuilder);
    }

    @Override
    public boolean share() {
        return false;
    }
}
