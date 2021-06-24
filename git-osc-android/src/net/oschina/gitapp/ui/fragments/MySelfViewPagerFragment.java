package net.oschina.gitapp.ui.fragments;

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
		titleList.add("项目");
		fragmentList.add(new MySelfListProjectFragment());
		String[] title = getResources().getStringArray(R.array.myself_title_array);
		for (String t : title) {
			titleList.add(t);
			fragmentList.add(new MySelfListProjectFragment());
			Log.i("MySelfViewPagerFragment", t);
		}
	}
}
