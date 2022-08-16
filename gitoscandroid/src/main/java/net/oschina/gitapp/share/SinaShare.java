package net.oschina.gitapp.share;

import android.app.Activity;

/**
 * sina
 * Created by huanghaibin on 2017/6/12.
 */

public class SinaShare extends BaseShare{

    public SinaShare(Builder mBuilder) {
        super(mBuilder);
    }

    @Override
    public boolean share() {
        return false;
    }
}
