package net.oschina.gitapp.ui.baseactivity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewConfiguration;

import net.oschina.gitapp.AppManager;
import net.oschina.gitapp.R;
import net.oschina.gitapp.common.StringUtils;

import java.lang.reflect.Field;

public class BaseActivity extends ActionBarActivity {

	// 是否可以返回
	protected static boolean isCanBack;
	
	protected ActionBar mActionBar;

	protected String mTitle;
	
	protected String mSubTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initActionBar();
		//将activity加入到AppManager堆栈中
		AppManager.getAppManager().addActivity(this);
	}

	// 关闭该Activity
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return super.onSupportNavigateUp();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}
	
	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
	
	// 初始化ActionBar
	private void initActionBar() {
		mActionBar = getSupportActionBar();
		int flags = ActionBar.DISPLAY_HOME_AS_UP;
		int change = mActionBar.getDisplayOptions() ^ flags;
		// 设置返回的图标
		mActionBar.setDisplayOptions(change, flags);
        if (mTitle != null && !StringUtils.isEmpty(mTitle)) {
            mActionBar.setTitle(mTitle);
        }
        if (mSubTitle != null && !StringUtils.isEmpty(mSubTitle)) {
            mActionBar.setSubtitle(mSubTitle);
        }
	}

    public void setActionBarTitle(String title) {
        mActionBar.setTitle(title);
    }

    public void setActionBarSubTitle(String subTitle) {
        mActionBar.setSubtitle(subTitle);
    }

}
