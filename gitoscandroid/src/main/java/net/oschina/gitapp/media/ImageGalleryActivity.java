package net.oschina.gitapp.media;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.oschina.gitapp.R;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


@SuppressWarnings("unused")
public class ImageGalleryActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        EasyPermissions.PermissionCallbacks {
    private PreviewerViewPager mImagePager;
    private TextView mIndexText;
    private ImageView mImageSave;
    private int mCurPosition;

    private String[] mImageSources;
    private boolean[] mImageDownloadStatus;
    private boolean mNeedSaveLocal;
    private RequestManager mLoader;

    public static SelectOptions mOptions;


    public static void show(Context context, SelectOptions options, int position) {
        if (options == null || options.getSelectedImages() == null && options.getSelectedImages().size() == 0)
            return;
        mOptions = options;
        Intent intent = new Intent(context, ImageGalleryActivity.class);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    public static void show(Context context, String image) {
        show(context, new SelectOptions.Builder()
                .setSelectCount(1)
                .setHasCam(false)
                .setSelectedImages(new String[]{image})
                .build(), 0);
    }

    public static void show(Context context, String[] images, int position) {
        show(context, new SelectOptions.Builder()
                .setSelectCount(1)
                .setHasCam(false)
                .setSavaPath(Environment.getExternalStorageDirectory() + "/oschina/gitosc/")
                .setSelectedImages(images)
                .build(), position);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.Theme_Translate);
        setContentView(R.layout.activity_image_gallery);
        initBundle(getIntent().getExtras());
        initWidget();
        initData();
    }


    protected void initBundle(Bundle bundle) {
        if (mOptions != null) {
            // 初始化下载状态
            mImageSources = Util.listToArray(mOptions.getSelectedImages());
            mImageDownloadStatus = new boolean[mImageSources.length];
            mNeedSaveLocal = !TextUtils.isEmpty(mOptions.getSavePath());
            mCurPosition = bundle.getInt("position", 0);
        }
    }


