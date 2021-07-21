package net.oschina.gitapp.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import android.graphics.Bitmap;
import android.util.Log;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.bean.Branch;
import net.oschina.gitapp.bean.CodeFile;
import net.oschina.gitapp.bean.CodeTree;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.CommitDiff;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.bean.GitNote;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.Session;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.common.StringUtils;

/**
 * API客户端接口：用于访问网络数据
 * 
 * @author 火蚁 (http://my.oschina.net/LittleDY)
 * @version 1.0
 * @created 2014-04-22
 */
public class ApiClient {

	private final static String PRIVATE_TOKEN = "private_token";

	// 私有token，每个用户都有一个唯一的
	private static String private_token;
	public static final ObjectMapper MAPPER = new ObjectMapper().configure(
			DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	/**
	 * 清除private_token，注销登录的时候清除
	 */
	public static void cleanToken() {
		private_token = "";
	}

	/**
	 * 获得private_token
	 * 
	 * @param appContext
	 * @return
	 */
	private static String getToken(AppContext appContext) {
		if (private_token == null || private_token == "") {
			private_token = appContext.getProperty(PRIVATE_TOKEN);
		}
		return private_token;
	}

	private static HTTPRequestor getHttpRequestor() {
		return new HTTPRequestor();
	}

	/**
	 * 给一个url拼接参数
	 * 
	 * @param p_url
	 * @param params
	 * @return
	 */
	private static String makeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder(p_url);
		if (params.size() == 0)
			return p_url;
		if (url.indexOf("?") < 0)
			url.append('?');

		for (String name : params.keySet()) {
			String value = String.valueOf(params.get(name));
			if (value != null && !StringUtils.isEmpty(value)
					&& !value.equalsIgnoreCase("null")) {
				url.append('&');
				url.append(name);
				url.append('=');
				try {
					url.append(URLEncoder.encode(String.valueOf(params.get(name)), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}

		return url.toString().replace("?&", "?");
	}

	/**
	 * 用户登录，将私有token保存
	 * 
	 * @param appContext
	 * @param username
	 * @param password
	 * @return GitlabUser用户信息
	 * @throws IOException
	 */
	public static User login(AppContext appContext, String userEmail,
			String password) throws AppException {
		String urlString = URLs.SESSION;
		Session session = getHttpRequestor()
				.init(appContext, HTTPRequestor.POST_METHOD, urlString)
				.with("email", userEmail).with("password", password)
				.to(Session.class);
		// 保存用户的私有token
		if (session != null && null != session.get_privateToken()) {
			appContext.setProperty(PRIVATE_TOKEN, session.get_privateToken());
		}
		return session;
	}

	/**
	 * 获得一个用户的信息
	 * 
	 * @param appContext
	 * @param userId
	 * @return
	 * @throws AppException
	 */
	public static User getUser(AppContext appContext, int userId)
			throws AppException {
		String url = URLs.USER + URLs.URL_SPLITTER + userId;
		return getHttpRequestor().init(appContext, HTTPRequestor.GET_METHOD,
				url).to(User.class);
	}

	/**
	 * 获取网络图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) throws AppException {
		return HTTPRequestor.getNetBitmap(url);
	}

	/**
	 * 获得一个项目的信息
	 * 
	 * @param appContext
	 * @param projectId
	 * @return
	 * @throws AppException
	 */
	public static Project getProject(AppContext appContext, int projectId)
			throws AppException {
		String url = URLs.PROJECT + URLs.URL_SPLITTER + projectId;
		return getHttpRequestor().init(appContext, HTTPRequestor.GET_METHOD,
				url).to(Project.class);
	}

	/**
	 * 获得发现页面最近更新的项目列表
	 * 
	 * @param appContext
	 * @param page
	 *            页数
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("serial")
	public static CommonList<Project> getExploreLatestProject(
			AppContext appContext, final int page) throws AppException {
		CommonList<Project> projects = new CommonList<Project>();
		String url = makeURL(URLs.EXPLORELATESTPROJECT,
				new HashMap<String, Object>() {
					{
						put("page", page);
					}
				});
		List<Project> list = getHttpRequestor()
				.init(appContext, HTTPRequestor.GET_METHOD, url)
				.with("page", page).getList(Project[].class);
		projects.setList(list);
		projects.setCount(list.size());
		projects.setPageSize(list.size());
		return projects;
	}

	/**
	 * 获得发现页面热门项目列表
	 * 
	 * @param appContext
	 * @param page
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("serial")
	public static CommonList<Project> getExplorePopularProject(
			AppContext appContext, final int page) throws AppException {
		CommonList<Project> projects = new CommonList<Project>();
		String url = makeURL(URLs.EXPLOREPOPULARPROJECT,
				new HashMap<String, Object>() {
					{
						put("page", page);
					}
				});
		List<Project> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Project[].class);
		projects.setList(list);
		projects.setCount(list.size());
		projects.setPageSize(list.size());
		return projects;
	}

	/**
	 * 获得发现页面推荐项目列表
	 * 
	 * @param appContext
	 * @param page
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("serial")
	public static CommonList<Project> getExploreFeaturedProject(
			AppContext appContext, final int page) throws AppException {
		CommonList<Project> projects = new CommonList<Project>();
		String url = makeURL(URLs.EXPLOREFEATUREDPROJECT,
				new HashMap<String, Object>() {
					{
						put("page", page);
					}
				});
		List<Project> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Project[].class);
		projects.setList(list);
		projects.setCount(list.size());
		projects.setPageSize(list.size());
		return projects;
	}

	/**
	 * 获得个人动态列表
	 * 
	 * @param appContext
	 * @param page
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("serial")
	public static CommonList<Event> getMySelfEvents(
			final AppContext appContext, final int page) throws AppException {
		CommonList<Event> events = new CommonList<Event>();
		String url = makeURL(URLs.EVENTS, new HashMap<String, Object>() {
			{
				put("page", page);
				put(PRIVATE_TOKEN, getToken(appContext));
			}
		});
		final List<Event> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Event[].class);
		events.setList(list);
		events.setCount(list.size());
		events.setPageSize(list.size());
		return events;
	}

	/**
	 * 获得个人的所有项目
	 * 
	 * @param appContext
	 * @param page
	 * @return
	 * @throws AppException
	 */
	public static CommonList<Project> getMySelfProjectList(
			AppContext appContext, int page) throws AppException {
		CommonList<Project> msProjects = new CommonList<Project>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		String url = makeURL(URLs.PROJECT, params);
		List<Project> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Project[].class);
		msProjects.setList(list);
		msProjects.setCount(list.size());
		msProjects.setPageSize(list.size());
		return msProjects;
	}

	/**
	 * 获得一个项目的commit列表
	 * 
	 * @param appContext
	 * @param projectId
	 *            指定项目的id
	 * @param page
	 *            页码
	 * @param ref_name
	 *            分支（optional）
	 * @return
	 * @throws AppException
	 */
	public static CommonList<Commit> getProjectCommitList(
			AppContext appContext, int projectId, int page, String ref_name)
			throws AppException {
		CommonList<Commit> commits = new CommonList<Commit>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		params.put("ref_name", ref_name);
		// 拼接url地址
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "repository/commits", params);
		List<Commit> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Commit[].class);
		commits.setList(list);
		commits.setCount(list.size());
		commits.setPageSize(list.size());
		return commits;
	}

	/**
	 * 获得项目的代码树列表
	 * 
	 * @param appContext
	 * @param projectId
	 *            项目的id
	 * @param path
	 *            (optional) 路径
	 * @param ref_name
	 *            (optional) 分支或者标签，空则为默认的master分支
	 * @return
	 * @throws AppException
	 */
	public static CommonList<CodeTree> getProjectCodeTree(
			AppContext appContext, int projectId, String path, String ref_name)
			throws AppException {
		CommonList<CodeTree> codeTree = new CommonList<CodeTree>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("path", path);
		params.put("ref_name", ref_name);
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "repository/tree", params);
		List<CodeTree> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(CodeTree[].class);
		codeTree.setList(list);
		codeTree.setCount(list.size());
		codeTree.setPageSize(list.size());
		return codeTree;
	}

