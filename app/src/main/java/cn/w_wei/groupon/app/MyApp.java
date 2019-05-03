package cn.w_wei.groupon.app;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import cn.w_wei.groupon.bean.CityNameBean;
import cn.w_wei.groupon.util.SPUtil;

/**
 * 需要在AndroidManifest中注册一下
 * 在application节点里添加android:name=".app.MyApp"
 */
public class MyApp extends Application {

    public static MyApp CONTEXT;
    //城市名称的缓存
    public static List<CityNameBean> cityNameBeanList;

    public static double myLng;
    public static double myLat;
    public static LatLng myLocation;

    public static String region;

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;
        cityNameBeanList = new ArrayList<>();
        SPUtil spUtil = new SPUtil(this);
        spUtil.setCloseBanner(false);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }
}
