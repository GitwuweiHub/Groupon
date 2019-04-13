package cn.w_wei.groupon.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.w_wei.groupon.R;
import cn.w_wei.groupon.config.Constant;
import cn.w_wei.groupon.util.SPUtil;

public class SplashActivity extends AppCompatActivity {

    SPUtil spUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        spUtil = new SPUtil(this);
        //界面停留几秒钟，模拟资源加载的时间
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //读取偏好设置文件中的只
                Intent intent;
//                SharedPreferences sp = getSharedPreferences("splash-info",MODE_PRIVATE);
//                boolean first = sp.getBoolean(Constant.FIRST,true);
                //根据是否是第一次使用进行相应的界面的跳转
                if(true){
//                    SharedPreferences.Editor editor = sp.edit();
//                    editor.putBoolean(Constant.FIRST,false);
//                    editor.commit();
                    spUtil.setFirst(false);
                    //第一次运行,向新手指导页跳转
                    intent = new Intent(SplashActivity.this,GuideActivity.class);
                }else{
                    //向主页跳转
                    intent = new Intent(SplashActivity.this,MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        },3000);
    }
}
