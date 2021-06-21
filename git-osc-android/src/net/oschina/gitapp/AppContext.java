package net.oschina.gitapp;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;

import net.oschina.gitapp.api.ApiClient;
import net.oschina.gitapp.bean.GitlabUser;
import net.oschina.gitapp.bean.MySelfProjectList;
import net.oschina.gitapp.common.StringUtils;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * @author 火蚁 (http://my.oschina.net/LittleDY)
 * @version 1.0
 * @created 2014-04-22
 */
public class AppContext extends Application {
	
	// 手机网络类型
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	
	public static final int PAGE_SIZE = 20;//默认分页大小
	private static final int CACHE_TIME = 60*60000;//缓存失效时间
	
	public static final String PROP_KEY_UID = "user.uid";
	public static final String PROP_KEY_USERNAME = "user.username";
	public static final String PROP_KEY_EMAIL = "user.useremail";
	public static final String PROP_KEY_NAME = "user.name";
	public static final String PROP_KEY_BIO = "user.bio";// 个人介绍
	public static final String PROP_KEY_WEIBO = "user.weibo";
	public static final String PROP_KEY_BLOG = "user.blog";
	public static final String PROP_KEY_THEME_ID = "user.theme_id";
	public static final String PROP_KEY_STATE = "user.state";
	public static final String PROP_KEY_CREATED_AT = "user.created_at";
	public static final String PROP_KEY_PORTRAIT = "user.portrait";// 用户头像-文件名
	public static final String PROP_KEY_IS_ADMIN = "user.is_admin";
	public static final String PROP_KEY_CAN_CREATE_GROUP = "user.can_create_group";
	public static final String PROP_KEY_CAN_CREATE_PROJECT = "user.can_create_project";
	public static final String PROP_KEY_CAN_CREATE_TEAM = "user.can_create_team";
	
	private boolean login = false;	//登录状态
	private int loginUid = 0;	//登录用户的id
	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();
	
	private String saveImagePath;//保存图片路径
	
	private Handler unLoginHandler = new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				//UIHelper.ToastMessage(AppContext.this, getString(R.string.msg_login_error));
				//UIHelper.showLoginDialog(AppContext.this);
			}
		}		
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
        //注册App异常崩溃处理器
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
	}
	
	public boolean containsProperty(String key){
		Properties props = getProperties();
		 return props.containsKey(key);
	}
	
	public void setProperties(Properties ps){
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties(){
		return AppConfig.getAppConfig(this).get();
	}
	
	public void setProperty(String key,String value){
		AppConfig.getAppConfig(this).set(key, value);
	}
	
	public String getProperty(String key){
		return AppConfig.getAppConfig(this).get(key);
	}
	public void removeProperty(String...key){
		AppConfig.getAppConfig(this).remove(key);
	}
	
	/**
	 * 检测当前系统声音是否为正常模式
	 * @return
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE); 
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}
	
	/**
	 * 检测网络是否可用
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}		
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if(!StringUtils.isEmpty(extraInfo)){
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}
	
	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}
	
	/**
	 * 获取App唯一标识
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if(StringUtils.isEmpty(uniqueID)){
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}
	
	/**
	 * 获取App安装包信息
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try { 
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
		if(info == null) info = new PackageInfo();
		return info;
	}
	
	/**
	 * 用户登录
	 * @param account
	 * @param pwd
	 * @return
	 * @throws AppException
	 * @throws IOException 
	 */
	public GitlabUser loginVerify(String account, String pwd) throws AppException {
		GitlabUser user = ApiClient.login(this, account, pwd);
		if (null != user) {
			// 保存登录用户的信息
			saveLoginInfo(user);
		}
		return user;
	}
	
	/**
	 * 获得个人的所有项目
	 * @param page
	 * @return
	 * @throws AppException
	 */
	public MySelfProjectList getMySelfProjectList(int page) throws AppException {
		return ApiClient.getMySelfProjectList(this, page);
	}
	
	/**
	 * 保存登录用户的信息
	 * @param user
	 */
	@SuppressWarnings("serial")
	private void saveLoginInfo(final GitlabUser user) {
		if (null == user) {
			return;
		}
		this.loginUid = user.get_id();
		this.login = true;
		setProperties(new Properties(){{
			setProperty(PROP_KEY_UID, String.valueOf(user.get_id()));
			setProperty(PROP_KEY_USERNAME, user.get_username());
			setProperty(PROP_KEY_EMAIL, user.get_email());
			setProperty(PROP_KEY_BIO, user.get_bio());// 个人介绍
			setProperty(PROP_KEY_WEIBO, user.get_weibo());
			setProperty(PROP_KEY_BLOG, user.get_blog());
			setProperty(PROP_KEY_THEME_ID, String.valueOf(user.get_theme_id()));
			setProperty(PROP_KEY_STATE, user.get_state());
			setProperty(PROP_KEY_CREATED_AT, user.get_created_at());
			setProperty(PROP_KEY_PORTRAIT, user.get_portrait());// 个人头像
			setProperty(PROP_KEY_IS_ADMIN, String.valueOf(user.is_isAdmin()));
			setProperty(PROP_KEY_CAN_CREATE_GROUP, String.valueOf(user.is_canCreateGroup()));
			setProperty(PROP_KEY_CAN_CREATE_PROJECT, String.valueOf(user.is_canCreateProject()));
			setProperty(PROP_KEY_CAN_CREATE_TEAM, String.valueOf(user.is_canCreateTeam()));
		}});
	}
}
