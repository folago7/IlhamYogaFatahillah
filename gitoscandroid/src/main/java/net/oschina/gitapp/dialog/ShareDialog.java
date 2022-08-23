package net.oschina.gitapp.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import net.oschina.gitapp.R;
import net.oschina.gitapp.share.BaseShare;
import net.oschina.gitapp.share.MomentsShare;
import net.oschina.gitapp.share.TencentQQShare;
import net.oschina.gitapp.share.QZoneShare;
import net.oschina.gitapp.share.SinaShare;
import net.oschina.gitapp.share.WeChatShare;

/**
 * 分享对话框
 * Created by huanghaibin on 2017/6/19.
 */

public class ShareDialog extends BottomSheetDialog implements View.OnClickListener {
    private BottomSheetBehavior behavior;
    private BaseShare.Builder mBuilder;

    @SuppressLint("InlinedApi")
    public ShareDialog(@NonNull Context context) {
        super(context);
        Window window = getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.dialog_share, null);
        setContentView(view);
        initialize(view);
        view.findViewById(R.id.ll_share_wx).setOnClickListener(this);
        view.findViewById(R.id.ll_share_moments).setOnClickListener(this);
        view.findViewById(R.id.ll_share_qq).setOnClickListener(this);
        view.findViewById(R.id.ll_share_qzone).setOnClickListener(this);
        view.findViewById(R.id.ll_share_weibo).setOnClickListener(this);
    }

    public ShareDialog init(Activity context, String title,
                     String url, String shareContent, Bitmap shareImage) {
        if (mBuilder == null) {
            mBuilder = new BaseShare.Builder(context);
        }
        mBuilder.title(title)
                .url(url)
                .content(shareContent)
                .bitmap(shareImage);
        return this;
    }

    @Override
    public void onClick(View v) {
        if(mBuilder == null)
            return;
        BaseShare share = null;
        switch (v.getId()) {
            case R.id.ll_share_wx:
                share = new WeChatShare(mBuilder);
                break;
            case R.id.ll_share_moments:
                share = new MomentsShare(mBuilder);
                break;
            case R.id.ll_share_qq:
                share = new TencentQQShare(mBuilder);
                break;
            case R.id.ll_share_qzone:
                share = new QZoneShare(mBuilder);
                break;
            case R.id.ll_share_weibo:
                share = new SinaShare(mBuilder);
                break;
        }
        if (share != null) {
            share.share();
        }
        dismiss();
    }

    @Override
    public void show() {
        super.show();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void initialize(final View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        behavior = (BottomSheetBehavior) params.getBehavior();
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }
}
