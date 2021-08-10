package net.oschina.gitapp.bean;

import java.io.Serializable;

/**
 * 接口URL实体类
 * @author 火蚁 (http://my.oschina.net/LittleDY)
 * @version 1.0
 * @created 2014-04-22
 */
@SuppressWarnings("serial")
public class URLs implements Serializable {
	
	public final static String HOST = "git.oschina.net";
	private static final String API_VERSION = "/api/v3";// API版本
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";
	
	public final static String URL_SPLITTER = "/";
	
	// 拼接的api根地址
	public final static String URL_API_HOST = HTTP + HOST + API_VERSION + URL_SPLITTER;
	
	// api URL地址
	public static String BRANCH = URL_API_HOST + "repository/branches";
	public static String COMMIT = URL_API_HOST + "commits";
	public static String ISSUE = URL_API_HOST + "issues";
	public static String MERGEREQUEST = URL_API_HOST + "merge_requests";
	public static String MILESTONE = URL_API_HOST + "milestones";
	public static String NAMESPACE = URL_API_HOST + "groups";
	public static String NOTE = URL_API_HOST + "notes";
	// 用户个人最新动态
	public static String EVENTS = URL_API_HOST + "events";
	// 项目
	public static String PROJECT = URL_API_HOST+ "projects";
	// 最近更新项目列表
	public static String EXPLORELATESTPROJECT = PROJECT +URL_SPLITTER +  "latest";
	// 热门项目列表
	public static String EXPLOREPOPULARPROJECT = PROJECT +URL_SPLITTER +  "popular";
	// 推荐项目列表
	public static String EXPLOREFEATUREDPROJECT =PROJECT +URL_SPLITTER +  "featured";
	public static String PROJECTHOOK = URL_API_HOST + "hooks";
	public static String PROJECTMEMBER = URL_API_HOST + "members";
	public final static String LOGIN_HTTP = HTTP + HOST + API_VERSION + URL_SPLITTER + "session";
	public final static String LOGIN_HTTPS = HTTPS + HOST + API_VERSION + URL_SPLITTER + "session";
	public static String USER = URL_API_HOST + "users";
	public static String UPLOAD = URL_API_HOST + "upload";
	// 获得通知
	public static String NOTIFICATION = URL_API_HOST + "user/notifications";
	// 设置通知为已读
	public static String NOTIFICATION_READED = NOTIFICATION + URL_SPLITTER + "readed";
}
