package cn.w_wei.groupon.app;

import android.app.Application;

/**
 * 需要在AndroidManifest中注册一下
 * 在application节点里添加android:name=".app.MyApp"
 */
public class MyApp extends Application {

    public static MyApp CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;
    }
}
