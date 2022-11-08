package net.oschina.gitapp.media;


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class SpaceGridItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    SpaceGridItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        outRect.top = space;
    }
}