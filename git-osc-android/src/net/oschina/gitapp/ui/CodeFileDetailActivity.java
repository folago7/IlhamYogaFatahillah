package net.oschina.gitapp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.CodeFile;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.interfaces.OnStatusListener;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.util.MarkdownUtils;
import net.oschina.gitapp.util.SourceEditor;

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
	
	private Menu optionsMenu;
	
	private WebView mWebView;
	
	private ProgressBar mLoading;
	
	private SourceEditor editor;
	
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
		loadDatasCode(mProject.getId(), mPath, mRef);
	}

	private void init() {
		mTitle = mFileName;
		mSubTitle = mRef;
		mWebView = (WebView) findViewById(R.id.code_file_webview);
		editor = new SourceEditor(mWebView);
		
		mLoading = (ProgressBar) findViewById(R.id.code_file_loading);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		optionsMenu = menu;
		// 刷新按钮
		MenuItem refreshItem = menu.add(0, MENU_REFRESH_ID, MENU_REFRESH_ID,
				"刷新");
		refreshItem.setIcon(R.drawable.abc_ic_menu_refresh);
		
		MenuItemCompat.setShowAsAction(refreshItem,
				MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		
		/*MenuItem moreOption = menu.add(0, MENU_MORE_ID, MENU_MORE_ID, "更多");
		moreOption
				.setIcon(R.drawable.abc_ic_menu_moreoverflow_normal_holo_dark);
		
		MenuItemCompat.setShowAsAction(moreOption,
				MenuItemCompat.SHOW_AS_ACTION_ALWAYS);*/
		
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
		
		if (status == STATUS_LOADING) {
			mLoading.setVisibility(View.VISIBLE);
			mWebView.setVisibility(View.GONE);
		} else {
			mLoading.setVisibility(View.GONE);
			mWebView.setVisibility(View.VISIBLE);
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

			@Override
			protected void onPostExecute(Message msg) {
				if (msg.what == 1 && msg.obj != null) {
					mCodeFile = (CodeFile) msg.obj;
					editor.setMarkdown(MarkdownUtils.isMarkdown(mPath));
					editor.setSource(mPath, mCodeFile);
					
					onStatus(STATUS_LOADED);
				} else {
					onStatus(STATUS_NONE);
					if (msg.obj instanceof AppException) {
						AppException appException = (AppException) msg.obj;
						if (mFileName.equalsIgnoreCase("readme.md") && appException.getCode() == 404) {
							UIHelper.ToastMessage(appContext, "该项目没有README.md文件");
						} else {
							appException.makeToast(appContext);
						}
					} else {
						UIHelper.ToastMessage(appContext,
								((Exception) msg.obj).getMessage());
					}
				}
			}
		}.execute();
	}
}
