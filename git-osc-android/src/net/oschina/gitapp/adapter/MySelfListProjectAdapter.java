package net.oschina.gitapp.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

import net.oschina.gitapp.R;
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
 * 个人项目列表适配器
 * @created 2014-05-12
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class MySelfListProjectAdapter extends MyBaseAdapter<Project> {
	
	private BitmapManager bmpManager;
	
	static class ListItemView {
		public ImageView face;
		public ImageView flag;// 项目标识
		public TextView project_name;
		public TextView description;//项目描述
		public TextView updateData;//日期
		public ImageView languageImage;
		public TextView language;//类型
		public TextView star;//加星数
		public TextView fork;//fork数
	}
	
	public MySelfListProjectAdapter(Context context, List<Project> data, int resource) {
		super(context, data, resource);
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.widget_dface_loading));
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
			listItemView.face = (ImageView) convertView.findViewById(R.id.myself_project_listitem_userface);
			listItemView.flag = (ImageView) convertView.findViewById(R.id.myself_project_listitem_flag);
			listItemView.project_name = (TextView) convertView.findViewById(R.id.myself_project_listitem_name);
			listItemView.description = (TextView) convertView.findViewById(R.id.myself_project_listitem_description);
			listItemView.updateData = (TextView) convertView.findViewById(R.id.myself_project_listitem_date);
			listItemView.languageImage = (ImageView) convertView.findViewById(R.id.myself_project_listitem_language_image);
			listItemView.language = (TextView) convertView.findViewById(R.id.myself_project_listitem_language);
			listItemView.star = (TextView) convertView.findViewById(R.id.myself_project_listitem_star);
			listItemView.fork = (TextView) convertView.findViewById(R.id.myself_project_listitem_fork);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		Project project = listData.get(position);
		
		// 加载项目作者头像
		String portrait = project.getOwner().getPortrait() == null ? "" : project.getOwner().getPortrait();
		if (portrait.endsWith("portrait.gif") || StringUtils.isEmpty(portrait)) {
			listItemView.face.setImageResource(R.drawable.widget_dface);
		} else {
			String portraitURL = URLs.HTTP + URLs.HOST + URLs.URL_SPLITTER + project.getOwner().getPortrait();
			bmpManager.loadBitmap(portraitURL, listItemView.face);
		}
		
		listItemView.project_name.setText(project.getOwner().getName() + " / " + project.getName());
		
		Date last_push_at = project.getLast_push_at() != null ? project.getLast_push_at() : project.getCreatedAt();
		listItemView.updateData.setText(StringUtils.friendly_time(last_push_at));
		
		// 判断项目的类型，显示不同的图标（私有项目、公有项目、fork项目）
		
		// 判断是否有项目的介绍
		String descriptionStr = project.getDescription();
		if (StringUtils.isEmpty(descriptionStr)) {
			listItemView.description.setVisibility(View.GONE);
		} else {
			listItemView.description.setText(descriptionStr);
		}
		
		// 显示项目的star、fork、language信息
		listItemView.star.setText(project.getStars_count().toString());
		listItemView.fork.setText(project.getForks_count().toString());
		String language = project.getLanguage() != null ? project.getLanguage() : "";
		if (project.getLanguage() != null) {
			listItemView.language.setText(language);
		} else {
			listItemView.language.setVisibility(View.GONE);
			listItemView.languageImage.setVisibility(View.GONE);
		}
		
		return convertView;
	}

}
