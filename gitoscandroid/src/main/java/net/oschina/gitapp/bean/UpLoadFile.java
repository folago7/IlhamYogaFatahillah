package net.oschina.gitapp.bean;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 上传文件实体类
 * @created 2014-07-03
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
@SuppressWarnings("serial")
public class UpLoadFile implements Serializable {
	
	@JsonProperty("success")
	private boolean _success;
	
	@JsonProperty("msg")
	private String _msg;
	
	@JsonProperty("files")
	private List<GitFile> _files;

	public boolean isSuccess() {
		return _success;
	}

	public void setSuccess(boolean _success) {
		this._success = _success;
	}

	public String getMsg() {
		return _msg;
	}

	public void setMsg(String _msg) {
		this._msg = _msg;
	}

	public List<GitFile> getFiles() {
		return _files;
	}

	public void setFiles(List<GitFile> _files) {
		this._files = _files;
	}

	@SuppressWarnings("serial")
	public static class GitFile implements Serializable{

		@JsonProperty("filename")
		private String _filename;
		
		@JsonProperty("title")
		private String _title;
		
		@JsonProperty("url")
		private String _url;

		public String getFilename() {
			return _filename;
		}

		public void setFilename(String _filename) {
			this._filename = _filename;
		}

		public String getTitle() {
			return _title;
		}

		public void setTitle(String _title) {
			this._title = _title;
		}

		public String getUrl() {
			return _url;
		}

		public void setUrl(String _url) {
			this._url = _url;
		}
	}
}
