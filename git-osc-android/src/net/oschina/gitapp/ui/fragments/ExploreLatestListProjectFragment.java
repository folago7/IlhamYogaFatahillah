package net.oschina.gitapp.ui.fragments;

import java.util.List;

import android.widget.BaseAdapter;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ExploreListProjectAdapter;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.bean.ProjectList;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;

/**
 * 发现页面最新项目列表Fragment
 * @created 2014-05-14 下午16:57
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class ExploreLatestListProjectFragment extends BaseSwipeRefreshFragment<Project, ProjectList> {
		
	public static ExploreLatestListProjectFragment newInstance() {
		return new ExploreLatestListProjectFragment();
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
			ProjectList list = mApplication.getExploreLatestProject(page, reflash);
			msg = new MessageData<ProjectList>(list);
		} catch (AppException e) {
			e.makeToast(mApplication);
			e.printStackTrace();
			msg = new MessageData<ProjectList>(e);
		}
		return msg;
	}
}
