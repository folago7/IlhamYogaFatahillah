package net.oschina.gitapp.ui.fragments;

import java.util.List;

import android.util.Log;
import android.widget.BaseAdapter;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ListMySelfProjectAdapter;
import net.oschina.gitapp.bean.GitlabProject;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.bean.MySelfProjectList;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;

/**
 * 个人项目列表Fragment
 * @created 2014-05-12 下午14：24
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 */
public class MySelfListProjectFragment extends BaseSwipeRefreshFragment<GitlabProject, MySelfProjectList> {
	
	public static MySelfListProjectFragment newInstance() {
		return new MySelfListProjectFragment();
	}
	
	@Override
	public BaseAdapter getAdapter(List<GitlabProject> list) {
		return new ListMySelfProjectAdapter<GitlabProject>(getActivity(), list, R.layout.myselfproject_listitem);
	}

	@Override
	public MessageData<MySelfProjectList> asyncLoadList(int page,
			boolean reflash) {
		MessageData<MySelfProjectList> msg = null;
		try {
			Log.i("MySelfViewPagerFragment", "开始取数据......");
			MySelfProjectList list = mApplication.getMySelfProjectList(page);
			Log.i("MySelfViewPagerFragment", list.getCount() + "");
			msg = new MessageData<MySelfProjectList>(list);
		} catch (AppException e) {
			e.makeToast(mApplication);
			e.printStackTrace();
			msg = new MessageData<MySelfProjectList>(e);
		}
		return msg;
	}
}
