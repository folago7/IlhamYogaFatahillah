package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ViewPageFragmentAdapter;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;

public class ProjectViewPageFragment extends BaseViewPagerFragment {
	
	private Project mProject;
	
	public static ProjectViewPageFragment newInstance(Project project) {
		ProjectViewPageFragment fragment = new ProjectViewPageFragment();
		Bundle args = new Bundle();
		args.putSerializable(Contanst.PROJECT, project);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		Bundle args = getArguments();
		if (args != null) {
			mProject = (Project) args.getSerializable(Contanst.PROJECT);
		}
		String[] title = getResources().getStringArray(R.array.project_title_array);
		
		adapter.addTab(title[0], "project_commitlist", ProjectCommitListFragment.class, args);
		adapter.addTab(title[1], "project_codetree", ProjectCodeTreeFragment.class, args);
		
		// 是否可以pr
		if (mProject.isPullRequestsEnabled()) {
			//adapter.addTab(title[2], "project_pullrequest", ProjectCodeTreeFragment.class, args);
		}
		
		// 是否可以接受issue
		if (mProject.isIssuesEnabled()) {
			adapter.addTab(title[3], "project_issuelist", ProjectIssuesListFragment.class, args);
		}
	}
}
