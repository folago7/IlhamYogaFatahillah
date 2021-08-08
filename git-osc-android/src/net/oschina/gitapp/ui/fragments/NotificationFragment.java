package net.oschina.gitapp.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.NotificationListAdapter;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Notification;
import net.oschina.gitapp.bean.ProjectNotification;
import net.oschina.gitapp.bean.ProjectNotificationArray;
import net.oschina.gitapp.ui.DrawerNavigation;
import net.oschina.gitapp.ui.basefragment.BaseFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 通知页面
 * @created 2014-04-29
 * @author 火蚁（http://my.oschina.net/LittleDY）
 */
public class NotificationFragment extends BaseFragment implements OnClickListener {
	
	private final int MENU_REFRESH_ID = 1;
	
	private ProgressBar mProgressBar;
	
	private View mEmpty;
	
	private ListView mList;
	
	private View mReaded;
	
	private List<Notification> mData;
	
	private AppContext mAppContext;
	
	private NotificationListAdapter adapter;
	
    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.notification_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView(view);
		mAppContext = getGitApplication();
		mData = new ArrayList<Notification>();
		adapter = new NotificationListAdapter(mAppContext, mData, R.layout.notification_listitem);
		mList.setAdapter(adapter);
		loadData();
	}
	
	private void initView(View view) {
		mProgressBar = (ProgressBar) view.findViewById(R.id.notification_fragment_loading);
		mEmpty = view.findViewById(R.id.notification_fragment_empty);
		mList = (ListView) view.findViewById(R.id.notification_fragment_list);
		mReaded = view.findViewById(R.id.notification_fragment_readed);
		
		mReaded.setOnClickListener(this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem createOption = menu.add(0, MENU_REFRESH_ID, MENU_REFRESH_ID, "刷新");
		createOption.setIcon(R.drawable.abc_ic_menu_refresh);
		
		MenuItemCompat.setShowAsAction(createOption, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int menuId = item.getItemId();
		switch (menuId) {
		case MENU_REFRESH_ID:
			loadData();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void loadData() {
		new AsyncTask<Void, Void, Message>() {
			
			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					CommonList<ProjectNotificationArray> commonList = mAppContext.getNotification("", "1", "");
					msg.what = 1;
					msg.obj = commonList;
				} catch (AppException e) {
					msg.what = -1;
					msg.obj = e;
					e.printStackTrace();
				}
				return msg;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mProgressBar.setVisibility(View.VISIBLE);
				mList.setVisibility(View.GONE);
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void onPostExecute(Message msg) {
				mData.clear();
				mProgressBar.setVisibility(View.GONE);
				if (msg.what == 1) {
					CommonList<ProjectNotificationArray> commonList = (CommonList<ProjectNotificationArray>) msg.obj;
					Log.i("Test", commonList.getList().size() + "");
					for (ProjectNotificationArray pna : commonList.getList()) {
						for (Notification n : pna.getProject().getNotifications()) {
							mData.add(n);
						}
					}
					if (commonList.getList().size() != 0) {
						mEmpty.setVisibility(View.GONE);
						DrawerNavigation.mNotification_bv.setText(mData.size() + "");
						DrawerNavigation.mNotification_bv.setVisibility(View.VISIBLE);
					} else {
						mEmpty.setVisibility(View.VISIBLE);
					}
					adapter.notifyDataSetChanged();
					mList.setVisibility(View.VISIBLE);
				}
			}
		}.execute();
	}

	@Override
	public void onClick(View v) {
	}
}
