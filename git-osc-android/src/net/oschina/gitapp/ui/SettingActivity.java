package net.oschina.gitapp.ui;

import static net.oschina.gitapp.common.Contanst.LOGIN_REQUESTCODE;

import java.io.File;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppManager;
import net.oschina.gitapp.R;
import net.oschina.gitapp.common.FileUtils;
import net.oschina.gitapp.common.MethodsCompat;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.common.UpdateManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * 设置界面
 * @created 2014-07-02
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
public class SettingActivity extends PreferenceActivity implements OnPreferenceClickListener {
		
	private SharedPreferences mPreferences;
	private Preference cache;
	private Preference feedback;
	private Preference update;
	private Preference about;

	private CheckBoxPreference httpslogin;
	private CheckBoxPreference loadimage;
	private CheckBoxPreference voice;
	private CheckBoxPreference checkup;
	
	private AppContext mAppContext;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		initView();
		AppManager.getAppManager().addActivity(this);
	}
	
	@SuppressWarnings("deprecation")
	private void initView() {
		mAppContext = (AppContext) getApplication();
		// 获得SharedPreferences
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		httpslogin = (CheckBoxPreference) findPreference("httpslogin");
		httpslogin.setChecked(mAppContext.isHttpsLogin());
		if (mAppContext.isHttpsLogin()) {
			httpslogin.setSummary("当前以 HTTPS 登录");
		} else {
			httpslogin.setSummary("当前以 HTTP 登录");
		}
		httpslogin.setOnPreferenceClickListener(this);
		
		loadimage = (CheckBoxPreference) findPreference("loadimage");
		loadimage.setChecked(mAppContext.isLoadImage());
		if (mAppContext.isLoadImage()) {
			loadimage.setSummary("页面加载图片 (默认在WIFI网络下加载图片)");
		} else {
			loadimage.setSummary("页面不加载图片 (默认在WIFI网络下加载图片)");
		}
		loadimage.setOnPreferenceClickListener(this);
		
		// 提示声音
		voice = (CheckBoxPreference) findPreference("voice");
		voice.setChecked(mAppContext.isVoice());
		if (mAppContext.isVoice()) {
			voice.setSummary("已开启提示声音");
		} else {
			voice.setSummary("已关闭提示声音");
		}
		voice.setOnPreferenceClickListener(this);
		
		checkup = (CheckBoxPreference) findPreference("checkup");
		checkup.setChecked(mAppContext.isCheckUp());
		checkup.setOnPreferenceClickListener(this);
		
		cache = (Preference) findPreference("cache");
		cache.setSummary(calCache());
		cache.setOnPreferenceClickListener(this);
		
		feedback = (Preference) findPreference("feedback");
		update = (Preference) findPreference("update");
		about = (Preference) findPreference("about");
		
		
		feedback.setOnPreferenceClickListener(this);
		update.setOnPreferenceClickListener(this);
		about.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference == httpslogin) {
			onHttpslogin();
		} else if (preference == loadimage) {
			onLoadimage();
		} else if (preference == voice) {
			onVoice();
		} else if (preference == checkup) {
			mAppContext.setConfigCheckUp(checkup.isChecked());
		} else if (preference == cache) {
			onCache();
		} else if (preference == feedback) {
			onFeedBack();
		} else if (preference == update) {
			UpdateManager.getUpdateManager().checkAppUpdate(this, true);
		}
		return true;
	}

	private void onHttpslogin() {
		mAppContext.setConfigHttpsLogin(httpslogin.isChecked());
		if (httpslogin.isChecked()) {
			httpslogin.setSummary("当前以 HTTPS 登录");
		} else {
			httpslogin.setSummary("当前以 HTTP 登录");
		}
	}
	
	private void onLoadimage() {
		mAppContext.setConfigLoadimage(loadimage.isChecked());
		if (loadimage.isChecked()) {
			loadimage.setSummary("页面加载图片 (默认在WIFI网络下加载图片)");
		} else {
			loadimage.setSummary("页面不加载图片 (默认在WIFI网络下加载图片)");
		}
	}
	
	private void onVoice() {
		mAppContext.setConfigVoice(voice.isChecked());
		if (voice.isChecked()) {
			voice.setSummary("已开启提示声音");
		} else {
			voice.setSummary("已关闭提示声音");
		}
	}
	
	private void onCache() {
		UIHelper.clearAppCache(SettingActivity.this);
		cache.setSummary("OKB");
	}
	
	private String calCache() {
		long fileSize = 0;
		String cacheSize = "0KB";
		File filesDir = getFilesDir();
		File cacheDir = getCacheDir();

		fileSize += FileUtils.getDirSize(filesDir);
		fileSize += FileUtils.getDirSize(cacheDir);
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			File externalCacheDir = MethodsCompat.getExternalCacheDir(this);
			fileSize += FileUtils.getDirSize(externalCacheDir);
		}
		if (fileSize > 0)
			cacheSize = FileUtils.formatFileSize(fileSize);
		return cacheSize;
	}
	
	/**
	 * 发送反馈意见到指定的邮箱
	 */
	private void onFeedBack() {
		Intent i = new Intent(Intent.ACTION_SEND);  
		//i.setType("text/plain"); //模拟器
		i.setType("message/rfc822") ; //真机
		i.putExtra(Intent.EXTRA_EMAIL, new String[]{"ld@oschina.net", "zhangdeyi@oschina.net"});  
		i.putExtra(Intent.EXTRA_SUBJECT,"用户反馈-git@osc Android客户端");  
		i.putExtra(Intent.EXTRA_TEXT, "");  
		startActivity(Intent.createChooser(i, "send email to me..."));
	}
}
