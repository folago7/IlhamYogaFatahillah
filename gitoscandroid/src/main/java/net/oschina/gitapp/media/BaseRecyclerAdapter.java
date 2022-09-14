package net.oschina.gitapp.media;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {

    LayoutInflater mInflater;
    private List<T> mItems;
    RequestManager mLoader;
    private OnItemClickListener onItemClickListener;
    private OnClickListener onClickListener;

     BaseRecyclerAdapter(Context context) {
        this.mItems = new ArrayList<>();
        mLoader = Glide.with(context);
        mInflater = LayoutInflater.from(context);
        onClickListener = new OnClickListener() {
            @Override
            public void onClick(int position, long itemId) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(position, itemId);
            }
        };

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder holder = onCreateDefaultViewHolder(parent, viewType);
        if (holder != null) {
            holder.itemView.setTag(holder);
            holder.itemView.setOnClickListener(onClickListener);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder(holder, mItems.get(position), position);
    }

    abstract RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type);

    abstract void onBindViewHolder(RecyclerView.ViewHolder holder, T item, int position);

    @Override
    public int getItemCount() {
        return mItems.size();
    }

     void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

     void addAll(List<T> items) {
        if (items != null && items.size() > 0) {
            mItems.addAll(items);
            notifyItemRangeInserted(mItems.size(), items.size());
        }
    }

     final void addItem(T item) {
        if (item != null) {
            this.mItems.add(item);
            notifyItemChanged(mItems.size());
        }
    }


     void updateItem(int position) {
        if (getItemCount() > position) {
            notifyItemChanged(position);
        }
    }



     final T getItem(int position) {
        if (position < 0 || position >= mItems.size())
            return null;
        return mItems.get(position);
    }

     final void resetItem(List<T> items) {
        if (items != null) {
            clear();
            addAll(items);
        }
    }

     final void clear() {
        this.mItems.clear();
        notifyDataSetChanged();
    }


     static abstract class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            onClick(holder.getAdapterPosition(), holder.getItemId());
        }

        public abstract void onClick(int position, long itemId);
    }


     interface OnItemClickListener {
        void onItemClick(int position, long itemId);
    }
}
