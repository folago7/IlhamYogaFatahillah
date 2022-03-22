package net.oschina.gitapp.api;

import android.text.TextUtils;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.common.CyptoUtils;

import org.kymjs.kjframe.Core;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;


/**
 * 获取一个httpClient
 * Created by 火蚁 on 15/4/13.
 */
public class AsyncHttpHelp {
    public final static String PRIVATE_TOKEN = "private_token";
    public final static String GITOSC_PRIVATE_TOKEN = "git@osc_token";

    public static void get(String url, HttpCallBack handler) {
        Core.get(url, handler);
    }

    public static void get(String url, HttpParams params, HttpCallBack handler) {
        Core.get(url, params, handler);
    }

    public static void post(String url, HttpParams params, HttpCallBack handler) {
        Core.post(url, params, false, handler);
    }

    /**
     * 获得UserAgent
     *
     * @return
     */
    private static String getUserAgent() {
        AppContext appContext = AppContext.getInstance();
        StringBuilder ua = new StringBuilder("Git@OSC.NET");
        ua.append('/' + appContext.getPackageInfo().versionName + '_' + appContext.getPackageInfo
                ().versionCode);//App版本
        ua.append("/Android");//手机系统平台
        ua.append("/" + android.os.Build.VERSION.RELEASE);//手机系统版本
        ua.append("/" + android.os.Build.MODEL); //手机型号
        ua.append("/" + AppContext.getInstance().getAppId());//客户端唯一标识
        return ua.toString();
    }

    public static HttpParams getPrivateTokenWithParams() {
        HttpParams params = new HttpParams();
        params.putHeaders("User-Agent", getUserAgent());
        String private_token = AppContext.getInstance().getProperty(PRIVATE_TOKEN);
        private_token = CyptoUtils.decode(GITOSC_PRIVATE_TOKEN, private_token);
        if (!TextUtils.isEmpty(private_token))
            params.put(PRIVATE_TOKEN, private_token);
        return params;
    }

    public static HttpParams getHttpParams() {
        return getPrivateTokenWithParams();
    }
}
