package net.oschina.gitapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ProjectAdapter;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.util.JsonUtils;
import net.oschina.gitapp.widget.TipInfoLayout;

import org.apache.http.Header;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 搜索项目界面
 *
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-07-10
 */
public class SearchActivity extends BaseActivity implements
        OnQueryTextListener, OnItemClickListener, AbsListView.OnScrollListener {

    private final int MESSAGESTATEFULL = 0;//已加载全部状态
    private final int MESSAGESTATEMORE = 1;//可以加载更多状态
    @InjectView(R.id.search_view)
    SearchView searchView;
    @InjectView(R.id.listView)
    ListView listView;
    @InjectView(R.id.tip_info)
    TipInfoLayout tipInfo;

    private InputMethodManager imm;

    private View mFooterView;

    private ProgressBar mFooterLoading;

    private TextView mFooterMsg;

    private ProjectAdapter adapter;

    private String mKey;

    private int mMessageState = MESSAGESTATEMORE;

    private boolean isLoading = false;

    private int mCurrentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);
        initView();
        steupList();
    }

    private void initView() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);
        tipInfo.setHiden();
        tipInfo.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load(mKey, 1);
            }
        });
    }

    private void steupList() {
        adapter = new ProjectAdapter(this,
                R.layout.list_item_project);
        mFooterView = LayoutInflater.from(this).inflate(
                R.layout.listview_footer, null);
        mFooterLoading = (ProgressBar) mFooterView.findViewById(R.id.listview_foot_progress);
        mFooterMsg = (TextView) mFooterView.findViewById(R.id.listview_foot_more);
        mFooterLoading.setVisibility(View.VISIBLE);
        mFooterMsg.setText(R.string.load_ing);
        listView.addFooterView(mFooterView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextChange(String arg0) {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String arg0) {
        mKey = arg0;
        adapter.clear();
        load(arg0, 1);
        listView.setSelection(0);
        imm.hideSoftInputFromWindow(listView.getWindowToken(), 0);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // 点击了底部
        if (view == mFooterView) {
            return;
        }
        Project p = adapter.getItem(position);
        if (p != null) {
            UIHelper.showProjectDetail(this, p, null);
        }
    }

    /**
     * 设置底部有更多数据的状态
     */
    private void setFooterHasMoreState() {
        if (mFooterView != null) {
            mFooterLoading.setVisibility(View.GONE);
            mFooterMsg.setText(R.string.load_more);
        }
    }

    /**
     * 设置底部已加载全部的状态
     */
    private void setFooterFullState() {
        if (mFooterView != null) {
            mMessageState = MESSAGESTATEFULL;
            mFooterLoading.setVisibility(View.GONE);
            mFooterMsg.setText(R.string.load_full);
        }
    }

    /**
     * 设置底部加载中的状态
     */
    private void setFooterLoadingState() {
        if (mFooterView != null) {
            mFooterLoading.setVisibility(View.VISIBLE);
            mFooterMsg.setText(R.string.load_ing);
        }
    }

    private void load(final String key, final int page) {
        GitOSCApi.searchProjects(key, page, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                List<Project> projects = JsonUtils.getList(Project[].class, responseBody);
                tipInfo.setHiden();
                if (projects.size() > 0) {
                    adapter.addItem(projects);
                    if (projects.size() < 15) {
                        setFooterFullState();
                    }
                    listView.setVisibility(View.VISIBLE);
                } else {
                    if (page == 1 || page == 0) {
                        listView.setVisibility(View.GONE);
                        tipInfo.setEmptyData("未找到相关的项目");
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                tipInfo.setLoadError();
            }

            @Override
            public void onStart() {
                super.onStart();
                isLoading = true;
                if (page != 0 && page != 1) {
                    setFooterLoadingState();
                } else {
                    tipInfo.setLoading();
                    listView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                isLoading = false;
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (adapter == null || adapter.getCount() == 0) {
            return;
        }
        // 数据已经全部加载，或数据为空时，或正在加载，不处理滚动事件
        if (mMessageState == MessageData.MESSAGE_STATE_FULL
                || mMessageState == MessageData.MESSAGE_STATE_EMPTY
                || isLoading) {
            return;
        }
        // 判断是否滚动到底部
        boolean scrollEnd = false;
        try {
            if (view.getPositionForView(mFooterView) == view
                    .getLastVisiblePosition())
                scrollEnd = true;
        } catch (Exception e) {
            scrollEnd = false;
        }

        if (scrollEnd) {
            ++mCurrentPage;
            load(mKey, mCurrentPage);
            setFooterLoadingState();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
