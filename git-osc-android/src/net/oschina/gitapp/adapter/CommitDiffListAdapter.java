package net.oschina.gitapp.adapter;

import java.util.ArrayList;
import java.util.List;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.CommitDiff;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.UIHelper;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 项目Commit diff列表适配器
 * 
 * @created 2014-06-13
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 *         最后更新： 更新者：
 */
public class CommitDiffListAdapter {

	private LinearLayout commitGroupView;// commitDiff的view总和
	private Context context;// 运行上下文
	private List<CommitDiff> listData;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源id
	
	private Project project;
	
	private Commit commit;

	/**
	 * 实例化MyBaseAdapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public CommitDiffListAdapter(Context context, List<CommitDiff> data,
			int resource, LinearLayout view) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listData = data;
		this.commitGroupView = view;
	}
	
	public void setData(Project project, Commit commit) {
		this.project = project;
		this.commit = commit;
	}

	public void add(int position) {

		final CommitDiff commitDiff = listData.get(position);

		// 获取list_item布局文件的视图
		View convertView = listContainer.inflate(this.itemViewResource, null);

		// 获取控件对象
		View ll = convertView.findViewById(R.id.commit_diff_ll);
		
		ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UIHelper.showCommitDiffFileDetail(context, project, commit, commitDiff);
			}
		});
		
		TextView file_name = (TextView) convertView
				.findViewById(R.id.commit_diff_file_name);
		TextView folder = (TextView) convertView
				.findViewById(R.id.commit_diff_folder);
		TextView stats = (TextView) convertView
				.findViewById(R.id.commit_diff_stats);
		TextView file = (TextView) convertView
				.findViewById(R.id.commit_diff_file);

		file_name.setText(getFileName(commitDiff));
		folder.setText(getFilePath(commitDiff));
		String diff = getCommitFileDiff(commitDiff);
		if (!TextUtils.isEmpty(diff)) {
			file.setText(diff);
		} else {
			file.setVisibility(View.GONE);
		}

		commitGroupView.addView(convertView);
	}

	public void notifyDataSetChanged() {
		int sum = listData.size() >= 30 ? 30 : listData.size();
		for (int i = 0; i < sum; i++) {
			add(i);
		}
		commitGroupView.invalidate();
	}

	private String getFileName(CommitDiff commitDiff) {
		String res = commitDiff.getNew_path();
		int index = commitDiff.getNew_path().lastIndexOf("/");
		if (index > 0) {
			res = res.substring(index + 1);
		}
		return res;
	}

	private String getFilePath(CommitDiff commitDiff) {
		String res = commitDiff.getNew_path();
		int index = commitDiff.getNew_path().lastIndexOf("/");
		if (index > 0) {
			res = res.substring(0, index);
		}
		return res;
	}

	private String getCommitFileDiff(CommitDiff commitDiff) {
		String fileName = getFileName(commitDiff);

		List<String> elementFilter = new ArrayList<String>();
		elementFilter.add(".png");
		elementFilter.add(".jar");
		elementFilter.add(".jpeg");
		elementFilter.add(".gif");
		elementFilter.add(".gif");
		elementFilter.add(".wav");
		elementFilter.add(".mp3");
		elementFilter.add(".mp4");
		elementFilter.add(".ogg");
		if (elementFilter
				.contains(fileName.substring(fileName.lastIndexOf(".")))) {
			return "";
		}
		String diff = commitDiff.getDiff().substring(
				commitDiff.getDiff().indexOf("@"));
		return diff;
	}

	// 判断是否是图片
	private boolean isPicture(CommitDiff commitDiff) {
		String fileName = getFileName(commitDiff);
		List<String> elementFilter = new ArrayList<String>();
		elementFilter.add(".png");
		elementFilter.add(".jpg");
		elementFilter.add(".jpeg");
		elementFilter.add(".gif");
		if (elementFilter
				.contains(fileName.substring(fileName.lastIndexOf(".")))) {
			return true;
		}
		return false;
	}
}
