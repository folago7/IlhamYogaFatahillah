package net.oschina.gitapp.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 个人动态列表适配器
 * @created 2014-05-20 下午15：28
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class MySelfEventListAdapter extends MyBaseAdapter<Event> {
	
	static class ListItemView {
		public ImageView face;//用户头像
		public TextView user_name;
		public TextView content;//更新内容
		public TextView date;//更新时间
	}
	
	public MySelfEventListAdapter(Context context, List<Event> data, int resource) {
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
			listItemView.face = (ImageView) convertView.findViewById(R.id.event_listitem_userface);
			listItemView.user_name = (TextView) convertView.findViewById(R.id.event_listitem_username);
			listItemView.content = (TextView) convertView.findViewById(R.id.event_listitem_content);
			listItemView.date = (TextView) convertView.findViewById(R.id.event_listitem_date);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		Event e = (Event) listData.get(position);
		
		listItemView.user_name.setText(UIHelper.parseEventTitle(String.valueOf(e.getAuthor_id()), String.valueOf(e.getProject_id()), e.getTarget_type(), e));
		
		// 1.加载项目作者头像
		
		// 2.显示相关信息
		SimpleDateFormat f = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date update_time = e.getUpdated_at();
		listItemView.date.setText(StringUtils.friendly_time(f.format(update_time)));
		
		return convertView;
	}

}
