package net.oschina.gitapp.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.common.FileUtils;
import net.oschina.gitapp.common.MethodsCompat;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.common.UpdateManager;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;

import java.io.File;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 设置界面
 * Created by 火蚁 on 15/4/29.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, EasyPermissions.PermissionCallbacks {

    @InjectView(R.id.cb_receive_notice)
    CheckBox cbReceiveNotice;
    @InjectView(R.id.cb_notice_vioce)
    CheckBox cbNoticeVioce;
    @InjectView(R.id.cb_check_update_start)
    CheckBox cbCheckUpdateStart;
    @InjectView(R.id.cb_check_sensor)
    CheckBox cbCheckSensor;
    @InjectView(R.id.tv_cache_size)
    TextView tvCacheSize;

    private AppContext appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.inject(this);
        initDate();
    }

    private void initDate() {
        appContext = AppContext.getInstance();
        cbReceiveNotice.setChecked(appContext.isReceiveNotice());
        cbNoticeVioce.setChecked(appContext.isVoice());
        cbCheckUpdateStart.setChecked(appContext.isCheckUp());
        cbCheckSensor.setChecked(appContext.isOpenSensor());
        tvCacheSize.setText(calCache());

        cbReceiveNotice.setOnCheckedChangeListener(this);
    }

    @Override
    @OnClick({R.id.ll_receive_notice, R.id.ll_notice_voice, R.id.ll_check_update_start, R.id.ll_check_sensor,
            R.id.ll_clear_cache, R.id.ll_check_update,R.id.ll_terms,
            R.id.ll_protocol,R.id.ll_about})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_receive_notice:
                updateReceiveNotice();
                break;
            case R.id.ll_notice_voice:
                updateNoticeVoice();
                break;
            case R.id.ll_check_update_start:
                updateCheckUpdateStart();
                break;
            case R.id.ll_check_sensor:
                updateCheckSensor();
                break;
            case R.id.ll_clear_cache:
                onCache();
                break;
            case R.id.ll_check_update:
                UpdateManager.getUpdateManager().checkAppUpdate(this, new UpdateManager.OnPermissionCallback() {
                    @Override
                    public void onPermissionCallback() {
                        requestExternalStorage();
                    }
                }, true);
                break;
            case R.id.ll_terms:
                WebActivity.show(this,"https://gitee.com/terms");
                break;
            case R.id.ll_protocol:
                WebActivity.show(this,"file:///android_asset/gitee_protocol.html","隐私政策");
                break;
            case R.id.ll_about:
                showAbout();
                break;
            default:
                break;
        }
    }

    private void updateReceiveNotice() {
        if (cbReceiveNotice.isChecked()) {
            cbReceiveNotice.setChecked(false);
        } else {
            cbReceiveNotice.setChecked(true);
        }
        appContext.setConfigReceiveNotice(cbReceiveNotice.isChecked());
    }

    private void updateNoticeVoice() {
        if (cbNoticeVioce.isChecked()) {
            cbNoticeVioce.setChecked(false);
        } else {
            cbNoticeVioce.setChecked(true);
        }
        appContext.setConfigVoice(cbNoticeVioce.isChecked());
    }

    private void updateCheckSensor(){
        if(cbCheckSensor.isChecked()){
            cbCheckSensor.setChecked(false);
        }else {
            cbCheckSensor.setChecked(true);
        }
        appContext.setConfigSensor(cbCheckSensor.isChecked());
    }

    private void updateCheckUpdateStart() {
        if (cbCheckUpdateStart.isChecked()) {
            cbCheckUpdateStart.setChecked(false);
        } else {
            cbCheckUpdateStart.setChecked(true);
        }
        appContext.setConfigCheckUp(cbCheckUpdateStart.isChecked());
    }

    private void onCache() {
        UIHelper.clearAppCache(SettingActivity.this);
        tvCacheSize.setText("OKB");
    }

    private String calCache() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = getFilesDir();
        File cacheDir = getCacheDir();

        fileSize += FileUtils.getDirSize(filesDir);
        fileSize += FileUtils.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = MethodsCompat.getExternalCacheDir(this);
            fileSize += FileUtils.getDirSize(externalCacheDir);
        }
        if (fileSize > 0)
            cacheSize = FileUtils.formatFileSize(fileSize);
        return cacheSize;
    }

    private void showAbout() {
        Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_receive_notice:
                appContext.setConfigReceiveNotice(isChecked);
                break;
            case R.id.cb_notice_vioce:
                appContext.setConfigVoice(isChecked);
                break;
            case R.id.cb_check_update_start:
                appContext.setConfigCheckUp(isChecked);
                break;
            default:
                break;
        }
    }

    private static final int RC_EXTERNAL_STORAGE = 0x04;//存储权限

    @AfterPermissionGranted(RC_EXTERNAL_STORAGE)
    public void requestExternalStorage() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            UpdateManager.getUpdateManager().showDownloadDialog();
        } else {
            EasyPermissions.requestPermissions(this, "", RC_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        UpdateManager.getUpdateManager().showNotPermissionDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
