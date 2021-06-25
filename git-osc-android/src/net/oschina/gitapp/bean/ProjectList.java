package net.oschina.gitapp.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目列表
 * @created 2014-05-13
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
@SuppressWarnings("serial")
public class ProjectList extends Entity implements PageList<Project> {
	
	private int pageSize;
	private int count;
	
	private List<Project> list = new ArrayList<Project>();
	
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

	public int getPageSize() {
		return pageSize;
	}

	@Override
	public List<Project> getList() {
		return list;
	}

	public void setList(List<Project> list) {
		this.list = list;
	}
}
