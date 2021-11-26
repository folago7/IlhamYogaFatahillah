package net.oschina.gitapp.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 通用性极高的ViewHolder
 * Created by 火蚁 on 15/4/8.
 */
public class ViewHolder {
    // 用于存储listview item的容器
    private SparseArray<View> mViews;

    // item根view
    private View mConvertView;

    public ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mViews = new SparseArray<View>();
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        this.mConvertView.setTag(this);
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
}
