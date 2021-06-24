package net.oschina.gitapp.ui.fragments;

import java.util.List;

import android.util.Log;
import android.widget.BaseAdapter;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.MySelfListProjectAdapter;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.bean.ProjectList;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;

/**
 * 个人项目列表Fragment
 * @created 2014-05-12 下午14：24
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class MySelfListProjectFragment extends BaseSwipeRefreshFragment<Project, ProjectList> {
	
	public static MySelfListProjectFragment newInstance() {
		return new MySelfListProjectFragment();
	}
	
	@Override
	public BaseAdapter getAdapter(List<Project> list) {
		return new MySelfListProjectAdapter(getActivity(), list, R.layout.myselfproject_listitem);
	}

	@Override
	public MessageData<ProjectList> asyncLoadList(int page,
			boolean reflash) {
		MessageData<ProjectList> msg = null;
		try {
			Log.i("MySelfViewPagerFragment", "开始取数据......");
			ProjectList list = mApplication.getMySelfProjectList(page);
			Log.i("MySelfViewPagerFragment", list.getCount() + "");
			msg = new MessageData<ProjectList>(list);
		} catch (AppException e) {
			e.makeToast(mApplication);
			e.printStackTrace();
			msg = new MessageData<ProjectList>(e);
		}
		return msg;
	}
}
