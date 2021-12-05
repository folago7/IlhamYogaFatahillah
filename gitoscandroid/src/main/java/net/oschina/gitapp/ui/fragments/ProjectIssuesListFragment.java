package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;
import android.widget.BaseAdapter;

import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ProjectIssuesAdapter;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragmentOld;

import java.util.List;

/**
 * 项目commits列表Fragment
 * @created 2014-05-26
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class ProjectIssuesListFragment extends BaseSwipeRefreshFragmentOld<Issue, CommonList<Issue>> {
	
	public final int MENU_CREATE_ID = 01;
	
	private Project mProject;
	
	public static ProjectIssuesListFragment newInstance(Project project) {
		ProjectIssuesListFragment fragment = new ProjectIssuesListFragment();
		Bundle args = new Bundle();
		args.putSerializable(Contanst.PROJECT, project);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		Bundle args = getArguments();
		if (args != null) {
			mProject = (Project) args.getSerializable(Contanst.PROJECT);
		}
		super.setUserVisibleHint(true);
	}

	@Override
	public BaseAdapter getAdapter(List<Issue> list) {
		return new ProjectIssuesAdapter(getActivity(), R.layout.list_item_projectissues);
	}

	@Override
	public MessageData<CommonList<Issue>> asyncLoadList(int page,
			boolean reflash) {
		MessageData<CommonList<Issue>> msg = null;
		try {
			CommonList<Issue> list = mApplication.getProjectIssuesList(StringUtils.toInt(mProject.getId()), page, reflash);
			msg = new MessageData<CommonList<Issue>>(list);
		} catch (AppException e) {
			e.makeToast(mApplication);
			e.printStackTrace();
			msg = new MessageData<CommonList<Issue>>(e);
		}
		return msg;
	}
	
	@Override
	public void onItemClick(int position, Issue issue) {
		UIHelper.showIssueDetail(mApplication, mProject, issue, null, null);
	}
}
