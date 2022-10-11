package net.oschina.gitapp.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件管理器类
 * Created by huanghaibin on 2017/7/21.
 */
@SuppressWarnings("unused")
public final class FileHelper {
    /**
     * 删除单个文件
     *
     * @param path 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean deleteFile(String path) {
        File file = new File(path);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param path 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public static boolean deleteFileOrDir(String path) {
        File file = new File(path);
        // 判断目录或文件是否存在
        if (!file.exists()) { // 不存在返回 false
            return false;
        } else {
            // 判断是否为文件
            if (file.isFile()) { // 为文件时调用删除文件方法
                return deleteFile(path);
            } else { // 为目录时调用删除目录方法
                return deleteDirectory(path);
            }
        }
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    private static boolean deleteDirectory(String sPath) {
        boolean flag;
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteFile(file.getAbsolutePath());
                if (!flag)
                    break;
            } // 删除子目录
            else {
                flag = deleteDirectory(file.getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        return flag && dirFile.delete();
    }


    public static boolean exists(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        // 如果存在,直接返回true
        return file.exists();
    }


    /**
     * 取得文件夹大小
     *
     * @param path 文件夹路径
     * @return 单位是b
     */
    private static long getDirSizeByte(String path) {
        long size = 0;
        File file = new File(path);
        File files[] = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                size = size + getDirSizeByte(path);
            } else {
                size = size + f.length();
            }
        }
        return size;
    }

    public static boolean copyFile(final File srcFile, final File saveFile) {
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
            IO.close(inputStream, outputStream);
        }
        return true;
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     */
    public static void copyFile(String oldPath, String newPath) {
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int read;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                inStream = new FileInputStream(oldPath); // 读入原文件
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((read = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, read);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            IO.close(inStream,fs);
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     */
    public static void copyFileSourceAndroidQ(Context context,String oldPath, String newPath) {

        InputStream inStream = null;
        FileOutputStream fs = null;
        String url = null;
        try {

            ContentResolver resolver = context.getContentResolver();
            Uri insertUri = Uri.parse(oldPath);
            if (insertUri != null) {
                inStream = resolver.openInputStream(insertUri);
            }
            if (inStream != null) {
                int read;
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((read = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, read);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IO.close(inStream, fs);
        }
    }

    /**
     * 流转文件
     *
     * @param is      输入流
     * @param newPath 路径
     */
    public static void inputStreamToFile(InputStream is, String newPath) {
        FileOutputStream fs = null;
        try {
            int byteRead;
             fs = new FileOutputStream(newPath);
            byte[] buffer = new byte[1444];
            while ((byteRead = is.read(buffer)) != -1) {
                fs.write(buffer, 0, byteRead);
            }
            is.close();
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            IO.close(fs);
        }
    }

    /**
     * 获取文件夹大小
     *
     * @param dirPath 文件夹路径
     * @return 大小
     */
    public static String getDirSize(String dirPath) {
        File file = new File(dirPath);
        long size = 0;
        if (file.exists() && file.isDirectory()) {

            File[] files = file.listFiles();
            for (int i = 0; i < file.listFiles().length; i++) {
                size += files[i].length();
            }
        }
        if (size > 1024 * 1024)
            return size / 1000 / 1000 + "MB";
        else
            return size / 1000 + "KB";

    }

    public static String getFileSize(long size){
        if (size > 1024 * 1024)
            return size / 1000 / 1000 + "MB";
        else
            return size / 1000 + "KB";
    }

    /**
     * 获取文件夹内文件数量
     */
    public static int getDirFileCount(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists() || file.isFile()) return 0;
        return file.listFiles().length;
    }

    public static long getFileSize(String path) {
        long size = 0;
        File file = new File(path);
        if (file.exists()) {
            size = file.length();
        }
        return size;
    }

    /**
     * 获取图片的真实后缀
     *
     * @param filePath 图片存储地址
     * @return 图片类型后缀
     */
    public static String getExtension(String filePath) {
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        android.graphics.BitmapFactory.decodeFile(filePath, options);
        String mimeType = options.outMimeType;
        if(mimeType == null){
            return "jpg";
        }
        return mimeType.substring(mimeType.lastIndexOf("/") + 1);
    }





    /**
     * 通过MediaStore保存，兼容AndroidQ
     *
     * @param context      context
     * @param sourceFile   源文件
     * @param saveFileName 保存的文件名
     * @param saveDirName  picture子目录
     * @return 成功或者失败
     */
    @SuppressLint("InlinedApi")
    public static boolean saveImageWithMediaStore(Context context,
                                                  File sourceFile,
                                                  String saveFileName,
                                                  String saveDirName) {
        String extension = getExtension(sourceFile.getAbsolutePath());

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DESCRIPTION, "This is an image");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, saveFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.TITLE, "Image.png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + saveDirName);

        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();

        Uri insertUri = resolver.insert(external, values);
        BufferedInputStream inputStream = null;
        OutputStream os = null;
        boolean result = false;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
            if (insertUri != null) {
                os = resolver.openOutputStream(insertUri);
            }
            if (os != null) {
                byte[] buffer = new byte[1024 * 4];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                os.flush();
            }
            result = true;
        } catch (IOException e) {
            result = false;
        } finally {
            IO.close(os, inputStream);
        }
        return result;
    }


    /**
     * 判断文件是否存在
     *
     * @param context context
     * @param path    path
     * @return 判断文件是否存在
     */
    public static boolean isFileExists(Context context, String path) {
        if(TextUtils.isEmpty(path)){
            return false;
        }
        //适配Android Q
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return isAndroidQFileExists(context,path);
        }
        return new File(path).exists();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public static boolean isAndroidQFileExists(Context context, String path){
        if (context == null) {
            return false;
        }
        AssetFileDescriptor afd = null;
        ContentResolver cr = context.getContentResolver();
        try {
            afd = cr.openAssetFileDescriptor(Uri.parse(path), "r");
            if (null == afd) {
                return false;
            } else {
                IO.close(afd);
            }
        } catch (FileNotFoundException e) {
            return false;
        }finally {
            IO.close(afd);
        }
        return true;
    }


    @SuppressLint("InlinedApi")
    public static String saveBitmapWithAndroidQ(Context context, Bitmap bitmap,
                                                String saveFileName,
                                                String saveDirName) {

        FileOutputStream os = null;
        String url = null;
        try {

            String fileName = System.currentTimeMillis() + ".jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DESCRIPTION, "This is an image");
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.TITLE, "Image.png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + saveDirName);

            Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver resolver = context.getContentResolver();

            Uri insertUri = resolver.insert(external, values);
            if (insertUri != null) {
                os = (FileOutputStream) resolver.openOutputStream(insertUri);
            }
            if (os != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
                url = insertUri.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            IO.close(os);
        }
        return url;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "deprecation"})
    public static String saveBitmapCustom(Bitmap bitmap,
                                          String saveFileName,
                                          String saveDirName) {
        FileOutputStream os = null;
        String url = null;
        try {
            File file = new File(url = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath() + File.separator + saveDirName);
            if (!file.exists()) {
                file.mkdirs();
            }
            url = file.getPath() + saveFileName;
            os = new FileOutputStream(url);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            IO.close(os);
        }
        return url;
    }


    @SuppressLint("InlinedApi")
    public static String copyToDownloadPDFAndroidQ(Context context,
                                             String sourcePath,
                                             String fileName,
                                             String saveDirName){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
        values.put(MediaStore.Downloads.RELATIVE_PATH, "Download/" + saveDirName.replaceAll("/","") + "/");

        Uri external = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();

        Uri insertUri = resolver.insert(external, values);
        if(insertUri == null) {
            return null;
        }

        String mFilePath = insertUri.toString();

        InputStream is = null;
        OutputStream os = null;
        try {
            os = resolver.openOutputStream(insertUri);
            if(os == null){
                return null;
            }
            int read;
            File sourceFile = new File(sourcePath);
            if (sourceFile.exists()) { // 文件存在时
                is = new FileInputStream(sourceFile); // 读入原文件
                byte[] buffer = new byte[1444];
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                is.close();
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            IO.close(is,os);
        }
        return insertUri.toString();

    }

    @SuppressLint("InlinedApi")
    public static void copyToDownloadAndroidQ(Context context, String sourcePath, String fileName, String saveDirName){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, "application/vnd.android.package-archive");
        values.put(MediaStore.Downloads.RELATIVE_PATH, "Download/" + saveDirName.replaceAll("/","") + "/");

        Uri external = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();

        Uri insertUri = resolver.insert(external, values);
        if(insertUri == null) {
            return;
        }

        String mFilePath = insertUri.toString();

        InputStream is = null;
        OutputStream os = null;
        try {
            os = resolver.openOutputStream(insertUri);
            if(os == null){
                return;
            }
            int read;
            File sourceFile = new File(sourcePath);
            if (sourceFile.exists()) { // 文件存在时
                is = new FileInputStream(sourceFile); // 读入原文件
                byte[] buffer = new byte[1444];
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                is.close();
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            IO.close(is,os);
        }
    }



    @SuppressLint("InlinedApi")
    public static void deleteShareDirWithAndroidQ(Context context, String saveDirName) {

        try {
            Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver resolver = context.getContentResolver();
            String url = external.toString();

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + saveDirName);
            Uri llUri = resolver.insert(external,values);
            resolver.delete(llUri,null,null);
            if(url == null){

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteShareDirCustom(Context context, String saveDirName) {


        try {
            String url = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath() + "/" + saveDirName;
            File file = new File(url);
            if (!file.exists()) {
                return;
            }
            deleteDirectory(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
