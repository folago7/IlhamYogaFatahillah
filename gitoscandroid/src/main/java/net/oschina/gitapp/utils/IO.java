package net.oschina.gitapp.utils;

import java.io.Closeable;

/**
 * IO操作
 * Created by huanghaibin on 2018/1/16.
 */

public final class IO {
    /**
     * 关闭流
     */
    public static void close(Closeable... closeables) {
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
