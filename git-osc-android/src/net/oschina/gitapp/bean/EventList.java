package net.oschina.gitapp.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人动态列表
 * @created 2014-05-20 下午15：08
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
@SuppressWarnings("serial")
public class EventList extends Entity implements PageList<Event> {
	
	private int pageSize;
	private int count;
	
	private List<Event> list = new ArrayList<Event>();
	
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
	public List<Event> getList() {
		return list;
	}

	public void setList(List<Event> list) {
		this.list = list;
	}
}
