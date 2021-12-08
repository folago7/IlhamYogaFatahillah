package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;

import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.CommitCommentdapter;
import net.oschina.gitapp.adapter.CommonAdapter;
import net.oschina.gitapp.bean.Comment;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;
import net.oschina.gitapp.util.JsonUtils;

import java.util.List;

/**
 * commit评论列表
 * @created 2014-05-14 下午16:57
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class CommitCommentFragment extends BaseSwipeRefreshFragment<Comment> {
	
	private Project mProject;
	
	private Commit mCommit;
	
	public static CommitCommentFragment newInstance(Project project, Commit commit) {
		CommitCommentFragment fragment = new CommitCommentFragment();
		Bundle args = new Bundle();
		args.putSerializable(Contanst.PROJECT, project);
		args.putSerializable(Contanst.COMMIT, commit);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mProject = (Project) args.getSerializable(Contanst.PROJECT);
			mCommit = (Commit) args.getSerializable(Contanst.COMMIT);
		}
	}

    @Override
    public CommonAdapter<Comment> getAdapter() {
        return new CommitCommentdapter(getActivity(), R.layout.list_item_commit_comment);
    }

    @Override
    public List<Comment> getDatas(byte[] responeString) {
        return JsonUtils.getList(Comment[].class, responeString);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onItemClick(int position, Comment data) {

    }


	public MessageData<CommonList<Comment>> asyncLoadList(int page,
			boolean refresh) {
		MessageData<CommonList<Comment>> msg = null;
		try {
			CommonList<Comment> list = mApplication.getCommitCommentList(mProject.getId(), mCommit.getId(), refresh);
			msg = new MessageData<CommonList<Comment>>(list);
		} catch (AppException e) {
			e.makeToast(mApplication);
			e.printStackTrace();
			msg = new MessageData<CommonList<Comment>>(e);
		}
		return msg;
	}
}
