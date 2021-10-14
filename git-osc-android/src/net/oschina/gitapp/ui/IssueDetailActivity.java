package net.oschina.gitapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.ui.fragments.IssueDetailViewPagerFragment;

/**
 * issue详情activity
 * @created 2014-08-25
 * @author 火蚁(http://my.oschina.net/LittleDY)
 *
 */
public class IssueDetailActivity extends BaseActionBarActivity {

	private FragmentManager mFragmentManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_detail);
		init(savedInstanceState);
	}
	
	private void init(Bundle savedInstanceState) {
		mFragmentManager = getSupportFragmentManager();
		Intent intent = getIntent();
		Project mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
		Issue mIssue = (Issue) intent.getSerializableExtra(Contanst.ISSUE);
		String title = "Issue " + (mIssue.getIid() == 0 ? "" :  "#" + mIssue.getIid());
		mActionBar.setTitle(title);
		mActionBar.setSubtitle(mProject.getOwner().getName() + "/"
				+ mProject.getName());
		
        if (null == savedInstanceState) {
        	FragmentTransaction ft = mFragmentManager.beginTransaction();
        	ft.replace(R.id.issue_content, IssueDetailViewPagerFragment.newInstance(mProject, mIssue)).commit();
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
}
