package net.oschina.gitapp.ui;

import static net.oschina.gitapp.common.Contanst.CHARSET_UTF8;

import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.HTTPRequestor;
import net.oschina.gitapp.bean.CodeFile;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.util.EncodingUtils;

/**
 * 项目ReadMe文件详情
 * @created 2014-07-17
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
public class ProjectReadMeActivity extends BaseActionBarActivity {
	
	private Project mProject;
	
	private View mLoading;
	
	private WebView mWebView;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project_readme_fragment);
		Intent intent = getIntent();
		if (intent != null) {
			mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
			mTitle = "README.md";
			mSubTitle = "master";
		}
		initView();
		loadData();
	}

	private void initView() {
		mLoading = findViewById(R.id.project_readme_loading);
		mWebView = (WebView) findViewById(R.id.project_readme_webview);
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
				if (getActivity().isFinishing()) {
					return;
				}
				mLoading.setVisibility(View.GONE);
				if (msg.what == 1) {
					CodeFile codeFile = (CodeFile) msg.obj;
					mWebView.setVisibility(View.VISIBLE);
					mWebView.loadDataWithBaseURL(null,
							UIHelper.WEB_STYLE + getCodeContent(codeFile), "text/html", HTTPRequestor.UTF_8, null);
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
			res = new String(EncodingUtils.fromBase64(codeFile.getContent()), CHARSET_UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return res;
	}
}
