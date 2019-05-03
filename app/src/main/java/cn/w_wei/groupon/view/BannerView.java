package cn.w_wei.groupon.view;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


import cn.w_wei.groupon.R;

/**
 * 前面音乐播放器，播放界面唱机部分的组件代码部分，
 * 犯的错误在于这里引入了之后，在布局文件中又引入了一次，
 * 实际上，这里引入之后，布局文件中就不用再引入了
 */
public class BannerView extends FrameLayout {
    private ViewPager vpBanner;
    private ImageView ivClose;
    private LinearLayout llContainer;
    private PagerAdapter adapter;

    private int[] resIds;//viewpager管理的所有图片（用户看到的图片加2，左右各加一）
    private Handler handler = new Handler();
    private boolean flag;

    private OnCloseBannerListener listener;

    public BannerView(Context context,int[] ids) {
        super(context);
        if(ids != null && ids.length > 0){
            //创建BannerView时，创建者传入了要显示的轮播图片
            this.resIds = new int[ids.length + 2];
            resIds[0] = ids[ids.length - 1];
            resIds[resIds.length - 1] = ids[0];
            for(int i = 0;i < ids.length;i++){
                resIds[i+1] = ids[i];
            }
        }else{
            //使用默认的轮播图片
            resIds = new int[]{R.drawable.banner_4,R.drawable.banner_1,R.drawable.banner_2,
                            R.drawable.banner_3,R.drawable.banner_4,R.drawable.banner_1};

        }
        View view = LayoutInflater.from(context).inflate(R.layout.banner_layout,this,false);
        //把view添加到FrameLayout中，
        addView(view);

        initView(view);
        start();//开始自动轮播
    }

    public void setOnCloseBannerListener(OnCloseBannerListener listener){
        this.listener = listener;
    }

    private void stop(){
        flag = false;
        //可以让已经提交但尚未执行的所有任务都终止掉
        handler.removeCallbacksAndMessages(null);
    }
    //一，可以自定义ViewPager,然后重写里面的onTouchEvent方法（是view的方法），来处理触摸操作
    //二，setOnTouchListener，给view绑定一个触摸事件
    //原则上，如果同时有以上两种操作，按下，移动，抬起事件会优先传递给方法二，
    //此时，是否会继续传给同一个view的onTouchEvent方法，取决于setOnTouchListener里面重写方法的返回值是true（不传递），还是false（传递）
    private void start() {
        flag = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(flag){
                    int idx = vpBanner.getCurrentItem();
                    vpBanner.setCurrentItem(idx + 1);
                    handler.postDelayed(this,3000);
                }
            }
        }, 3000);
    }

    private void initView(View view) {
        vpBanner = view.findViewById(R.id.vp_banner);
        ivClose = view.findViewById(R.id.iv_close);
        llContainer = view.findViewById(R.id.ll_banner);
        //TODO
        adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return resIds.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                ImageView iv = new ImageView(getContext());
                int resId = resIds[position];
                iv.setImageResource(resId);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                container.addView(iv);
                return iv;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        };
        vpBanner.setAdapter(adapter);
        vpBanner.setCurrentItem(1);
        vpBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if(i == 0){
                    //第二个参数为是否平滑滚动，本例需要设置为false，切换时滑动
                    vpBanner.setCurrentItem(resIds.length - 2,false);
                    setIndicator(llContainer.getChildCount() - 1);
                }else if(i == resIds.length - 1){
                    vpBanner.setCurrentItem(1,false);
                    setIndicator(0);
                }else{
                    setIndicator(i - 1);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        vpBanner.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if(action == MotionEvent.ACTION_DOWN){
                    stop();
                }
                if(action == MotionEvent.ACTION_UP){
                    start();
                }
                //是否会继续传给同一个view的onTouchEvent方法，取决于setOnTouchListener里面重写方法的返回值是true（不传递），还是false（传递）
                return false;
            }
        });

        //llContainer 中填充对应数量的ImageView作为滑动指示器
        for(int i = 0;i < resIds.length - 2;i++){
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(R.drawable.banner_dot);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            //获得3个dp对应的像素值（直接传数字，对应的是像素值）
            int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,3,getResources().getDisplayMetrics());
            params.setMargins(margin,0,margin,0);
            iv.setLayoutParams(params);//向父容器要间隔值

            llContainer.addView(iv);
        }
        setIndicator(0);
        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onClose();
                }
            }
        });
    }

    /**
     * 设置某指示器中图片的显示为橘红色图片
     * @param idx
     */
    private void setIndicator(int idx) {
        for(int i = 0;i < llContainer.getChildCount();i++){
            ImageView iv = (ImageView) llContainer.getChildAt(i);
            if(i == idx){
                iv.setImageResource(R.drawable.banner_dot_pressed);
            }else{
                iv.setImageResource(R.drawable.banner_dot);
            }
        }
    }

    public interface OnCloseBannerListener{
        void onClose();
    }
}
