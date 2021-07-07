package net.oschina.gitapp.ui.basefragment;

import java.util.ArrayList;
import java.util.List;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ListFragmentPagerAdapter;
import net.oschina.gitapp.interfaces.OnBaseListFragmentResumeListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
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
	protected ListFragmentPagerAdapter<T> adapter;
	protected ViewPager pager;
	protected PagerTabStrip mPagerTabStrip;
	protected boolean hasBaseListFragmentResumed;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("Test", "父类的created");
		//fragmentList = new ArrayList<T>();
		Log.i("Test", fragmentList.size() + "前");
		//titleList = new ArrayList<String>();
		Log.i("Test", fragmentList.size() + "后");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.i("Test", "父类的onCreateView");
		View view = inflater.inflate(R.layout.base_viewpage_fragment, container, false);
		pager = (ViewPager) view.findViewById(R.id.pager);
		mPagerTabStrip = (PagerTabStrip) view.findViewById(R.id.pager_tabstrip);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i("Test", "父类的onActivityCreated");
		adapter = new ListFragmentPagerAdapter<T>(
				getActivity().getSupportFragmentManager(), titleList,
				fragmentList);
		pager.setAdapter(adapter);
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
