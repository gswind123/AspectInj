package demo.windning.view.pointcut;

import android.widget.Toast;

import com.windning.pointcut.PointCutProcessor;

import demo.windning.view.MainActivity;

/**
 * Created by yw_sun on 2016/4/8.
 */
public class DemoPointCut implements PointCutProcessor {
    static private final String toastBtnClickSig = "demo.windning.view.MainActivity.onCreate.class#0.onClick(View)";
    static private final String ignoreBtnClickSig = "demo.windning.view.MainActivity.onCreate.class#1.onClick(View)";
    static private final String innerToastSig = "demo.windning.view.DemoUtil.showToast(Activity,String)";

    @Override
    public boolean onBefore(String position, Object self, Object[] args) {
        if(toastBtnClickSig.equals(position)) {
            if(self instanceof MainActivity) {
                Toast.makeText((MainActivity)self, "This is Toast Click Button!", Toast.LENGTH_SHORT).show();
            }
        } else if(ignoreBtnClickSig.equals(position)) {
            return false;
        } else if(innerToastSig.equals(position)) {
            if(args.length == 2) {
                MainActivity activity = (MainActivity)args[0];
                String content = (String)args[1];
                Toast.makeText(activity, content, Toast.LENGTH_SHORT).show();
            }

        }
        return true;
    }

    @Override
    public void onAfter(String position, Object self, Object[] args) {

    }
}
