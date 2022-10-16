package net.oschina.gitapp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import net.oschina.gitapp.R;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.widget.MWebView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WebActivity extends BaseActivity implements
        MWebView.OnFinishListener{

    protected MWebView mWebView;
    @InjectView(R.id.progressBar)
    ProgressBar mProgressBar;
    @InjectView(R.id.ll_root)
    LinearLayout mLinearRoot;
    private String mUrl;

    public static void show(Context context, String url) {
        if (TextUtils.isEmpty(url))
            return;
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.inject(this);
        initView();
    }

    private void initView(){
        mWebView = new MWebView(this);
        mWebView.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        mWebView.setLayoutParams(params);
        mLinearRoot.addView(mWebView);
        mUrl = getIntent().getStringExtra("url");
        mWebView.setOnFinishFinish(this);
        mWebView.loadUrl(mUrl);
    }


    @Override
    public void onReceivedTitle(String title) {

    }

    @Override
    public void onProgressChange(int progress) {
        if (isDestroyed())
            return;
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {
        if (isDestroyed())
            return;
        mProgressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.onDestroy();
        }
    }
}
