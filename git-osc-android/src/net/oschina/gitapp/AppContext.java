package net.oschina.gitapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;

import net.oschina.gitapp.api.ApiClient;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.User;
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
	
	// 用户私有token
	public static final String PROP_KEY_PRIVATE_TOKEN = "private_token";
	
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
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler(this));
        init();
	}
	
	/**
	 * 初始化Application
	 */
	private void init() {
		//初始化用记的登录信息
		User loginUser = getLoginInfo();
		if(null != loginUser && StringUtils.toInt(loginUser.getId()) > 0 && StringUtils.isEmpty(getProperty(PROP_KEY_PRIVATE_TOKEN))){
			// 记录用户的id和状态
			this.loginUid = StringUtils.toInt(loginUser.getId());
			this.login = true;
		}
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
	 * 应用程序是否发出提示音
	 * @return
	 */
	public boolean isAppSound() {
		return isAudioNormal() && isVoice();
	}
	
	/**
	 * 是否发出提示音
	 * @return
	 */
	public boolean isVoice() {
		String perf_voice = getProperty(AppConfig.CONF_VOICE);
		//默认是开启提示声音
		if(StringUtils.isEmpty(perf_voice)) {
			return true;
		} else {
			return StringUtils.toBool(perf_voice);
		}
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
	 * 判断缓存数据是否可读
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}
	
	/**
	 * 保存对象
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try{
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			try {
				oos.close();
			} catch (Exception e) {}
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 读取对象
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file){
		if(!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable)ois.readObject();
		}catch(FileNotFoundException e){
		}catch(Exception e){
			e.printStackTrace();
			//反序列化失败 - 删除缓存文件
			if(e instanceof InvalidClassException){
				File data = getFileStreamPath(file);
				data.delete();
			}
		}finally{
			try {
				ois.close();
			} catch (Exception e) {}
			try {
				fis.close();
			} catch (Exception e) {}
		}
		return null;
	}
	
	/**
	 * 判断缓存是否存在
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists())
			exist = true;
		return exist;
	}
	
	/**
	 * 判断缓存是否失效
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile) {
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists() && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if(!data.exists())
			failure = true;
		return failure;
	}
	
	/**
	 * 用户登录
	 * @param account
	 * @param pwd
	 * @return
	 * @throws AppException
	 * @throws IOException 
	 */
	public User loginVerify(String account, String pwd) throws AppException {
		User user = ApiClient.login(this, account, pwd);
		if (null != user) {
			// 保存登录用户的信息
			saveLoginInfo(user);
		}
		return user;
	}
	
	/**
	 * 获取登录信息
	 * @return
	 */
	public User getLoginInfo() {		
		User user = new User();		
		user.setId(getProperty(PROP_KEY_UID));
		user.setUsername(getProperty(PROP_KEY_USERNAME));
		//user.setEmail(getProperty(PROP_KEY_EMAIL));
		user.setName(getProperty(PROP_KEY_NAME));
		user.setBio(getProperty(PROP_KEY_BIO));
		user.setWeibo(getProperty(PROP_KEY_WEIBO));
		user.setBlog(getProperty(PROP_KEY_BLOG));
		user.setTheme_id(StringUtils.toInt(getProperty(PROP_KEY_THEME_ID), 1));
		user.setState(getProperty(PROP_KEY_STATE));
		user.setCreated_at(getProperty(PROP_KEY_STATE));
		user.setPortrait(getProperty(PROP_KEY_STATE));
		user.setIsAdmin(StringUtils.toBool(getProperty(PROP_KEY_IS_ADMIN)));
		user.setCanCreateGroup(StringUtils.toBool(getProperty(PROP_KEY_CAN_CREATE_GROUP)));
		user.setCanCreateProject(StringUtils.toBool(getProperty(PROP_KEY_CAN_CREATE_PROJECT)));
		user.setCanCreateTeam(StringUtils.toBool(getProperty(PROP_KEY_CAN_CREATE_TEAM)));
		return user;
	}
	
	/**
	 * 保存登录用户的信息
	 * @param user
	 */
	@SuppressWarnings("serial")
	private void saveLoginInfo(final User user) {
		if (null == user) {
			return;
		}
		// 保存用户的信息
		this.loginUid = StringUtils.toInt(user.getId());
		this.login = true;
		setProperties(new Properties(){{
			setProperty(PROP_KEY_UID, String.valueOf(user.getId()));
			setProperty(PROP_KEY_USERNAME, user.getUsername());
			//setProperty(PROP_KEY_EMAIL, user.getEmail());
			setProperty(PROP_KEY_NAME, user.getName());
			setProperty(PROP_KEY_BIO, user.getBio());// 个人介绍
			setProperty(PROP_KEY_WEIBO, user.getWeibo());
			setProperty(PROP_KEY_BLOG, user.getBlog());
			setProperty(PROP_KEY_THEME_ID, String.valueOf(user.getTheme_id()));
			setProperty(PROP_KEY_STATE, user.getState());
			setProperty(PROP_KEY_CREATED_AT, user.getCreated_at());
			setProperty(PROP_KEY_PORTRAIT, user.getPortrait());// 个人头像
			setProperty(PROP_KEY_IS_ADMIN, String.valueOf(user.isIsAdmin()));
			setProperty(PROP_KEY_CAN_CREATE_GROUP, String.valueOf(user.isCanCreateGroup()));
			setProperty(PROP_KEY_CAN_CREATE_PROJECT, String.valueOf(user.isCanCreateProject()));
			setProperty(PROP_KEY_CAN_CREATE_TEAM, String.valueOf(user.isCanCreateTeam()));
		}});
	}
	
	/**
	 * 清除登录信息，用户的私有token也一并清除
	 */
	void cleanLoginInfo() {
		this.loginUid = 0;
		this.login = false;
		removeProperty(PROP_KEY_UID, PROP_KEY_USERNAME, PROP_KEY_EMAIL, PROP_KEY_NAME,
				PROP_KEY_BIO, PROP_KEY_WEIBO, PROP_KEY_BLOG, PROP_KEY_THEME_ID, PROP_KEY_STATE, PROP_KEY_CREATED_AT,
				PROP_KEY_PORTRAIT, PROP_KEY_IS_ADMIN, PROP_KEY_CAN_CREATE_GROUP, PROP_KEY_CAN_CREATE_PROJECT, PROP_KEY_CAN_CREATE_TEAM);
	}
	
	/**
	 * 清除保存的Token
	 */
	public void cleanToken() {
		removeProperty(AppConfig.CONF_PRIVATE_TOKEN);
	}
	
	/**
	 * 用户是否登录
	 * @return
	 */
	public boolean isLogin() {
		return login;
	}
	
	/**
	 * 获取登录用户id
	 * @return
	 */
	public int getLoginUid() {
		return this.loginUid;
	}
	
	/**
	 * 用户注销
	 */
	public void logout() {
		ApiClient.cleanToken();
		// 清除token
		cleanToken();
		this.login = false;
		this.loginUid = 0;
		//发送广播通知
		//BroadcastController.sendUserChangeBroadcase(this);
	}
	
	/**
	 * 获得最近更新的项目
	 * @param page
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<Project> getExploreLatestProject(int pageIndex, boolean isRefresh) throws AppException {
		CommonList<Project> list = null;
		String cacheKey = "latestProjectList_" + pageIndex + "_" + PAGE_SIZE;
		if(!isReadDataCache(cacheKey) || isRefresh) {
			try{
				list = ApiClient.getExploreLatestProject(this, pageIndex);
				if(list != null && pageIndex == 0){
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			}catch(AppException e){
				e.printStackTrace();
				list = (CommonList<Project>)readObject(cacheKey);
				if(list == null)
					throw e;
			}		
		} else {
			// 从缓存中读取
			list = (CommonList<Project>)readObject(cacheKey);
			if(list == null)
				list = new CommonList<Project>();
		}
		return list;
	}
	
	/**
	 * 获取热门项目
	 * @param page
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<Project> getExplorePopularProject(int pageIndex, boolean isRefresh) throws AppException {
		CommonList<Project> list = null;
		String cacheKey = "popularProjectList_" + pageIndex + "_" + PAGE_SIZE;
		if(!isReadDataCache(cacheKey) || isRefresh) {
			try{
				list = ApiClient.getExplorePopularProject(this, pageIndex);
				if(list != null && pageIndex == 0){
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			}catch(AppException e){
				e.printStackTrace();
				list = (CommonList<Project>)readObject(cacheKey);
				if(list == null)
					throw e;
			}		
		} else {
			// 从缓存中读取
			list = (CommonList<Project>)readObject(cacheKey);
			if(list == null)
				list = new CommonList<Project>();
		}
		return list;
	}
	
	/**
	 * 获取推荐项目
	 * @param page
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<Project> getExploreFeaturedProject(int pageIndex, boolean isRefresh) throws AppException {
		CommonList<Project> list = null;
		String cacheKey = "faturedProjectList_" + pageIndex + "_" + PAGE_SIZE;
		if(!isReadDataCache(cacheKey) || isRefresh) {
			try{
				list = ApiClient.getExploreFeaturedProject(this, pageIndex);
				if(list != null && pageIndex == 0){
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			}catch(AppException e){
				e.printStackTrace();
				list = (CommonList<Project>)readObject(cacheKey);
				if(list == null)
					throw e;
			}		
		} else {
			// 从缓存中读取
			list = (CommonList<Project>)readObject(cacheKey);
			if(list == null)
				list = new CommonList<Project>();
		}
		return list;
	}
	
	/**
	 * 获得个人动态列表
	 * @param pageIndex
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<Event> getMySelfEvents(int pageIndex, boolean isRefresh) throws AppException {
		CommonList<Event> list = null;
		String cacheKey = "myselfEventsList_" + + pageIndex + "_" + PAGE_SIZE;
		if(!isReadDataCache(cacheKey) || isRefresh) {
			try{
				list = ApiClient.getMySelfEvents(this, pageIndex);
				if(list != null && pageIndex == 0){
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			}catch(AppException e){
				e.printStackTrace();
				list = (CommonList<Event>)readObject(cacheKey);
				if(list == null)
					throw e;
			}		
		} else {
			// 从缓存中读取
			list = (CommonList<Event>)readObject(cacheKey);
			if(list == null)
				list = new CommonList<Event>();
		}
		return list;
	}
	
	/**
	 * 获得个人的所有项目
	 * @param page
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<Project> getMySelfProjectList(int pageIndex, boolean isRefresh) throws AppException {
		CommonList<Project> list = null;
		String cacheKey = "myselfProjectList_" + pageIndex + "_" + PAGE_SIZE;
		if(!isReadDataCache(cacheKey) || isRefresh) {
			try{
				list = ApiClient.getMySelfProjectList(this, pageIndex);
				if(list != null && pageIndex == 0){
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			}catch(AppException e){
				e.printStackTrace();
				list = (CommonList<Project>)readObject(cacheKey);
				if(list == null)
					throw e;
			}		
		} else {
			// 从缓存中读取
			list = (CommonList<Project>)readObject(cacheKey);
			if(list == null)
				list = new CommonList<Project>();
		}
		return list;
	}
	
	/**
	 * 获得一个项目的commit列表
	 * @param projectId
	 * @param pageIndex
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<Commit> getProjectCommitList(int projectId, int pageIndex, boolean isRefresh) throws AppException {
		CommonList<Commit> list = null;
		String cacheKey = "projectCommitList_" + projectId + "_" + pageIndex + "_" + PAGE_SIZE;
		if(!isReadDataCache(cacheKey) || isRefresh) {
			try{
				list = ApiClient.getProjectCommitList(this, projectId, pageIndex);
				if(list != null && pageIndex == 0){
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			}catch(AppException e){
				e.printStackTrace();
				list = (CommonList<Commit>)readObject(cacheKey);
				if(list == null)
					throw e;
			}		
		} else {
			// 从缓存中读取
			list = (CommonList<Commit>)readObject(cacheKey);
			if(list == null)
				list = new CommonList<Commit>();
		}
		return list;
	}
}
