package net.oschina.gitapp.ui;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.ApiClient;
import net.oschina.gitapp.api.GitlabAPI;
import net.oschina.gitapp.bean.GitlabSession;
import net.oschina.gitapp.bean.GitlabUser;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.widget.EditTextWithDel;

public class LoginActivity extends BaseActionBarActivity implements OnClickListener {
	
	private final String TAG = LoginActivity.class.getName();
	
	private Context mContext;
	private final AppContext ac = (AppContext) this.getApplication();
	private EditTextWithDel mAccount;
	private EditTextWithDel mPassword;
	private ProgressDialog dialog;
	private Button mLogin;
	private InputMethodManager imm;
	private TextWatcher textWatcher;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		initActionBar();
		//init();
	}
	
	// 初始化ActionBar
	private void initActionBar() {
		ActionBar bar = getSupportActionBar();
		int flags = ActionBar.DISPLAY_HOME_AS_UP;
		int change = bar.getDisplayOptions() ^ flags;
        bar.setDisplayOptions(change, flags);
	}
	
	private void init() {
		mContext = this;
		mAccount = (EditTextWithDel) findViewById(R.id.login_account);
		mPassword = (EditTextWithDel) findViewById(R.id.login_password);
		mLogin = (Button) findViewById(R.id.login_btn_login);
		mLogin.setOnClickListener(this);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		textWatcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				String account = mAccount.getText().toString();
				String pwd = mPassword.getText().toString();
				if (StringUtils.isEmpty(account) || StringUtils.isEmpty(pwd)) {
					mLogin.setEnabled(false);
				} else {
					mLogin.setEnabled(true);
				}
			}
		};
		mAccount.addTextChangedListener(textWatcher);
		mPassword.addTextChangedListener(textWatcher);
		dialog = new ProgressDialog(mContext);
		dialog.setMessage(mContext.getString(R.string.login_status));
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		imm.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
		//String username = mAccount.getText().toString();
		//String pwd = mPassword.getText().toString();
		
		new Thread(){
			
			public void run() {
				
				String username = mAccount.getText().toString();
				String pwd = mPassword.getText().toString();
				
				try {
					GitlabSession session = GitlabAPI.connect("http://git.oschina.net", username, pwd);
					Log.i("Login", session.getPrivateToken());
					UIHelper.ToastMessage(mContext, session.getPrivateToken());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*try {
					//ApiClient.login(ac, username, pwd);
					//ApiClient.getAllProject(ac, "n5Ex6qGzq76mgAxozxx7");
				} catch (AppException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		}.start();
		
	}
	
	class LoginTask extends AsyncTask<String, Integer, GitlabUser>  {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.i("Login", "显示");
			dialog.show();
		}

		@Override
		protected void onPostExecute(GitlabUser result) {
			super.onPostExecute(result);
			Log.i("Login", "结束");
			dialog.dismiss();
		}

		@Override
		protected GitlabUser doInBackground(String... params) {
			try {
				//ApiClient.login(ac, params[0], params[1]);
				ApiClient.getAllProject(ac, "n5Ex6qGzq76mgAxozxx7");
			} catch (AppException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
