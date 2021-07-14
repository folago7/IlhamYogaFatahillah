package net.oschina.gitapp.ui.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ProjectCodeTreeListAdapter;
import net.oschina.gitapp.bean.Branch;
import net.oschina.gitapp.bean.CodeTree;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.FullTree;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.bean.FullTree.Folder;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.BroadcastController;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.DataRequestThreadHandler;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.interfaces.OnStatusListener;
import net.oschina.gitapp.ui.CodeFileDetailActivity;
import net.oschina.gitapp.ui.LoginActivity;
import net.oschina.gitapp.ui.basefragment.BaseFragment;

/**
 * 项目代码树Fragment
 * 
 * @created 2014-05-26
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 *         最后更新 更新者
 */
public class ProjectCodeTreeFragment extends BaseFragment implements
		SwipeRefreshLayout.OnRefreshListener, OnItemClickListener,
		OnClickListener {

	private static final int ACTION_INIT = 0;// 初始化
	private static final int ACTION_REFRESH = 1;// 刷新
	private static final int ACTION_LOADING_TREE = 2;// 加载代码层级树
	private static final int ACTION_PRE_TREE = 3;// 前一级代码树

	private View mView;

	private Project mProject;

	private ListView mCodeTree;

	private FullTree mFullTree;

	private Folder mCurrentFolder;// 当前文件夹

	private LinearLayout mSwitch_branch;

	private ImageView mBranchIcon;

	private TextView mBranchName;

	private ProjectCodeTreeListAdapter mAdapter;

	private List<CodeTree> mTrees;

	private View mHeadView;

	private String mPath = null;

	private String mBranch;

	private AppContext mAppContext;

	private SwipeRefreshLayout mSwipeRefreshLayout;
	
	private ProgressDialog mLoad;
	
	private List<Branch> mBranchList;// 标签和分支列表
	
	private AlertDialog.Builder dialog;

	private DataRequestThreadHandler mRequestThreadHandler = new DataRequestThreadHandler();

	public static ProjectCodeTreeFragment newInstance(Project project) {
		ProjectCodeTreeFragment fragment = new ProjectCodeTreeFragment();
		Bundle args = new Bundle();
		args.putSerializable(Contanst.PROJECT, project);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle args = getArguments();
		if (args != null) {
			mProject = (Project) args.getSerializable(Contanst.PROJECT);
		}
		mBranch = "master";
		mView = inflater.inflate(R.layout.projectcode_fragment, null);
		initView(inflater);
		setupListView();
		loadDatas("", "master", ACTION_INIT);
		return mView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAppContext = getGitApplication();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mRequestThreadHandler.quit();
	}

	private void initView(LayoutInflater inflater) {
		mHeadView = inflater.inflate(R.layout.projectcodetree_listitem_head,
				null);
		mSwipeRefreshLayout = (SwipeRefreshLayout) mView
				.findViewById(R.id.projectcode_swiperefreshlayout);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorScheme(R.color.swiperefresh_color1,
				R.color.swiperefresh_color2, R.color.swiperefresh_color3,
				R.color.swiperefresh_color4);
		mCodeTree = (ListView) mView.findViewById(R.id.projectcode_tree);
		mSwitch_branch = (LinearLayout) mView
				.findViewById(R.id.projectcode_switch_branch);
		mBranchIcon = (ImageView) mView
				.findViewById(R.id.projectcode_branch_icon);
		mBranchName = (TextView) mView
				.findViewById(R.id.projectcode_branch_name);
	}

	private void setupListView() {
		mTrees = new ArrayList<CodeTree>();
		mAdapter = new ProjectCodeTreeListAdapter(getGitApplication(), mTrees,
				R.layout.projectcodetree_listitem);
		mCodeTree.addHeaderView(mHeadView);
		mCodeTree.setAdapter(mAdapter);
		mCodeTree.removeHeaderView(mHeadView);
		mCodeTree.setOnItemClickListener(this);
		mSwitch_branch.setOnClickListener(this);
	}

	private void loadDatas(final String path, final String ref_name, int action) {
		mRequestThreadHandler.request(0, new AsyncDataHandler(path, ref_name,
				action));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if (view == mHeadView) {
			loadDatas(getPrePath(), mBranch, ACTION_PRE_TREE);
			return;
		}

		position -= mCodeTree.getHeaderViewsCount();

		CodeTree codeTree = mTrees.get(position);
		if (codeTree.getType().equalsIgnoreCase(CodeTree.TYPE_TREE)) {
			loadDatas(getPath(codeTree.getName()), mBranch, ACTION_LOADING_TREE);
		} else {
			showDetail(codeTree.getName(), mBranch);
		}
	}

	// 获得访问的路径
	private String getPath(String path) {
		if (mPath == null || StringUtils.isEmpty(mPath)) {
			return path;
		} else {
			return mPath + "/" + path;
		}
	}

	// 获得上一级路径
	private String getPrePath() {
		int index = mPath.lastIndexOf("/");

		return index != -1 ? mPath.substring(0, index) : "";
	}

	// 查看代码文件详情
	private void showDetail(String fileName, String ref) {
		Intent intent = new Intent(getGitApplication(),
				CodeFileDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Contanst.PROJECT, mProject);
		bundle.putString("fileName", fileName);
		bundle.putString("path",
				mPath == null || StringUtils.isEmpty(mPath) ? fileName : mPath
						+ "/" + fileName);
		bundle.putString("ref", ref);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.projectcode_switch_branch:
			if (dialog == null) {
				loadBranchAndTags();
			} else {
				dialog.show();
			}
			break;

		default:
			break;
		}
	}

	// 加载数据
	private class AsyncDataHandler implements
			DataRequestThreadHandler.AsyncDataHandler<Message> {

		private String _mPath;
		private String _mRef_name;
		private int _mAction;

		AsyncDataHandler(String path, String ref_name, int action) {
			_mPath = path;
			_mRef_name = ref_name;
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
				List<CodeTree> tree = mAppContext
						.getProjectCodeTree(
								StringUtils.toInt(mProject.getId()), _mPath,
								_mRef_name);
				msg.what = 1;
				msg.obj = tree;
			} catch (Exception e) {
				msg.what = -1;
				msg.obj = e;
			}
			return msg;
		}

		// 加载完成
		@SuppressWarnings("unchecked")
		@Override
		public void onPostExecute(Message result) {
			setSwipeRefreshLoadedState();
			if (result.what == 1 && result.obj != null) {
				mTrees.clear();
				mTrees.addAll((List<CodeTree>) result.obj);
				// 加载成功，记录相关信息
				if (_mAction == ACTION_LOADING_TREE
						|| _mAction == ACTION_PRE_TREE) {
					mPath = _mPath;
					mBranch = _mRef_name;
				}
				setCodeTreeListHeadView();
				mAdapter.notifyDataSetChanged();
			} else {
				((AppException) result.obj).makeToast(mAppContext);
			}
		}
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

	/** 设置代码树的头部信息 */
	void setCodeTreeListHeadView() {
		if (mPath == null || StringUtils.isEmpty(mPath)) {
			if (mCodeTree.getHeaderViewsCount() > 0) {
				mCodeTree.removeHeaderView(mHeadView);
			}

		} else {
			if (mCodeTree.getHeaderViewsCount() <= 0) {
				mCodeTree.addHeaderView(mHeadView);
			}
		}
		mHeadView.invalidate();
	}

	@Override
	public void onRefresh() {
		loadDatas(mPath, mBranch, ACTION_REFRESH);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void loadBranchAndTags() {
		
		if (mLoad == null) {
			mLoad = new ProgressDialog(getActivity());
			mLoad.setCancelable(true);
			mLoad.setCanceledOnTouchOutside(false);
			mLoad.setMessage("加载分支和标签...");
			mLoad.show();
		}
		
		//异步登录
    	new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg =new Message();
				try {
					CommonList<Branch> branchs = mAppContext.getProjectBranchsOrTagsLsit(mProject.getParent_id(), 1, "branch");
					for (Branch branch : branchs.getList()) {
						branch.setType(Branch.TYPE_BRANCH);
					}
					mBranchList.addAll(branchs.getList());
					
					CommonList<Branch> tags = mAppContext.getProjectBranchsOrTagsLsit(mProject.getParent_id(), 1, "tags");
					for (Branch branch : tags.getList()) {
						branch.setType(Branch.TYPE_TAG);
					}
					mBranchList.addAll(tags.getList());
	                msg.what = 1;
	                
	            } catch (Exception e) {
			    	msg.what = -1;
			    	msg.obj = e;
	            }
				return msg;
			}
			
			@Override
			protected void onPreExecute() {
				
			}
			
			@Override
			protected void onPostExecute(Message msg) {
				if (mLoad != null) mLoad.dismiss();
				if (msg.what == 1) {
					dialog = new AlertDialog.Builder(mAppContext).setTitle("选择分支")
							.setSingleChoiceItems(new String[]{"1", "2"}, 0, new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}).setNegativeButton("确定", null);
					dialog.show();
				} else {
					/*if (msg.obj instanceof AppException) {
						((AppException)msg.obj).makeToast(getActivity());
					} else {
						Log.i("Test", ((Exception)msg.obj).getMessage());
					}*/
				}
			}
		}.execute();
	}
}
