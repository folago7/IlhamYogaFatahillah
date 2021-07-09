package net.oschina.gitapp.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Project;
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
public class MySelfListProjectAdapter extends MyBaseAdapter<Project> {
	
	static class ListItemView {
		public ImageView flag;// 项目标识
		public TextView project_name;
		public TextView updateData;//日期
	}
	
	public MySelfListProjectAdapter(Context context, List<Project> data, int resource) {
		super(context, data, resource);
	}

	@Override
	public int getCount() {
		return listData.size();
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
			listItemView.updateData = (TextView) convertView.findViewById(R.id.myself_project_listitem_date);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		Project project = listData.get(position);
		
		listItemView.project_name.setText(project.getOwner().getName() + " / " + project.getName());
		
		SimpleDateFormat f = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date last_push_at = project.getLast_push_at() != null ? project.getLast_push_at() : project.getCreatedAt();
		listItemView.updateData.setText(StringUtils.friendly_time(f.format(last_push_at)));
		
		// 判断项目的类型，显示不同的图标（私有项目、公有项目、fork项目）
		
		return convertView;
	}

}
