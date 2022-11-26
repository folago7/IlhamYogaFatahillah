package net.oschina.gitapp.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 浏览器,视频、图片都支持
 * Created by huanghaibin on 2019/07/26.
 */
public class MWebView extends WebView {

    private OnFinishListener mOnFinishFinish;
    private OnLoadedHtmlListener mHTMLListener;
    private OnImageClickListener mImageClickListener;
    private OnVideoClickListener mVideoClickListener;
    private boolean isFinish;

    public MWebView(Context context) {
        this(context, null);
    }

    public MWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (mOnFinishFinish != null) {
                    mOnFinishFinish.onReceivedTitle(title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (mOnFinishFinish != null) {
                    mOnFinishFinish.onProgressChange(newProgress);
                }
            }

        });

        setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isFinish = true;
                    }
                }, 2000);
                if (mOnFinishFinish != null) {
                    mOnFinishFinish.onFinish();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (mOnFinishFinish != null) {
                    mOnFinishFinish.onError();
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }
        });

        getSettings().setDomStorageEnabled(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(new JavascriptInterface(), "mark");

    }

    @SuppressWarnings("all")
    public void setUserAgent(String ua) {
        getSettings().setUserAgentString(ua);
    }


    @SuppressWarnings("deprecation")
    public void onDestroy() {
        isFinish = true;
        ViewParent parent = getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(this);
        }
        stopLoading();
        getSettings().setJavaScriptEnabled(false);
        clearHistory();
        clearView();
        removeAllViews();
        mOnFinishFinish = null;
        mImageClickListener = null;
        mVideoClickListener = null;
        destroy();
    }

    public void getHtml(OnLoadedHtmlListener listener) {
        this.mHTMLListener = listener;
        loadUrl("javascript:window.mark.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
    }

    private void addJavaScript() {
        loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.mark.openImage(this.src);  " +
                "    }  " +
                "}" +
                "})()");

        loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"video\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.mark.openVideo(this.src);  " +
                "    }  " +
                "}" +
                "})()");
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.mImageClickListener = listener;
    }

    public void setOnVideoClickListener(OnVideoClickListener listener) {
        this.mVideoClickListener = listener;
    }

    public void setOnFinishFinish(OnFinishListener listener) {
        this.mOnFinishFinish = listener;
    }

    private class JavascriptInterface {

        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            if (mImageClickListener != null)
                mImageClickListener.onClick(img);
        }

        @android.webkit.JavascriptInterface
        public void openVideo(String img) {
            if (mVideoClickListener != null)
                mVideoClickListener.onClick(img);
        }

        @android.webkit.JavascriptInterface
        public void showHtml(String html) {
            if (mHTMLListener != null)
                mHTMLListener.showHtml(html);
        }
    }

    public interface OnLoadedHtmlListener {
        void showHtml(String html);
    }


    public interface OnImageClickListener {
        void onClick(String url);
    }

    public interface OnVideoClickListener {
        void onClick(String url);
    }

    public interface OnFinishListener {
        void onReceivedTitle(String title);

        void onProgressChange(int progress);

        void onError();

        void onFinish();
    }
}