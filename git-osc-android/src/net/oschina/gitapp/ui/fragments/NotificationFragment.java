package net.oschina.gitapp.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.NotificationListAdapter;
import net.oschina.gitapp.adapter.NotificationListAdapter1;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Notification;
import net.oschina.gitapp.bean.NotificationReadResult;
import net.oschina.gitapp.bean.ProjectNotification;
import net.oschina.gitapp.bean.ProjectNotificationArray;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.DrawerNavigation;
import net.oschina.gitapp.ui.basefragment.BaseFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 通知页面
 * @created 2014-07-08
 * @author 火蚁（http://my.oschina.net/LittleDY）
 */
public class NotificationFragment extends BaseFragment implements OnClickListener, OnChildClickListener {
	
	private final int MENU_REFRESH_ID = 1;
	
	private final int ACTION_UNREAD = 0;//未读
	
	private final int ACTION_READED = 1;//已读
	
	
	private int mDefaultAction = ACTION_UNREAD;//默认动作为加载未读
	
	private ProgressBar mProgressBar;
	
	private View mEmpty;
	
	private ExpandableListView mUnReadListView;
	
	private ExpandableListView mReadedListView;
	
	private View mReaded;
	
	private List<List<Notification>> mData;
	
	private List<String> mGroupStrings;
	
	private AppContext mAppContext;
	
	private NotificationListAdapter1 adapter;
	
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
		mAppContext = getGitApplication();
		initView(view);
		steupList();
		loadData("", "", "");
	}
	
	private void initView(View view) {
		mProgressBar = (ProgressBar) view.findViewById(R.id.notification_fragment_loading);
		mEmpty = view.findViewById(R.id.notification_fragment_empty);
		mUnReadListView = (ExpandableListView) view.findViewById(R.id.notification_fragment_unread_list);
		mReadedListView = (ExpandableListView) view.findViewById(R.id.notification_fragment_readed_list);
		mReaded = view.findViewById(R.id.notification_fragment_readed);
		
		mReaded.setOnClickListener(this);
	}
	
	private void steupList() {
		mData = new ArrayList<List<Notification>>();
		mGroupStrings = new ArrayList<String>();
		adapter = new NotificationListAdapter1(mAppContext, mData, mGroupStrings);
		mUnReadListView.setAdapter(adapter);
		mReadedListView.setAdapter(adapter);
		
		mUnReadListView.setOnChildClickListener(this);
		mReadedListView.setOnChildClickListener(this);
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
			String all = mDefaultAction == ACTION_UNREAD ? "" : "1";
			loadData("", all ,"");
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void beforeLoading() {
		mEmpty.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
		if (mDefaultAction == ACTION_UNREAD) {
			
			mUnReadListView.setVisibility(View.INVISIBLE);
			mReadedListView.setVisibility(View.GONE);
			
		} else if (mDefaultAction == ACTION_READED) {
			
			mReadedListView.setVisibility(View.INVISIBLE);
			mUnReadListView.setVisibility(View.GONE);
			
		}
	}
	
	private void afterLoading() {
		mProgressBar.setVisibility(View.GONE);
		if (mDefaultAction == ACTION_UNREAD) {
			
			mUnReadListView.setVisibility(View.VISIBLE);
			
			if (mData.size() == 0) {
				mEmpty.setVisibility(View.VISIBLE);
			} else {
				mUnReadListView.expandGroup(0);
				mEmpty.setVisibility(View.GONE);
			}
			
		} else if (mDefaultAction == ACTION_READED) {
			
			mUnReadListView.setVisibility(View.GONE);
			mReadedListView.setVisibility(View.VISIBLE);
			if (mData.size() == 0) {
				mEmpty.setVisibility(View.VISIBLE);
			} else {
				mReadedListView.expandGroup(0);
				mEmpty.setVisibility(View.GONE);
			}
		}
	}

	private void loadData(final String filter, final String all, final String project_id) {
		new AsyncTask<Void, Void, Message>() {
			
			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					CommonList<ProjectNotificationArray> commonList = mAppContext.getNotification(filter, all, project_id);
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
				beforeLoading();
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void onPostExecute(Message msg) {
				mData.clear();
				if (msg.what == 1) {
					CommonList<ProjectNotificationArray> commonList = (CommonList<ProjectNotificationArray>) msg.obj;
					
					if (commonList.getList().size() != 0) {
						mEmpty.setVisibility(View.GONE);
						for (ProjectNotificationArray pna : commonList.getList()) {
							mGroupStrings.add(pna.getProject().getOwner().getName() + "/" + pna.getProject().getName());
							List<Notification> ns = new ArrayList<Notification>();
							ns.addAll(pna.getProject().getNotifications());
							mData.add(ns);
						}
					}
					adapter.notifyDataSetChanged();
					afterLoading();
				}
			}
		}.execute();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.notification_fragment_readed:
			if (mReaded.getTag() != null) {
				mDefaultAction = ACTION_READED;
				loadData("", "1", "");
				mReaded.setTag(null);
			} else {
				mDefaultAction = ACTION_UNREAD;
				loadData("", "", "");
				mReaded.setTag("1");
			}
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		final Notification notification = adapter.getChild(groupPosition, childPosition);
		if (notification != null) {
			// 设置未读通知为已读
			if (!notification.isRead()) {
				new Thread(){
					public void run() {
						try {
							mAppContext.setNotificationIsRead(notification.getId());
						} catch (AppException e) {
							e.printStackTrace();
						}
					}
				}.start();;
			}
			if (notification.getTarget_type().equalsIgnoreCase("Issue")) {
				UIHelper.showProjectDetail(mAppContext, null, notification.getProject_id(), 2);
			} else {
				UIHelper.showProjectDetail(mAppContext, null, notification.getProject_id(), 0);
			}
		}
		return false;
	}
}
