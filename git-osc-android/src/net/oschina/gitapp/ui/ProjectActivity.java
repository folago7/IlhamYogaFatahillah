package net.oschina.gitapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.ui.fragments.ProjectViewPageFragment;

/**
 * 项目详情界面
 * @created 2014-05-26 上午10：26
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
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
		// 拿到传过来的project
		Intent intent = getIntent();
		Project p = (Project) intent.getSerializableExtra("project");
        if (null == savedInstanceState) {
        	FragmentTransaction ft = mFragmentManager.beginTransaction();
        	ft.replace(R.id.project_content, new ProjectViewPageFragment(p)).commit();
        }
	}
}
