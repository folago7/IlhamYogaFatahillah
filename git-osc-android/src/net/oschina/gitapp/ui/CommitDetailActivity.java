package net.oschina.gitapp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
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

/**
 * commit详情
 * 
 * @created 2014-06-12
 * @author 火蚁
 *
 */
public class CommitDetailActivity extends BaseActionBarActivity implements
		OnStatusListener {
	
	private AppContext mAppContext;
	
	private Commit mCommit;
	
	private Project mProject;
	
	private ImageView mCommitAuthorFace;
	
	private TextView mCommitAuthorName;
	
	private TextView mCommitDate;
	
	private TextView mCmmitMessage;
	
	private View mLoading;
	
	private TextView mCommitFileSum;
	
	private LinearLayout mCommitDiffll;
	
	private CommonList<CommitDiff> mCommitDiffList = new CommonList<CommitDiff>();
	
	private CommitDiffListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commit_detail_file_view);
		mAppContext = getGitApplication();
		initView();
		initData();
	}

	private void initView() {
		
		mCommitAuthorFace = (ImageView) findViewById(R.id.commit_author_face);
		
		mCommitAuthorName = (TextView) findViewById(R.id.commit_author_name);
		
		mCommitDate = (TextView) findViewById(R.id.commit_date);
		
		mCmmitMessage = (TextView) findViewById(R.id.commit_message);
		
		mLoading = findViewById(R.id.commit_diff_ll_loading);
		
		mCommitFileSum = (TextView) findViewById(R.id.commit_diff_changefile_sum);
		
		mCommitDiffll = (LinearLayout) findViewById(R.id.commit_diff_file_list);
	}
	
	private void initData() {
		Intent intent = getIntent();
		mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
		mCommit = (Commit) intent.getSerializableExtra(Contanst.COMMIT);
		
		mCommitAuthorName.setText(mCommit.getAuthor_name());
		
		mCommitDate.setText("提交于" + StringUtils.friendly_time(mCommit.getCreatedAt()));
		mActionBar.setTitle(mCommit.getShortId());
		mActionBar.setSubtitle(mProject.getOwner().getName() + "/" + mProject.getName());
		mCmmitMessage.setText(mCommit.getTitle());
		loadAuthorFace();
		loadDatasCode(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void onStatus(int status) {
		
	}
	
	// 加载头像
	private void loadAuthorFace() {
		new Thread(){
			public void run() {
				UIHelper.showUserFace(mCommitAuthorFace, mCommit.getAuthor().getPortrait());
			}
		}.start();
	}

	private void loadDatasCode(final boolean isRefresh) {
		onStatus(STATUS_LOADING);
		new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					AppContext ac = getGitApplication();
					CommonList<CommitDiff> commitDiffList = ac.getCommitDiffList(mProject.getId(), mCommit.getId(), isRefresh);
					msg.what = 1;
					msg.obj = commitDiffList;
				} catch (Exception e) {
					msg.what = -1;
					msg.obj = e;
				}
				return msg;
			}

			@Override
			protected void onPreExecute() {
				
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void onPostExecute(Message msg) {
				if (msg.what == 1 && msg.obj != null) {
					onStatus(STATUS_LOADED);
					mLoading.setVisibility(View.GONE);
					mCommitDiffList = (CommonList<CommitDiff>) msg.obj;
					mCommitFileSum.setText(mCommitDiffList.getCount() + " 个文件发生了变化");
					adapter = new CommitDiffListAdapter(mAppContext, mCommitDiffList.getList(), R.layout.commit_diff_listitem, mCommitDiffll);
					adapter.setData(mProject, mCommit);
					mCommitDiffll.setVisibility(View.VISIBLE);
					adapter.notifyDataSetChanged();
				} else {
					((AppException)msg.obj).makeToast(getActivity());
					onStatus(STATUS_NONE);
				}
			}
		}.execute();
	}
}
