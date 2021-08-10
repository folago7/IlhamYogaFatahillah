package net.oschina.gitapp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
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
	
	private Bundle mSavedInstanceState;
	
	private Project mProject;
	
	private String projectId;
	
	private ProgressBar mLoading;
	
	private AppContext mAppContext;
	
	private int mCurrentItem = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
		mAppContext = getGitApplication();
		this.mSavedInstanceState = savedInstanceState;
		initView();
	}
	
	private void initView() {
		mFragmentManager = getSupportFragmentManager();
		// 拿到传过来的project对象
		Intent intent = getIntent();
		mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
		projectId = intent.getStringExtra(Contanst.PROJECTID);
		mCurrentItem = intent.getIntExtra(Contanst.CURRENTITEM, 0);

		mLoading = (ProgressBar) findViewById(R.id.project_detail_loading);
		
		if (null == mProject) {
			loadProject();
		} else {
			initViewPage();
		}
	}
	
	private void initViewPage() {
		mActionBar.setTitle(mProject.getName());
		mActionBar.setSubtitle(mProject.getOwner().getName());
		if (null == mSavedInstanceState) {
        	FragmentTransaction ft = mFragmentManager.beginTransaction();
        	ft.replace(R.id.project_content, ProjectViewPageFragment.newInstance(mProject, mCurrentItem)).commit();
        }
	}
	
	private void loadProject() {
    	new AsyncTask<Void, Void, Message>() {

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				
				try {
					msg.obj = mAppContext.getProject(projectId);
					msg.what = 1;
				} catch (AppException e) {
					mLoading.setVisibility(View.GONE);
					msg.what = -1;
					msg.obj = e;
					e.printStackTrace();
				}
				
				return msg;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mLoading.setVisibility(View.VISIBLE);
			}

			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				mLoading.setVisibility(View.GONE);
				if (msg != null) {
					if (msg.what == 1) {
						mProject = (Project) msg.obj;
						initViewPage();
					} else {
						((AppException)msg.obj).makeToast(mAppContext);
					}
				}
			}
    		
    	}.execute();
	}
}
