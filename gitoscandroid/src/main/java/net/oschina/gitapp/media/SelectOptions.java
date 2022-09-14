package net.oschina.gitapp.media;


import android.util.Log;

import com.bumptech.glide.load.model.LazyHeaders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
public class SelectOptions {
    private boolean isCrop;
    private int mCropWidth, mCropHeight;
    private Callback mCallback;
    private boolean hasCam;
    private int mSelectCount;
    private List<String> mSelectedImages;
    private LazyHeaders mHeaders;
    private String mSavePath;

    private SelectOptions() {

    }

    public boolean isCrop() {
        return isCrop;
    }

    public int getCropWidth() {
        return mCropWidth;
    }

    public int getCropHeight() {
        return mCropHeight;
    }

    public Callback getCallback() {
        return mCallback;
    }

    public boolean isHasCam() {
        return hasCam;
    }

    public int getSelectCount() {
        return mSelectCount;
    }

    public List<String> getSelectedImages() {
        return mSelectedImages;
    }

    public LazyHeaders getHeaders() {
        return mHeaders;
    }

    public String getSavePath() {
        return mSavePath;
    }

    public static class Builder {
        private boolean isCrop;
        private int cropWidth, cropHeight;
        private Callback callback;
        private boolean hasCam;
        private int selectCount;
        private List<String> selectedImages;
        private LazyHeaders
                .Builder glideHeader;
        private String savePath;

        public Builder() {
            selectCount = 1;
            hasCam = true;
            selectedImages = new ArrayList<>();
        }

        public Builder setCrop(int cropWidth, int cropHeight) {
            Log.e("www","  --  " + cropWidth + "  --  " + cropHeight);
            if (cropWidth <= 0 || cropHeight <= 0){
                this.isCrop = false;
                return this;
            }
            this.isCrop = true;
            this.cropWidth = cropWidth;
            this.cropHeight = cropHeight;
            return this;
        }

        public Builder setCallback(Callback callback) {
            this.callback = callback;
            return this;
        }

        public Builder setHasCam(boolean hasCam) {
            this.hasCam = hasCam;
            return this;
        }

        public Builder setSelectCount(int selectCount) {
            this.selectCount = selectCount <= 0 ? 1 : selectCount;
            return this;
        }

        public Builder setSelectedImages(List<String> selectedImages) {
            if (selectedImages == null || selectedImages.size() == 0) return this;
            this.selectedImages.addAll(selectedImages);
            return this;
        }

        public Builder setSelectedImages(String[] selectedImages) {
            if (selectedImages == null || selectedImages.length == 0) return this;
            if (this.selectedImages == null) this.selectedImages = new ArrayList<>();
            this.selectedImages.addAll(Arrays.asList(selectedImages));
            return this;
        }

        public Builder setGlideHeader(String key, String value) {
            if (glideHeader == null) glideHeader = new LazyHeaders.Builder();
            glideHeader.addHeader(key, value);
            return this;
        }

        public Builder setSavaPath(String path){
            this.savePath = path;
            return this;
        }

        public SelectOptions build() {
            SelectOptions options = new SelectOptions();
            options.hasCam = hasCam;
            options.isCrop = isCrop;
            options.mCropHeight = cropHeight;
            options.mCropWidth = cropWidth;
            options.mCallback = callback;
            options.mSelectCount = selectCount;
            options.mSelectedImages = selectedImages;
            options.mSavePath = savePath;
            if (isCrop) options.mSelectCount = 1;
            if (glideHeader != null) options.mHeaders = glideHeader.build();
            return options;
        }
    }

    public interface Callback {
        void doSelected(String[] images);
    }
}
