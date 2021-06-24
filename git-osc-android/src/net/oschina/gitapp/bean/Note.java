package net.oschina.gitapp.bean;

import java.util.Date;
import org.codehaus.jackson.annotate.JsonProperty;

public class Note {

    public static final String URL = "/notes";

    private Integer _id;
    private String _body;
    private String _attachment;
    private User _author;

    @JsonProperty("created_at")
    private Date _createdAt;

    public Integer getId() {
        return _id;
    }

    public void setId(Integer id) {
        _id = id;
    }

    public String getBody() {
        return _body;
    }

    public void setBody(String body) {
        _body = body;
    }

    public User getAuthor() {
        return _author;
    }

    public void setAuthor(User author) {
        _author = author;
    }

    public Date getCreatedAt() {
        return _createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        _createdAt = createdAt;
    }

    public String getAttachment() {
        return _attachment;
    }

    public void setAttachment(String attachment) {
        _attachment = attachment;
    }
}
