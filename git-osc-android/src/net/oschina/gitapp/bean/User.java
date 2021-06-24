package net.oschina.gitapp.bean;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

public class User {
	public static final String URL = "/users";
	
    @JsonProperty("id")
    private Integer _id;
    
    @JsonProperty("username")
    private String _username;
    
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

	public Integer getId() {
		return _id;
	}

	public void setId(Integer _id) {
		this._id = _id;
	}

	public String getUsername() {
		return _username;
	}

	public void setUsername(String _username) {
		this._username = _username;
	}
	
	public String getName() {
		return _name;
	}

	public void setName(String _name) {
		this._name = _name;
	}

	public String getBio() {
		return _bio;
	}

	public void setBio(String _bio) {
		this._bio = _bio;
	}

	public String getWeibo() {
		return _weibo;
	}

	public void setWeibo(String _weibo) {
		this._weibo = _weibo;
	}

	public String getBlog() {
		return _blog;
	}

	public void setBlog(String _blog) {
		this._blog = _blog;
	}

	public Integer getTheme_id() {
		return _theme_id;
	}

	public void setTheme_id(Integer _theme_id) {
		this._theme_id = _theme_id;
	}

	public String getState() {
		return _state;
	}

	public void setState(String _state) {
		this._state = _state;
	}

	public String getCreated_at() {
		return _created_at;
	}

	public void setCreated_at(String _created_at) {
		this._created_at = _created_at;
	}

	public String getPortrait() {
		return _portrait;
	}

	public void setPortrait(String _portrait) {
		this._portrait = _portrait;
	}

	public boolean isIsAdmin() {
		return _isAdmin;
	}

	public void setIsAdmin(boolean _isAdmin) {
		this._isAdmin = _isAdmin;
	}

	public boolean isCanCreateGroup() {
		return _canCreateGroup;
	}

	public void setCanCreateGroup(boolean _canCreateGroup) {
		this._canCreateGroup = _canCreateGroup;
	}

	public boolean isCanCreateProject() {
		return _canCreateProject;
	}

	public void setCanCreateProject(boolean _canCreateProject) {
		this._canCreateProject = _canCreateProject;
	}

	public boolean isCanCreateTeam() {
		return _canCreateTeam;
	}

	public void setCanCreateTeam(boolean _canCreateTeam) {
		this._canCreateTeam = _canCreateTeam;
	}
}
