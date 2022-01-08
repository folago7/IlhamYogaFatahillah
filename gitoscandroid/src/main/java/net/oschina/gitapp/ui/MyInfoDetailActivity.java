package net.oschina.gitapp.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.BroadcastController;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.photoBrowse.PhotoBrowseActivity;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.widget.CircleImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 火蚁 on 15/5/4.
 */
public class MyInfoDetailActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.iv_portrait)
    CircleImageView ivPortrait;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_jointime)
    TextView tvJointime;
    @InjectView(R.id.tv_description)
    TextView tvDescription;
    @InjectView(R.id.tv_followers)
    TextView tvFollowers;
    @InjectView(R.id.tv_stared)
    TextView tvStared;
    @InjectView(R.id.tv_following)
    TextView tvFollowing;
    @InjectView(R.id.tv_watched)
    TextView tvWatched;
    @InjectView(R.id.btn_logout)
    Button btnLogout;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo_detail);
        ButterKnife.inject(this);
        initData();
    }

    private void initData() {
        mUser = AppContext.getInstance().getLoginInfo();
        if (mUser != null) {
            tvName.setText(mUser.getName());
            if (TextUtils.isEmpty(mUser.getWeibo())) {
                tvDescription.setText("暂无填写");
            } else {
                tvDescription.setText(mUser.getBio());
            }

            tvJointime.setText(mUser.getCreated_at().substring(0, 10));

            String portrait = mUser.getPortrait() == null || mUser.getPortrait().equals("null") ? "" : mUser.getPortrait();
            if (portrait.endsWith("portrait.gif") || StringUtils.isEmpty(portrait)) {
                ivPortrait.setImageResource(R.drawable.mini_avatar);
            } else {
                ImageLoader.getInstance().displayImage(mUser.getNew_portrait(), ivPortrait);
            }

            tvFollowers.setText(mUser.getFollow().getFollowers() + "");
            tvStared.setText(mUser.getFollow().getStarred() + "");
            tvFollowing.setText(mUser.getFollow().getFollowing() + "");
            tvWatched.setText(mUser.getFollow().getWatched() + "");
        }
    }

    @Override
    @OnClick({R.id.iv_portrait, R.id.ll_user_portrait, R.id.ll_user_nickname, R.id.ll_user_jointime,
            R.id.ll_user_description, R.id.ll_followers, R.id.ll_stared, R.id.ll_following,
            R.id.ll_watched, R.id.btn_logout})
    public void onClick(View v) {
        if (mUser == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_portrait:
                PhotoBrowseActivity.showPhotoBrowse(this, new String[]{mUser.getNew_portrait()}, 0);
                break;
            case R.id.ll_user_portrait:

                break;
            case R.id.btn_logout:
                loginOut();
                break;
            default:
                break;
        }
    }

    private void loginOut() {
        AppContext.getInstance().logout();
        BroadcastController.sendUserChangeBroadcase(AppContext.getInstance());
        this.finish();
    }
}
