package net.oschina.gitapp.bean;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

import android.widget.AbsListView.SelectionBoundsAdjuster;

/**
 * commit实体类
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
@SuppressWarnings("serial")
public class Commit implements Serializable {

    public final static String URL = "/commits";
    
    private String _id;

	private String _title;
	
	@JsonProperty("message")
	private String _message;

	@JsonProperty("short_id")
    private String _shortId;
	
	@JsonProperty("author")
	private User _author;

    @JsonProperty("created_at")
    private Date _createdAt;
    
    public String getId() {
		return _id;
	}

	public void setId(String id) {
		this._id = id;
	}
    
    public String getShortId() {
        return _shortId;
    }

    public void setShortId(String shortId) {
        _shortId = shortId;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }
    
    public User getAuthor() {
		return _author;
	}

	public void setAuthor(User author) {
		this._author = author;
	}

	public String getMessage() {
		return _message;
	}

	public void setMessage(String message) {
		this._message = message;
	}

    public Date getCreatedAt() {
        return _createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        _createdAt = createdAt;
    }
}
