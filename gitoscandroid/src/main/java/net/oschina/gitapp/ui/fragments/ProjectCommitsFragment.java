package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.CommonAdapter;
import net.oschina.gitapp.adapter.ProjectCommitAdapter;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;
import net.oschina.gitapp.util.JsonUtils;

import java.util.List;

/**
 * 项目commits列表Fragment
 * @created 2014-05-26
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class ProjectCommitsFragment extends BaseSwipeRefreshFragment<Commit> {
	
	private Project mProject;
	
	public static ProjectCommitsFragment newInstance(Project project) {
		ProjectCommitsFragment fragment = new ProjectCommitsFragment();
		Bundle args = new Bundle();
		args.putSerializable(Contanst.PROJECT, project);
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mProject = (Project) args.getSerializable(Contanst.PROJECT);
		}
		setUserVisibleHint(true);
	}

    @Override
    public CommonAdapter<Commit> getAdapter() {
        return new ProjectCommitAdapter(getActivity(), R.layout.list_item_projectcommit);
    }

    @Override
    public List<Commit> getDatas(byte[] responeString) {
        return JsonUtils.getList(Commit[].class, responeString);
    }

    @Override
    public void requestData() {
        GitOSCApi.getProjectCommits(mProject.getId(), mCurrentPage, "master", mHandler);
    }

	@Override
	public void onItemClick(int position, Commit commit) {
		UIHelper.showCommitDetail(getActivity(), mProject, commit);
	}
}
