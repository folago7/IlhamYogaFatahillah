package net.oschina.gitapp.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.IssueListCommentAdapter;
import net.oschina.gitapp.api.HTTPRequestor;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.GitNote;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.BroadcastController;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.DataRequestThreadHandler;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.widget.CBSwipeRefreshLayout;

public class IssueDetailActivity extends BaseActionBarActivity implements
		SwipeRefreshLayout.OnRefreshListener, OnClickListener,
		AbsListView.OnScrollListener {

	// 没有状态
	public static final int LISTVIEW_ACTION_NONE = -1;
	// 更新状态，不显示toast
	public static final int LISTVIEW_ACTION_UPDATE = 0;
	// 初始化时，加载缓存状态
	public static final int LISTVIEW_ACTION_INIT = 1;
	// 刷新状态，显示toast
	public static final int LISTVIEW_ACTION_REFRESH = 2;
	// 下拉到底部时，获取下一页的状态
	public static final int LISTVIEW_ACTION_SCROLL = 3;

	private LayoutInflater mInflater;

	private Issue mIssue;

	private Project mProject;

	private View mHeadView;

	private TextView mIssueTitle;

	private TextView mIssueUserName;

	private ImageView mIssueUserFace;

	private TextView mIssueData;

	private WebView mWebView;

	private CBSwipeRefreshLayout mSwipeRefreshLayout;

	private ListView mList;

	private View mListFooter;

	private TextView mListFooterTextView;

	private View mListFooterProgressBar;

	private EditText mCommentContent;

	private Button mCommentPub;

	private IssueListCommentAdapter adapter;

	private List<GitNote> mListData = new ArrayList<GitNote>();

	private DataRequestThreadHandler mRequestThreadHandler = new DataRequestThreadHandler();
	
	private TextWatcher mCommtentWatcher;
	
	private ProgressDialog mProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_detail);
		init();
		initHead();
		mActionBar.setTitle("Issue");
		mActionBar.setSubtitle(mProject.getOwner().getName() + "/"
				+ mProject.getName());
		steupList();
		loadComment(1, LISTVIEW_ACTION_INIT);
	}

	private void init() {
		Intent intent = getIntent();
		mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
		mIssue = (Issue) intent.getSerializableExtra(Contanst.ISSUE);

		mInflater = getLayoutInflater();
		mHeadView = mInflater.inflate(R.layout.activity_issue_detail_header,
				null);

		mSwipeRefreshLayout = (CBSwipeRefreshLayout) findViewById(R.id.issue_swiperefreshlayout);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorScheme(R.color.swiperefresh_color1,
				R.color.swiperefresh_color2, R.color.swiperefresh_color3,
				R.color.swiperefresh_color4);
		mList = (ListView) findViewById(R.id.issue_comment_list);

		mListFooter = mInflater.inflate(R.layout.listview_footer, null);
		mListFooterProgressBar = mListFooter
				.findViewById(R.id.listview_foot_progress);
		mListFooterTextView = (TextView) mListFooter
				.findViewById(R.id.listview_foot_more);

		mCommentContent = (EditText) findViewById(R.id.issue_comment_content);
		mCommentPub = (Button) findViewById(R.id.issue_comment_pub);
		mCommtentWatcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (StringUtils.isEmpty(mCommentContent.getText().toString())) {
					mCommentPub.setEnabled(false);
				} else {
					mCommentPub.setEnabled(true);
				}
			}
		};
		mCommentContent.addTextChangedListener(mCommtentWatcher);
		mCommentPub.setOnClickListener(this);
		mCommentPub.setEnabled(false);
	}

	private void initHead() {
		mIssueTitle = (TextView) mHeadView.findViewById(R.id.issue_title);
		mIssueUserName = (TextView) mHeadView
				.findViewById(R.id.issue_author_name);
		mIssueUserFace = (ImageView) mHeadView
				.findViewById(R.id.issue_author_face);
		mIssueData = (TextView) mHeadView.findViewById(R.id.issue_date);
		mWebView = (WebView) mHeadView.findViewById(R.id.issue_content);

		mIssueTitle.setText(mIssue.getTitle());
		mIssueUserName.setText(mIssue.getAuthor().getName());
		mIssueData.setText("创建于"
				+ StringUtils.friendly_time(mIssue.getCreatedAt()));
		mWebView.loadDataWithBaseURL(null, mIssue.getDescription(),
				"text/html", HTTPRequestor.UTF_8, null);
		new Thread() {
			public void run() {
				String faceUrl = mIssue.getAuthor().getPortrait() == null ? null
						: mIssue.getAuthor().getPortrait();
				if (faceUrl != null) {
					UIHelper.showUserFace(mIssueUserFace, mIssue.getAuthor()
							.getPortrait());
				}
			}
		}.start();
	}

	public void steupList() {
		mList.addHeaderView(mHeadView);
		mList.addFooterView(mListFooter);
		adapter = new IssueListCommentAdapter(getGitApplication(), mListData,
				R.layout.issue_commtent_listitem);
		mList.setAdapter(adapter);
		mList.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.issue_comment_pub:
			pubComment();
			break;

		default:
			break;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}

	@Override
	public void onRefresh() {
		loadComment(1, LISTVIEW_ACTION_REFRESH);
	}

	private void loadComment(int page, int action) {
		mRequestThreadHandler.request(page, new AsyncDataHandler(page, action));
	}

	/** 设置顶部正在加载的状态 */
	void setSwipeRefreshLoadingState() {
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(true);
			// 防止多次重复刷新
			mSwipeRefreshLayout.setEnabled(false);
		}
	}

	/** 设置顶部加载完毕的状态 */
	void setSwipeRefreshLoadedState() {
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(false);
			mSwipeRefreshLayout.setEnabled(true);
		}
	}

	/** 设置底部有错误的状态 */
	void setFooterErrorState() {
		if (mListFooter != null) {
			mListFooterProgressBar.setVisibility(View.GONE);
			mListFooterTextView.setText(R.string.load_error);
		}
	}

	/** 设置底部有更多数据的状态 */
	void setFooterHasMoreState() {
		if (mListFooter != null) {
			mListFooterProgressBar.setVisibility(View.GONE);
			mListFooterTextView.setText(R.string.load_more);
		}
	}

	/** 设置底部已加载全部的状态 */
	void setFooterFullState() {
		if (mListFooter != null) {
			mListFooterProgressBar.setVisibility(View.GONE);
			mListFooterTextView.setText(R.string.load_full);
		}
	}

	/** 设置底部无数据的状态 */
	void setFooterNoMoreState() {
		if (mListFooter != null) {
			mListFooterProgressBar.setVisibility(View.GONE);
			mListFooterTextView.setText(R.string.load_empty);
		}
	}

	/** 设置底部加载中的状态 */
	void setFooterLoadingState() {
		if (mListFooter != null) {
			mListFooterProgressBar.setVisibility(View.VISIBLE);
			mListFooterTextView.setText(R.string.load_ing);
		}
	}
	
	// 提交issue的评论
	private void pubComment() {
		if (StringUtils.isEmpty(mCommentContent.getText().toString())) {
			return;
		}
		if(mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setCancelable(true);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setMessage(getString(R.string.login_tips));
    	}
		
		//异步登录
    	new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg =new Message();
				AppContext ac = getGitApplication();
				try {
					String res = ac.pubIssueComment(mProject.getId(), mIssue.getId(), mCommentContent.getText().toString());
					msg.what = 1;
					msg.obj = res;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				return msg;
			}
			
			@Override
			protected void onPreExecute() {
				if(mProgressDialog != null) {
					mProgressDialog.show();
				}
			}
			
			@Override
			protected void onPostExecute(Message msg) {
				//如果程序已经关闭，则不再执行以下处理
				if(isFinishing()) {
					return;
				}
				if(mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				if (msg.what == 1) {
					UIHelper.ToastMessage(getGitApplication(), (String)msg.obj);
				} else {
					((AppException)(msg.obj)).makeToast(getGitApplication());
				}
			}
		}.execute();
	}

	// 加载数据
	private class AsyncDataHandler implements
			DataRequestThreadHandler.AsyncDataHandler<Message> {

		private int _mPage;
		private int _mAction;

		AsyncDataHandler(int page, int action) {
			_mPage = page;
			_mAction = action;
		}

		// 加载前
		@Override
		public void onPreExecute() {
			setSwipeRefreshLoadingState();
		}

		// 加载ing
		@Override
		public Message execute() {
			Message msg = new Message();
			try {
				boolean refresh = true;
				if (_mAction == LISTVIEW_ACTION_INIT) {
					refresh = false;
				}
				CommonList<GitNote> list = getGitApplication()
						.getIssueCommentList(mProject.getId(), mIssue.getId(),
								1, refresh);
				List<GitNote> nots = list.getList();
				msg.what = 1;
				msg.obj = nots;
			} catch (Exception e) {
				msg.what = -1;
				msg.obj = e;
			}
			return msg;
		}

		// 加载完成
		@SuppressWarnings("unchecked")
		@Override
		public void onPostExecute(Message msg) {
			setSwipeRefreshLoadedState();
			if (msg.what == 1 && msg.obj != null) {
				List<GitNote> list = (List<GitNote>) msg.obj;

				if (_mAction == LISTVIEW_ACTION_INIT) {
					if (list.size() == 0 && mListData.size() == 0) {
						setFooterNoMoreState();
					} else if (list.size() < 20 && mListData.size() >= 0) {
						setFooterFullState();
					} else {
						setFooterHasMoreState();
					}
				} else if (_mAction == LISTVIEW_ACTION_REFRESH) {
					mListData.clear();
				}
				mListData.addAll(list);
				adapter.notifyDataSetChanged();
			} else {
				if (msg.obj instanceof AppException) {
					((AppException) msg.obj).makeToast(getGitApplication());
				} else {
					UIHelper.ToastMessage(getActivity(),
							((Exception) msg.obj).getMessage());
				}
			}
		}
	}
}
