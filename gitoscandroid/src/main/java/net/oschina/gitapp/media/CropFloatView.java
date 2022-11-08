package net.oschina.gitapp.media;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Region;
import android.view.View;


 class CropFloatView extends View {

     private CropDrawable mCropDrawable;
    private Rect mFloatRect = new Rect();
    private boolean isCrop;

    public CropFloatView(Context context) {
        super(context);
        mCropDrawable = new CropDrawable(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        cropDrawable();
        canvas.save();
        canvas.clipRect(mFloatRect, Region.Op.DIFFERENCE);
        canvas.drawColor(Color.parseColor("#a0000000"));
        canvas.restore();
        mCropDrawable.draw(canvas);
    }

    private void cropDrawable() {
        if (isCrop) return;
        mCropDrawable.setRegion(mFloatRect);
        isCrop = true;
    }

    public void setCropWidth(int mCropWidth) {

        mCropDrawable.setCropWidth(mCropWidth);
    }

    public void setCropHeight(int mCropHeight) {

        mCropDrawable.setCropHeight(mCropHeight);
    }
}
