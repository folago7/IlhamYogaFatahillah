package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.ui.BaseFragmentActivity;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.ui.basefragment.BaseFragment;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;

public class ProjectViewPageFragment extends BaseViewPagerFragment<BaseFragment> {
	
	private Project _project;
	
	public ProjectViewPageFragment(Project project) {
		this._project = project;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] title = getResources().getStringArray(R.array.project_title_array);
		for (String t : title) {
			titleList.add(t);
		}
		fragmentList.add(new ProjectCommitListFragment(_project));
		fragmentList.add(new ProjectCommitListFragment(_project));
		fragmentList.add(new ProjectCommitListFragment(_project));
		fragmentList.add(new ProjectCommitListFragment(_project));
	}
}
