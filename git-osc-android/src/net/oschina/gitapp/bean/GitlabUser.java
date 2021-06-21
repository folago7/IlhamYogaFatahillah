package net.oschina.gitapp.bean;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

public class GitlabUser {
	public static final String URL = "/users";
	
    @JsonProperty("id")
    private Integer _id;
    
    @JsonProperty("username")
    private String _username;
    
    @JsonProperty("email")
    private String _email;
    
    @JsonProperty("name")
    private String _name;
    
    @JsonProperty("bio")
    private String _bio;
    
    @JsonProperty("weibo")
    private String _weibo;
    
    @JsonProperty("blog")
    private String _blog;
    
    @JsonProperty("theme_id")
    private Integer _theme_id;
    
    @JsonProperty("state")
    private String _state;
    
    @JsonProperty("created_at")
    private String _created_at;
    
    @JsonProperty("portrait")
    private String _portrait;// 头像
    
    @JsonProperty("is_admin")
    private boolean _isAdmin;
    
    @JsonProperty("can_create_group")
    private boolean _canCreateGroup;

    @JsonProperty("can_create_project")
    private boolean _canCreateProject;

    @JsonProperty("can_create_team")
    private boolean _canCreateTeam;

	public Integer get_id() {
		return _id;
	}

	public void set_id(Integer _id) {
		this._id = _id;
	}

	public String get_username() {
		return _username;
	}

	public void set_username(String _username) {
		this._username = _username;
	}

	public String get_email() {
		return _email;
	}

	public void set_email(String _email) {
		this._email = _email;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public String get_bio() {
		return _bio;
	}

	public void set_bio(String _bio) {
		this._bio = _bio;
	}

	public String get_weibo() {
		return _weibo;
	}

	public void set_weibo(String _weibo) {
		this._weibo = _weibo;
	}

	public String get_blog() {
		return _blog;
	}

	public void set_blog(String _blog) {
		this._blog = _blog;
	}

	public Integer get_theme_id() {
		return _theme_id;
	}

	public void set_theme_id(Integer _theme_id) {
		this._theme_id = _theme_id;
	}

	public String get_state() {
		return _state;
	}

	public void set_state(String _state) {
		this._state = _state;
	}

	public String get_created_at() {
		return _created_at;
	}

	public void set_created_at(String _created_at) {
		this._created_at = _created_at;
	}

	public String get_portrait() {
		return _portrait;
	}

	public void set_portrait(String _portrait) {
		this._portrait = _portrait;
	}

	public boolean is_isAdmin() {
		return _isAdmin;
	}

	public void set_isAdmin(boolean _isAdmin) {
		this._isAdmin = _isAdmin;
	}

	public boolean is_canCreateGroup() {
		return _canCreateGroup;
	}

	public void set_canCreateGroup(boolean _canCreateGroup) {
		this._canCreateGroup = _canCreateGroup;
	}

	public boolean is_canCreateProject() {
		return _canCreateProject;
	}

	public void set_canCreateProject(boolean _canCreateProject) {
		this._canCreateProject = _canCreateProject;
	}

	public boolean is_canCreateTeam() {
		return _canCreateTeam;
	}

	public void set_canCreateTeam(boolean _canCreateTeam) {
		this._canCreateTeam = _canCreateTeam;
	}
}
