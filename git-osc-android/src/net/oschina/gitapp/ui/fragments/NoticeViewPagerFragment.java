package net.oschina.gitapp.ui.fragments;

import net.oschina.gitapp.R;
import net.oschina.gitapp.ui.basefragment.BaseFragment;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * 通知页面
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-29
 */
public class NoticeViewPagerFragment extends BaseViewPagerFragment<BaseFragment> {

    public static NoticeViewPagerFragment newInstance() {
        return new NoticeViewPagerFragment();
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
