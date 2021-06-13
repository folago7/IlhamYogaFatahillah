package net.oschina.gitapp.ui;

import net.oschina.gitapp.AppManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 应用程序FragmentActivity基类
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-29
 */
public class BaseFragmentActivity extends FragmentActivity {
	
	// 是否允许全屏
	private boolean allowFullScreen;
	// 是否允许销毁
	private boolean allowDestroy;
	
	private View view;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		allowFullScreen = true;
		allowDestroy = true;
		AppManager.getAppManager().addActivity(this);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getAppManager().finishActivity(this);
	}
	
	public boolean isAllowFullScreen() {
		return allowFullScreen;
	}
	public void setAllowFullScreen(boolean allowFullScreen) {
		this.allowFullScreen = allowFullScreen;
	}
	public boolean isAllowDestroy() {
		return allowDestroy;
	}
	public void setAllowDestroy(boolean allowDestroy, View ivew) {
		this.allowDestroy = allowDestroy;
		this.view = view;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && view != null) {
			view.onKeyDown(keyCode, event);
			if (!allowDestroy) {
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
