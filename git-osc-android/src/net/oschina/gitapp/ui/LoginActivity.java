package net.oschina.gitapp.ui;

import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.GitlabUser;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.widget.EditTextWithDel;

public class LoginActivity extends BaseActionBarActivity 
	implements OnClickListener, OnEditorActionListener {
	
	private final String TAG = LoginActivity.class.getName();
	
	private Context mContext;
	private EditTextWithDel mAccountEditText;
	private EditTextWithDel mPasswordEditText;
	private ProgressDialog mLoginProgressDialog;
	private Button mLogin;
	private InputMethodManager imm;
	private TextWatcher textWatcher;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initActionBar();
		init();
	}
	
	// 初始化ActionBar
	private void initActionBar() {
		ActionBar bar = getSupportActionBar();
		int flags = ActionBar.DISPLAY_HOME_AS_UP;
		int change = bar.getDisplayOptions() ^ flags;
        bar.setDisplayOptions(change, flags);
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return super.onSupportNavigateUp();
	}

	private void init() {
		mContext = this;
		mAccountEditText = (EditTextWithDel) findViewById(R.id.login_account);
		mPasswordEditText = (EditTextWithDel) findViewById(R.id.login_password);
		mLogin = (Button) findViewById(R.id.login_btn_login);
		mLogin.setOnClickListener(this);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		textWatcher = new TextWatcher() {
			//账号只能输入特定的字符
	    	String regEx = "[^-._@0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ]";
	    	Pattern p = Pattern.compile(regEx);
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				
				// 若密码和帐号都为空，则登录按钮不可操作
				String account = mAccountEditText.getText().toString();
				String pwd = mPasswordEditText.getText().toString();
				if (StringUtils.isEmpty(account) || StringUtils.isEmpty(pwd)) {
					mLogin.setEnabled(false);
				} else {
					mLogin.setEnabled(true);
				}
				
				// 判断输入的帐号字符是否正确
				int editEnd = mAccountEditText.getSelectionEnd();
				String text = s.toString();
				String resultText = p.matcher(text).replaceAll("");
				//如果处理后的文本是一样的，则不再需要执行以下
				if(text.equals(resultText)) {
					return;
				}
				
				if(editEnd > resultText.length()) {
					editEnd = resultText.length();
				} else {
					editEnd = editEnd - 1;
				}
				editEnd = editEnd < 0 ? 0 : editEnd;
				mAccountEditText.removeTextChangedListener(this);
				mAccountEditText.setText(resultText);
				mAccountEditText.setSelection(editEnd);
				mAccountEditText.addTextChangedListener(this);
			}
		};
		// 添加文本变化监听事件
		mAccountEditText.addTextChangedListener(textWatcher);
		mPasswordEditText.addTextChangedListener(textWatcher);
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		//在输入法里点击了“完成”，则去登录
		if(actionId == EditorInfo.IME_ACTION_DONE) {
			checkLogin();
			//将输入法隐藏
			InputMethodManager imm = (InputMethodManager)getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
			return true;
		}
		return false;
	}
	
	/**
	 * 检查登录
	 */
	private void checkLogin() {
		
		String account = mAccountEditText.getText().toString();
		String passwd = mPasswordEditText.getText().toString();
		
		////检查用户输入的参数
		if(StringUtils.isEmpty(account)){
			UIHelper.ToastMessage(this, getString(R.string.msg_login_email_null));
			return;
		}
		if(StringUtils.isEmpty(passwd)){
			UIHelper.ToastMessage(this, getString(R.string.msg_login_pwd_null));
			return;
		}
		
		login(account, passwd);
	}
	
	// 登录验证
	private void login(final String account, final String passwd) {
		if(mLoginProgressDialog == null) {
    		mLoginProgressDialog = new ProgressDialog(this);
    		mLoginProgressDialog.setCancelable(false);
    		mLoginProgressDialog.setMessage(getString(R.string.login_tips));
    	}
		
		//异步登录
    	new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg =new Message();
				try {
					AppContext ac = getGitApplication(); 
	                GitlabUser user = ac.loginVerify(account, passwd);
	                msg.what = 1;
	                msg.obj = user;
	            } catch (Exception e) {
	            	e.printStackTrace();
			    	msg.what = -1;
			    	msg.obj = e;
	            }
				return msg;
			}
			
			@Override
			protected void onPreExecute() {
				if(mLoginProgressDialog != null) {
					mLoginProgressDialog.show();
				}
			}
			
			@Override
			protected void onPostExecute(Message msg) {
				//如果程序已经关闭，则不再执行以下处理
				if(isFinishing()) {
					return;
				}
				if(mLoginProgressDialog != null) {
					mLoginProgressDialog.dismiss();
				}
				Context context = LoginActivity.this;
				GitlabUser user = (GitlabUser)msg.obj;
				UIHelper.ToastMessage(context, user.get_name());
				/*if(msg.what == 1){
					GitlabUser user = (GitlabUser)msg.obj;
					if(user != null){
						//提示登陆成功
						UIHelper.ToastMessage(context, R.string.msg_login_success);
						//返回标识，成功登录
						setResult(RESULT_OK);
						finish();
					}
				} else if(msg.what == 0){
					UIHelper.ToastMessage(context, getString(
							R.string.msg_login_fail) + msg.obj);
				} else if(msg.what == -1){
					((AppException)msg.obj).makeToast(context);
				}*/
			}
		}.execute();
	}

	@Override
	public void onClick(View v) {
		imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
		checkLogin();
	}
}
