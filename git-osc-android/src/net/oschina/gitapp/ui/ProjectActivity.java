package net.oschina.gitapp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;

/**
 * 项目详情界面
 * 
 * @created 2014-05-26 上午10：26
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * @最后更新：
 * @更新内容：
 * @更新者：
 */
public class ProjectActivity extends BaseActionBarActivity implements
		OnClickListener {
	
	private final int MENU_MORE_ID = 1;
	private final int MENU_CREATE_ID = 0;
	
	private final int ACTION_LOAD_PROJECT = 0;// 加载项目
	private final int ACTION_LOAD_PARENT_PROJECT = 1;// 加载项目的父项目信息
	
	private Bundle mSavedInstanceState;

	private Project mProject;
	
	private String projectId;

	private ProgressBar mLoading;
	
	private ScrollView mContent;
	
	private TextView mProjectName;
	
	private TextView mUpdateTime;
	
	private TextView mDescription;
	
	private TextView mLocked;
	
	private TextView mLanguage;
	
	private TextView mStarNum;
	
	private TextView mForkNum;
	
	private TextView mOwnerName;
	
	private View mForkView;
	
	private TextView mForkMes;
	
	private AppContext mAppContext;

	private LayoutInflater mInflater;
	
	private LinearLayout mLLOwner;
	private LinearLayout mLLReadMe;
	private LinearLayout mLLCodes;
	private LinearLayout mLLIssues;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
		mAppContext = getGitApplication();
		this.mSavedInstanceState = savedInstanceState;
		initView();
		mInflater = getLayoutInflater();
	}

	private void initView() {
		// 拿到传过来的project对象
		Intent intent = getIntent();
		mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
		projectId = intent.getStringExtra(Contanst.PROJECTID);

		mLoading = (ProgressBar) findViewById(R.id.project_detail_loading);
		mContent = (ScrollView) findViewById(R.id.project_content);
		mProjectName = (TextView) findViewById(R.id.project_name);
		mUpdateTime = (TextView) findViewById(R.id.project_update);
		mDescription = (TextView) findViewById(R.id.project_description);
		mLocked = (TextView) findViewById(R.id.project_locked);
		mLanguage = (TextView) findViewById(R.id.project_language);
		mStarNum = (TextView) findViewById(R.id.project_starnum);
		mForkNum = (TextView) findViewById(R.id.project_forknum);
		mOwnerName = (TextView) findViewById(R.id.project_ownername);
		mForkView = findViewById(R.id.project_ll_fork);
		mForkMes = (TextView) findViewById(R.id.project_fork_form);
		
		mLLOwner = (LinearLayout) findViewById(R.id.project_owner);
		mLLReadMe = (LinearLayout) findViewById(R.id.project_readme);
		mLLCodes = (LinearLayout) findViewById(R.id.project_issues);
		mLLIssues = (LinearLayout) findViewById(R.id.project_code);
		
		
		mLLOwner.setOnClickListener(this);
		mLLReadMe.setOnClickListener(this);
		mLLCodes.setOnClickListener(this);
		//findViewById(R.id.project_commits).setOnClickListener(this);
		mLLIssues.setOnClickListener(this);
		
		if (null == mProject) {
			loadProject(ACTION_LOAD_PROJECT, projectId);
		} else {
			initData();
		}
	}

	private void initData() {
		mActionBar.setTitle(mProject.getName());
		mActionBar.setSubtitle(mProject.getOwner().getName());
		
		mProjectName.setText(mProject.getName());
		mUpdateTime.setText("更新于 " + getUpdateTime());
		
		mDescription.setText(getDescription(mProject.getDescription()));
		mLocked.setText(getLocked());
		mLanguage.setText(getLanguage());
		mStarNum.setText(mProject.getStars_count() + "");
		mForkNum.setText(mProject.getForks_count() + "");
		mOwnerName.setText(mProject.getOwner().getName());
		
		// 显示是否有fork信息
		initForkMess();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem createOption = menu.add(0, MENU_CREATE_ID, MENU_CREATE_ID, "创建Issue");
		createOption.setIcon(R.drawable.action_create);
		MenuItemCompat.setShowAsAction(createOption, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		
		/*MenuItem moreOption = menu.add(1, MENU_MORE_ID, MENU_MORE_ID, "更多");
		moreOption.setIcon(R.drawable.abc_ic_menu_moreoverflow_normal_holo_dark);
		
		MenuItemCompat.setShowAsAction(moreOption,
				MenuItemCompat.SHOW_AS_ACTION_ALWAYS);*/
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case MENU_MORE_ID:
			break;
		case MENU_CREATE_ID:
			// 新增issue
			UIHelper.showIssueEditOrCreate(getGitApplication(), mProject, null);
			break;
		}
		return super.onOptionsItemSelected(item); 
	}

	private String getUpdateTime() {
		if (mProject.getLast_push_at() !=null) {
			return StringUtils.friendly_time(mProject.getLast_push_at());
		} else {
			return StringUtils.friendly_time(mProject.getCreatedAt());
		}
	}
	
	private String getDescription(String description) {
		if (description == null || StringUtils.isEmpty(description)) {
			return "该项目暂无简介！";
		} else {
			return description;
		}
	}
	
	private String getLocked() {
		if (mProject.isPublic()) {
			return "Public";
		} else {
			return "Private";
		}
	}
	
	private String getLanguage() {
		if (mProject.getLanguage() == null) {
			return "未指定";
		} else {
			return mProject.getLanguage();
		}
	}
	
	private void initForkMess() {
		if (mProject.getParent_id() == null) {
			return;
		} else {
			mForkView.setVisibility(View.VISIBLE);
			findViewById(R.id.project_fork_ll_line).setVisibility(View.VISIBLE);
			loadProject(ACTION_LOAD_PARENT_PROJECT, mProject.getParent_id() + "");
			mForkView.setOnClickListener(this);
		}
	}

	private void loadProject(final int action, final String projectId) {
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
				if (action == ACTION_LOAD_PROJECT) {
					mLoading.setVisibility(View.VISIBLE);
					mContent.setVisibility(View.GONE);
				}
			}

			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				if (action == ACTION_LOAD_PROJECT) {
					mLoading.setVisibility(View.GONE);
					mContent.setVisibility(View.VISIBLE);
				}
				if (msg != null) {
					if (msg.what == 1) {
						if (action == ACTION_LOAD_PROJECT) {
							mProject = (Project) msg.obj;
							initData();
						} else {
							Project p = (Project) msg.obj;
							if (p != null) {
								mForkMes.setText(p.getOwner().getName() + " / " + p.getName());
							}
						}
					} else {
						if (action == ACTION_LOAD_PROJECT) {
							((AppException) msg.obj).makeToast(mAppContext);
						}
					}
				}
			}

		}.execute();
	}

	@Override
	public void onClick(View v) {
		
		if (mProject == null ) {
			return;
		}
		
		int id = v.getId();
		switch (id) {
		case R.id.project_owner:
			if (mProject.getOwner() != null) {
				UIHelper.showUserInfoDetail(ProjectActivity.this, mProject.getOwner(), mProject.getOwner().getId());
			}
			break;
		case R.id.project_ll_fork:
			if (mProject.getParent_id() != null) {
				UIHelper.showProjectDetail(ProjectActivity.this, null, mProject.getParent_id() + "");
			}
			break;
		case R.id.project_readme:
			UIHelper.showProjectReadMeActivity(ProjectActivity.this, mProject);
			break;
		case R.id.project_code:
			UIHelper.showProjectCodeActivity(ProjectActivity.this, mProject);
			break;
		case R.id.project_commits:
			UIHelper.showProjectListActivity(ProjectActivity.this, mProject, ProjectSomeInfoListActivity.PROJECT_LIST_TYPE_COMMITS);
			break;
		case R.id.project_issues:
			UIHelper.showProjectListActivity(ProjectActivity.this, mProject, ProjectSomeInfoListActivity.PROJECT_LIST_TYPE_ISSUES);
			break;
		default:
			break;
		}
	}
}
