package net.oschina.gitapp.bean;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings("serial")
public class Project extends Entity {

    public static final String URL = "/projects";
    
    @JsonProperty("name")
    private String _name;
    
    @JsonProperty("description")
    private String _description;

    @JsonProperty("default_branch")
    private String _defaultBranch;
    
    @JsonProperty("owner")
    private User _owner;
    
    @JsonProperty("public")
    private boolean _public;
    
    @JsonProperty("path")
    private String _path;

    @JsonProperty("path_with_namespace")
    private String _pathWithNamespace;

    @JsonProperty("issues_enabled")
    private boolean _issuesEnabled;

    @JsonProperty("merge_requests_enabled")
    private boolean _mergeRequestsEnabled;

    @JsonProperty("wall_enabled")
    private boolean _wallEnabled;

    @JsonProperty("wiki_enabled")
    private boolean _wikiEnabled;

    @JsonProperty("created_at")
    private Date _createdAt;

    @JsonProperty("ssh_url_to_repo")
    private String _sshUrl;

    @JsonProperty("http_url_to_repo")
    private String _httpUrl;
    
    @JsonProperty("namespace")
    private Namespace _namespace;
    
    @JsonProperty("last_push_at")
    private Date _last_push_at;
    
    // 项目的父id，不为null则为是fork的项目
    private Integer _parent_id;
    
    //项目的fork数量
    private Integer _forks_count;
    
    //项目的star数量
    private Integer _stars_count;
    
    //项目的语言类型
    private String _language;
    
    public Integer getParent_id() {
		return _parent_id;
	}

	public void setParent_id(Integer parent_id) {
		this._parent_id = parent_id;
	}

	public Integer getForks_count() {
		return _forks_count;
	}

	public void setForks_count(Integer forks_count) {
		this._forks_count = forks_count;
	}

	public Integer getStars_count() {
		return _stars_count;
	}

	public void setStars_count(Integer stars_count) {
		this._stars_count = stars_count;
	}

	public String getLanguage() {
		return _language;
	}

	public void setLanguage(String language) {
		this._language = language;
	}

	public Date getLast_push_at() {
		return _last_push_at;
	}

	public void setLast_push_at(Date last_push_at) {
		this._last_push_at = last_push_at;
	}

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public String getDefaultBranch() {
        return _defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        _defaultBranch = defaultBranch;
    }

    public User getOwner() {
        return _owner;
    }

    public void setOwner(User owner) {
        _owner = owner;
    }

    public String getPath() {
        return _path;
    }

    public void setPath(String path) {
        _path = path;
    }

    public String getPathWithNamespace() {
        return _pathWithNamespace;
    }

    public void setPathWithNamespace(String pathWithNamespace) {
        _pathWithNamespace = pathWithNamespace;
    }

    public boolean isIssuesEnabled() {
        return _issuesEnabled;
    }

    public void setIssuesEnabled(boolean issuesEnabled) {
        _issuesEnabled = issuesEnabled;
    }

    public boolean isMergeRequestsEnabled() {
        return _mergeRequestsEnabled;
    }

    public void setMergeRequestsEnabled(boolean mergeRequestsEnabled) {
        _mergeRequestsEnabled = mergeRequestsEnabled;
    }

    public boolean isWallEnabled() {
        return _wallEnabled;
    }

    public void setWallEnabled(boolean wallEnabled) {
        _wallEnabled = wallEnabled;
    }

    public boolean isWikiEnabled() {
        return _wikiEnabled;
    }

    public void setWikiEnabled(boolean wikiEnabled) {
        _wikiEnabled = wikiEnabled;
    }

    public Date getCreatedAt() {
        return _createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        _createdAt = createdAt;
    }

    public String getSshUrl() {
        return _sshUrl;
    }

    public void setSshUrl(String sshUrl) {
        _sshUrl = sshUrl;
    }

    public String getHttpUrl() {
        return _httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        _httpUrl = httpUrl;
    }

    public Namespace getNamespace() {
        return _namespace;
    }

    public void setNamespace(Namespace namespace) {
        _namespace = namespace;
    }

    public boolean isPublic() {
        return _public;
    }

    public void setPublic(boolean aPublic) {
        _public = aPublic;
    }
}
