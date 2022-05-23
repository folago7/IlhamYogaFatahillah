package net.oschina.gitapp.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;

import net.oschina.gitapp.AppConfig;
import net.oschina.gitapp.api.AsyncHttpHelp;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.CodeTree;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.FileUtils;
import net.oschina.gitapp.common.UIHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.oschina.gitapp.api.AsyncHttpHelp.get;

/**
 * APP缓存文件,/data/data/files
 * Created by 黄海彬 on 2016/3/9
 */
public class FileUtil {
    private ExecutorService executorService;
    private static FileUtil instance = new FileUtil();

    private Context context;
    private String path;

    private FileUtil() {
        executorService = Executors.newFixedThreadPool(3);
    }

    public static FileUtil getInstance() {
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        this.path = context.getFilesDir().getPath() + "/";
    }

    public void saveFile(String fileName, byte[] data) {
        executorService.submit(new FileLoader(fileName, data));
    }

    public File getFile(String fileName) {
        return new File(fileName);
    }

    public void clearFiles() {
        File[] files = context.getFilesDir().listFiles();
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public void openFile(File file) {
        if (file != null) {
            try{
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "*/*");
                context.startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void loadFile(Project project, final CodeTree codeTree){

        String url_link = GitOSCApi.NO_API_BASE_URL + project.getOwner().getUsername() + "/" + project.getPath() + "/blob/master/" + codeTree.getPath();
        AsyncHttpHelp.get(GitOSCApi.PROJECTS + project.getId() + "/repository/files", new HttpCallback(){

            @Override
            public void onSuccessInAsync(byte[] t) {

            }
        });
    }

    class FileLoader implements Runnable {
        private String fileName;//文件名，唯一id
        private byte[] data;//文件数据

        public FileLoader(String fileName, byte[] data) {
            this.fileName = fileName;
            this.data = data;
        }

        @Override
        public void run() {
            try {
                File file = new File(path + fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream os = new FileOutputStream(file);
                os.write(data);
                os.close();
                openFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
