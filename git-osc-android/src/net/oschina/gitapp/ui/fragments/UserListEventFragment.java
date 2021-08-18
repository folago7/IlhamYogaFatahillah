package net.oschina.gitapp.ui.fragments;

import java.util.List;

import android.widget.BaseAdapter;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.MySelfEventListAdapter;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;

/**
 * 用户最新动态列表Fragment
 * @created 2014-7-11 下午15:47
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class UserListEventFragment extends BaseSwipeRefreshFragment<Event, CommonList<Event>> {
	
	public static UserListEventFragment newInstance() {
		return new UserListEventFragment();
	}
	
	@Override
	public BaseAdapter getAdapter(List<Event> list) {
		return new MySelfEventListAdapter(getActivity(), list, R.layout.myselfevent_listitem);
	}

	@Override
	public MessageData<CommonList<Event>> asyncLoadList(int page,
			boolean refresh) {
		MessageData<CommonList<Event>> msg = null;
		try {
			CommonList<Event> list = mApplication.getMySelfEvents(page, refresh);
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
		showEventDetail(event);
	}
	
	private void showEventDetail(Event event) {
		
		if ((event.getAction() == Event.EVENT_TYPE_COMMENTED && event.getTarget_type().equalsIgnoreCase("issue"))
				|| (event.getAction() == Event.EVENT_TYPE_CREATED && event.getTarget_type().equalsIgnoreCase("issue"))) {
			event.getProject().setIssuesEnabled(true);
			UIHelper.showProjectDetail(mApplication, event.getProject(), null, 2);
		} else {
			UIHelper.showProjectDetail(mApplication, event.getProject(), null, 0);
		}
	}
}
