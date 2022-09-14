package net.oschina.gitapp.media;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@SuppressWarnings("unused")
public class Util {
    static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    static String getCameraPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/";// filePath:/sdcard/
    }

    @SuppressLint("SimpleDateFormat")
    static String getSaveImageFullName() {
        return "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";// 照片命名
    }

    static ArrayList<String> toArrayList(List<Image> images) {
        ArrayList<String> strings = new ArrayList<>();
        for (Image i : images) {
            strings.add(i.getPath());
        }
        return strings;
    }

    static String[] toArray(List<Image> images) {
        if (images == null)
            return null;
        int len = images.size();
        if (len == 0)
            return null;

        String[] strings = new String[len];
        int i = 0;
        for (Image image : images) {
            strings[i] = image.getPath();
            i++;
        }
        return strings;
    }

  public   static String[] listToArray(List<String> images) {
        if (images == null)
            return null;
        int len = images.size();
        if (len == 0)
            return null;

        String[] strings = new String[len];
        int i = 0;
        for (String image : images) {
            strings[i] = image;
            i++;
        }
        return strings;
    }

    /**
     * 获得屏幕的宽度
     *
     * @param context context
     * @return width
     */
    static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * 获得屏幕的高度
     *
     * @param context context
     * @return height
     */
    static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

     static void resetOptions(BitmapFactory.Options options) {
        options.inTempStorage = null;
        options.inDither = false;
        options.inScaled = false;
        options.inSampleSize = 1;
        options.inPreferredConfig = null;
        options.inJustDecodeBounds = false;
        options.inDensity = 0;
        options.inTargetDensity = 0;
        options.outWidth = 0;
        options.outHeight = 0;
        options.outMimeType = null;

        if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
            options.inBitmap = null;
            options.inMutable = true;
        }
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    static int dipTopx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     *
     * @param context context
     * @param pxValue px
     * @return dp
     */
    static float pxTodip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }

    static void close(Closeable... closeables) {
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

    /**
     * 获取图片的真实后缀
     *
     * @param filePath 图片存储地址
     * @return 图片类型后缀
     */
    static String getExtension(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        String mimeType = options.outMimeType;
        return mimeType.substring(mimeType.lastIndexOf("/") + 1);
    }


    static boolean copyFile(final File srcFile, final File saveFile) {
        File parentFile = saveFile.getParentFile();
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs())
                return false;
        }

        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(srcFile));
            outputStream = new BufferedOutputStream(new FileOutputStream(saveFile));
            byte[] buffer = new byte[1024 * 4];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(inputStream, outputStream);
        }
        return true;
    }
}
