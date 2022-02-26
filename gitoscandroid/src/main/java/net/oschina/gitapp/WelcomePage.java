package net.oschina.gitapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.oschina.gitapp.git2.Main;

/**
 * app的欢迎界面
 *
 * @author deyi（http://my.oschina.net/LittleDY）
 * @created 2014-07-22
 */
public class WelcomePage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.app_welcome_page, 
// null);
//		setContentView(view);
//        
//		//渐变展示启动屏
//		AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
//		aa.setDuration(3000);
//		view.startAnimation(aa);
//		aa.setAnimationListener(new AnimationListener()
//		{
//			@Override
//			public void onAnimationEnd(Animation arg0) {
//				finish();
//				UIHelper.goMainActivity(WelcomePage.this);
//			}
//			@Override
//			public void onAnimationRepeat(Animation animation) {}
//			@Override
//			public void onAnimationStart(Animation animation) {}
//			
//		});
        startActivity(new Intent(this, Main.class));
    }
}
