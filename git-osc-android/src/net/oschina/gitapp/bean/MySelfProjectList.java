package net.oschina.gitapp.bean;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class MySelfProjectList extends Entity implements PageList<GitlabProject> {

	private int catalog;
	private int pageSize;
	private int count;
	
	private List<GitlabProject> list = new ArrayList<GitlabProject>();
	
	public void setCatalog(int catalog) {
		this.catalog = catalog;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int getCount() {
		return count;
	}

	public int getCatalog() {
		return catalog;
	}

	public int getPageSize() {
		return pageSize;
	}

	@Override
	public List<GitlabProject> getList() {
		return list;
	}

	public void setList(List<GitlabProject> list) {
		this.list = list;
	}
}
