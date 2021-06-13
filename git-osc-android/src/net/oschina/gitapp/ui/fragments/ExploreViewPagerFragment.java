package net.oschina.gitapp.ui.fragments;

import net.oschina.gitapp.R;
import net.oschina.gitapp.ui.NewProject;
import net.oschina.gitapp.ui.basefragment.BaseListFragment;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;
import android.os.Bundle;
import android.util.Log;

/**
 * 发现页面
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-29
 */
public class ExploreViewPagerFragment extends BaseViewPagerFragment<BaseListFragment> {

    public static ExploreViewPagerFragment newInstance() {
    	Log.i("ExploreViewPagerFragment", "创建"+1);
        return new ExploreViewPagerFragment();
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("ExploreViewPagerFragment", "创建"+2);
		String[] title = getResources().getStringArray(R.array.explore_title_array);
		for (String t : title) {
			titleList.add(t);
			fragmentList.add(new NewProject(null));
			Log.i("ExploreViewPagerFragment", t);
		}
	}
}
