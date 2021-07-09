package net.oschina.gitapp.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.CodeTree;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.common.BitmapManager;
import net.oschina.gitapp.common.StringUtils;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 项目代码树列表适配器
 * @created 2014-05-26 下午17：25
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class ProjectCodeTreeListAdapter extends MyBaseAdapter<CodeTree> {
	
	static class ListItemView {
		public ImageView tag;
		public TextView name;
	}
	
	public ProjectCodeTreeListAdapter(Context context, List<CodeTree> data, int resource) {
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
			listItemView.tag = (ImageView) convertView.findViewById(R.id.projectcodetree_listitem_tag);
			listItemView.name = (TextView) convertView.findViewById(R.id.projectcodetree_listitem_name);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		CodeTree code = listData.get(position);
		
		// 1.显示相关的信息
		String type = code.getType();
		if (type.equalsIgnoreCase(CodeTree.blob)) {
			listItemView.tag.setBackgroundResource(R.drawable.file);
		} else {
			listItemView.tag.setBackgroundResource(R.drawable.folder);
		}
		listItemView.name.setText(code.getName());
		
		return convertView;
	}
}
