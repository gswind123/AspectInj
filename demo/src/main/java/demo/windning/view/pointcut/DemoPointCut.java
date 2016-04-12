package demo.windning.view.pointcut;

import android.widget.Toast;

import org.windning.pointcut.PointCut;
import org.windning.pointcut.PointCutEntry;
import org.windning.pointcut.PointCutRegistery;

import demo.windning.view.MainActivity;

/**
 * Created by yw_sun on 2016/4/8.
 */
public class DemoPointCut implements PointCutRegistery {
    @Override
    public void register(PointCutEntry entry) {
        entry.add("demo.windning.view.MainActivity.onCreate.class#0.onClick(View)",
                new PointCut() {
                    @Override
                    public boolean onBefore(Object self, Object[] args) {
                        if (self instanceof MainActivity) {
                            Toast.makeText((MainActivity) self, "This is Toast Click Button!", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
        entry.add("demo.windning.view.MainActivity.onCreate.class#1.onClick(View)",
                new PointCut() {
                    @Override
                    public boolean onBefore(Object self, Object[] args) {
                        Toast.makeText((MainActivity) self, "The actual body is ignored!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
        entry.add("demo.windning.view.MainActivity.onCreate.class#1.onClick(View)",
                new PointCut() {
                    @Override
                    public boolean onBefore(Object self, Object[] args) {
                        return false;
                    }
                });
        entry.add("demo.windning.view.DemoUtil.showToast(Activity,String)",
                new PointCut() {
                    @Override
                    public boolean onBefore(Object self, Object[] args) {
                        if (args.length == 2) {
                            MainActivity activity = (MainActivity) args[0];
                            String content = (String) args[1];
                            Toast.makeText(activity, content, Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
    }
}
