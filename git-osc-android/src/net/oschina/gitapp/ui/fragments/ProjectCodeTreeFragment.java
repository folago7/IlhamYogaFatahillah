package net.oschina.gitapp.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ProjectCodeTreeListAdapter;
import net.oschina.gitapp.bean.Branch;
import net.oschina.gitapp.bean.CodeTree;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.FullTree;
import net.oschina.gitapp.bean.FullTree.Folder;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.Tree;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.DataRequestThreadHandler;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.CodeFileDetailActivity;
import net.oschina.gitapp.ui.basefragment.BaseFragment;

/**
 * 项目代码树Fragment
 * 
 * @created 2014-05-26
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 *         最后更新 更新者
 */
public class ProjectCodeTreeFragment extends BaseFragment implements OnItemClickListener,
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

	private String mPath = "";

	private String mBranch;

	private AppContext mAppContext;

	private LinearLayout mContentLayout;
	
	private LinearLayout mCodeTreeFolder;
	
	private TextView mCodeFloders;
	
	private ProgressBar mCodeTreeLoading;

	private ProgressBar mLoading;

	private ProgressDialog mLoadBranch;

	private List<Branch> mBranchList = new ArrayList<Branch>();// 标签和分支列表

	private int mBranchIndex = 0;

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
		mAppContext = getGitApplication();
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
	public void onDestroy() {
		super.onDestroy();
		mRequestThreadHandler.quit();
	}

	private void initView(LayoutInflater inflater) {

		mContentLayout = (LinearLayout) mView.findViewById(R.id.projectcode_content_layout);
		
		mCodeTreeFolder = (LinearLayout) mView.findViewById(R.id.projectcode_tree_prefolder);
		
		mCodeFloders = (TextView) mView.findViewById(R.id.projectcode_floders);
		
		mCodeTreeFolder.setOnClickListener(this);
		
		mCodeTreeLoading = (ProgressBar) mView.findViewById(R.id.projectcode_tree_loading);
		
		mLoading = (ProgressBar) mView.findViewById(R.id.projectcode_loading);
		
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
		mCodeTree.setAdapter(mAdapter);
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
			switchBranch();
			break;
		case R.id.projectcode_tree_prefolder:
			if (StringUtils.isEmpty(mPath)) {
				return;
			}
			loadDatas(getPrePath(), mBranch, ACTION_PRE_TREE);
			break;
		default:
			break;
		}
	}

	private void switchBranch() {
		if (mProject == null) {
			return;
		}
		if (dialog == null || mBranchList.isEmpty()) {
			loadBranchAndTags(true);
		} else {
			loadBranchAndTags(false);
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
			if (_mAction != ACTION_INIT) {
				mCodeTreeLoading.setVisibility(View.VISIBLE);
			}
		}

		// 加载ing
		@Override
		public Message execute() {
			Message msg = new Message();
			try {
				boolean refresh = true;
				if (_mAction == ACTION_INIT) {
					refresh = false;
				}
				CommonList<CodeTree> list = mAppContext.getProjectCodeTree(
						StringUtils.toInt(mProject.getId()), _mPath,
						_mRef_name, refresh);
				List<CodeTree> tree = list.getList();
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
			if (_mAction == ACTION_INIT) {
				mContentLayout.setVisibility(View.VISIBLE);
				mSwitch_branch.setVisibility(View.VISIBLE);
				mLoading.setVisibility(View.GONE);
			} else {
				mCodeTreeLoading.setVisibility(View.INVISIBLE);
			}
			if (result.what == 1 && result.obj != null) {
				mTrees.clear();
				mTrees.addAll((List<CodeTree>) result.obj);
				if (_mAction == ACTION_INIT) {
					mFullTree = new FullTree(mTrees);
				}
				// 加载成功，记录相关信息
				if (_mAction == ACTION_LOADING_TREE
						|| _mAction == ACTION_PRE_TREE) {
					mPath = _mPath;
					mBranch = _mRef_name;
				}
				String floders = mProject.getName() + (StringUtils.isEmpty(mPath) ? "" : "/" + mPath);
				mCodeFloders.setText(floders);
				mAdapter.notifyDataSetChanged();
			} else {
				if (result.obj instanceof AppException) {
					((AppException) result.obj).makeToast(mAppContext);
				} else {
					UIHelper.ToastMessage(getActivity(),
							((Exception) result.obj).getMessage());
				}
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 加载分支和标签数据
	private void loadBranchAndTags(final boolean isRefalsh) {

		// 异步
		new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					msg.what = 1;
					if (!isRefalsh) {
						return msg;
					}
					// 1.加载分支
					CommonList<Branch> branchs = mAppContext
							.getProjectBranchsOrTagsLsit(mProject.getId(), 1,
									Branch.TYPE_BRANCH, isRefalsh);
					for (Branch branch : branchs.getList()) {
						// 设置为分支类型
						branch.setType(Branch.TYPE_BRANCH);
						mBranchList.add(branch);
					}

					// 2.加载标签
					CommonList<Branch> tags = mAppContext
							.getProjectBranchsOrTagsLsit(mProject.getId(), 1,
									Branch.TYPE_TAG, isRefalsh);
					for (Branch branch : tags.getList()) {
						// 设置为标签类型
						branch.setType(Branch.TYPE_TAG);
						mBranchList.add(branch);
					}
				} catch (Exception e) {
					msg.what = -1;
					msg.obj = e;
				}
				return msg;
			}

			@Override
			protected void onPreExecute() {
				if (mLoadBranch == null) {
					mLoadBranch = new ProgressDialog(getActivity());
					mLoadBranch.setCancelable(true);
					mLoadBranch.setCanceledOnTouchOutside(false);
					mLoadBranch.setMessage("加载分支和标签...");
					mLoadBranch.setProgressStyle(R.style.Spinner);
					mLoadBranch.show();
				}
				if (dialog == null) {
					dialog = new AlertDialog.Builder(getActivity())
							.setTitle("选择分支或标签");
				}
			}

			@Override
			protected void onPostExecute(Message msg) {
				if (mLoadBranch != null)
					mLoadBranch.dismiss();
				if (msg.what == 1) {
					final String baArrays[] = new String[mBranchList.size()];
					for (int i = 0; i < mBranchList.size(); i++) {
						baArrays[i] = mBranchList.get(i).getName();
					}
					dialog.setSingleChoiceItems(baArrays, mBranchIndex,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									if (which == mBranchIndex)
										return;
									mBranchIndex = which;
									mBranch = baArrays[which];
									mPath = "";
									loadDatas(mPath, mBranch, ACTION_REFRESH);
									mBranchName.setText(mBranch);
								}
							}).setNegativeButton("取消", null);
					dialog.show();
				} else {
					if (msg.obj instanceof AppException) {
						((AppException) msg.obj).makeToast(getActivity());
					} else {
						UIHelper.ToastMessage(mAppContext, "加载分支和标签失败");
					}
				}
			}
		}.execute();
	}
}
