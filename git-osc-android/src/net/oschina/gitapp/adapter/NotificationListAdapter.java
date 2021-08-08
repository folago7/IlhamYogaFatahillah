package net.oschina.gitapp.adapter;

import java.util.Date;
import java.util.List;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Notification;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.common.BitmapManager;
import net.oschina.gitapp.common.StringUtils;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 通知列表适配器
 * @created 2014-07-07
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class NotificationListAdapter extends MyBaseAdapter<Notification> {
	
	private BitmapManager bmpManager;
	
	static class ListItemView {
		public ImageView face;
		public TextView user_name;
		public TextView title;
		public TextView date;//日期
	}
	
	public NotificationListAdapter(Context context, List<Notification> data, int resource) {
		super(context, data, resource);
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.widget_dface_loading));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ListItemView  listItemView = null;
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//获取控件对象
			listItemView.face = (ImageView) convertView.findViewById(R.id.notification_listitem_userface);
			listItemView.user_name = (TextView) convertView.findViewById(R.id.notification_listitem_name);
			listItemView.title = (TextView) convertView.findViewById(R.id.notification_listitem_title);
			listItemView.date = (TextView) convertView.findViewById(R.id.notification_listitem_date);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		initInfo(listItemView, position);
		return convertView;
	}
	
	private void initInfo(ListItemView listItemView, int position) {
		Notification notification = listData.get(position);
		
		// 加载项目作者头像
		String portrait = notification.getUserinfo().getPortrait() == null ? "" : notification.getUserinfo().getPortrait();
		if (portrait.endsWith("portrait.gif") || StringUtils.isEmpty(portrait)) {
			listItemView.face.setImageResource(R.drawable.widget_dface);
		} else {
			String portraitURL = URLs.HTTP + URLs.HOST + URLs.URL_SPLITTER + notification.getUserinfo().getPortrait();
			bmpManager.loadBitmap(portraitURL, listItemView.face);
		}
		
		listItemView.user_name.setText(notification.getUserinfo().getName());
		
		listItemView.title.setText(notification.getTitle());
		
		listItemView.date.setText(StringUtils.friendly_time(notification.getCreated_at()));
		
	}
}
