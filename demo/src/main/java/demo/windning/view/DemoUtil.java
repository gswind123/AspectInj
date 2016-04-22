package demo.windning.view;

import android.app.Activity;

import org.windning.annotation.JoinPoints;

import java.util.Random;

/**
 * Created by yw_sun on 2016/4/8.
 */
@JoinPoints
public class DemoUtil {
    static public boolean showToast(Activity activity, String content) {
        //Toast will be shown the in point cut
        Random rand = new Random();
        int a = rand.nextInt();
        if(a > 300) {
            return true;
        } else {
            return false;
        }
    }
}
