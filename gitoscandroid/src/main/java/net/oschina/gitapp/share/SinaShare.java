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

    private static final String APP_KEY = "3616966952";
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
        if (mBuilder.bitmap == null) {
            mBuilder.bitmap = BitmapFactory.decodeResource(mBuilder.mActivity.getResources(), R.drawable.icon_logo);
        }


        // 设置 Bitmap 类型的图片到视频对象里         最好设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        webpageObject.setThumbImage(mBuilder.bitmap);
        webpageObject.actionUrl = mBuilder.url;
        webpageObject.defaultText = mBuilder.content;


        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

        weiboMessage.mediaObject = webpageObject;
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        boolean isTrue = mAPI.sendRequest(mBuilder.mActivity, request);
    }
}
