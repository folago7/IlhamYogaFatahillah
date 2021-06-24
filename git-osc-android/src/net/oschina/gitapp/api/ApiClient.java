package net.oschina.gitapp.api;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
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
	
	/**
	 * 获得发现页面最近更新的项目列表
	 * @param appContext
	 * @param page 页数
	 * @return
	 * @throws AppException
	 */
	public static ProjectList getExploreLatestProject(AppContext appContext, int page) throws AppException {
		ProjectList projects = new ProjectList();
		String url = URLs.EXPLORELATESTPROJECT;
		List<Project> list = getHttpRequestor().init(appContext, HTTPRequestor.GET_METHOD, url)
				.with("page", page)
				.getList(Project[].class);
		projects.setList(list);
		projects.setCount(list.size());
		projects.setPageSize(list.size());
		return projects;
	}
}





