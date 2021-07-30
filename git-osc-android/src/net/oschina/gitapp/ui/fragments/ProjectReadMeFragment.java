package net.oschina.gitapp.ui.fragments;

import java.io.UnsupportedEncodingException;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.CommitDiffListAdapter;
import net.oschina.gitapp.adapter.CommitListCommentdapter;
import net.oschina.gitapp.adapter.ExploreListProjectAdapter;
import net.oschina.gitapp.api.HTTPRequestor;
import net.oschina.gitapp.bean.CodeFile;
import net.oschina.gitapp.bean.Comment;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseFragment;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;

/**
 * 项目介绍readmeFragment
 * @created 2014-06-24
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class ProjectReadMeFragment extends BaseFragment {
	
	private Project mProject;
	
	private View mLoading;
	
	private WebView mWebView;
	
	private TextView mText;
	
	public static ProjectReadMeFragment newInstance(Project project) {
		ProjectReadMeFragment fragment = new ProjectReadMeFragment();
		Bundle args = new Bundle();
		args.putSerializable(Contanst.PROJECT, project);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle args = getArguments();
		if (args != null) {
			mProject = (Project) args.getSerializable(Contanst.PROJECT);
		}
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView(view);
		loadData();
	}
	
	private void initView(View view) {
		mLoading = view.findViewById(R.id.project_readme_loading);
		mWebView = (WebView) view.findViewById(R.id.project_readme_webview);
		mText = (TextView) view.findViewById(R.id.project_readme_textview);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.project_readme_fragment, null);
	}
	
	
	private void loadData() {
		
		new AsyncTask<Void, Void, Message>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					CodeFile codeFile = getGitApplication().getCodeFile(mProject.getId(), "README.md", "master");
					msg.what = 1;
					msg.obj = codeFile;
				} catch (Exception e) {
					msg.what = -1;
					msg.obj = e;
					e.printStackTrace();
				}
				return msg;
			}
			
			@Override
			protected void onPostExecute(Message msg) {
				mLoading.setVisibility(View.GONE);
				if (msg.what == 1) {
					CodeFile codeFile = (CodeFile) msg.obj;
					mWebView.setVisibility(View.VISIBLE);
					mWebView.loadDataWithBaseURL(null,
							getCodeContent(codeFile), "text/html", HTTPRequestor.UTF_8, null);
					/*mText.setText(getCodeContent(codeFile));
					mText.setVisibility(View.VISIBLE);*/
				} else {
					if (msg.obj instanceof AppException) {
						AppException e = (AppException)msg.obj;
						if (e.getCode() == 404) {
							getActivity().findViewById(R.id.project_readme_empty).setVisibility(View.VISIBLE);
						} else {
							((AppException)msg.obj).makeToast(getGitApplication());
						}
					}
				}
			}
		}.execute();
	}
	
	private String getCodeContent(CodeFile codeFile) {
		String res = null;
		try {
			byte[] buff = codeFile.getContent().getBytes(HTTPRequestor.UTF_8);
			res = new String(Base64.decode(buff, 0), HTTPRequestor.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return res.replace("/r|/t|/n", "");
	}
}
