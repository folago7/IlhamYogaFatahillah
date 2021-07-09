package net.oschina.gitapp.ui.fragments;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ViewPageFragmentAdapter;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;

/**
 * 发现页面
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-29
 */
public class ExploreViewPagerFragment extends BaseViewPagerFragment {
	
    public static ExploreViewPagerFragment newInstance() {
        return new ExploreViewPagerFragment();
    }

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.explore_title_array);
		adapter.addTab(title[0], "featured", ExploreFeaturedListProjectFragment.class, null);
		adapter.addTab(title[1], "popular", ExplorePopularListProjectFragment.class, null);
		adapter.addTab(title[2], "latest", ExploreLatestListProjectFragment.class, null);
	}
}
