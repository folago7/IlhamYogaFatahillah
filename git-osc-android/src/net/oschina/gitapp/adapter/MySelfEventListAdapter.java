package net.oschina.gitapp.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.ApiClient;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.BitmapManager;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.LoginActivity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
	
	// 图像管理线程类
	private BitmapManager bmpManager;
	
	static class ListItemView {
		public ImageView face;//用户头像
		public TextView user_name;
		public TextView content;//更新内容
		public TextView date;//更新时间
	}
	
	public MySelfEventListAdapter(Context context, List<Event> data, int resource) {
		super(context, data, resource);
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.image_loading));
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
		
		Event event = (Event) listData.get(position);
		
		/*//异步登录
    	new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg =new Message();
				try {
					event.setUser(ApiClient.getUser(null, event.getAuthor_id()));
					event.setProject(ApiClient.getProject(null, event.getProject_id()));
	                msg.what = 1;
	                msg.obj = event;
	            } catch (Exception e) {
			    	msg.what = -1;
			    	msg.obj = e;
	            }
				return msg;
			}
			
			@Override
			protected void onPreExecute() {
				
			}
			
			@Override
			protected void onPostExecute(Message msg) {
				if(msg.what == 1){
					user_name.setText(UIHelper.parseEventTitle(event.getUser().getName(), 
							event.getProject().getOwner().getName() + "/" + event.getProject().getName(), event.getTarget_type(), event));
					
					if (portrait.endsWith("portrait.gif") || StringUtils.isEmpty(portrait)) {
						user_face.setImageResource(R.drawable.mini_avatar);
					} else {
						String portraitURL = URLs.HTTP + URLs.HOST + URLs.URL_SPLITTER + portrait;
						bmpManager.loadBitmap(portraitURL, user_face);
					}
				} else if(msg.what == 0){
					
				} else if(msg.what == -1){
					((AppException)msg.obj).makeToast(context);
				}
			}
		}.execute();*/
		// 1.加载项目作者头像
		/*String portrait = event.getUser().getPortrait() == null ? "" : event.getUser().getPortrait();
		if (portrait.endsWith("portrait.gif") || StringUtils.isEmpty(portrait)) {
			listItemView.face.setImageResource(R.drawable.mini_avatar);
		} else {
			String portraitURL = URLs.HTTP + URLs.HOST + URLs.URL_SPLITTER + portrait;
			bmpManager.loadBitmap(portraitURL, listItemView.face);
		}*/
		
		/*try {
			User u = ApiClient.getUser((AppContext) this.context, e.getAuthor_id());
		} catch (AppException e1) {
			e1.printStackTrace();
		}*/
		/*String portrait = project.getOwner().getPortrait() == null ? "" : project.getOwner().getPortrait();
		if (portrait.endsWith("portrait.gif") || StringUtils.isEmpty(portrait)) {
			listItemView.face.setImageResource(R.drawable.mini_avatar);
		} else {
			String portraitURL = URLs.HTTP + URLs.HOST + URLs.URL_SPLITTER + project.getOwner().getPortrait();
			bmpManager.loadBitmap(portraitURL, listItemView.face);
		}*/
		/*if (faceClickEnable) {
			listItemView.face.setOnClickListener(faceClickListener);
		}*/
		
		// 2.显示相关信息
		SimpleDateFormat f = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date update_time = event.getUpdated_at();
		listItemView.date.setText(StringUtils.friendly_time(f.format(update_time)));
		
		return convertView;
	}

}
