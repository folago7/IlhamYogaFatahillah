package net.oschina.gitapp.ui;

import java.io.UnsupportedEncodingException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.HTTPRequestor;
import net.oschina.gitapp.bean.CodeFile;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Base64Util;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.interfaces.OnStatusListener;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;

/**
 * 代码文件详情
 * 
 * @created 2014-06-04
 * @author 火蚁
 *
 */
public class CodeFileDetailActivity extends BaseActionBarActivity implements
		OnStatusListener {

	private final int MENU_REFRESH_ID = 0;
	private final int MENU_MORE_ID = 1;

	private int mStatus;// 状态
	private Menu optionsMenu;
	private WebView mWebView;
	private CodeFile mCodeFile;
	private Project mProject;
	private String mFileName;
	private String mPath;
	private String mRef;
	private AppContext appContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置actionbar加载动态
		setContentView(R.layout.activity_code_file_view);
		appContext = getGitApplication();
		Intent intent = getIntent();
		mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
		mFileName = intent.getStringExtra("fileName");
		mPath = intent.getStringExtra("path");
		mRef = intent.getStringExtra("ref");
		init();
		setupWebView();
	}

	private void init() {
		mActionBar.setTitle(mFileName);
		mActionBar.setSubtitle(mRef);
		mWebView = (WebView) findViewById(R.id.code_file_webview);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setupWebView() {
		/*mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDefaultTextEncodingName(HTTPRequestor.UTF_8);*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		optionsMenu = menu;
		// 刷新按钮
		MenuItem refreshItem = menu.add(0, MENU_REFRESH_ID, MENU_REFRESH_ID,
				"刷新");
		refreshItem.setIcon(R.drawable.abc_ic_menu_refresh);

		MenuItem moreOption = menu.add(0, MENU_MORE_ID, MENU_MORE_ID, "更多");
		moreOption
				.setIcon(R.drawable.abc_ic_menu_moreoverflow_normal_holo_dark);
		MenuItemCompat.setShowAsAction(refreshItem,
				MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		/*
		 * MenuItemCompat.setShowAsAction(moreOption,
		 * MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		 */
		loadDatasCode(mProject.getId(), mPath, mRef);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		switch (id) {
		case MENU_REFRESH_ID:
			loadDatasCode(mProject.getId(), mPath, mRef);
			break;
		case MENU_MORE_ID:

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStatus(int status) {
		if (optionsMenu == null) {
			return;
		}
		// 更新菜单的状态
		final MenuItem refreshItem = optionsMenu.findItem(MENU_REFRESH_ID);
		if (refreshItem == null) {
			return;
		}
		if (status == STATUS_LOADING) {
			MenuItemCompat.setActionView(refreshItem,
					R.layout.actionbar_indeterminate_progress);
		} else {
			MenuItemCompat.setActionView(refreshItem, null);
			if (status == STATUS_NONE) {

			}
		}
	}

	private void loadDatasCode(final String projectId, final String path,
			final String ref_name) {
		onStatus(STATUS_LOADING);
		new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					AppContext ac = getGitApplication();
					CodeFile codeFile = ac.getCodeFile(projectId, path,
							ref_name);
					msg.what = 1;
					msg.obj = codeFile;
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
					CodeFile codeFile = (CodeFile) msg.obj;

					mWebView.loadDataWithBaseURL(null,
							getCodeContent(codeFile.getContent()), "text/html",
							HTTPRequestor.UTF_8, null);
					mWebView.setVisibility(View.VISIBLE);
					TextView v = (TextView) findViewById(R.id.code_file_textview);
					v.setText(getCodeContent(codeFile.getContent()));
				} else {
					onStatus(STATUS_NONE);
					if (msg.obj instanceof AppException) {
						((AppException) msg.obj).makeToast(appContext);
					} else {
						UIHelper.ToastMessage(appContext,
								((Exception) msg.obj).getMessage());
					}
				}
			}
		}.execute();
	}

	private String getCodeContent(String s) {
		String res = null;
		try {
			byte[] buff = s.getBytes(HTTPRequestor.UTF_8);
			res = new String(Base64.decode(buff, Base64.DEFAULT), HTTPRequestor.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return res;
	}
}
