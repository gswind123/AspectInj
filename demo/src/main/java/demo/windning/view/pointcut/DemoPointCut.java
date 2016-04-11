package demo.windning.view.pointcut;

import android.widget.Toast;

import com.windning.pointcut.PointCut;
import com.windning.pointcut.PointCutRegistery;

import java.util.HashMap;
import java.util.Map;

import demo.windning.view.MainActivity;

/**
 * Created by yw_sun on 2016/4/8.
 */
public class DemoPointCut implements PointCutRegistery {
    @Override
    public Map<String, PointCut> register() {
        HashMap<String, PointCut> map = new HashMap<String, PointCut>();
        map.put("demo.windning.view.MainActivity.onCreate.class#0.onClick(View)",
            new PointCut(){
                @Override
                public boolean onBefore(Object self, Object[] args) {
                    if(self instanceof MainActivity) {
                        Toast.makeText((MainActivity)self, "This is Toast Click Button!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        map.put("demo.windning.view.MainActivity.onCreate.class#1.onClick(View)",
            new PointCut(){
                @Override
                public boolean onBefore(Object self, Object[] args){
                    return false;
                }
            });
        map.put("demo.windning.view.DemoUtil.showToast(Activity,String)",
            new PointCut(){
                @Override
                public boolean onBefore(Object self, Object[] args){
                    if(args.length == 2) {
                        MainActivity activity = (MainActivity)args[0];
                        String content = (String)args[1];
                        Toast.makeText(activity, content, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        return map;
    }
}
