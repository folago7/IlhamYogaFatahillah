package com.kymjs.core.bitmap.client;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.kymjs.core.bitmap.BitmapMemoryCache;
import com.kymjs.core.bitmap.DiskImageDisplayer;
import com.kymjs.core.bitmap.ImageBale;
import com.kymjs.core.bitmap.ImageDisplayer;
import com.kymjs.core.bitmap.interf.IBitmapCache;
import com.kymjs.core.bitmap.toolbox.CreateBitmap;
import com.kymjs.core.bitmap.toolbox.DensityUtils;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.http.Request;
import com.kymjs.rxvolley.http.RequestQueue;
import com.kymjs.rxvolley.http.RetryPolicy;
import com.kymjs.rxvolley.rx.Result;
import com.kymjs.rxvolley.toolbox.Loger;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author kymjs (http://www.kymjs.com/) on 12/21/15.
 */
public final class BitmapCore {

    private BitmapCore() {
    }

    private static ImageDisplayer sDisplayer;
    private static DiskImageDisplayer sDiskImageDisplayer;

    private final static ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private static ArrayList<ImageBale> requestArray = new ArrayList<>();

    /**
     * 获取一个请求队列(单例)
     */
    public synchronized static ImageDisplayer getDisplayer() {
        if (sDisplayer == null) {
            createDisplayer(null, null);
        }
        return sDisplayer;
    }

    public synchronized static DiskImageDisplayer getDiskDisplayer() {
        if (sDiskImageDisplayer == null) {
            createDisplayer(null, null);
        }
        return sDiskImageDisplayer;
    }

    public static ExecutorService getExecutorService() {
        return DEFAULT_EXECUTOR_SERVICE;
    }

    /**
     * 设置请求队列,必须在调用BitmapCore#getDisplayer()之前设置
     *
     * @return 是否设置成功
     */
    public synchronized static boolean createDisplayer(RequestQueue queue, IBitmapCache
            mMemoryCache) {
        if (queue == null)
            queue = RxVolley.getRequestQueue();
        if (mMemoryCache == null) mMemoryCache = new BitmapMemoryCache();
        if (sDiskImageDisplayer == null) {
            sDiskImageDisplayer = new DiskImageDisplayer(mMemoryCache);
        }
        if (sDisplayer == null) {
            sDisplayer = new ImageDisplayer(queue, mMemoryCache);
            return true;
        } else {
            return false;
        }
    }

    public static class Builder {
        private HttpCallback realCallback;
        private HttpCallback callback;
        private Request<?> request;
        private Drawable defaultDrawable;
        private View view;
        private BitmapRequestConfig config = new BitmapRequestConfig();

        /**
         * 请求回调,不需要可以为空
         */
        public Builder callback(HttpCallback callback) {
            this.callback = callback;
            return this;
        }

        /**
         * HttpRequest
         */
        public Builder setRequest(Request<?> request) {
            this.request = request;
            return this;
        }

        /**
         * 要显示图片的view
         */
        public Builder view(View view) {
            this.view = view;
            return this;
        }

        /**
         * HttpRequest的配置器
         */
        public Builder confit(BitmapRequestConfig config) {
            this.config = config;
            return this;
        }

        public Builder loadResId(int loadResId) {
            this.config.loadRes = loadResId;
            return this;
        }

        public Builder errorResId(int errorResId) {
            this.config.errorRes = errorResId;
            return this;
        }

        public Builder loadDrawable(Drawable loadDrawable) {
            this.config.loadDrawable = loadDrawable;
            return this;
        }

        public Builder errorDrawable(Drawable errorDrawable) {
            this.config.errorDrawable = errorDrawable;
            return this;
        }

        public Builder putHeader(String k, String v) {
            this.config.putHeader(k, v);
            return this;
        }

        /**
         * 请求超时时间,如果不设置则使用重连策略的超时时间,默认2500ms
         */
        public Builder timeout(int timeout) {
            this.config.mTimeout = timeout;
            return this;
        }

        /**
         * 为了更真实的模拟网络,如果读取缓存,延迟一段时间再返回缓存内容
         */
        public Builder delayTime(int delayTime) {
            this.config.mDelayTime = delayTime;
            return this;
        }

        /**
         * 显示图片的最大宽高,若图片高于这个值则压缩,否则不作处理
         */
        public Builder size(int w, int h) {
            this.config.maxWidth = w;
            this.config.maxHeight = h;
            return this;
        }

        /**
         * 是否使用服务器控制的缓存有效期(如果使用服务器端的,则无视#cacheTime())
         */
        public Builder useServerControl(boolean useServerControl) {
            this.config.mUseServerControl = useServerControl;
            return this;
        }

        /**
         * 是否启用缓存
         */
        public Builder shouldCache(boolean shouldCache) {
            this.config.mShouldCache = shouldCache;
            return this;
        }

        /**
         * 网络请求接口url
         */
        public Builder url(String url) {
            this.config.mUrl = url;
            return this;
        }

        /**
         * 重连策略,不传则使用默认重连策略
         */
        public Builder retryPolicy(RetryPolicy retryPolicy) {
            this.config.mRetryPolicy = retryPolicy;
            return this;
        }

        /**
         * 编码,默认UTF-8
         */
        public Builder encoding(String encoding) {
            this.config.mEncoding = encoding;
            return this;
        }

        private Drawable getDefaultDrawable() {
            if (defaultDrawable == null) {
                defaultDrawable = new ColorDrawable(0xFFCFCFCF);
            }
            return defaultDrawable;
        }

        /**
         * 安全校验
         */
        private synchronized void build() {
            if (view == null) {
                final String warn = "view is null";
                Loger.debug(warn);
                if (callback != null)
                    callback.onFailure(-1, warn);
                RxVolley.getRequestQueue().getPoster().put(config.mUrl,
                        new RuntimeException(warn));
                return;
            }

            if (TextUtils.isEmpty(config.mUrl)) {
                final String warn = "image url is empty";
                Loger.debug(warn);
                doFailure(view, config.errorDrawable, config.errorRes);
                if (callback != null)
                    callback.onFailure(-1, warn);
                RxVolley.getRequestQueue().getPoster().put(config.mUrl,
                        new RuntimeException(warn));
                return;
            }

            if (config.maxWidth == BitmapRequestConfig.DEF_WIDTH_HEIGHT &&
                    config.maxHeight == BitmapRequestConfig.DEF_WIDTH_HEIGHT) {
                config.maxWidth = view.getWidth();
                config.maxHeight = view.getHeight();
                if (config.maxWidth <= 0) {
                    config.maxWidth = DensityUtils.getScreenW(view.getContext()) / 2;
                }
                if (config.maxHeight <= 0) {
                    config.maxHeight = DensityUtils.getScreenH(view.getContext()) / 2;
                }
            } else if (config.maxWidth == BitmapRequestConfig.DEF_WIDTH_HEIGHT) {
                config.maxWidth = DensityUtils.getScreenW(view.getContext());
            } else if (config.maxHeight == BitmapRequestConfig.DEF_WIDTH_HEIGHT) {
                config.maxHeight = DensityUtils.getScreenH(view.getContext());
            }

            if (config.loadRes == 0 && config.loadDrawable == null) {
                config.loadDrawable = getDefaultDrawable();
            }
            if (config.errorRes == 0 && config.errorDrawable == null) {
                config.errorDrawable = getDefaultDrawable();
            }

            if (realCallback == null)
                realCallback = new HttpCallback() {
                    @Override
                    public void onPreStart() {
                        view.setTag(config.mUrl);
                        if (callback != null) callback.onPreStart();
                    }

                    @Override
                    public void onPreHttp() {
                        setImageWithResource(view, config.loadDrawable, config.loadRes);
                        if (callback != null) callback.onPreHttp();
                    }

                    @Override
                    public void onSuccessInAsync(byte[] t) {
                        if (callback != null) callback.onSuccessInAsync(t);
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        if (config.mUrl.equals(view.getTag())) {
                            setImageWithResource(view, config.errorDrawable, config.errorRes);
                        }
                        if (callback != null) callback.onFailure(errorNo, strMsg);
                    }

                    @Override
                    public void onFinish() {
                        if (callback != null) callback.onFinish();
                    }

                    @Override
                    public void onSuccess(Map<String, String> headers, Bitmap bitmap) {
                        if (config.mUrl.equals(view.getTag())) {
                            setViewImage(view, bitmap);
                        }
                        if (callback != null) callback.onSuccess(headers, bitmap);
                    }
                };
        }

        public Observable<Bitmap> getResult() {
            doTask();
            return RxVolley.getRequestQueue().getPoster().take(config.mUrl)
                    .filter(new Func1<Result, Boolean>() {
                        @Override
                        public Boolean call(Result result) {
                            return result != null
                                    && result.data != null
                                    && result.data.length != 0;
                        }
                    })
                    .map(new Func1<Result, Bitmap>() {
                        @Override
                        public Bitmap call(Result result) {
                            return CreateBitmap.create(result.data,
                                    config.maxWidth, config.maxHeight);
                        }
                    })
                    .subscribeOn(Schedulers.io());
        }

        public void doTask() {
            build();
            if (config.mUrl.startsWith("http")) {
                ImageBale bale = getDisplayer().get(config, realCallback);
                requestArray.add(bale);
            } else {
                getDiskDisplayer().load(config, realCallback, true);
            }
        }
    }

    /**
     * 取消一个请求
     *
     * @return 是否成功
     */
    public static boolean cancle(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        for (ImageBale bale : requestArray) {
            if (url.equals(bale.getRequestUrl())) {
                bale.cancelRequest();
                requestArray.remove(bale);
                return true;
            }
        }
        return false;
    }

    public static Bitmap getMemoryBitmap(String url) {
        return getDisplayer().getMemoryCache().getBitmap(url);
    }

    /**
     * 按照优先级为View设置图片资源
     * 优先使用drawable，仅当drawable无效时使用bitmapRes，若两值均无效，则不作处理
     *
     * @param view          要设置图片的控件(View设置bg，ImageView设置src)
     * @param errorImage    优先使用项
     * @param errorImageRes 次级使用项
     */
    public static void doFailure(View view, Drawable errorImage, int errorImageRes) {
        setImageWithResource(view, errorImage, errorImageRes);
    }

    /**
     * 按照优先级为View设置图片资源
     *
     * @param view          要设置图片的控件(View设置bg，ImageView设置src)
     * @param bitmap        优先使用项
     * @param errorImage    二级使用项
     * @param errorImageRes 三级使用项
     */
    public static void doSuccess(View view, Bitmap bitmap, Drawable errorImage,
                                 int errorImageRes) {
        if (bitmap != null) {
            setViewImage(view, bitmap);
        } else {
            setImageWithResource(view, errorImage, errorImageRes);
        }
    }

    /**
     * 按照优先级为View设置图片资源
     * 优先使用drawable，仅当drawable无效时使用bitmapRes，若两值均无效，则不作处理
     *
     * @param imageView 要设置图片的控件(View设置bg，ImageView设置src)
     * @param drawable  优先使用项
     * @param bitmapRes 次级使用项
     */
    public static void setImageWithResource(View imageView, Drawable drawable,
                                            int bitmapRes) {
        if (drawable != null) {
            setViewImage(imageView, drawable);
        } else if (bitmapRes > 0) { //大于0视为有效ImageResource
            setViewImage(imageView, bitmapRes);
        }
    }

    public static void setViewImage(View view, int background) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(background);
        } else {
            view.setBackgroundResource(background);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void setViewImage(View view, Bitmap background) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageBitmap(background);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                view.setBackground(new BitmapDrawable(view.getResources(),
                        background));
            } else {
                view.setBackgroundDrawable(new BitmapDrawable(view
                        .getResources(), background));
            }
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void setViewImage(View view, Drawable background) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(background);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                view.setBackground(background);
            } else {
                view.setBackgroundDrawable(background);
            }
        }
    }
}
