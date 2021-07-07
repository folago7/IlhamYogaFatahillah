package net.oschina.gitapp.ui.fragments;

import java.util.ArrayList;

import net.oschina.gitapp.R;
import net.oschina.gitapp.ui.basefragment.BaseFragment;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;
import android.os.Bundle;
import android.util.Log;

/**
 * 用户主界面
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-29
 */
public class MySelfViewPagerFragment extends BaseViewPagerFragment<BaseFragment> {

    public static MySelfViewPagerFragment newInstance() {
        return new MySelfViewPagerFragment();
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] title = getResources().getStringArray(R.array.myself_title_array);
		for (String t : title) {
			titleList.add(t);
			Log.i("MySelfViewPagerFragment", t);
		}
		fragmentList.add(new MySelfListEventFragment());
		fragmentList.add(new MySelfListProjectFragment());
		fragmentList.add(new MySelfListEventFragment());
		fragmentList.add(new MySelfListProjectFragment());
	}
}
