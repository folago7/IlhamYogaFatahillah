package net.oschina.gitapp.ui.basefragment;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ViewPageFragmentAdapter;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 类名 BaseMainFragment.java</br>
 * 创建日期 2014年4月27日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月27日 下午4:29:16</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 各类主界面的基类
 */
public abstract class BaseViewPagerFragment extends BaseFragment{

	protected PagerTabStrip mTabStrip;
	protected ViewPager  mViewPager;
	protected ViewPageFragmentAdapter mTabsAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.base_viewpage_fragment, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
        mTabStrip = (PagerTabStrip) view.findViewById(R.id.pager_tabstrip);
        
        mViewPager = (ViewPager)view.findViewById(R.id.pager);

        mTabsAdapter = new ViewPageFragmentAdapter(
        		getChildFragmentManager(), mTabStrip, mViewPager);

        onSetupTabAdapter(mTabsAdapter);
        mTabsAdapter.notifyDataSetChanged();

        if (savedInstanceState != null) {
        	int pos = savedInstanceState.getInt("position");
        	mViewPager.setCurrentItem(pos, false);
        }
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", mViewPager.getCurrentItem());
    }
	
	protected abstract void onSetupTabAdapter(ViewPageFragmentAdapter adapter);
}