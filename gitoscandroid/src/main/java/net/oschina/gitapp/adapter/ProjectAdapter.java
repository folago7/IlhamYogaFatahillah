package net.oschina.gitapp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.UIHelper;

import java.util.List;

/**
 * Created by 火蚁 on 15/4/9.
 */
public class ProjectAdapter extends CommonAdapter<Project> {

    public ProjectAdapter(Context context, List datas,int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder vh, final Project project) {
        // 2.显示相关信息
        vh.setText(R.id.exploreproject_listitem_title, project.getOwner().getName() + " / " + project.getName());

        // 判断是否有项目的介绍
        String descriptionStr = project.getDescription();
        vh.setText(R.id.exploreproject_listitem_description, project.getDescription(), R.string.msg_project_empty_description);

        vh.setText(R.id.exploreproject_listitem_star, project.getStars_count().toString());
        vh.setText(R.id.exploreproject_listitem_fork, project.getForks_count().toString());
        // 显示项目的star、fork、language信息
        String language = project.getLanguage() != null ? project.getLanguage() : "";
        if (project.getLanguage() != null) {
            vh.setText(R.id.exploreproject_listitem_language, language);
        } else {
            vh.getView(R.id.exploreproject_listitem_language).setVisibility(View.GONE);
            vh.getView(R.id.exploreproject_listitem_language_image).setVisibility(View.GONE);
        }

        // 1.加载头像
        ImageView face = vh.getView(R.id.exploreproject_listitem_userface);
        String portraitURL = project.getOwner().getNew_portrait();
        if (portraitURL.endsWith("portrait.gif")) {
            face.setImageResource(R.drawable.mini_avatar);
        } else {
            vh.setImageForUrl(R.id.exploreproject_listitem_userface, portraitURL);
        }
        face.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                User user = project.getOwner();
                if (user == null) {
                    return;
                }
                UIHelper.showUserInfoDetail(mContext, user, null);
            }
        });
    }
}
