package net.oschina.gitapp.adapter;

import android.widget.BaseAdapter;

/**
 * 适配器基础类
 * @author 火蚁（http://my.oschina.net/LittleDY）
 */
public abstract class MyBaseAdapter extends BaseAdapter {
	//标识LinkView上的链接
	private boolean isLinkViewClick = false;

	public boolean isLinkViewClick() {
		return isLinkViewClick;
	}

	public void setLinkViewClick(boolean isLinkViewClick) {
		this.isLinkViewClick = isLinkViewClick;
	}

}
