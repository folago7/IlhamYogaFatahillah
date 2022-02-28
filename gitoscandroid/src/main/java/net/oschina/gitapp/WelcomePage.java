package net.oschina.gitapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import net.oschina.gitapp.common.UIHelper;

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
        LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.app_welcome_page,
                null);
        setContentView(view);

        //渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(3000);
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                finish();
                UIHelper.goMainActivity(WelcomePage.this);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }
}
