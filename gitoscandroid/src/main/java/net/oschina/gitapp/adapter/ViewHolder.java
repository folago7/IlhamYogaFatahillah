package net.oschina.gitapp.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.SpannableString;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.oschina.gitapp.R;
import net.oschina.gitapp.common.BitmapManager;
import net.oschina.gitapp.common.StringUtils;

/**
 * 通用性极高的ViewHolder
 * Created by 火蚁 on 15/4/8.
 */
public class ViewHolder {
    // 用于存储listview item的容器
    private SparseArray<View> mViews;

    // item根view
    private View mConvertView;

    private Context mContext;

    public ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mViews = new SparseArray<View>();
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        this.mConvertView.setTag(this);
        this.mContext = context;
    }

    /***
     * 获取一个viewHolder
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public  static ViewHolder getViewHolder(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context,parent,layoutId,position);
        }
        return (ViewHolder)convertView.getTag();
    }

    // 通过一个viewId来获取一个view
    public <T extends View> T getView(int viewId) {

        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T)view;
    }

    // 返回viewHolder的容器类
    public View getConvertView() {
        return this.mConvertView;
    }

    // 给TextView设置文字
    public void setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
    }

    // 给TextView设置文字
    public void setText(int viewId, SpannableString text) {
        TextView tv = getView(viewId);
        tv.setText(text);
    }

    // 给TextView设置文字
    public void setText(int viewId, int textRes) {
        TextView tv = getView(viewId);
        tv.setText(textRes);
    }

    public void setText(int viewId, String text, int emptyRes) {
        TextView tv = getView(viewId);
        if (StringUtils.isEmpty(text)) {
            tv.setText(emptyRes);
        } else {
            tv.setText(text);
        }
    }

    public void setText(int viewId, String text, String emptyText) {
        TextView tv = getView(viewId);
        if (StringUtils.isEmpty(text)) {
            tv.setText(emptyText);
        } else {
            tv.setText(text);
        }
    }

    // 给ImageView设置图片资源
    public void setImageResource(int viewId, int resId) {
        ImageView iv = getView(viewId);
        iv.setImageResource(resId);
    }

    public void setImageForUrl(int viewId, String imgUrl) {
        ImageView iv = getView(viewId);
        BitmapManager bitmapManager = new BitmapManager(BitmapFactory.decodeResource(
                this.mContext.getResources(), R.drawable.widget_dface_loading));
        bitmapManager.loadBitmap(imgUrl, iv);
    }
}
