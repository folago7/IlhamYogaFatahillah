package net.oschina.gitapp.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kymjs.rxvolley.client.HttpCallback;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.UpLoadFile;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.BroadcastController;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.dialog.LightProgressDialog;
import net.oschina.gitapp.media.ImageGalleryActivity;
import net.oschina.gitapp.media.ImagePickerActivity;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.utils.JsonUtils;
import net.oschina.gitapp.widget.CircleImageView;

import java.io.File;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 火蚁
 * on 15/5/4.
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
        if (mUser == null)
            return;
        GitOSCApi.getUserInfo(mUser.getId(), new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                try {
                    User user = JsonUtils.toBean(User.class, t);
                    initUserInfo(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                if (mUser != null) {
                    initUserInfo(mUser);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initUserInfo(User mUser) {
        tvName.setText(mUser.getName());
        if (TextUtils.isEmpty(mUser.getBio())) {
            tvDescription.setText("暂无填写");
        } else {
            tvDescription.setText(mUser.getBio());
        }

        tvJointime.setText(mUser.getCreated_at().substring(0, 10));

        // 加载用户头像
//        String portrait = mUser.getNew_portrait() == null || mUser.getNew_portrait().equals("null")
//                ? "" : mUser.getNew_portrait();
//        if (portrait.endsWith("portrait.gif") || StringUtils.isEmpty(portrait)) {
//            ivPortrait.setImageResource(R.drawable.mini_avatar);
//        } else {
//            new BitmapCore.Builder().url(mUser.getNew_portrait()).view(ivPortrait).doTask();
//        }

        Glide.with(this)
                .load(mUser.getNew_portrait())
                .asBitmap()
                .fitCenter()
                .into(ivPortrait);

        tvFollowers.setText(mUser.getFollow().getFollowers() + "");
        tvStared.setText(mUser.getFollow().getStarred() + "");
        tvFollowing.setText(mUser.getFollow().getFollowing() + "");
        tvWatched.setText(mUser.getFollow().getWatched() + "");
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
                ImageGalleryActivity.show(this,new String[]{mUser.getNew_portrait()},0);
                break;
            case R.id.ll_user_portrait:
                ImagePickerActivity.show(this, images -> uploadNewPhoto(new File(images[0])), true);
                break;
            case R.id.btn_logout:
                loginOut();
                break;
            default:
                break;
        }
    }


    private void uploadNewPhoto(final File file) {
        final AlertDialog loading = LightProgressDialog.create(this, "正在上传头像...");
        loading.setCanceledOnTouchOutside(false);
        try {
            GitOSCApi.upLoadFile(file, new HttpCallback() {
                @Override
                public void onSuccess(Map<String, String> headers, byte[] t) {
                    super.onSuccess(headers, t);
                    UpLoadFile upLoadFile = JsonUtils.toBean(UpLoadFile.class, t);
                    if (upLoadFile != null && upLoadFile.isSuccess()) {
                        final String protraitUrl = upLoadFile.getFiles().get(0).getUrl();
                        Log.e("po0", protraitUrl);
                        GitOSCApi.updateUserProtrait(protraitUrl.replace("https","http"), new HttpCallback() {
                            @Override
                            public void onSuccess(Map<String, String> headers, byte[] t) {
                                super.onSuccess(headers, t);
                                if (isDestroyed())
                                    return;
                                Log.e("onSuccess", protraitUrl);
                                UIHelper.toastMessage(MyInfoDetailActivity.this, "头像已更新");
                                Glide.with(MyInfoDetailActivity.this)
                                        .load(protraitUrl)
                                        .asBitmap()
                                        .into(ivPortrait);
                                AppContext.getInstance().setProperty(Contanst
                                        .PROP_KEY_NEWPORTRAIT, protraitUrl);
                                BroadcastController.sendUserChangeBroadcase(MyInfoDetailActivity
                                        .this);
                            }

                            @Override
                            public void onFailure(int errorNo, String strMsg) {
                                super.onFailure(errorNo, strMsg);
                                if (isDestroyed())
                                    return;
                                UIHelper.toastMessage(MyInfoDetailActivity.this, errorNo +
                                        "更新头像失败");
                            }

                            @Override
                            public void onFinish() {
                                super.onFinish();
                                if (isDestroyed())
                                    return;
                                loading.dismiss();
                            }

                            @Override
                            public void onPreStart() {
                                super.onPreStart();
                                if (isDestroyed())
                                    return;
                                loading.setMessage("正在更新头像...");
                            }
                        });
                    } else {
                        UIHelper.toastMessage(MyInfoDetailActivity.this, "头像上传失败");
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    UIHelper.toastMessage(MyInfoDetailActivity.this, "上传图片失败, 网络错误");
                }

                @Override
                public void onPreStart() {
                    super.onPreStart();
                    loading.show();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    loading.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loginOut() {
        AppContext.getInstance().logout();
        BroadcastController.sendUserChangeBroadcase(AppContext.getInstance());
        this.finish();
    }
}
