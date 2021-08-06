package net.oschina.gitapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.ui.fragments.ProjectViewPageFragment;

/**
 * 项目详情界面
 * @created 2014-05-26 上午10：26
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新内容：
 * 更新者：
 */
public class ProjectActivity extends BaseActionBarActivity {
	
	private FragmentManager mFragmentManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
		initView(savedInstanceState);
	}
	
	private void initView(Bundle savedInstanceState) {
		mFragmentManager = getSupportFragmentManager();
		// 拿到传过来的project对象
		Intent intent = getIntent();
		Project project = (Project) intent.getSerializableExtra(Contanst.PROJECT);
		mActionBar.setTitle(project.getName());
		mActionBar.setSubtitle(project.getOwner().getName());
		
        if (null == savedInstanceState) {
        	FragmentTransaction ft = mFragmentManager.beginTransaction();
        	ft.replace(R.id.project_content, ProjectViewPageFragment.newInstance(project)).commit();
        }
	}
}
