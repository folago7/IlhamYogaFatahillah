package net.oschina.gitapp.adapter;

import android.content.Context;
import android.widget.TextView;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.CodeTree;
import net.oschina.gitapp.util.TypefaceUtils;

/**
 * 项目代码树列表适配器
 * @created 2014-05-26 下午17：25
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class ProjectCodeTreeAdapter extends CommonAdapter<CodeTree> {
	
	public ProjectCodeTreeAdapter(Context context, int resource) {
		super(context, resource);
	}

    @Override
    public void convert(ViewHolder vh, CodeTree code) {
        // 1.显示相关的信息
        String type = code.getType();
        int tagRes = R.string.oct_folder;
        if (type.equalsIgnoreCase(CodeTree.TYPE_BLOB)) {
            tagRes = R.string.oct_file;
        }
        TypefaceUtils.setOcticons((TextView)vh.getView(R.id.projectcodetree_listitem_tag));
        vh.setText(R.id.projectcodetree_listitem_name, code.getName());
    }
}
