package net.oschina.gitapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.kymjs.rxvolley.client.HttpCallback;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.CodeFile;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.FileUtils;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PDFActivity extends BaseActivity {

    @InjectView(R.id.pdfView)
    PDFView mPDFView;

    private CodeFile mCodeFile;

    private Project mProject;

    private String mFileName;

    private String mPath;

    private String mRef;

    private String url_link = null;

    public static void show(Context context, Project project, String fileName, String ref, String path) {
        Intent intent = new Intent(AppContext.getInstance(), PDFActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Contanst.PROJECT, project);
        bundle.putString("fileName", fileName);
        bundle.putString("path", path);
        bundle.putString("ref", ref);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pdf);
        ButterKnife.inject(this);
        initView();
    }

    private void initView() {

        Intent intent = getIntent();
        mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
        mFileName = intent.getStringExtra("fileName");
        mPath = intent.getStringExtra("path");
        mRef = intent.getStringExtra("ref");
        load();
    }


    private void load() {
        if (new File(AppContext.getInstance().getFilesDir() + "/pdf_cache/" + mFileName).exists()) {
            loadPdf();
            return;
        }
        GitOSCApi.downloadFile(mProject.getId(), mPath, mRef, new HttpCallback() {
            @Override
            public void onSuccessInAsync(byte[] t) {
                super.onSuccessInAsync(t);
                if (isDestroyed()) {
                    return;
                }
                Log.e("aaaa","aaaa");
                FileUtils.writePDFFile(t, mFileName);
                loadPdf();
            }

            @Override
            public void onPreStart() {
                super.onPreStart();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                Log.e("cccc","cccc");

            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void loadPdf() {
        if (new File(AppContext.getInstance().getFilesDir() + "/pdf_cache/" + mFileName).exists()) {
            Log.e("bbbb","bbbb");
        }
        mPDFView.fromFile(new File(AppContext.getInstance().getFilesDir() + "/pdf_cache/" + mFileName))
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(true)
                .spacing(16)
                .pageSnap(true)
                .pageFling(true)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        if (isDestroyed()) {
                            return;
                        }
//                        mTextCount.setVisibility(View.VISIBLE);
//                        mTextCount.removeCallbacks(this);
//                        mTextCount.setText(String.format("%s/%s", i + 1, i1));
//                        mTextCount.postDelayed(this, 1600);
                    }
                })
                .load();
    }
}
