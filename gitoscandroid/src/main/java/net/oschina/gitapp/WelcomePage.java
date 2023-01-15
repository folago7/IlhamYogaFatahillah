package net.oschina.gitapp;

import android.app.Activity;
import android.os.Bundle;

import net.oschina.gitapp.ui.MainActivity;
import net.oschina.gitapp.utils.UI;

/**
 * app的欢迎界面
 */
public class WelcomePage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        UI.runDelay(() -> {
            MainActivity.show(this);
            finish();
        },1000);
    }

    @Override
    public void onBackPressed() {

    }
}
