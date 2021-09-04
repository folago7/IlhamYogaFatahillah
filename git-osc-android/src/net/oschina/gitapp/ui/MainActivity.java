package net.oschina.gitapp.ui;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppManager;
import net.oschina.gitapp.R;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import net.oschina.gitapp.common.DoubleClickExitHelper;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.common.UpdateManager;
import net.oschina.gitapp.interfaces.*;
import net.oschina.gitapp.ui.fragments.ExploreViewPagerFragment;
import net.oschina.gitapp.ui.fragments.MySelfViewPagerFragment;
import net.oschina.gitapp.ui.fragments.NotificationFragment;

/**
 * 程序主界面
 * @created 2014-04-29
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：2014-05-29
 * 更新内容：更改以callBack的方式进行交互
 * 更新者：火蚁
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends ActionBarActivity implements
		DrawerMenuCallBack {
	
	private final int MENU_SEARCH_ID = 6186;

	static final String DRAWER_MENU_TAG = "drawer_menu";
	static final String DRAWER_CONTENT_TAG = "drawer_content";

	static final String CONTENT_TAG_EXPLORE = "content_explore";
	static final String CONTENT_TAG_MYSELF = "content_myself";
	static final String CONTENT_TAG_NOTICE = "content_notice";
	static final String CONTENT_TAG_SETTING = "content_setting";
	static final String CONTENT_TAG_EXIT = "content_exit";

	static final String CONTENTS[] = { 
		CONTENT_TAG_EXPLORE, 
		CONTENT_TAG_MYSELF,
		CONTENT_TAG_NOTICE, 
		CONTENT_TAG_SETTING, 
		CONTENT_TAG_EXIT };
	
	static final String FRAGMENTS[] = {
		ExploreViewPagerFragment.class.getName(),
		MySelfViewPagerFragment.class.getName(),
		NotificationFragment.class.getName()
	};
	
	static final int TITLES[] = { 
		R.string.fragment_menu_title_explore,
		R.string.fragment_menu_title_myself,
		R.string.fragment_menu_title_notice,
		R.string.fragment_menu_title_setting,
		R.string.fragment_menu_title_exit 
	};

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private FragmentManager mFragmentManager;
	private DoubleClickExitHelper mDoubleClickExitHelper;

	// 当前显示的界面标识
	private String mCurrentContentTag;
	private ActionBar mActionBar;
	private AppContext mContext;
	
	private int mTitle;// actionbar标题
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = (AppContext) getApplicationContext();
		initView(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		// 检查新版本
		if (mContext.isCheckUp()) {
			UpdateManager.getUpdateManager().checkAppUpdate(this, false);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mActionBar.setTitle(mTitle);
	}

	private void initView(Bundle savedInstanceState) {
		
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);
		
		mDoubleClickExitHelper = new DoubleClickExitHelper(this);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerListener(new DrawerMenuListener());
		// 设置滑出菜单的阴影效果
		//mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,GravityCompat.START);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, 0, 0);

		
		mFragmentManager = getSupportFragmentManager();
		if (null == savedInstanceState) {
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			ft.replace(R.id.main_slidingmenu_frame,
					DrawerNavigationMenu.newInstance(), DRAWER_MENU_TAG)
					.replace(R.id.main_content,
							ExploreViewPagerFragment.newInstance(),
							DRAWER_CONTENT_TAG).commit();

			mTitle = TITLES[0];
			mCurrentContentTag = CONTENT_TAG_EXPLORE;
		}
		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem search = menu.add(MENU_SEARCH_ID, MENU_SEARCH_ID, MENU_SEARCH_ID, "搜索项目");
		search.setIcon(R.drawable.abc_ic_search);
		MenuItemCompat.setShowAsAction(search, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == MENU_SEARCH_ID) {
			UIHelper.showSearch(mContext);
			return true;
		}
		return mDrawerToggle.onOptionsItemSelected(item)
				|| super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 判断菜单是否打开
			if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
				mDrawerLayout.closeDrawers();
				return true;
			}
			return mDoubleClickExitHelper.onKeyDown(keyCode, event);
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
				mDrawerLayout.closeDrawers();
				return true;
			} else {
				mDrawerLayout.openDrawer(Gravity.START);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 显示内容*/
	private void showMainContent(int pos) {
		mDrawerLayout.closeDrawers();
		String tag = CONTENTS[pos];
		if (tag.equalsIgnoreCase(mCurrentContentTag)) return;
		
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		if(mCurrentContentTag != null) {
			Fragment fragment = mFragmentManager.findFragmentByTag(mCurrentContentTag);
			if(fragment != null) {
				ft.remove(fragment);
			}
		}
		ft.replace(R.id.main_content, Fragment.instantiate(this, FRAGMENTS[pos]), tag);
		ft.commit();
		
		mActionBar.setTitle(TITLES[pos]);
		mTitle = TITLES[pos];//记录主界面的标题
		mCurrentContentTag = tag;
	}
	
	private void showLoginActivity() {
		if (!mContext.isLogin()) {
			Intent intent = new Intent(mContext, LoginActivity.class);
			startActivity(intent);
		} else {
			UIHelper.showMySelfInfoDetail(MainActivity.this);
		}
	}
	
	@Override
	public void onClickLogin() {
		showLoginActivity();
	}

	@Override
	public void onClickExplore() {
		showMainContent(0);
	}

	@Override
	public void onClickMySelf() {
		if (!mContext.isLogin()) {
			UIHelper.showLoginActivity(this);
			return;
		} else {
			showMainContent(1);
		}
	}

	@Override
	public void onClickNotice() {
		if (!mContext.isLogin()) {
			UIHelper.showLoginActivity(this);
			return;
		}
		showMainContent(2);
	}
	
	@Override
	public void onClickSetting() {
		Intent intent = new Intent(mContext, SettingActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClickExit() {
		this.finish();
	}
	
	private class DrawerMenuListener implements DrawerLayout.DrawerListener {
		@Override
		public void onDrawerOpened(View drawerView) {
			mDrawerToggle.onDrawerOpened(drawerView);
		}

		@Override
		public void onDrawerClosed(View drawerView) {
			mDrawerToggle.onDrawerClosed(drawerView);
		}

		@Override
		public void onDrawerSlide(View drawerView, float slideOffset) {
			mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
		}

		@Override
		public void onDrawerStateChanged(int newState) {
			mDrawerToggle.onDrawerStateChanged(newState);
		}
	}
}
