package net.oschina.gitapp.ui.fragments;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ViewPageFragmentAdapter;
import net.oschina.gitapp.common.BroadcastController;
import net.oschina.gitapp.ui.basefragment.BaseFragment;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * 用户主界面
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-29
 */
public class MySelfViewPagerFragment extends BaseViewPagerFragment {
	
	
	
    public static MySelfViewPagerFragment newInstance() {
        return new MySelfViewPagerFragment();
    }
    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// 注册一个用户发生变化的广播
		BroadcastController.registerUserChangeReceiver(activity, mUserChangeReceiver);
		Log.i("Test", "广播注册成功");
	}

	private BroadcastReceiver mUserChangeReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			//接收到变化后
			Log.i("Test", "用户发生了改变");
		}
	};

	@Override
	public void onDetach() {
		super.onDetach();
		BroadcastController.unregisterReceiver(getActivity(), mUserChangeReceiver);
	}

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.myself_title_array);
		adapter.addTab(title[0], "event", MySelfListEventFragment.class, null);
		adapter.addTab(title[1], "project", MySelfListProjectFragment.class, null);
	}
}
