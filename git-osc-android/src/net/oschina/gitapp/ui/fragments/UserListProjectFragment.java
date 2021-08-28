package net.oschina.gitapp.ui.fragments;

import java.util.List;

import android.widget.BaseAdapter;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ExploreListProjectAdapter;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;

/**
 * 发现页面推荐项目列表Fragment
 * @created 2014-05-19 上午10：43
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class UserListProjectFragment extends BaseSwipeRefreshFragment<Project, CommonList<Project>> {
		
	public static UserListProjectFragment newInstance() {
		return new UserListProjectFragment();
	}
	
	@Override
	public BaseAdapter getAdapter(List<Project> list) {
		return new ExploreListProjectAdapter(getActivity(), list, R.layout.exploreproject_listitem);
	}

	@Override
	public MessageData<CommonList<Project>> asyncLoadList(int page,
			boolean refresh) {
		MessageData<CommonList<Project>> msg = null;
		try {
			CommonList<Project> list = mApplication.getExploreFeaturedProject(page, refresh);
			msg = new MessageData<CommonList<Project>>(list);
		} catch (AppException e) {
			e.makeToast(mApplication);
			e.printStackTrace();
			msg = new MessageData<CommonList<Project>>(e);
		}
		return msg;
	}
	
	@Override
	public void onItemClick(int position, Project project) {
		UIHelper.showProjectDetail(getActivity(), project, project.getId());
	}
}