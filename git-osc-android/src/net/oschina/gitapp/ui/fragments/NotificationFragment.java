package net.oschina.gitapp.ui.fragments;

import java.util.List;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.ProjectNotification;
import net.oschina.gitapp.ui.basefragment.BaseFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 通知页面
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-29
 */
public class NotificationFragment extends BaseFragment {
	
	private final int MENU_REFRESH_ID = 1;
	
	private View mEmpty;
	
	private ListView mList;
	
	private View mReaded;
	
	private List<ProjectNotification> mData;
	
	private AppContext mAppContext;
	
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
	}
	
	private void initView(View view) {
		mEmpty = view.findViewById(R.id.notification_fragment_empty);
		mList = (ListView) view.findViewById(R.id.notification_fragment_list);
		mReaded = view.findViewById(R.id.notification_fragment_readed);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem createOption = menu.add(0, MENU_REFRESH_ID, MENU_REFRESH_ID, "刷新");
		createOption.setIcon(R.drawable.abc_ic_menu_refresh);
		
		MenuItemCompat.setShowAsAction(createOption, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
	}
	
	private void loadData() {
		new AsyncTask<Void, Void, Message>() {

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					CommonList<ProjectNotification> commonList = mAppContext.getNotification("", "", "");
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
			}

			@Override
			protected void onPostExecute(Message msg) {
				if (msg.what == 1) {
					CommonList<ProjectNotification> commonList = (CommonList<ProjectNotification>) msg.obj;
					if (commonList.getList().size() != 0) {
						mEmpty.setVisibility(View.VISIBLE);
					} else {
						mEmpty.setVisibility(View.GONE);
					}
				}
			}
		}.execute();
	}
}
