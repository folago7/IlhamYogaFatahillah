package net.oschina.gitapp.ui;

import net.oschina.gitapp.R;
import net.oschina.gitapp.ui.fragments.ExploreViewPagerFragment;
import net.oschina.gitapp.ui.fragments.NoticeViewPagerFragment;
import net.oschina.gitapp.ui.fragments.SettingViewPagerFragment;
import net.oschina.gitapp.ui.fragments.MySelfViewPagerFragment;
import net.oschina.gitapp.ui.fragments.SuggestViewPagerFragment;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import net.oschina.gitapp.interfaces.*;

/**
 * 导航菜单栏
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-29
 */
public class DrawerNavigation extends ListFragment {

    private Context mContext;
    private View mSavedView;

    private Integer mScheduledPosition;
    private View mScheduledView;

    protected static NavigationTransactionListener sNavigationTransactionListener;

    // Menu Entries for the Navigation Drawer.
    private static final int MENU_EXPLORE = 0;// 发现
    private static final int MENU_MYSELF = 1;// 我的
    private static final int MENU_NOTICE = 2;// 通知
    private static final int MENU_SETTING = 3;// 设置
    private static final int MENU_SUGGEST = 4;// 建议
    private static final int MENU_EXIT = 5;// 退出

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.slidingmenu_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();

        // Get Menu icons and Menu strings for the Navigation Drawer.
        TypedArray menuIcons = getResources().obtainTypedArray(R.array.navigation_drawer_icons);
        String[] menuItems = getResources().getStringArray(R.array.navigation_drawer_strings);

        // Populate the ListView with a custom Adapter to include icons.
        CustomListAdapter mAdapter = new CustomListAdapter(getActivity(), R.layout.row_slidingmenu, R.id.slidingmenu_text, menuItems, menuIcons);
        // 注意给listview设置头部需要在设置适配器之前
        setListAdapter(mAdapter);
        
        sNavigationTransactionListener = new NavigationTransactionListener();
    }


    @Override
    public void onListItemClick(ListView listview, View v, int position, long id) {
        mScheduledPosition = position;
        mScheduledView = v;

        if (MainActivity.sToggleListener != null) {
            MainActivity.sToggleListener.onShowAbove();
        }
    }

    private void switchFragments(Fragment fragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content_container, fragment)
                .commit();
    }

    private void highlightSelectedItem(View v) {
        setSelected(null, false);
        setSelected(v, true);
    }

    private void setSelected(View v, boolean selected) {
        View view;

        // If both are null, cancel the method call.
        if (v == null && mSavedView == null) {
            return;
        }

        if (v != null) {
            mSavedView = v;
            view = mSavedView;

        } else {
            view = mSavedView;
        }

        if (selected) {
            ViewCompat.setHasTransientState(view, true);
            view.setBackgroundColor(getResources().getColor(R.color.accent_color));

        } else {
            ViewCompat.setHasTransientState(view, false);
            view.setBackgroundColor(getResources().getColor(R.color.transparent));
        }
    }

    private void navigate(int scheduledPosition, View scheduledView) {
        switch (scheduledPosition) {
            case MENU_EXPLORE:// 发现
                switchFragments(ExploreViewPagerFragment.newInstance());
                highlightSelectedItem(scheduledView);
                break;

            case MENU_MYSELF:// 我的
                switchFragments(MySelfViewPagerFragment.newInstance());
                highlightSelectedItem(scheduledView);
                break;

            case MENU_NOTICE:// 通知
                switchFragments(NoticeViewPagerFragment.newInstance());
                highlightSelectedItem(scheduledView);
                break;

            case MENU_SETTING:// 设置
                switchFragments(SettingViewPagerFragment.newInstance());
                highlightSelectedItem(scheduledView);
                break;

            case MENU_SUGGEST:// 建议
                switchFragments(SuggestViewPagerFragment.newInstance());
                highlightSelectedItem(scheduledView);
                break;

            case MENU_EXIT:// 退出程序
                switchFragments(SuggestViewPagerFragment.newInstance());
                highlightSelectedItem(scheduledView);
                break;

            default:
                switchFragments(ExploreViewPagerFragment.newInstance());
                highlightSelectedItem(scheduledView);
                break;
        }
    }

    public final class NavigationTransactionListener implements Interfaces.NavigationDrawerListener {

        @Override
        public void onDrawerClosed() {
            if (mScheduledPosition != null && mScheduledView != null) {
                navigate(mScheduledPosition, mScheduledView);

                mScheduledPosition = null;
                mScheduledView = null;
            }
        }

        @Override
        public void onDrawerOpened() {

        }
    }

    private final class CustomListAdapter extends ArrayAdapter<String> {
        private Activity mActivity;
        private TypedArray iconArray;
        private String[] textArray;

        /**
         * The Constructor for the CustomListAdapter.
         *
         * @param activity           The corresponding Activity.
         * @param resource           The layout for the {@link android.widget.ListView}.
         * @param textViewResourceId The {@link android.widget.TextView} Resource ID for the displayed Text.
         * @param text               The Text which shall be displayed.
         * @param icons              The Icons which shall be displayed.
         */
        public CustomListAdapter(Activity activity, int resource, int textViewResourceId, String[] text, TypedArray icons) {
            super(activity, resource, textViewResourceId, text);

            // Declare local class Variables.
            this.mActivity = activity;
            this.iconArray = icons;
            this.textArray = text;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mLayoutInflater = mActivity.getLayoutInflater();
            ViewHolder mViewHolder;

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.row_slidingmenu, null);

                mViewHolder = new ViewHolder();
                mViewHolder.icon = (ImageView) convertView.findViewById(R.id.slidingmenu_icon);
                mViewHolder.text = (TextView) convertView.findViewById(R.id.slidingmenu_text);

                convertView.setTag(mViewHolder);

            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }

            mViewHolder.icon.setImageDrawable(iconArray.getDrawable(position));
            mViewHolder.text.setText(textArray[position]);

            if (position == 0) {
                setSelected(convertView, true);
            }

            return convertView;
        }

    }

    /**
     * Simple ViewHolder.
     */
    private static class ViewHolder {
        TextView text;
        ImageView icon;
    }
}
