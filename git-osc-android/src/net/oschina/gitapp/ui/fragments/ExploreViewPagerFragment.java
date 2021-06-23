package net.oschina.gitapp.ui.fragments;

import net.oschina.gitapp.R;
import net.oschina.gitapp.ui.basefragment.BaseFragment;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;
import android.os.Bundle;
import android.util.Log;

/**
 * 发现页面
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-29
 */
public class ExploreViewPagerFragment extends BaseViewPagerFragment<BaseFragment> {

    public static ExploreViewPagerFragment newInstance() {
        return new ExploreViewPagerFragment();
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] title = getResources().getStringArray(R.array.explore_title_array);
		for (String t : title) {
			titleList.add(t);
			fragmentList.add(new ExploreListLatestProjectFragment());
		}
	}
}