	/**
	 * 获得一个项目issues列表
	 * 
	 * @param appContext
	 * @param projectId
	 * @param page
	 * @return
	 * @throws AppException
	 */
	public static CommonList<Issue> getProjectIssuesList(AppContext appContext,
			int projectId, int page) throws AppException {
		CommonList<Issue> commits = new CommonList<Issue>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		// 拼接url地址
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "issues", params);
		List<Issue> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Issue[].class);
		commits.setList(list);
		commits.setCount(list.size());
		commits.setPageSize(list.size());
		return commits;
	}

	/**
	 * 获得一个项目的Branchs或者Tags列表
	 * 
	 * @param appContext
	 * @param projectId
	 * @param page
	 * @param branchOrTag
	 * @return
	 * @throws AppException
	 */
	public static CommonList<Branch> getProjectBranchsOrTagsLsit(
			AppContext appContext, String projectId, int page,
			String branchOrTag) throws AppException {
		CommonList<Branch> commits = new CommonList<Branch>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		// 拼接url地址
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "repository" + URLs.URL_SPLITTER
				+ branchOrTag, params);
		List<Branch> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Branch[].class);
		commits.setList(list);
		commits.setCount(list.size());
		commits.setPageSize(list.size());
		return commits;
	}
	
	/**
	 * 获得issue的评论列表
	 * @param appContext
	 * @param projectId
	 * @param noteId
	 * @param page
	 * @param isRefresh
	 * @return
	 * @throws Exception
	 */
	public static CommonList<GitNote> getIssueCommentList(AppContext appContext, String projectId, String issueId, int page) throws Exception {
		CommonList<GitNote> commits = new CommonList<GitNote>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		// 拼接url地址
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "issues" + URLs.URL_SPLITTER + issueId + URLs.URL_SPLITTER + "notes", params);
		List<GitNote> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(GitNote[].class);
		commits.setList(list);
		commits.setCount(list.size());
		commits.setPageSize(list.size());
		return commits;
	}

	/**
	 * 获得代码文件详情
	 * 
	 * @param appContext
	 * @param projectId
	 * @param file_path
	 * @param ref
	 * @return
	 * @throws AppException
	 */
	public static CodeFile getCodeFile(AppContext appContext, String projectId,
			String file_path, String ref) throws AppException {
		CodeFile codeFile = null;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("file_path", file_path);
		params.put("ref", ref);
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "repository/files", params);
		codeFile = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).to(CodeFile.class);
		return codeFile;
	}

	/**
	 * 获得commit文件diff
	 * 
	 * @param appContext
	 * @param projectId
	 * @param commitId
	 * @return
	 * @throws Exception
	 */
	public static CommonList<CommitDiff> getCommitDiffList(
			AppContext appContext, String projectId, String commitId)
			throws Exception {
		CommonList<CommitDiff> commits = new CommonList<CommitDiff>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		// 拼接url地址
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "repository/commits" + URLs.URL_SPLITTER
				+ commitId + URLs.URL_SPLITTER + "diff", params);
		List<CommitDiff> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(CommitDiff[].class);
		commits.setList(list);
		commits.setCount(list.size());
		commits.setPageSize(list.size());
		return commits;
	}
	
	/**
	 * 通过commits获取代码文件的内容
	 * @param appContext
	 * @param projectId
	 * @param commitId
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static String getCommitFileDetail(AppContext appContext, String projectId, String commitId, String filePath) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("filepath", filePath);
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "repository/commits" + URLs.URL_SPLITTER
				+ commitId + URLs.URL_SPLITTER + "blob", params);
		Log.i("Test", url);
		return getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getResponseBodyString();
	}
}
