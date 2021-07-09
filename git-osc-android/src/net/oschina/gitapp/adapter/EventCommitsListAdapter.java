package net.oschina.gitapp.adapter;

import java.util.List;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Commit;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 个人动态Commits列表适配器
 * @created 2014-05-23 上午11：11
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class EventCommitsListAdapter extends MyBaseAdapter<Commit> {
	
	static class ListItemView {
		public TextView commitId;//commit的id
		public TextView user_name;
		public TextView message;//commits信息
		
	}
	
	public EventCommitsListAdapter(Context context, List<Commit> data, int resource) {
		super(context, data, resource);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView  listItemView = null;
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			
			//获取控件对象
			listItemView.commitId = (TextView) convertView.findViewById(R.id.event_commits_listitem_commitid);
			listItemView.user_name = (TextView) convertView.findViewById(R.id.event_commits_listitem_username);
			listItemView.message = (TextView) convertView.findViewById(R.id.event_commits_listitem_message);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		// 显示相关信息
		Commit commit = listData.get(position);
		listItemView.commitId.setText(commit.getId());
		listItemView.user_name.setText(commit.getAuthor().getName());
		listItemView.message.setText(commit.getMessage() == null ? "" : commit.getMessage());
		
		return convertView;
	}
}
