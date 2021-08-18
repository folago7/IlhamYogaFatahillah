package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ViewPageFragmentAdapter;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseFragment;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;

public class UserInfoViewPageFragment extends BaseViewPagerFragment {
	
	private final int MENU_MORE_ID = 1;
	private final int MENU_CREATE_ID = 0;
	
	private User mUser;
	
	private int mCurrentItem;
	
	public static UserInfoViewPageFragment newInstance(User user) {
		UserInfoViewPageFragment fragment = new UserInfoViewPageFragment();
		Bundle args = new Bundle();
		args.putSerializable(Contanst.USER, user);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		Bundle args = getArguments();
		if (args != null) {
			mUser = (User) args.getSerializable(Contanst.USER);
		}
		String[] title = getResources().getStringArray(R.array.userinfo_title_array);
		
		adapter.addTab(title[0], "user_events", UserListEventFragment.class, args);
		adapter.addTab(title[1], "user_projects", UserListProjectFragment.class, args);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		//setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem createOption = menu.add(0, MENU_CREATE_ID, MENU_CREATE_ID, "创建Issue");
		createOption.setIcon(R.drawable.action_create);
		
		MenuItem moreOption = menu.add(1, MENU_MORE_ID, MENU_MORE_ID, "更多");
		moreOption.setIcon(R.drawable.abc_ic_menu_moreoverflow_normal_holo_dark);
		
		MenuItemCompat.setShowAsAction(createOption, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		MenuItemCompat.setShowAsAction(moreOption,
				MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		/*int id = item.getItemId();
		switch (id) {
		case MENU_MORE_ID:
			break;
		case MENU_CREATE_ID:
			// 新增issue
			UIHelper.showIssueEditOrCreate(getGitApplication(), mProject, null);
			break;
		}*/
		return super.onOptionsItemSelected(item);
	}
}
