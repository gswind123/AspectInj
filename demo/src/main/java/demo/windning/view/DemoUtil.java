package demo.windning.view;

import android.app.Activity;
import android.util.Log;

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

    static public void testVoid() {
        String name = "windning";
        Log.e(name, name);
    }

    static public String testString() {
        return "windning";
    }

    static public Activity testObject(Activity activity) {
        return activity;
    }

    static public float testFloat() {
        return 3.57f;
    }

    static public Integer testInteger() {
        return 899229;
    }
}
