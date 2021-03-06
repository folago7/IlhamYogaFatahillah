package net.oschina.gitapp.api;

import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.bean.CodeTree;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.ShippingAddress;
import net.oschina.gitapp.common.ImageUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static net.oschina.gitapp.api.AsyncHttpHelp.get;
import static net.oschina.gitapp.api.AsyncHttpHelp.getPrivateTokenWithParams;
import static net.oschina.gitapp.api.AsyncHttpHelp.post;

/**
 * Git@OSC API
 * <p/>
 * Created by 火蚁 on 15/4/10.
 */
public class GitOSCApi {

    private final static String HOST = "gitee.com/";
    private static final String API_VERSION = "api/v3/";// API版本
    private final static String HTTP = "https://";
    private final static String BASE_URL = HTTP + HOST + API_VERSION;
    public final static String NO_API_BASE_URL = HTTP + HOST;
    private final static String PROJECTS = BASE_URL + "projects/";
    public final static String USER = BASE_URL + "user/";
    public final static String EVENT = BASE_URL + "events/";
    private final static String NOTIFICATION = USER + "notifications/";
    public final static String VERSION = BASE_URL + "app_version/new/android";

    public static void login(String account, String passwod, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("email", account);
        params.put("password", urlEncode(passwod));
        AsyncHttpHelp.post(BASE_URL + "session", params, handler);
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return value;
        }
    }

    public static void getExploreLatestProject(int page, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("page", page);
        AsyncHttpHelp.get(PROJECTS + "latest", params, handler);
    }

    public static void getExploreFeaturedProject(int page, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("page", page);
        get(PROJECTS + "featured", params, handler);
    }

    public static void getExplorePopularProject(int page, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("page", page);
        get(PROJECTS + "popular", params, handler);
    }

    public static void getMyProjects(int page, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("page", page);
        get(PROJECTS, params, handler);
    }

    public static void getMyEvents(int page, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("page", page);
        get(EVENT, params, handler);
    }

    public static void getStarProjects(String uid, int page, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("page", page);
        get(USER + uid + "/stared_projects", params, handler);
    }

    public static void getWatchProjects(String uid, int page, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("page", page);
        get(USER + uid + "/watched_projects", params, handler);
    }

    public static void getProject(String pId, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        get(PROJECTS + pId, params, handler);
    }

    public static void getProject(String userName, String projectName, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        get(PROJECTS + userName + "%2F" + projectName, params, handler);
    }

    public static void searchProjects(String query, int page, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("page", page);
        get(PROJECTS + "search/" + urlEncode(query), params, handler);
    }

    public static void getUserProjects(String uid, int page, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("page", page);
        get(USER + uid + "/" + "projects", params, handler);
    }

    public static void getUserEvents(String uid, int page, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("page", page);
        get(EVENT + "user" + "/" + uid, params, handler);
    }

    /**
     * 获取用户信息
     */
    public static void getUserInfo(String uid, HttpCallback callback) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        get("https://gitee.com//api/v3/users/"  + uid, params, callback);
    }

    public static void getProjectCommits(String pId, int page, String refName, HttpCallback
            handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("page", page);
        params.put("ref_name", refName);
        get(PROJECTS + pId + "/" + "/repository/commits", params, handler);
    }

    public static void getProjectCodeTree(String pId, String path, String refName, HttpCallback
            handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("path", urlEncode(path));
        params.put("ref_name", refName);
        get(PROJECTS + pId + "/repository/tree", params, handler);
    }

    public static void getProjectIssues(String pId, int page, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("page", page);
        get(PROJECTS + pId + "/" + "issues", params, handler);
    }

    public static void getProjectBranchs(String pId, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        get(PROJECTS + pId + "/repository/branches", params, handler);
    }

    public static void getProjectTags(String pId, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        get(PROJECTS + pId + "/repository/tags", params, handler);
    }

    public static void getIssueDetail(String pId, String issueId, HttpCallback handler) {
        get(PROJECTS + pId + "/issues/" + issueId, handler);
    }

    public static void getIssueComments(String pId, String issueId, int page, HttpCallback
            handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("page", page);
        get(PROJECTS + pId + "/issues/" + issueId + "/notes", params, handler);
    }

    public static void pubIssueComment(String pId, String issueId, String body, HttpCallback
            handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("body", urlEncode(body));
        post(PROJECTS + pId + "/issues/" + issueId + "/notes", params, handler);
    }

    public static void getCodeFileDetail(String projectId, String file_path, String ref,
                                         HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("file_path", urlEncode(file_path));
        params.put("ref", ref);
        get(PROJECTS + projectId + "/repository/files", params, handler);
    }


    public static void getReadMeFile(String projectId, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        get(PROJECTS + projectId + "/readme", params, handler);
    }

    public static void getCommitDiffList(String projectId, String commitId, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        get(PROJECTS + projectId + "/repository/commits/" + commitId + "/diff", params, handler);
    }

    public static void getCommitCommentList(String projectId, String commitId, HttpCallback
            handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        get(PROJECTS + projectId + "/repository/commits/" + commitId + "/comment", params, handler);
    }

    public static void getCommitFileDetail(String projectId, String commitId, String filePath,
                                           HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();

        params.put("filepath", urlEncode(filePath));
        get(PROJECTS + projectId + "/repository/commits/" + commitId + "/blob", params, handler);
    }

    public static void getProjectMembers(String projectId, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        get(PROJECTS + projectId + "/members", params, handler);
    }

    /**
     * 加载项目的里程碑
     */
    public static void getProjectMilestone(String projectId, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        get(PROJECTS + projectId + "/milestones", params, handler);
    }

    /**
     * 创建一个issue
     */
    public static void pubCreateIssue(String projectId, String title, String description, String
            assignee_id, String milestone_id, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("description", urlEncode(description));
        params.put("title", urlEncode(title));
        params.put("assignee_id", urlEncode(assignee_id));
        params.put("milestone_id", urlEncode(milestone_id));
        post(PROJECTS + projectId + "/issues", params, handler);
    }

    /**
     * 上传文件
     */
    public static void upLoadFile(File file, HttpCallback handler) throws Exception {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase();
        params.put("files", ImageUtils.fileToByteArray(file), "image/" + suffix, file.getName());
        post("https://gitee.com/upload", params, handler);
    }

    /***
     * 更新用户头像
     */
    public static void updateUserProtrait(String protraitUrl, HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("path", urlEncode(protraitUrl));
        post(USER + "portrait", params, handler);
    }

    /***
     * 获取通知
     *
     */
    public static void getNotification(String filter, String all, HttpCallback
            handler) {
        HttpParams params = getPrivateTokenWithParams();
        params.put("filter", urlEncode(filter));
        params.put("all", urlEncode(all));
        get(NOTIFICATION, params, handler);
    }

    /**
     * 设置通知为已读
     */
    public static void setNotificationReaded(String notificationId, HttpCallback handler) {
        HttpParams params = getPrivateTokenWithParams();
        get(NOTIFICATION + notificationId, params, handler);
    }

    /**
     * 获得App更新的信息
     */
    public static void getUpdateInfo(HttpCallback handler) {
        get(VERSION, handler);
    }

    /**
     * 获得语言列表
     */
    public static void getLanguageList(HttpCallback handler) {
        get(PROJECTS + "languages", handler);
    }

    /**
     * 根据语言的ID获得项目的列表
     */
    public static void getLanguageProjectList(String languageId, int page, HttpCallback handler) {
        HttpParams params = getPrivateTokenWithParams();
        params.put("page", page);
        get(PROJECTS + "languages/" + languageId, params, handler);
    }

    /**
     * star or unstar一个项目
     */
    public static void starProject(String projectId, HttpCallback handler) {
        HttpParams params = getPrivateTokenWithParams();
        post(PROJECTS + projectId + "/star", params, handler);
    }

    public static void unStarProject(String projectId, HttpCallback handler) {
        HttpParams params = getPrivateTokenWithParams();
        post(PROJECTS + projectId + "/unstar", params, handler);
    }

    public static void watchProject(String projectId, HttpCallback handler) {
        HttpParams params = getPrivateTokenWithParams();
        post(PROJECTS + projectId + "/watch", params, handler);
    }

    public static void unWatchProject(String projectId, HttpCallback handler) {
        HttpParams params = getPrivateTokenWithParams();
        post(PROJECTS + projectId + "/unwatch", params, handler);
    }

    public static void getRandomProject(HttpCallback handler) {
        HttpParams params = getPrivateTokenWithParams();
        params.put("luck", 1);
        get(PROJECTS + "random", params, handler);
    }

    public static void updateRepositoryFiles(String projectId, String ref, String file_path,
                                             String branch_name, String content, String
                                                     commit_message, HttpCallback handler) {
        HttpParams params = getPrivateTokenWithParams();
        params.put("ref", urlEncode(ref));
        params.put("file_path", urlEncode(file_path));
        params.put("branch_name", urlEncode(branch_name));
        params.put("content", urlEncode(content));
        params.put("commit_message", urlEncode(commit_message));
        post(PROJECTS + projectId + "/repository/files", params, handler);
    }

    /**
     * 获得某个用户star的项目列表
     */
    public static void getUserStarProjects(String uId, int page, HttpCallback handler) {
        HttpParams params = getPrivateTokenWithParams();
        params.put("page", page);
        get(USER + uId + "/stared_projects", params, handler);
    }

    public static void getUserWatchProjects(String uId, int page, HttpCallback handler) {
        HttpParams params = getPrivateTokenWithParams();
        params.put("page", page);
        get(USER + uId + "/watched_projects", params, handler);
    }

    /**
     * 获取用户的收货信息
     */
    public static void getUserShippingAddress(String uid, HttpCallback handler) {
        HttpParams params = getPrivateTokenWithParams();
        get(BASE_URL + "users/" + uid, params, handler);
    }

    /**
     * 更新用户的收货信息
     */
    public static void updateUserShippingAddress(String uid, ShippingAddress shippingAddress,
                                                 HttpCallback handler) {
        HttpParams params = getPrivateTokenWithParams();
        params.put("name", urlEncode(shippingAddress.getName()));
        params.put("tel", urlEncode(shippingAddress.getTel()));
        params.put("address", urlEncode(shippingAddress.getAddress()));
        params.put("comment", urlEncode(shippingAddress.getComment()));
        post(BASE_URL + "users/" + uid, params, handler);
    }

    /**
     * 获得抽奖活动的信息
     */
    public static void getLuckMsg(HttpCallback handler) {
        HttpParams params = getPrivateTokenWithParams();
        get(PROJECTS + "luck_msg", handler);
    }


    /**
     * 用户反馈,其实就是发了个issue
     *
     */
    public static void feedback(String title, String message, HttpCallback callback) {
        HttpParams params = AsyncHttpHelp.getHttpParams();
        params.put("description", urlEncode(message));
        params.put("title", urlEncode(title));
        params.put("assignee_id", 355540);
        params.put("milestone_id", "");
        post(PROJECTS + "142148/issues", params, callback);
    }

    /**
     * 下载二进制文件
     *
     */
    public static void downloadFile(Project project, CodeTree codeTree, String path, String refName, HttpCallback callback) {
        String uri = GitOSCApi.NO_API_BASE_URL + project.getPathWithNamespace() + "/raw/" + refName + "/" + path + codeTree.getName() + "?private_token=" + AppContext.getToken();
        get(uri, callback);
    }

    public static void downloadFile(Project project, String fileName, String path, String refName, HttpCallback callback) {
        String uri = GitOSCApi.NO_API_BASE_URL + project.getPathWithNamespace() + "/raw/" + refName + "/" + path + fileName + "?private_token=" + AppContext.getToken();
        get(uri, callback);
    }

    public static void downloadFile(String projectId, String file_path, String ref,
                                         HttpCallback handler) {
        HttpParams params = AsyncHttpHelp.getHttpParams();

        params.put("file_path", urlEncode(file_path));
        params.put("ref", ref);
        get(PROJECTS + projectId + "/repository/files", params, handler);
    }

}
