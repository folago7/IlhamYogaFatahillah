package net.oschina.gitapp.ui.fragments;

import net.oschina.gitapp.R;
import net.oschina.gitapp.ui.NewProject;
import net.oschina.gitapp.ui.basefragment.BaseListFragment;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 用户主界面
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-29
 */
public class MySelfViewPagerFragment extends BaseViewPagerFragment<BaseListFragment> {

    public static MySelfViewPagerFragment newInstance() {
    	Log.i("ExploreViewPagerFragment", "创建"+1);
        return new MySelfViewPagerFragment();
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("ExploreViewPagerFragment", "创建"+2);
		String[] title = getResources().getStringArray(R.array.myself_title_array);
		for (String t : title) {
			titleList.add(t);
			fragmentList.add(new NewProject(null));
			Log.i("MySelfViewPagerFragment", t);
		}
	}
}
