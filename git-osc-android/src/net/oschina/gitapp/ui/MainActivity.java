package net.oschina.gitapp.ui;

import net.oschina.gitapp.R;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import net.oschina.gitapp.common.DoubleClickExitHelper;
import net.oschina.gitapp.interfaces.*;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.ui.fragments.ExploreViewPagerFragment;

/**
 * 程序主界面
 * @created 201-04-29
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 */
public class MainActivity extends BaseActionBarActivity {
	
    protected static ToggleListener sToggleListener;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager mFragmentManager;
    private DoubleClickExitHelper mDoubleClickExitHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView(savedInstanceState);
    }
    
    private void initView(Bundle savedInstanceState) {
    	
    	mDoubleClickExitHelper = new DoubleClickExitHelper(this);
        ActionBar actionBar = getSupportActionBar();
        sToggleListener = new ToggleListener();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                R.drawable.ic_navigation_drawer,
                R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
                DrawerNavigation.sNavigationTransactionListener.onDrawerClosed();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
                DrawerNavigation.sNavigationTransactionListener.onDrawerOpened();
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mFragmentManager = getSupportFragmentManager();
        if (null == savedInstanceState) {
        	FragmentTransaction ft = mFragmentManager.beginTransaction();
        	ft.replace(R.id.main_slidingmenu_frame, new DrawerNavigation())
        		.replace(R.id.main_content, new ExploreViewPagerFragment())
            	.commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			//判断菜单是否打开
			if(mDrawerLayout.isDrawerOpen(Gravity.START)) {
				mDrawerLayout.closeDrawers();
				return true;
			}
			return mDoubleClickExitHelper.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

    @Override
    protected void onDestroy() {
        // Workaround for Crouton issue #24 (https://github.com/keyboardsurfer/Crouton/issues/24).
        super.onDestroy();
    }

    public final class ToggleListener implements NavigationInterfaces.SlidingMenuListener {

        @Override
        public void onShowAbove() {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mDrawerToggle.syncState();
        }
    }
}
