package net.oschina.gitapp.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.HtmlRegexpUtils;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;

import java.util.List;

/**
 * 个人动态列表适配器
 * 
 * @created 2014-05-20 下午15：28
 * @author 火蚁（http://my.oschina.net/LittleDY）
 */
public class MySelfEventListAdapter extends CommonAdapter<Event> {

    public MySelfEventListAdapter(Context context, List<Event> data,
            int resource) {
        super(context, data, resource);
    }

    @Override
    public void convert(ViewHolder vh, final Event event) {
        displayContent(vh, event);
    }

    private void displayContent(ViewHolder vh, final Event event) {

        // 1.加载头像
        String portraitURL = event.getAuthor().getNew_portrait();
        if (portraitURL.endsWith("portrait.gif")) {
            vh.setImageResource(R.id.event_listitem_userface, R.drawable.mini_avatar);
        } else {
            vh.setImageForUrl(R.id.event_listitem_userface, portraitURL);
        }

        vh.getView(R.id.event_listitem_userface).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                User user = event.getAuthor();
                if (user == null) {
                    return;
                }
                UIHelper.showUserInfoDetail(mContext, user, null);
            }
        });

        // 2.显示相关信息
        SpannableString title = UIHelper.parseEventTitle(event
                .getAuthor().getName(), event.getProject().getOwner().getName()
                + " / " + event.getProject().getName(), event);
        vh.setText(R.id.event_listitem_username, title);

        // commits信息的显示
        LinearLayout commitLists = vh.getView(R.id.event_listitem_commits_list);
        commitLists.setVisibility(View.GONE);
        commitLists.removeAllViews();
        if (event.getData() != null) {
            List<Commit> commits = event.getData().getCommits();
            if (commits != null && commits.size() > 0) {
                showCommitInfo(commitLists, commits);
                commitLists.setVisibility(View.VISIBLE);
            }
        }

        TextView content = vh.getView(R.id.event_listitem_content);
        content.setVisibility(View.GONE);
        // 评论的内容
        if (event.getEvents().getNote() != null
                && event.getEvents().getNote().getNote() != null) {
            content.setText(HtmlRegexpUtils.filterHtml(event
                    .getEvents().getNote().getNote()));
            content.setVisibility(View.VISIBLE);
        }

        // issue的title
        if (event.getEvents().getIssue() != null
                && event.getEvents().getNote() == null) {
            content.setText(event.getEvents().getIssue()
                    .getTitle());
            content.setVisibility(View.VISIBLE);
        }

        // pr的title
        if (event.getEvents().getPull_request() != null
                && event.getEvents().getNote() == null) {
            content.setText(event.getEvents().getPull_request()
                    .getTitle());
            content.setVisibility(View.VISIBLE);
        }
        vh.setText(R.id.event_listitem_date, StringUtils.friendly_time(event
                .getUpdated_at()));
    }

    private void showCommitInfo(LinearLayout layout, List<Commit> commits) {
        if (commits.size() >= 2) {
            addCommitItem(layout, commits.get(0));
            addCommitItem(layout, commits.get(1));
        } else {
            for (Commit commit : commits) {
                addCommitItem(layout, commit);
            }
        }
    }

    /**
     * 添加commit项
     * 
     * @param layout
     * @param commit
     */
    private void addCommitItem(LinearLayout layout, Commit commit) {
        View v = mInflater.inflate(R.layout.event_commits_listitem, null);
        ((TextView) v.findViewById(R.id.event_commits_listitem_commitid))
                .setText(commit.getId());
        ((TextView) v.findViewById(R.id.event_commits_listitem_username))
                .setText(commit.getAuthor().getName());
        ((TextView) v.findViewById(R.id.event_commits_listitem_message))
                .setText(commit.getMessage());
        layout.addView(v);
    }
}
