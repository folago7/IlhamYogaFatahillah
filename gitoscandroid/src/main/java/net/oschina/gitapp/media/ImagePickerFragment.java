package net.oschina.gitapp.media;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.oschina.gitapp.R;
import net.oschina.gitapp.utils.FileHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImagePickerFragment extends Fragment implements Contract.View, View.OnClickListener,
        BaseRecyclerAdapter.OnItemClickListener {

    RecyclerView mContentView;
    TextView mTextFolder;
    ImageView mImageArrow;
    FrameLayout mLayoutBack;
    View mToolbar;
    TextView mTextDone;
    TextView mTextPreviewView;

    private ImageFolderPopupWindow mFolderPopupWindow;
    private FolderAdapter mImageFolderAdapter;
    private ImageAdapter mImageAdapter;

    private List<Image> mSelectedImage;

    private String mCamImageName;

    private LoaderListener mCursorLoader = new LoaderListener();
    private Contract.Presenter mPresenter;
    private View mRootView;

    private static SelectOptions mOption;

    public static ImagePickerFragment newInstance(SelectOptions options) {
        mOption = options;
        return new ImagePickerFragment();
    }

    @Override
    public void onAttach(Context context) {
        this.mPresenter = (Contract.Presenter) context;
        this.mPresenter.setDataView(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_select_image, container, false);
            initView();
            initData();
        } else {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) parent.removeView(mRootView);
        }
        return mRootView;
    }

    private void initView() {
        mContentView = mRootView.findViewById(R.id.rv_image);
        mTextFolder = mRootView.findViewById(R.id.tv_folder_name);
        mImageArrow = mRootView.findViewById(R.id.iv_arrow);
        mLayoutBack = mRootView.findViewById(R.id.ib_back);
        mTextDone = mRootView.findViewById(R.id.btn_done);
        mTextPreviewView = mRootView.findViewById(R.id.btn_preview);
        mToolbar = mRootView.findViewById(R.id.toolbar);
        mRootView.findViewById(R.id.fl_folder).setOnClickListener(this);
        mLayoutBack.setOnClickListener(this);
        mTextDone.setOnClickListener(this);
        mTextPreviewView.setOnClickListener(this);
    }

    private void initData() {
        if(mOption ==null){
            getActivity().finish();
            return;
        }
        mSelectedImage = new ArrayList<>();
        mContentView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        mContentView.addItemDecoration(new SpaceGridItemDecoration(5));
        mImageAdapter = new ImageAdapter(getContext());
        mImageAdapter.setSingleSelect(mOption.getSelectCount() <= 1);
        mRootView.findViewById(R.id.lay_button).setVisibility(mOption.getSelectCount() == 1 ? View.GONE : View.VISIBLE);
        mImageFolderAdapter = new FolderAdapter(getActivity());
        mContentView.setAdapter(mImageAdapter);
        mContentView.setItemAnimator(null);
        mImageAdapter.setOnItemClickListener(this);
        getLoaderManager().initLoader(0, null, mCursorLoader);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                getActivity().finish();
                break;
            case R.id.btn_done:
                handleResult();
                break;
            case R.id.btn_preview:
                if (mSelectedImage.size() > 0) {
                    ImageGalleryActivity.show(getActivity(), new SelectOptions.Builder()
                            .setSelectedImages(Util.toArray(mSelectedImage))
                            .build(), 0);
                    //ImageGalleryActivity.show(getActivity(), Util.toArray(mSelectedImage), 0, false);
                }
                break;
            case R.id.fl_folder:
                showPopupFolderList();
                break;
        }
    }

    @Override
    public void onItemClick(int position, long itemId) {
        if (mOption.isHasCam()) {
            if (position != 0) {
                handleSelectChange(position);
            } else {
                if (mSelectedImage.size() < mOption.getSelectCount()) {
                    mPresenter.requestCamera();
                } else {
                    Toast.makeText(getActivity(), "最多只能选择 " + mOption.getSelectCount() + " 张图片", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            handleSelectChange(position);
        }
    }

    @Override
    public void onOpenCameraSuccess() {
        openCamera();
    }

    @Override
    public void onCameraPermissionDenied() {

    }

    private void handleSelectSizeChange(int size) {
        if (size > 0) {
            mTextPreviewView.setEnabled(true);
            mTextDone.setEnabled(true);
            mTextDone.setText(String.format("%s(%s)", "完成", size));
        } else {
            mTextPreviewView.setEnabled(false);
            mTextDone.setEnabled(false);
            mTextDone.setText("完成");
        }
    }

    private void handleSelectChange(int position) {
        Image image = mImageAdapter.getItem(position);
        if (image == null) return;
        //如果是多选模式
        final int selectCount = mOption.getSelectCount();
        if (selectCount > 1) {
            if (image.isSelect()) {
                image.setSelect(false);
                mSelectedImage.remove(image);
                mImageAdapter.updateItem(position);
            } else {
                if (mSelectedImage.size() == selectCount) {
                    Toast.makeText(getActivity(), "最多只能选择 " + selectCount + " 张照片", Toast.LENGTH_SHORT).show();
                } else {
                    image.setSelect(true);
                    mSelectedImage.add(image);
                    mImageAdapter.updateItem(position);
                }
            }
            handleSelectSizeChange(mSelectedImage.size());
        } else {
            mSelectedImage.add(image);
            handleResult();
        }
    }

    private void handleResult() {
        if (mSelectedImage.size() != 0) {
            if (mOption.isCrop()) {
                List<String> selectedImage = mOption.getSelectedImages();
                selectedImage.clear();
                selectedImage.add(mSelectedImage.get(0).getPath());
                mSelectedImage.clear();
                CropActivity.show(this, mOption);
            } else {
                mOption.getCallback().doSelected(Util.toArray(mSelectedImage));
                getActivity().finish();
            }
        }
    }

    /**
     * 创建弹出的相册
     */
    private void showPopupFolderList() {
        if (mFolderPopupWindow == null) {
            ImageFolderPopupWindow popupWindow = new ImageFolderPopupWindow(getActivity(), new ImageFolderPopupWindow.Callback() {
                @Override
                public void onSelect(ImageFolderPopupWindow popupWindow, Folder model) {
                    addImagesToAdapter(model.getImages());
                    mContentView.scrollToPosition(0);
                    popupWindow.dismiss();
                    mTextFolder.setText(model.getName());
                }

                @Override
                public void onDismiss() {
                    mImageArrow.setImageResource(R.drawable.ic_arrow_bottom);
                }

                @Override
                public void onShow() {
                    mImageArrow.setImageResource(R.drawable.ic_arrow_top);
                }
            });
            popupWindow.setAdapter(mImageFolderAdapter);
            mFolderPopupWindow = popupWindow;
        }
        mFolderPopupWindow.showAsDropDown(mToolbar);
    }

    private void openCamera(){
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q){
            toOpenCamera();
        }else {

            Uri photoUri = createImageUri();
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (photoUri != null) {
                mCamImageName = photoUri.toString();
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                captureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(captureIntent, 0x03);
            }
        }


    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return getContext().getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }

    /**
     * 打开相机
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void toOpenCamera() {
        // 判断是否挂载了SD卡
        mCamImageName = null;
        String savePath = "";
        if (Util.hasSDCard()) {
            savePath = Util.getCameraPath();
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (TextUtils.isEmpty(savePath)) {
            Toast.makeText(getActivity(), "无法保存照片，请检查SD卡是否挂载", Toast.LENGTH_LONG).show();
            return;
        }

        mCamImageName = Util.getSaveImageFullName();
        File out = new File(savePath, mCamImageName);


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(getContext(), "net.oschina.gitapp.provider", out);
        } else {
            uri = Uri.fromFile(out);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent,
                0x03);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            switch (requestCode) {
                case 0x03:
                    if (mCamImageName == null || mCursorLoader == null){
                        return;
                    }
                    if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
                        getLoaderManager().initLoader(0, null, mCursorLoader);
                    }else {
                        String[] paths = new String[]{Util.getCameraPath() + mCamImageName};
                        MediaScannerConnection.scanFile(getContext(), paths, null, new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {

                            }
                        });
                    }

                    break;
                case 0x04:
                    if (data == null) return;
                    mOption.getCallback().doSelected(new String[]{data.getStringExtra("crop_path")});
                    getActivity().finish();
                    break;
            }
        }
    }

    @SuppressLint("InlinedApi")
    private static final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED};

    private class LoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == 0) {
                //数据库光标加载器
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[5] + " DESC");
            }
            return null;
        }

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        @Override
        public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
            if (data != null && data.getCount() > 0) {

                final ArrayList<Image> images = new ArrayList<>();
                //final List<Folder> imageFolders = new ArrayList<>();
                final Map<String, Folder> imageFolders = new HashMap<>();
                final Folder defaultFolder = new Folder();
                defaultFolder.setName("全部照片");
                defaultFolder.setPath("");
                //imageFolders.add(defaultFolder);


                boolean isFirst = true;
                data.moveToFirst();
                do {
                    String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                    String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                    String folderPath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                    String folderName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                    int id = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
                    //适配Android Q
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        path = MediaStore.Images.Media
                                .EXTERNAL_CONTENT_URI
                                .buildUpon()
                                .appendPath(String.valueOf(id)).build().toString();
                    }

                    Image image = new Image();
                    image.setPath(path);
                    image.setName(name);
                    image.setId(1);

                    if (isFirst) {
                        isFirst = false;
                        defaultFolder.setAlbumPath(image.getPath());
                    }

                    images.add(image);

                    //如果是新拍的照片
                    if (mCamImageName != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && path.equalsIgnoreCase(mCamImageName)) {
                            image.setSelect(true);
                            mSelectedImage.add(image);
                            mCamImageName = null;
                        }else if(mCamImageName.equals(image.getName())){
                            image.setSelect(true);
                            mSelectedImage.add(image);
                            mCamImageName = null;
                        }

                    }

                    //如果是被选中的图片
                    if (mSelectedImage != null && mSelectedImage.size() > 0) {
                        for (Image i : mSelectedImage) {
                            if (i.getPath().equals(image.getPath())) {
                                image.setSelect(true);
                            }
                        }
                    }


                    Folder folder;
                    if (!imageFolders.containsKey(folderPath)) {
                        folder = new Folder();
                        folder.getImages().add(image);
                        folder.setName(folderName);
                        folder.setPath(folderPath);
                        folder.setAlbumPath(image.getPath());//默认相册封面
                        imageFolders.put(folderPath, folder);
                    } else {
                        // 更新
                        Folder f = imageFolders.get(folderPath);
                        f.getImages().add(image);
                    }

                } while (data.moveToNext());

                defaultFolder.getImages().addAll(images);
                List<Folder> folderList = new ArrayList<>();
                folderList.add(defaultFolder);
                for (String key : imageFolders.keySet()) {
                    folderList.add(imageFolders.get(key));
                }

                addImagesToAdapter(images);

                if (mOption.isHasCam()) {
                    defaultFolder.setAlbumPath(images.size() > 1 ? images.get(1).getPath() : null);
                } else {
                    defaultFolder.setAlbumPath(images.size() > 0 ? images.get(0).getPath() : null);
                }
                mImageFolderAdapter.resetItem(folderList);

                //删除掉不存在的，在于用户选择了相片，又去相册删除
                if (mSelectedImage.size() > 0) {
                    List<Image> rs = new ArrayList<>();
                    for (Image i : mSelectedImage) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            if (FileHelper.isAndroidQFileExists(getContext(), i.getPath())) {
                                rs.add(i);
                            }
                        } else {
                            if (FileHelper.exists(i.getPath())) {
                                rs.add(i);
                            }
                        }

                    }
                    //mSelectedImage.removeAll(rs);
                }

                if (mOption.getSelectCount() == 1 && mCamImageName != null) {
                    handleResult();
                }

                handleSelectSizeChange(mSelectedImage.size());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    private void addImagesToAdapter(ArrayList<Image> images) {
        mImageAdapter.clear();
        if (mOption.isHasCam()) {
            Image cam = new Image();
            mImageAdapter.addItem(cam);
        }
        mImageAdapter.addAll(images);
    }

    @Override
    public void onDestroy() {
        mOption = null;
        super.onDestroy();
    }
}
