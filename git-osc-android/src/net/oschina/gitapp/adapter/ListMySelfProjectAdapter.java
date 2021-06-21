package net.oschina.gitapp.adapter;

import java.util.List;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.GitlabProject;
import net.oschina.gitapp.common.StringUtils;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 个人项目列表适配器
 * @created 2014-05-12
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class ListMySelfProjectAdapter<T> extends MyBaseAdapter<T> {
	
	static class ListItemView {
		public ImageView flag;// 项目标识
		public TextView project_name;
		public TextView data;//日期
	}
	
	public ListMySelfProjectAdapter(Context context, List<T> data, int resource) {
		super(context, data, resource);
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ListItemView  listItemView = null;
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//获取控件对象
			listItemView.flag = (ImageView) convertView.findViewById(R.id.myself_project_listitem_flag);
			listItemView.project_name = (TextView) convertView.findViewById(R.id.myself_project_listitem_name);
			listItemView.data = (TextView) convertView.findViewById(R.id.myself_project_listitem_date);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		GitlabProject project = (GitlabProject) listData.get(position);
		listItemView.project_name.setText(project.getName());
		listItemView.data.setText(StringUtils.friendly_time(project.getCreatedAt().toLocaleString()));
		
		// 判断项目的类型，显示不同的图标（私有项目、公有项目、fork项目）
		
		return convertView;
	}

}
