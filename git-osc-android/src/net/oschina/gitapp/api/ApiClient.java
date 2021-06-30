package net.oschina.gitapp.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.bean.EventList;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.Session;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.bean.ProjectList;
import net.oschina.gitapp.bean.URLs;

/**
 * API客户端接口：用于访问网络数据
 * @author 火蚁 (http://my.oschina.net/LittleDY)
 * @version 1.0
 * @created 2014-04-22
 */
public class ApiClient {
	
	private final static String PRIVATE_TOKEN = "private_token";
	
	// 私有token，每个用户都有一个唯一的
	private static String private_token;
	public static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	/**
	 * 清除private_token，注销登录的时候清除
	 */
	public static void cleanToken() {
		private_token = "";
	}
	
	/**
	 * 获得private_token
	 * @param appContext
	 * @return
	 */
	private static String getToken(AppContext appContext) {
		if(private_token == null || private_token == "") {
			private_token = appContext.getProperty(PRIVATE_TOKEN);
		}
		return private_token;
	}
	
	private static HTTPRequestor getHttpRequestor() {
		return new HTTPRequestor();
	}
	
	/**
	 * 给一个url拼接参数
	 * @param p_url
	 * @param params
	 * @return
	 */
	private static String makeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder(p_url);
		if (params.size() == 0) 
			return p_url;
		if(url.indexOf("?")<0)
			url.append('?');

		for(String name : params.keySet()){
			url.append('&');
			url.append(name);
			url.append('=');
			url.append(String.valueOf(params.get(name)));
		}

		return url.toString().replace("?&", "?");
	}
	
	/**
	 * 用户登录，将私有token保存
	 * @param appContext
	 * @param username
	 * @param password
	 * @return GitlabUser用户信息
	 * @throws IOException 
	 */
	public static User login(AppContext appContext, String userEmail, String password) throws AppException {
		String urlString = URLs.SESSION;
		Session session = getHttpRequestor().init(appContext, HTTPRequestor.POST_METHOD, urlString)
				.with("email", userEmail)
				.with("password", password)
				.to(Session.class);
		// 保存用户的私有token
		if (session != null && null != session.get_privateToken()) {
			appContext.setProperty(PRIVATE_TOKEN, session.get_privateToken());
		}
		return session;
	}
	
	/**
	 * 获得一个用户的信息
	 * @param appContext
	 * @param userId
	 * @return
	 * @throws AppException
	 */
	public static User getUser(AppContext appContext, int userId)  throws AppException {
		String url = URLs.USER + URLs.URL_SPLITTER + userId;
		return getHttpRequestor().init(appContext, HTTPRequestor.GET_METHOD, url).to(User.class);
	}
	
	/**
	 * 获取网络图片
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) throws AppException {
		return HTTPRequestor.getNetBitmap(url);
	}
	
	/**
	 * 获得一个项目的信息
	 * @param appContext
	 * @param projectId
	 * @return
	 * @throws AppException
	 */
	public static Project getProject(AppContext appContext, int projectId) throws AppException {
		String url = URLs.PROJECT + URLs.URL_SPLITTER + projectId;
		Log.i("MySelfViewPagerFragment", url);
		return getHttpRequestor().init(appContext, HTTPRequestor.GET_METHOD, url).to(Project.class);
	}
	
	/**
	 * 获得发现页面最近更新的项目列表
	 * @param appContext
	 * @param page 页数
	 * @return
	 * @throws AppException
	 */
	public static ProjectList getExploreLatestProject(AppContext appContext, final int page) throws AppException {
		ProjectList projects = new ProjectList();
		String url = makeURL(URLs.EXPLORELATESTPROJECT, new HashMap<String, Object>(){{
			put("page", page);
		}});
		List<Project> list = getHttpRequestor().init(appContext, HTTPRequestor.GET_METHOD, url)
				.with("page", page)
				.getList(Project[].class);
		projects.setList(list);
		projects.setCount(list.size());
		projects.setPageSize(list.size());
		return projects;
	}
	
	/**
	 * 获得发现页面热门项目列表
	 * @param appContext
	 * @param page
	 * @return
	 * @throws AppException
	 */
	public static ProjectList getExplorePopularProject(AppContext appContext, final int page) throws AppException {
		ProjectList projects = new ProjectList();
		String url = makeURL(URLs.EXPLOREPOPULARPROJECT, new HashMap<String, Object>(){{
			put("page", page);
		}});
		List<Project> list = getHttpRequestor().init(appContext, HTTPRequestor.GET_METHOD, url)
				.getList(Project[].class);
		projects.setList(list);
		projects.setCount(list.size());
		projects.setPageSize(list.size());
		return projects;
	}
	
	/**
	 * 获得发现页面推荐项目列表
	 * @param appContext
	 * @param page
	 * @return
	 * @throws AppException
	 */
	public static ProjectList getExploreFeaturedProject(AppContext appContext, final int page) throws AppException {
		ProjectList projects = new ProjectList();
		String url = makeURL(URLs.EXPLOREFEATUREDPROJECT, new HashMap<String, Object>(){{
			put("page", page);
		}});
		List<Project> list = getHttpRequestor().init(appContext, HTTPRequestor.GET_METHOD, url)
				.getList(Project[].class);
		projects.setList(list);
		projects.setCount(list.size());
		projects.setPageSize(list.size());
		return projects;
	}
	
	/**
	 * 获得个人动态列表
	 * @param appContext
	 * @param page
	 * @return
	 * @throws AppException
	 */
	public static EventList getMySelfEvents(final AppContext appContext, final int page) throws AppException {
		EventList events = new EventList();
		String url = makeURL(URLs.EVENTS, new HashMap<String, Object>(){{
			put("page", page);
			put(PRIVATE_TOKEN, getToken(appContext));
		}});
		final List<Event> list = getHttpRequestor().init(appContext, HTTPRequestor.GET_METHOD, url)
				.getList(Event[].class);
		events.setList(list);
		events.setCount(list.size());
		events.setPageSize(list.size());
		return events;
	}
	
	/**
	 * 获得个人的所有项目
	 * @param appContext
	 * @param page
	 * @return
	 * @throws AppException
	 */
	public static ProjectList getMySelfProjectList(AppContext appContext, int page) throws AppException {
		ProjectList msProject = new ProjectList();
		Map<String,Object> params = new HashMap<String,Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		String url = makeURL(URLs.PROJECT, params);
		List<Project> list = getHttpRequestor().init(appContext, HTTPRequestor.GET_METHOD, url)
				.getList(Project[].class);
		msProject.setList(list);
		msProject.setCount(list.size());
		msProject.setPageSize(list.size());
		return msProject;
	}
	
}





