package net.oschina.gitapp.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * UI
 * Created by haibin on 2019/12/3.
 */

public  final class UI {

    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    public static void runOnMainThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    public static void runDelay(Runnable runnable, long delay) {
        mHandler.postDelayed(runnable, delay);
    }

}
