package net.oschina.gitapp.media;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


import net.oschina.gitapp.R;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class ImagePickerActivity extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks,
        Contract.Presenter {

    private static final int RC_CAMERA_PERM = 0x03;
    private static final int RC_EXTERNAL_STORAGE = 0x04;

    private static SelectOptions mOption;
    private Contract.View mView;

    public static void show(Context context, SelectOptions options) {
        mOption = options;
        context.startActivity(new Intent(context, ImagePickerActivity.class));
    }

    public static void show(Context context, SelectOptions.Callback callback, boolean isCrop) {
        show(context, new SelectOptions.Builder()
                .setSelectCount(1)
                .setHasCam(false)
                .setCrop(!isCrop ? 0 : Util.getScreenWidth(context) / 4 * 3,
                        !isCrop ? 0 : Util.getScreenWidth(context) / 4 * 3)
                .setCallback(callback)
                .build());
    }

    public static void show(Context context, SelectOptions.Callback callback) {
        show(context, callback, false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && actionBar.isShowing())
            actionBar.hide();
        requestExternalStorage();
    }

    @AfterPermissionGranted(RC_CAMERA_PERM)
    @Override
    public void requestCamera() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            if (mView != null) {
                mView.onOpenCameraSuccess();
            }
        } else {
            EasyPermissions.requestPermissions(this, "", RC_CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }


    @AfterPermissionGranted(RC_EXTERNAL_STORAGE)
    @Override
    public void requestExternalStorage() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (mView == null) {
                handleView();
            }
        } else {
            EasyPermissions.requestPermissions(this, "", RC_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }


    @Override
    public void setDataView(Contract.View view) {
        mView = view;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == RC_EXTERNAL_STORAGE) {
            removeView();
            showDialog("", "没有权限, 你需要去设置中开启读取手机存储权限.", "去设置", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                    finish();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        } else {
            if (mView != null)
                mView.onCameraPermissionDenied();
            showDialog("", "没有权限, 你需要去设置中开启相机权限.", "去设置", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                }
            }, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void removeView() {
        Contract.View view = mView;
        if (view == null)
            return;
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove((Fragment) view)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleView() {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_content, ImagePickerFragment.newInstance(mOption))
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("all")
    private void showDialog(String title, String message, String positiveButton, String negativeButton,
                            DialogInterface.OnClickListener listener,
                            DialogInterface.OnClickListener negativeListener) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, listener)
                .setNegativeButton(negativeButton, negativeListener)
                .setCancelable(false)
                .create().show();
    }

    @Override
    protected void onDestroy() {
        mOption = null;
        super.onDestroy();
    }
}
