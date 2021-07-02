package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;
import net.oschina.gitapp.R;
import net.oschina.gitapp.ui.BaseFragmentActivity;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.ui.basefragment.BaseFragment;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;

public class ProjectViewPageFragment extends BaseActionBarActivity {
	
	public static ProjectViewPageFragment newInstance() {
        return new ProjectViewPageFragment();
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}
