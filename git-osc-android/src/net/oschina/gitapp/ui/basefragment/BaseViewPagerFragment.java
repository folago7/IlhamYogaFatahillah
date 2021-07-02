package net.oschina.gitapp.ui.basefragment;

import java.util.ArrayList;
import java.util.List;

import net.oschina.gitapp.R;
import net.oschina.gitapp.interfaces.OnBaseListFragmentResumeListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * ViewPagerFragment基类
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-30 11:47
 * @param <T>
 */
public class BaseViewPagerFragment<T extends BaseFragment> extends Fragment implements
		OnPageChangeListener{
	protected List<T> fragmentList = new ArrayList<T>();
	protected List<String> titleList = new ArrayList<String>();
	protected ViewPager pager;
	protected PagerTabStrip mPagerTabStrip;
	protected boolean hasBaseListFragmentResumed;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.base_viewpage_fragment, container, false);
		pager = (ViewPager) view.findViewById(R.id.pager);
		mPagerTabStrip = (PagerTabStrip) view.findViewById(R.id.pager_tabstrip);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ListFragmentPagerAdapter<T> adapter = new ListFragmentPagerAdapter<T>(
				getActivity().getSupportFragmentManager(), titleList,
				fragmentList);
		pager.setAdapter(adapter);
	}
	
	// pager适配器
	private class ListFragmentPagerAdapter<T extends BaseFragment> extends FragmentPagerAdapter {
		private List<T> fragmentList;
		private List<String> titleList;

		public ListFragmentPagerAdapter(FragmentManager fm,
				List<String> titleList, List<T> fragmentList) {
			super(fm);
			this.titleList = titleList;
			this.fragmentList = fragmentList;
		}

		@Override
		public Fragment getItem(int position) {
			return (fragmentList == null || fragmentList.size() == 0) ? null : fragmentList.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return (titleList.size() > position) ? titleList.get(position) : "";

		}

		@Override
		public int getCount() {
			return fragmentList == null ? 0 : fragmentList.size();
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		//fragmentList.get(arg0).showList();
	}
	
	public boolean isHasBaseListFragmentResumed() {
		return hasBaseListFragmentResumed;
	}

	public void setHasBaseListFragmentResumed(boolean hasBaseListFragmentResumed) {
		this.hasBaseListFragmentResumed = hasBaseListFragmentResumed;
	}
}
