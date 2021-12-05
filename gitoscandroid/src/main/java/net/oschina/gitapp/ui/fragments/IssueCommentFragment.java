package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;
import android.widget.BaseAdapter;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.IssueCommentAdapter;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.GitNote;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragmentOld;

import java.util.List;

/**
 * issue评论列表
 *
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *         <p/>
 *         最后更新
 *         更新者
 * @created 2014-05-14 下午16:57
 */
public class IssueCommentFragment extends BaseSwipeRefreshFragmentOld<GitNote, CommonList<GitNote>> {

    private Project mProject;

    private Issue mIssue;

    public static IssueCommentFragment newInstance(Project project, Issue issue) {
        IssueCommentFragment fragment = new IssueCommentFragment();
        Bundle args = new Bundle();
        args.putSerializable(Contanst.PROJECT, project);
        args.putSerializable(Contanst.COMMIT, issue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mProject = (Project) args.getSerializable(Contanst.PROJECT);
            mIssue = (Issue) args.getSerializable(Contanst.ISSUE);
        }
    }

    @Override
    public BaseAdapter getAdapter(List<GitNote> list) {
        return new IssueCommentAdapter(getActivity(), R.layout.list_item_issue_commtent);
    }

    @Override
    public MessageData<CommonList<GitNote>> asyncLoadList(int page,
                                                          boolean refresh) {
        MessageData<CommonList<GitNote>> msg = null;
        try {
            CommonList<GitNote> list = mApplication.getIssueCommentList(mProject.getId(), mIssue.getId(), page, refresh);
            msg = new MessageData<CommonList<GitNote>>(list);
        } catch (Exception e) {
            e.printStackTrace();
            msg = new MessageData<CommonList<GitNote>>(e);
        }
        return msg;
    }
}
