package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.BaseFragmentActivity;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.ui.basefragment.BaseFragment;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;

public class ProjectViewPageFragment extends BaseViewPagerFragment<BaseFragment> {
	
	private Project mProject;
	
	public static ProjectViewPageFragment newInstance(Project project) {
		ProjectViewPageFragment fragment = new ProjectViewPageFragment();
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
		String[] title = getResources().getStringArray(R.array.project_title_array);
		for (String t : title) {
			titleList.add(t);
		}
		
		fragmentList.add(ProjectCommitListFragment.newInstance(mProject));
		fragmentList.add(ProjectCodeTreeFragment.newInstance(mProject));
		// 是否可以pr
		if (mProject.isPullRequestsEnabled()) {
			fragmentList.add(ProjectCommitListFragment.newInstance(mProject));
		} else {
			titleList.remove(2);
		}
		
		// 是否可以接受issue
		if (mProject.isIssuesEnabled()) {
			fragmentList.add(ProjectIssuesListFragment.newInstance(mProject));
		} else {
			titleList.remove(3);
		}
	}
}
