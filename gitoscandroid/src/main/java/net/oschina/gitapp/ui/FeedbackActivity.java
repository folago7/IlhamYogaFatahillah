package net.oschina.gitapp.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kymjs.rxvolley.client.HttpCallback;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.UpLoadFile;
import net.oschina.gitapp.common.ImageUtils;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.util.JsonUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 反馈界面
 * Created by thanatosx on 3/1/16.
 */
public class FeedbackActivity extends BaseActivity{

    private static final int REQUEST_CODE_PICK_IMAGE = 110;

    @InjectView(R.id.radio_group) RadioGroup mRadioGroup;
    @InjectView(R.id.et_input) EditText mInput;
    @InjectView(R.id.iv_pick_image) ImageView mPickImage;

    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.inject(this);
    }

    /**
     * 选择图片
     */
    @OnClick(R.id.iv_pick_image) void pickImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_PICK_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode != REQUEST_CODE_PICK_IMAGE) return;

        Uri imageURI = data.getData();
        try {

            Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
            file = new File(ImageUtils.getAbsoluteImagePath(this, imageURI));
            if (!file.exists()){
                file = null;
                return;
            }
            if (file.length() > 1024*1024){
                Toast.makeText(this, "请选择1M以下的图片", Toast.LENGTH_SHORT).show();
                file = null;
                return;
            }
            mPickImage.setImageBitmap(image);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "读取图片异常", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 提交反馈
     */
    @OnClick(R.id.btn_submit) void submit(){
        String content = mInput.getText().toString();
        if (content.trim().equals("")){
            Toast.makeText(this, "请输入点什么吧", Toast.LENGTH_SHORT).show();
            return;
        }
        int id = mRadioGroup.getCheckedRadioButtonId();
        String title;
        Date date = new Date();
        if (id == R.id.rb_program_error){
            title = String.format("[Android客户端-%s-%d]", "程序错误", date.getTime());
        }else{
            title = String.format("[Android客户端-%s-%d]", "功能建议", date.getTime());
        }

        // 是否要上传文件
        if (file == null){
            submitFeedback(title, content);
        }else{
            if (file.exists()){
                try {
                    uploadFilesAndSubmit(title, content, file);
                } catch (Exception e) {
                    submitFeedback(title, content);
                }
            }else{
                submitFeedback(title, content);
            }
        }
        Toast.makeText(this, "正在后台发送~", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void uploadFilesAndSubmit(final String title, final String content, File file) throws Exception {
        GitOSCApi.upLoadFile(file, new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                UpLoadFile upLoadFile = JsonUtils.toBean(UpLoadFile.class, t);
                if (upLoadFile != null && upLoadFile.isSuccess()) {
                    String fullContent = content + String.format("    \n![](%s)", upLoadFile.getFiles().get(0).getUrl());
                    submitFeedback(title, fullContent);
                }else{
                    Toast.makeText(AppContext.getInstance(),
                            upLoadFile == null ? "上传图片失败" : upLoadFile.getMsg(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                Toast.makeText(AppContext.getInstance(), "上传图片失败~", Toast.LENGTH_SHORT).show();
            }


        });
    }

    private void submitFeedback(String title, String content){
        GitOSCApi.feedback(title, content, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Toast.makeText(AppContext.getInstance(), "我们已经收到你的建~(≧▽≦)/~", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                Toast.makeText(AppContext.getInstance(), "反馈失败~", Toast.LENGTH_SHORT).show();
            }

        });
    }

}
