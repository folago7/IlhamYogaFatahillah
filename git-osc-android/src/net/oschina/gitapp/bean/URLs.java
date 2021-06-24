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
	
	private final static String URL_SPLITTER = "/";
	
	// 拼接的api根地址
	private final static String URL_API_HOST = HTTP + HOST + API_VERSION;
	
	// api URL地址
	public static String BRANCH = URL_API_HOST + URL_SPLITTER + "repository/branches";
	public static String COMMIT = URL_API_HOST + URL_SPLITTER + "commits";
	public static String ISSUE = URL_API_HOST + URL_SPLITTER + "issues";
	public static String MERGEREQUEST = URL_API_HOST + URL_SPLITTER + "merge_requests";
	public static String MILESTONE = URL_API_HOST + URL_SPLITTER + "milestones";
	public static String NAMESPACE = URL_API_HOST + URL_SPLITTER + "groups";
	public static String NOTE = URL_API_HOST + URL_SPLITTER + "notes";
	public static String PROJECT = URL_API_HOST + URL_SPLITTER + "projects";
	public static String EXPLORELATESTPROJECT = URL_API_HOST + URL_SPLITTER + "projects" +URL_SPLITTER +  "latest";
	public static String PROJECTHOOK = URL_API_HOST + URL_SPLITTER + "hooks";
	public static String PROJECTMEMBER = URL_API_HOST + URL_SPLITTER + "members";
	public static String SESSION = URL_API_HOST + URL_SPLITTER + "session";
	public static String USER = URL_API_HOST + URL_SPLITTER + "users";
	
}
