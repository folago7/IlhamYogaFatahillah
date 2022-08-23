package net.oschina.gitapp.share;

import android.os.Bundle;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import net.oschina.gitapp.R;
import net.oschina.gitapp.utils.T;

/**
 * QZoneShare
 * Created by huanghaibin on 2017/6/12.
 */

public class TencentQQShare extends BaseShare implements IUiListener {

    private static final String APP_ID = "1101982202";
    private Tencent tencent;

    public TencentQQShare(Builder mBuilder) {
        super(mBuilder);
        tencent = Tencent.createInstance(APP_ID, mBuilder.mActivity.getApplicationContext());
    }

    @Override
    public boolean share() {
        tencent.shareToQQ(mBuilder.mActivity, initShare(), this);
        return true;
    }


    private Bundle initShare() {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, mBuilder.title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mBuilder.content);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mBuilder.url);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mBuilder.imageUrl);
        if (mBuilder.isShareApp) {
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mBuilder.title);
            params.putInt(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, R.drawable.icon_logo);
        }
        return params;
    }


    @Override
    public void onComplete(Object o) {
        T.showToastShort(mBuilder.mActivity, "成功分享");
    }

    @Override
    public void onError(UiError uiError) {
        T.showToastShort(mBuilder.mActivity, "分享失败");
    }

    @Override
    public void onCancel() {
        T.showToastShort(mBuilder.mActivity, "分享取消");
    }
}
