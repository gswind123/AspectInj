package demo.windning.view;

import android.app.Activity;

import com.windning.annotation.JoinPoints;

/**
 * Created by yw_sun on 2016/4/8.
 */
@JoinPoints
public class DemoUtil {
    static public void showToast(Activity activity, String content) {
        //Toast will be shown the in point cut
    }
}
