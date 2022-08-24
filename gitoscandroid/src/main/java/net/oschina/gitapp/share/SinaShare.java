package net.oschina.gitapp.share;

import android.graphics.BitmapFactory;

import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;

import net.oschina.gitapp.R;

/**
 * sina
 * Created by huanghaibin on 2017/6/12.
 */

public class SinaShare extends BaseShare{

    private static final String APP_KEY = "3645105737";
    private IWeiboShareAPI mAPI;

    public SinaShare(Builder mBuilder) {
        super(mBuilder);
        mAPI = WeiboShareSDK.createWeiboAPI(mBuilder.mActivity, APP_KEY, false);
        mAPI.registerApp();
    }

    @Override
    public boolean share() {
        toShare();
        return true;
    }

    private void toShare(){

        WebpageObject webpageObject = new WebpageObject();
        webpageObject.identify = Utility.generateGUID();
        webpageObject.title = mBuilder.title;
        webpageObject.description = mBuilder.content;
        mBuilder.bitmap = BitmapFactory.decodeResource(mBuilder.mActivity.getResources(), R.drawable.icon_logo);
        webpageObject.setThumbImage(mBuilder.bitmap);
        webpageObject.actionUrl = mBuilder.url;
        webpageObject.defaultText = mBuilder.content;

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

        weiboMessage.mediaObject = webpageObject;

        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        mAPI.sendRequest(mBuilder.mActivity, request);
    }
}
