package net.oschina.gitapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.gitapp.R;

/**
 * 确认对话框
 * Created by huanghaibin on 2018/4/14.
 */
@SuppressWarnings("all")
public class ConfirmDialog extends Dialog implements View.OnClickListener {


    protected TextView mTextTitle;
    protected TextView mTextMessage;
    protected TextView mTextSure;
    protected TextView mTextCancel;
    protected LinearLayout mLinearRoot;
    private LinearLayout mLinearButton;
    private OnDialogClickListener mCancelListener;
    private OnDialogClickListener mSureListener;

    public ConfirmDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        getWindow().addFlags(Window.FEATURE_NO_TITLE);
        getWindow().setGravity(Gravity.CENTER);
        setContentView(getLayoutId());
        initView();
    }


    protected int getLayoutId() {
        return R.layout.dialog_confirm;
    }

    protected void initView() {
        mLinearButton = findViewById(R.id.ll_button);
        mLinearRoot = findViewById(R.id.ll_dialog);
        mTextTitle = findViewById(R.id.tv_title);
        mTextMessage = findViewById(R.id.tv_message);
        mTextSure = findViewById(R.id.tv_sure);
        mTextCancel = findViewById(R.id.tv_cancel);
        mTextSure.setOnClickListener(this);
        mTextCancel.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sure:
                if (mSureListener != null) {
                    mSureListener.onClick(v);
                }
                break;
            case R.id.tv_cancel:
                if (mCancelListener != null) {
                    mCancelListener.onClick(v);
                }
                break;
        }
        dismiss();
    }


    public ConfirmDialog canDismiss(boolean canDismiss){
        setCancelable(canDismiss);
        return this;
    }

    public ConfirmDialog hideCancelText() {
        mTextCancel.setVisibility(View.GONE);
        return this;
    }

    public ConfirmDialog setTitleText(int title) {
        mTextTitle.setText(title);
        return this;
    }

    public ConfirmDialog setTitleText(String title) {
        mTextTitle.setText(title);
        return this;
    }

    public ConfirmDialog setMessageText(int message) {
        mTextMessage.setText(message);
        return this;
    }

    public ConfirmDialog setMessageText(String message) {
        mTextMessage.setText(message);
        return this;
    }

    public ConfirmDialog setSureButtonText(int sure) {
        mTextSure.setText(sure);
        return this;
    }

    public ConfirmDialog setSureButtonText(String sure) {
        mTextSure.setText(sure);
        return this;
    }

    public ConfirmDialog setCancelButtonText(int cancel) {
        mTextCancel.setText(cancel);
        return this;
    }

    public ConfirmDialog setCancelButtonText(String cancel) {
        mTextCancel.setText(cancel);
        return this;
    }

    public ConfirmDialog setSureListener(OnDialogClickListener listener) {
        this.mSureListener = listener;
        return this;
    }

    public ConfirmDialog setCancelListener(OnDialogClickListener listener) {
        this.mCancelListener = listener;
        return this;
    }


    public interface OnDialogClickListener {
        void onClick(View view);
    }
}
