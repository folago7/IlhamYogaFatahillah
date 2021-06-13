package net.oschina.gitapp.ui.basefragment;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.interfaces.OnBaseListFragmentResumeListener;
import net.oschina.gitapp.widget.PullToRefreshListView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 列表Fragment基础类
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-30 11:09
 */
public abstract class BaseListFragment extends Fragment {

	protected ListView listView;
	protected View list_footer;
	protected TextView list_foot_more;
	protected ProgressBar list_foot_progress;
	protected AppContext appContext;// 全局Context
	protected OnBaseListFragmentResumeListener baseListFragmentResumeListener;
	protected boolean hasInit;
	
	public BaseListFragment(
			OnBaseListFragmentResumeListener baseListFragmentResumeListener) {
		this.baseListFragmentResumeListener = baseListFragmentResumeListener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.listview_fragment, container,
				false);
		TextView textView = (TextView) view.findViewById(R.id.textView);
		listView = (ListView) view.findViewById(R.id.listView);
		listView.setEmptyView(textView);

		list_footer = inflater.inflate(R.layout.listview_footer, null);
		list_foot_more = (TextView) list_footer
				.findViewById(R.id.listview_foot_more);
		list_foot_progress = (ProgressBar) list_footer
				.findViewById(R.id.listview_foot_progress);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		appContext = (AppContext) getActivity().getApplication();
	}

	@Override
	public void onResume() {
		super.onResume();
		if(baseListFragmentResumeListener != null) {
			baseListFragmentResumeListener.onBaseListFragmentResume(this);
		}
	}
	
	@Override
	public void onDestroyView() {
		setHasInit(false);
		super.onDestroyView();
	}

	public boolean isHasInit() {
		return hasInit;
	}

	public void setHasInit(boolean hasInit) {
		this.hasInit = hasInit;
	}

	public abstract void showList();
}