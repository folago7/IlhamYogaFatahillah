package net.oschina.gitapp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.CommitDiffListAdapter;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.CommitDiff;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.interfaces.OnStatusListener;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.ui.fragments.CommitDetailViewPagerFragment;
import net.oschina.gitapp.ui.fragments.ProjectViewPageFragment;

/**
 * commit详情
 * 
 * @created 2014-06-12
 * @author 火蚁
 *
 */
public class CommitDetailActivity extends BaseActionBarActivity implements
		OnStatusListener {
	
	private Menu optionMenu;
	
	private FragmentManager mFragmentManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commit_detail);
		init(savedInstanceState);
	}
	
	private void init(Bundle savedInstanceState) {
		mFragmentManager = getSupportFragmentManager();
		Intent intent = getIntent();
		Project mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
		Commit mCommit = (Commit) intent.getSerializableExtra(Contanst.COMMIT);
		mActionBar.setTitle(mCommit.getShortId());
		mActionBar.setSubtitle(mProject.getOwner().getName() + "/" + mProject.getName());
		
        if (null == savedInstanceState) {
        	FragmentTransaction ft = mFragmentManager.beginTransaction();
        	ft.replace(R.id.commit_content, CommitDetailViewPagerFragment.newInstance(mProject, mCommit)).commit();
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		optionMenu = menu;
		
		return true;
	}

	@Override
	public void onStatus(int status) {
		
	}
}
