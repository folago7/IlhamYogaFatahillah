package net.oschina.gitapp;

import net.oschina.gitapp.ui.MainActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

/**
 * 启动界面
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-14 9:16
 */
public class AppStart extends Activity {
	
	private AppContext mAppContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAppContext = (AppContext) getApplication();
		//setContentView(R.layout.app_start);
		boolean isFrist = mAppContext.isFristStart();
		if (isFrist) {
			goWelComePage();
		} else {
			goMainActivity();
		}
		finish();
	}
	
	private void goMainActivity() {
		Intent intent = new Intent(AppStart.this, MainActivity.class);
		startActivity(intent);
	}
	
	private void goWelComePage() {
		Intent intent = new Intent(AppStart.this, WelComePage.class);
		startActivity(intent);
	}
}
