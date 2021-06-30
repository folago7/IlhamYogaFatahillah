package net.oschina.gitapp.bean;

/**
 * 个人动态中的数据实体类
 * @created 2004-05-19 下午18：08
 * @author 火蚁(http://my.oschina.net/LittleDY)
 *
 */
@SuppressWarnings("serial")
public class Data extends Entity {
	
	private String _before;
	private String _after;
	private String _ref;
	private int _user_id;
	private String _user_name;
	// 仓库
	private Repository _repository;
	// 提交，可能有多个commits
	private Commit[] _commits;
	// 提交的数量
	private int _total_commits_count;
	
	public int getTotal_commits_count() {
		return _total_commits_count;
	}
	public void setTotal_commits_count(int total_commits_count) {
		this._total_commits_count = total_commits_count;
	}
	public Commit[] getCommits() {
		return _commits;
	}
	public void setCommits(Commit[] commits) {
		this._commits = commits;
	}
	public Repository getRepository() {
		return _repository;
	}
	public void setRepository(Repository repository) {
		this._repository = repository;
	}
	public String getBefore() {
		return _before;
	}
	public void setBefore(String before) {
		this._before = before;
	}
	public String getAfter() {
		return _after;
	}
	public void setAfter(String after) {
		this._after = after;
	}
	public String getRef() {
		return _ref;
	}
	public void setRef(String ref) {
		this._ref = ref;
	}
	public int getUser_id() {
		return _user_id;
	}
	public void setUser_id(int user_id) {
		this._user_id = user_id;
	}
	public String getUser_name() {
		return _user_name;
	}
	public void setUser_name(String user_name) {
		this._user_name = user_name;
	}
}
