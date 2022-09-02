package net.oschina.gitapp.share;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.oschina.gitapp.R;
import net.oschina.gitapp.utils.T;

/**
 * WeChatShare
 * Created by huanghaibin on 2017/6/12.
 */

public class WeChatShare extends BaseShare implements IWXAPIEventHandler {
    public static final String APP_ID = "wx850b854f6aad6764";
    private IWXAPI wxAPI;

    public WeChatShare(Builder mBuilder) {
        super(mBuilder);
        wxAPI = WXAPIFactory.createWXAPI(mBuilder.mActivity, APP_ID, false);
        wxAPI.handleIntent(mBuilder.mActivity.getIntent(), this);
    }

    @Override
    public boolean share() {
        wechatShare(0);
        return false;
    }

    @Override
    public void onReq(BaseReq baseReq) {
        wechatShare(0);
    }

    @Override
    public void onResp(BaseResp baseResp) {

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                T.showToastShort(mBuilder.mActivity, "分享成功");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                T.showToastShort(mBuilder.mActivity, "取消分享");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                T.showToastShort(mBuilder.mActivity, "分享失败");
                break;
        }
    }

    void wechatShare(int flag) {

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mBuilder.url;
        final WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.setThumbImage(BitmapFactory.decodeResource(mBuilder.mActivity.getResources(), R.drawable.ic_share_logo));
        msg.title = mBuilder.title;
        msg.description = mBuilder.content;

        final SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        wxAPI.sendReq(req);
    }
}
