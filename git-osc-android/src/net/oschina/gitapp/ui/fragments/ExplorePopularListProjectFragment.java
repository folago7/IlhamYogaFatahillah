package net.oschina.gitapp.ui.fragments;

import java.util.List;

import android.util.Log;
import android.widget.BaseAdapter;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ExploreListProjectAdapter;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.bean.ProjectList;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;

/**
 * 发现页面热门项目列表Fragment
 * @created 2014-05-19 上午10：44
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class ExplorePopularListProjectFragment extends BaseSwipeRefreshFragment<Project, ProjectList> {
		
	public static ExplorePopularListProjectFragment newInstance() {
		return new ExplorePopularListProjectFragment();
	}
	
	@Override
	public BaseAdapter getAdapter(List<Project> list) {
		return new ExploreListProjectAdapter(getActivity(), list, R.layout.exploreproject_listitem);
	}

	@Override
	public MessageData<ProjectList> asyncLoadList(int page,
			boolean reflash) {
		MessageData<ProjectList> msg = null;
		try {
			ProjectList list = mApplication.getExplorePopularProject(page, reflash);
			msg = new MessageData<ProjectList>(list);
		} catch (AppException e) {
			e.makeToast(mApplication);
			e.printStackTrace();
			msg = new MessageData<ProjectList>(e);
		}
		return msg;
	}
	
	@Override
	public void onItemClick(int position, Project project) {
		UIHelper.showProjectDetail(getActivity(), project);
	}
}
