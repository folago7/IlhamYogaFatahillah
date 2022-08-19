package net.oschina.gitapp.share;

import android.app.Activity;

/**
 * QZoneShare
 * Created by huanghaibin on 2017/6/12.
 */

public class QZoneShare extends BaseShare{
    public QZoneShare(Builder mBuilder) {
        super(mBuilder);
    }

    @Override
    public boolean share() {
        return false;
    }
}
