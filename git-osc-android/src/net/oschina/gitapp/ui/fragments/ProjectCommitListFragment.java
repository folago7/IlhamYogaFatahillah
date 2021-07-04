package net.oschina.gitapp.ui.fragments;

import java.util.List;

import android.widget.BaseAdapter;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ExploreListProjectAdapter;
import net.oschina.gitapp.adapter.ProjectCommitListAdapter;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;

/**
 * 项目commits列表Fragment
 * @created 2014-05-26
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class ProjectCommitListFragment extends BaseSwipeRefreshFragment<Commit, CommonList<Commit>> {
	
	private Project project;
	
	public ProjectCommitListFragment(Project project) {
		this.project = project;
	}
	
	@Override
	public BaseAdapter getAdapter(List<Commit> list) {
		return new ProjectCommitListAdapter(getActivity(), list, R.layout.projectcommit_listitem);
	}

	@Override
	public MessageData<CommonList<Commit>> asyncLoadList(int page,
			boolean reflash) {
		MessageData<CommonList<Commit>> msg = null;
		try {
			CommonList<Commit> list = mApplication.getProjectCommitList(StringUtils.toInt(project.getId()), page, reflash);
			msg = new MessageData<CommonList<Commit>>(list);
		} catch (AppException e) {
			e.makeToast(mApplication);
			e.printStackTrace();
			msg = new MessageData<CommonList<Commit>>(e);
		}
		return msg;
	}
	
	@Override
	public void onItemClick(int position, Commit commit) {
		//UIHelper.showProjectDetail(getActivity(), project);
	}
}