    private void initWidget() {
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        setTitle("");
        mImageSave = (ImageView) findViewById(R.id.iv_save);
        mImageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFileByPermission();
            }
        });
        mImagePager = (PreviewerViewPager) findViewById(R.id.vp_image);
        mIndexText = (TextView) findViewById(R.id.tv_index);
        mImagePager.addOnPageChangeListener(this);
    }

    private void initData() {

        mLoader = Glide.with(this);
        int len = mImageSources.length;
        if (mCurPosition < 0 || mCurPosition >= len)
            mCurPosition = 0;

        // If only one, we not need the text to show
        if (len == 1)
            mIndexText.setVisibility(View.GONE);

        mImagePager.setAdapter(new ViewPagerAdapter());
        mImagePager.setCurrentItem(mCurPosition);
        // First we call to init the TextView
        onPageSelected(mCurPosition);
    }

    private void changeSaveButtonStatus(boolean isShow) {
        if (mNeedSaveLocal) {
            mImageSave.setVisibility(isShow ? View.VISIBLE : View.GONE);
        } else
            mImageSave.setVisibility(View.GONE);
    }

    private void updateDownloadStatus(int pos, boolean isOk) {
        mImageDownloadStatus[pos] = isOk;
        if (mCurPosition == pos) {
            changeSaveButtonStatus(isOk);
        }
    }

    private static final int PERMISSION_ID = 0x0001;

    @SuppressWarnings("unused")
    @AfterPermissionGranted(PERMISSION_ID)
    public void saveToFileByPermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, permissions)) {
            saveToFile();
        } else {
            EasyPermissions.requestPermissions(this, "请授予保存图片权限", PERMISSION_ID, permissions);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveToFile() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "没有外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        final String extDir = mOptions.getSavePath();
        File file = new File(extDir);
        if (!file.exists()) {
            file.mkdirs();
//            Toast.makeText(this, "没有指定存储路径", Toast.LENGTH_SHORT).show();
//            return;
        }

        String path = mImageSources[mCurPosition];

        Object urlOrPath;
        // Do load
        if (mOptions.getHeaders() != null)
            urlOrPath = getGlideUrlByUser(path);
        else
            urlOrPath = path;

        // In this save max image size is source
        final Future<File> future = mLoader
                .load(urlOrPath)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

        new Thread(){
            @Override
            public void run() {
                try {
                    File sourceFile = future.get();
                    if (sourceFile == null || !sourceFile.exists())
                        return;
                    String extension = Util.getExtension(sourceFile.getAbsolutePath());
                    File extDirFile = new File(extDir);
                    if (!extDirFile.exists()) {
                        if (!extDirFile.mkdirs()) {
                            // If mk dir error
                            callSaveStatus(false, null);
                            return;
                        }
                    }
                    final File saveFile = new File(extDirFile, String.format("IMG_%s.%s", System.currentTimeMillis(), extension));
                    final boolean isSuccess = Util.copyFile(sourceFile, saveFile);
                    callSaveStatus(isSuccess, saveFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    callSaveStatus(false, null);
                }
            }
        }.start();
    }


    private void callSaveStatus(final boolean success, final File savePath) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (success) {
                    // notify
                    Uri uri = Uri.fromFile(savePath);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                    Toast.makeText(ImageGalleryActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ImageGalleryActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurPosition = position;
        mIndexText.setText(String.format("%s/%s", (position + 1), mImageSources.length));
        // 滑动时自动切换当前的下载状态
        changeSaveButtonStatus(mImageDownloadStatus[position]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private Point mDisplayDimens;

    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @SuppressWarnings("deprecation")
    private synchronized Point getDisplayDimens() {
        if (mDisplayDimens != null) {
            return mDisplayDimens;
        }
        Point displayDimens;
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        assert windowManager != null;
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            displayDimens = new Point();
            display.getSize(displayDimens);
        } else {
            displayDimens = new Point(display.getWidth(), display.getHeight());
        }

        // In this we can only get 85% width and 60% height
        displayDimens.y = (int) (displayDimens.y * 0.60f);
        displayDimens.x = (int) (displayDimens.x * 0.85f);

        mDisplayDimens = displayDimens;
        return mDisplayDimens;
    }

    private class ViewPagerAdapter extends PagerAdapter implements ImagePreviewView.OnReachBorderListener {

        private View.OnClickListener mFinishClickListener;

        @Override
        public int getCount() {
            return mImageSources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.lay_gallery_page_item_contener, container, false);
            ImagePreviewView previewView = (ImagePreviewView) view.findViewById(R.id.iv_preview);
            previewView.setOnReachBorderListener(this);
            ImageView defaultView = (ImageView) view.findViewById(R.id.iv_default);
            mLoader.load(mImageSources[position])
                    .fitCenter()
                    .into(previewView);
            ProgressBar loading = (ProgressBar) view.findViewById(R.id.progressBar);

            if (mOptions.getHeaders() != null)
                loadImage(position, getGlideUrlByUser(mImageSources[position]),
                        previewView, defaultView, loading);
            else
                loadImage(position, mImageSources[position], previewView, defaultView, loading);

            previewView.setOnClickListener(getListener());
            container.addView(view);
            return view;
        }

        private View.OnClickListener getListener() {
            if (mFinishClickListener == null) {
                mFinishClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                };
            }
            return mFinishClickListener;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void onReachBorder(boolean isReached) {
            mImagePager.isInterceptable(isReached);
        }

        private <T> void loadImage(final int pos, final T urlOrPath,
                                   final ImageView previewView,
                                   final ImageView defaultView, final ProgressBar loading) {

            loadImageDoDownAndGetOverrideSize(urlOrPath, new DoOverrideSizeCallback() {
                @Override
                public void onDone(int overrideW, int overrideH, boolean isTrue) {
                    DrawableRequestBuilder builder = mLoader
                            .load(urlOrPath)
                            .listener(new RequestListener<T, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e,
                                                           T model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFirstResource) {
                                    if (e != null)
                                        e.printStackTrace();
//                                    loading.stop();
                                    loading.setVisibility(View.GONE);
                                    defaultView.setVisibility(View.VISIBLE);
                                    updateDownloadStatus(pos, false);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource,
                                                               T model,
                                                               Target<GlideDrawable> target,
                                                               boolean isFromMemoryCache,
                                                               boolean isFirstResource) {
//                                    loading.stop();
                                    loading.setVisibility(View.GONE);
                                    updateDownloadStatus(pos, true);
                                    return false;
                                }
                            }).diskCacheStrategy(DiskCacheStrategy.SOURCE);

                    // If download or get option error we not set override
                    if (isTrue && overrideW > 0 && overrideH > 0) {
                        builder = builder.override(overrideW, overrideH).fitCenter();
                    }

                    builder.into(previewView);
                }
            });
        }

        private <T> void loadImageDoDownAndGetOverrideSize(final T urlOrPath, final DoOverrideSizeCallback callback) {
            // In this save max image size is source
            final Future<File> future = Glide.with(ImageGalleryActivity.this).load(urlOrPath)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

            new Thread() {
                @Override
                public void run() {
                    try {
                        File sourceFile = future.get();

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        // First decode with inJustDecodeBounds=true to checkShare dimensions
                        options.inJustDecodeBounds = true;
                        // First decode with inJustDecodeBounds=true to checkShare dimensions
                        BitmapFactory.decodeFile(sourceFile.getAbsolutePath(), options);

                        int width = options.outWidth;
                        int height = options.outHeight;
                        Util.resetOptions(options);

                        if (width > 0 && height > 0) {
                            // Get Screen
                            final Point point = getDisplayDimens();

                            // This max size
                            final int maxLen = Math.min(Math.min(point.y, point.x) * 5, 1366 * 3);

                            // Init override size
                            final int overrideW, overrideH;

                            if ((width / (float) height) > (point.x / (float) point.y)) {
                                overrideH = Math.min(height, point.y);
                                overrideW = Math.min(width, maxLen);
                            } else {
                                overrideW = Math.min(width, point.x);
                                overrideH = Math.min(height, maxLen);
                            }

                            // Call back on main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onDone(overrideW, overrideH, true);
                                }
                            });
                        } else {
                            // Call back on main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onDone(0, 0, false);
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                        // Call back on main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onDone(0, 0, false);
                            }
                        });
                    }
                }
            }.start();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    interface DoOverrideSizeCallback {
        void onDone(int overrideW, int overrideH, boolean isTrue);
    }

    @Override
    protected void onDestroy() {
        mOptions = null;
        super.onDestroy();
    }

    private GlideUrl getGlideUrlByUser(String url) {
        if (mOptions == null || mOptions.getHeaders() == null) return new GlideUrl(url);
        return new GlideUrl(url, mOptions.getHeaders());
    }
}
