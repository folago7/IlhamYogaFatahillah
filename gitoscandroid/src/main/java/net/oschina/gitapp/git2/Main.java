package net.oschina.gitapp.git2;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

import net.oschina.gitapp.R;
import net.oschina.gitapp.ui.basefragment.SupportFragment;
import net.oschina.gitapp.ui.fragments.ExploreViewPagerFragment;
import net.oschina.gitapp.ui.fragments.MySelfViewPagerFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author kymjs (http://www.kymjs.com/) on 11/19/15.
 */
public class Main extends AppCompatActivity implements View.OnClickListener {

    protected SupportFragment currentSupportFragment;

    @InjectView(R.id.tab1)
    RadioButton tab1;
    @InjectView(R.id.tab2)
    RadioButton tab2;
    @InjectView(R.id.tab3)
    RadioButton tab3;

    private SupportFragment content1;
    private SupportFragment content2;
    private SupportFragment content3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_main);
        ButterKnife.inject(this);
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);

        content1 = new ExploreViewPagerFragment();
        content2 = new MySelfViewPagerFragment();
        content3 = new ExploreViewPagerFragment();
        changeFragment(content1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab1:
                changeFragment(content1);
                break;
            case R.id.tab2:
                changeFragment(content2);
                break;
            case R.id.tab3:
                changeFragment(content3);
                break;
        }
    }

    /**
     * 用Fragment替换视图
     *
     * @param targetFragment 用来替换的Fragment
     */
    public void changeFragment(SupportFragment targetFragment) {
        if (targetFragment.equals(currentSupportFragment)) {
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction.add(R.id.main_content, targetFragment, targetFragment.getClass().getName());
        }
        if (targetFragment.isHidden()) {
            transaction.show(targetFragment);
            targetFragment.onChange();
        }
        if (currentSupportFragment != null && currentSupportFragment.isVisible()) {
            transaction.hide(currentSupportFragment);
        }
        currentSupportFragment = targetFragment;
        transaction.commit();
    }
}
