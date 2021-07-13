package net.oschina.gitapp.ui.fragments;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ProjectCodeTreeListAdapter;
import net.oschina.gitapp.bean.CodeTree;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.ui.basefragment.BaseFragment;

/**
 * 项目代码树Fragment
 * @created 2014-05-26
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class ProjectCodeTreeFragment extends BaseFragment {
	
	private View mView;
	private Project _project;
	private ProgressBar mProgressBar;
	private ListView mCodeTree;
	private LinearLayout mSwitch_branch;
	private ImageView mBranchIcon;
	private TextView mBranchName;
	private ProjectCodeTreeListAdapter mAdapter;
	private List<CodeTree> mTrees;
	private View mHeaderView;
	private String mPath;
	private String mBranch;
	
	private View.OnClickListener mSwitch_branchListener = new OnClickListener() {
		public void onClick(View v) {
			
		}
	};
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			CodeTree codeTree = mTrees.get(position);
			
			if (codeTree.getType().equalsIgnoreCase(CodeTree.tree)) {
				loadDatas(codeTree.getName(), mBranch);
			} else {
				
			}
		}
	};
	
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
			_project = (Project) args.getSerializable(Contanst.PROJECT);
		}
		mView = inflater.inflate(R.layout.projectcode_fragment, null);
		initView(inflater);
		loadDatas("", "master");
		return mView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void initView(LayoutInflater inflater) {
		mPath = "";
		mBranch = "master";
		mProgressBar = (ProgressBar) mView.findViewById(R.id.projectcode_loading);
		mCodeTree = (ListView) mView.findViewById(R.id.projectcode_tree);
		mSwitch_branch = (LinearLayout) mView.findViewById(R.id.projectcode_switch_branch);
		mBranchIcon = (ImageView) mView.findViewById(R.id.projectcode_branch_icon);
		mBranchName = (TextView) mView.findViewById(R.id.projectcode_branch_name);
		
		mCodeTree.setOnItemClickListener(itemClickListener);
		mSwitch_branch.setOnClickListener(mSwitch_branchListener);
	}
	
	private void loadDatas(final String path, final String ref_name) {
		if (!StringUtils.isEmpty(path) && path != null) {
			
			mPath = StringUtils.isEmpty(mPath) ?  path : mPath + "/" + path;
		}
		Log.i("Test", mPath);
		if(_project == null || !isAdded()) {
			return;
		}
		
		new AsyncTask<Void, Void, Message>() {
			
			@Override
			protected Message doInBackground(Void... params) {
				mProgressBar.setVisibility(View.VISIBLE);
				Message msg =new Message();
				try {
					AppContext ac = getGitApplication();
	                List<CodeTree> tree = ac.getProjectCodeTree(StringUtils.toInt(_project.getId()), mPath, ref_name);
	                msg.what = 1;
	                msg.obj = tree;
	            } catch (Exception e) {
			    	msg.what = -1;
			    	msg.obj = e;
			    	if(mProgressBar != null) {
			    		mProgressBar.setVisibility(View.GONE);
					}
	            }
				return msg;
			}
			
			@Override
			protected void onPreExecute() {
				
			}
			
			@SuppressWarnings("unchecked")
			@Override
			protected void onPostExecute(Message msg) {
				if(mProgressBar != null) {
		    		mProgressBar.setVisibility(View.GONE);
				}
				if(isDetached()) {
					return;
				}
				if (msg.what == 1 && msg.obj != null) {
					mTrees = (List<CodeTree>)msg.obj;
					mAdapter = new ProjectCodeTreeListAdapter(getActivity(), mTrees, R.layout.projectcodetree_listitem);
					mCodeTree.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
					mCodeTree.setVisibility(View.VISIBLE);
				}
			}
		}.execute();
	}
}
