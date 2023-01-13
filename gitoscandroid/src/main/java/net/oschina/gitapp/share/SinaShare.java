package net.oschina.gitapp.share;

import android.graphics.BitmapFactory;

import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;

import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;

import net.oschina.gitapp.R;

/**
 * sina
 * Created by huanghaibin on 2017/6/12.
 */

public class SinaShare extends BaseShare{

    public static final String APP_KEY = "3645105737";
    public static final String APP_SECRET = "3645105737";
    private WbShareHandler shareHandler;

    public SinaShare(Builder mBuilder) {
        super(mBuilder);
        shareHandler = new WbShareHandler(mBuilder.mActivity);
        shareHandler.registerApp();
        shareHandler.setProgressColor(0xff33b5e5);
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
        mBuilder.bitmap = BitmapFactory.decodeResource(mBuilder.mActivity.getResources(), R.drawable.ic_launcher);
        webpageObject.setThumbImage(mBuilder.bitmap);
        webpageObject.actionUrl = mBuilder.url;
        webpageObject.defaultText = mBuilder.content;

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

        weiboMessage.mediaObject = webpageObject;
        shareHandler.shareMessage(weiboMessage, false);
    }
}
