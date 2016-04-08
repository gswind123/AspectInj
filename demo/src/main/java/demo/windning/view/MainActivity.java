package demo.windning.view;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.windning.annotation.JoinPoints;
import com.windning.aspectinj.R;
import com.windning.pointcut.AspectPointCut;

import demo.windning.view.pointcut.DemoPointCut;


/**
 * Created by yw_sun on 2016/4/8.
 */
@JoinPoints
public class MainActivity extends FragmentActivity {
    private View mToastBtn, mIgnoreBtn, mInnerToastBtn;
    @Override
    public void onCreate(Bundle savedInstance) {
        AspectPointCut.weave(new DemoPointCut());
        super.onCreate(savedInstance);
        setContentView(R.layout.main_activity_layout);
        mToastBtn = findViewById(R.id.toast_btn);
        mIgnoreBtn = findViewById(R.id.ignore_btn);
        mInnerToastBtn = findViewById(R.id.toast_inner_btn);
        mToastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast logic is in the point cut.
            }
        });
        mIgnoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "This wound not be toasted", Toast.LENGTH_SHORT).show();
            }
        });
        mInnerToastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemoUtil.showToast(MainActivity.this, "Toast Inner Button Clicked!");
            }
        });
    }
}
