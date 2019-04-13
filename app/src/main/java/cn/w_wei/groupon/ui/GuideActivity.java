package cn.w_wei.groupon.ui;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import java.util.ArrayList;
import java.util.List;

import cn.w_wei.groupon.R;
import cn.w_wei.groupon.adapter.VPAdapter;
import cn.w_wei.groupon.fragment.GuideFour;
import cn.w_wei.groupon.fragment.GuideOne;
import cn.w_wei.groupon.fragment.GuideThree;
import cn.w_wei.groupon.fragment.GuideTwo;
import cn.w_wei.groupon.util.KnifeUtil;
import cn.w_wei.groupon.util.BindView;
import cn.w_wei.groupon.util.OnClick;

//或者继承自fragmentactivity以便调用getSupportFragmentManager()
public class GuideActivity extends AppCompatActivity {
    @BindView(R.id.vp_guide)
    private ViewPager vpGuide;
    private List<Fragment> fragments;
    @BindView(R.id.indicator)
    private CirclePageIndicator indicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        KnifeUtil.bind(this);
        FragmentManager manager = getSupportFragmentManager();
        VPAdapter adapter = new VPAdapter(manager);
        vpGuide.setAdapter(adapter);
        addFragment();
        adapter.addFragment(fragments,true);
        indicator.setViewPager(vpGuide);
        //获得当前运行程序所用设备的屏幕密度,像素每英寸-->px/inch,每英寸2.54厘米
        //低密度 ldpi-->屏幕上120px/inch
        //中密度 mdpi-->屏幕上160px/inch
        //高密度 hdpi-->屏幕上240px/inch
        //很高密度 xhdpi-->屏幕上320px/inch
        //非常高密度 xxhdpi-->屏幕上480px/inch
        //
        //dp是绝对单位 160 dp = 1 inch
        //1dp 在低密度屏幕上 0.75px
        //1dp 在中密度屏幕上 1px
        //1dp 在高密度屏幕上 1.5px
        //1dp 在很高密度屏幕上 2px
        //1dp 在非常高密度屏幕上 3px
        //
        //另外一种获得10dp在当前设备屏幕密度上的像素值
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                ,10
                ,getResources().getDisplayMetrics());
        float density = getResources().getDisplayMetrics().density;
//        indicator.setBackgroundColor(Color.parseColor("#1AFA29"));
        //10*density是10dp在当前设备上所对应的的像素值

        indicator.setRadius(10 * density);
        indicator.setFillColor(Color.parseColor("#ff6633"));
        indicator.setStrokeColor(Color.parseColor("#1AFA29"));
        vpGuide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                //NO_OP
            }

            @Override
            public void onPageSelected(int i) {
                if(i != 3){
                    indicator.setVisibility(View.VISIBLE);
                }else{
                    indicator.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                //NO_OP
            }
        });
    }

    /**
     * 可以写到适配器的构造方法中去
     */
    private void addFragment() {
        fragments = new ArrayList<>();
        fragments.add(new GuideOne());
        fragments.add(new GuideTwo());
        fragments.add(new GuideThree());
        fragments.add(new GuideFour());
    }

}
