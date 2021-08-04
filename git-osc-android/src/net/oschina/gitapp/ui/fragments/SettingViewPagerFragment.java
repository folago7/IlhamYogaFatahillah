package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * 设置界面
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-29
 */
public class SettingViewPagerFragment extends Fragment {

    public static SettingViewPagerFragment newInstance() {
        return new SettingViewPagerFragment();
    }
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//addPreferencesFromResource(R.xml.preferences);
	}
}
