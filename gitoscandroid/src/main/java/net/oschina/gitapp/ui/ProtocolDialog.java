package net.oschina.gitapp.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.gitapp.R;


/**
 * 协议确认对话框
 * Created by huanghaibin on 2019/12/18.
 */
@SuppressWarnings("all")
public class ProtocolDialog extends Dialog implements View.OnClickListener {


    private TextView mTextTitle;
    private TextView mTextMessage;
    private TextView mTextItem;
    private TextView mTextSure;
    private TextView mTextCancel;
    private LinearLayout mLinearRoot;
    private LinearLayout mLinearButton;
    private OnDialogClickListener mCancelListener;
    private OnDialogClickListener mSureListener;
    private Activity mActivity;

    public ProtocolDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        mActivity = (Activity)context;
        getWindow().addFlags(Window.FEATURE_NO_TITLE);
        getWindow().setGravity(Gravity.CENTER);
        setContentView(getLayoutId());
        initView();
    }


    protected int getLayoutId() {
        return R.layout.dialog_protocol;
    }

    protected void initView() {
        setCancelable(false);
        mLinearButton = findViewById(R.id.ll_button);
        mLinearRoot = findViewById(R.id.ll_dialog);
        mTextTitle = findViewById(R.id.tv_title);
        mTextMessage = findViewById(R.id.tv_message);
        mTextSure = findViewById(R.id.tv_sure);
        mTextCancel = findViewById(R.id.tv_cancel);
        mTextItem = findViewById(R.id.tv_item);
        mTextSure.setOnClickListener(this);
        mTextCancel.setOnClickListener(this);
        SpannableStringBuilder sb = new SpannableStringBuilder("您可阅读《码云用户协议》和《隐私政策》来了解详细的信息。");



        mTextItem.setMovementMethod(LinkMovementMethod.getInstance());
        sb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                WebActivity.show(mActivity,"https://gitee.com/terms");
            }
        },4,12,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        sb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                WebActivity.show(mActivity,"file:///android_asset/gitee_protocol.html","隐私政策");
            }
        },13,19,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        sb.setSpan(new ForegroundColorSpan(0xFF4183c4),4,12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new ForegroundColorSpan(0xFF4183c4),13,19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextItem.setText(sb);
    }

    public ProtocolDialog setSureListener(OnDialogClickListener mSureListener) {
        this.mSureListener = mSureListener;
        return this;
    }

    public ProtocolDialog setCancelListener(OnDialogClickListener mCancelListener) {
        this.mCancelListener = mCancelListener;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sure:
                if (mSureListener != null) {
                    mSureListener.onClick(v);
                }
                dismiss();
                break;
            case R.id.tv_cancel:
                if (mCancelListener != null) {
                    mCancelListener.onClick(v);
                }
                dismiss();
                break;
        }
    }

    public interface OnDialogClickListener {
        void onClick(View view);

    }
}
