package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;
import android.widget.BaseAdapter;

import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.EventAdapter;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragmentOld;

import java.util.List;

/**
 * 用户最新动态列表Fragment
 * @created 2014-7-11 下午15:47
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class UserListEventFragment extends BaseSwipeRefreshFragmentOld<Event, CommonList<Event>> {
	
	private User mUser;
	
	public static UserListEventFragment newInstance(User user) {
		UserListEventFragment fragment = new UserListEventFragment();
		Bundle args = new Bundle();
		args.putSerializable(Contanst.USER, user);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mUser = (User) args.getSerializable(Contanst.USER);
		}
	}
	
	@Override
	public BaseAdapter getAdapter(List<Event> list) {
		return new EventAdapter(getActivity(), R.layout.list_item_event);
	}

	@Override
	public MessageData<CommonList<Event>> asyncLoadList(int page,
			boolean refresh) {
		MessageData<CommonList<Event>> msg = null;
		try {
			CommonList<Event> list = mApplication.getUserEvents(mUser.getId(), page);
			msg = new MessageData<CommonList<Event>>(list);
		} catch (AppException e) {
			e.makeToast(mApplication);
			e.printStackTrace();
			msg = new MessageData<CommonList<Event>>(e);
		}
		return msg;
	}

	@Override
	public void onItemClick(int position, Event event) {
		UIHelper.showEventDetail(mApplication, event);
	}

}
