package net.oschina.gitapp.util;

import android.view.View;

/**
 * Created by 火蚁 on 15/4/16.
 */
public class GitViewUtils {

    // 通过一个viewId来获取一个view
    public static <T extends View> T findViewById(View container, int viewId) {
        return (T)container.findViewById(viewId);
    }
}
