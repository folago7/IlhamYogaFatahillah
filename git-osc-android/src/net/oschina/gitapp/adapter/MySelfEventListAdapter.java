package net.oschina.gitapp.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.bean.Note;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.common.BitmapManager;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
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
	
	// 图像管理线程类
	private BitmapManager bmpManager;
	
	static class ListItemView {
		public ImageView face;//用户头像
		public TextView user_name;
		public TextView content;//更新内容
		public ListView commitLists;// commits的列表
		public TextView date;//更新时间
	}
	
	public MySelfEventListAdapter(Context context, List<Event> data, int resource) {
		super(context, data, resource);
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.mini_avatar));
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
			listItemView.commitLists = (ListView) convertView.findViewById(R.id.enent_listitem_commits_list);
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		Event event = listData.get(position);
		
		// 1.加载项目作者头像
		String portrait = event.getAuthor().getPortrait() == null ? "" : event.getAuthor().getPortrait();
		if (portrait.endsWith("portrait.gif") || StringUtils.isEmpty(portrait)) {
			listItemView.face.setImageResource(R.drawable.mini_avatar);
		} else {
			String portraitURL = URLs.HTTP + URLs.HOST + URLs.URL_SPLITTER + portrait;
			bmpManager.loadBitmap(portraitURL, listItemView.face);
		}
		
		/*if (faceClickEnable) {
			listItemView.face.setOnClickListener(faceClickListener);
		}*/
		
		// 2.显示相关信息
		listItemView.user_name.setText(UIHelper.parseEventTitle(event.getAuthor().getName(), 
				event.getProject().getOwner().getName() + " / " + event.getProject().getName(), event));
		
		//displayContent(listItemView, event);
		SimpleDateFormat f = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date update_time = event.getUpdated_at();
		listItemView.date.setText(StringUtils.friendly_time(f.format(update_time)));
		
		return convertView;
	}
	
	private void displayContent(ListItemView listItemView, Event event) {
		
		switch (event.getAction()) {
		case Event.EVENT_TYPE_CREATED:// 创建了issue
			
			break;
			
		case Event.EVENT_TYPE_UPDATED:
		case Event.EVENT_TYPE_CLOSED:
		case Event.EVENT_TYPE_REOPENED:
		case Event.EVENT_TYPE_JOINED://# User joined project
		case Event.EVENT_TYPE_LEFT://# User left project
		case Event.EVENT_TYPE_FORKED:// fork了项目
			listItemView.content.setTag(null);
			listItemView.commitLists.setTag(null);
			break;
			
		case Event.EVENT_TYPE_PUSHED:// push
			//listItemView.content.setTag(null);
			//listItemView.commitLists.setAdapter(new EventCommitsListAdapter(context, event.getData().getCommits(), R.layout.event_commits_listitem));
			break;
			
		case Event.EVENT_TYPE_COMMENTED:// 评论
			Note note = event.getNote();
			if (note != null && note.getNote() != null) {
				listItemView.content.setText(note.getNote());
			}
			listItemView.commitLists.setTag(null);
			break;
			
		case Event.EVENT_TYPE_MERGED:// 合并
			break;
		default:
			break;
		}
		checkIsShow(listItemView);
	}
	
	private void checkIsShow(ListItemView listItemView) {
		if (listItemView.content.getTag() == null) 
			listItemView.content.setVisibility(View.GONE);
		if (listItemView.commitLists.getTag() == null) 
			listItemView.commitLists.setVisibility(View.GONE);
	}
}
