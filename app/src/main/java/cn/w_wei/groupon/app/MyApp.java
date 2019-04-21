package cn.w_wei.groupon.app;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import cn.w_wei.groupon.bean.CityNameBean;

/**
 * 需要在AndroidManifest中注册一下
 * 在application节点里添加android:name=".app.MyApp"
 */
public class MyApp extends Application {

    public static MyApp CONTEXT;
    //城市名称的缓存
    public static List<CityNameBean> cityNameBeanList;

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;
        cityNameBeanList = new ArrayList<>();
    }
}
