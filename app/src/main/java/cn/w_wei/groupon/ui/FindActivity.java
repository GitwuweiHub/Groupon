package cn.w_wei.groupon.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.List;

import cn.w_wei.groupon.R;
import cn.w_wei.groupon.app.MyApp;
import cn.w_wei.groupon.bean.BusinessBean;

/**
 * .so文件是用c/c++写的代码
 * 与底层硬件直接调用的只能用c/c++语言来进行
 * c语言可以反编译，它没有反编译机制，而Java有，所以安全性更好
 * 包里另外五个文件夹是对主流硬件架构的支持
 *
 * 参考百度Android地图SDK
 */
public class FindActivity extends AppCompatActivity implements View.OnClickListener {

    private BusinessBean.Business business;
    private MapView mapView = null;//用来显示地图的容器
    private BaiduMap baiduMap;
    private String from;

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    private Button btSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        from = getIntent().getStringExtra("from");

        business = (BusinessBean.Business) getIntent().getSerializableExtra("business");
        mapView = findViewById(R.id.bmapView);
        btSearch = findViewById(R.id.bt_search);
        btSearch.setOnClickListener(this);
        baiduMap = mapView.getMap();
        //更改地图默认的比例尺(5km-->100m),缩放至某个级别
        MapStatusUpdate update = MapStatusUpdateFactory.zoomTo(17);
        baiduMap.animateMapStatus(update);
        //为标志物添加监听
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = marker.getExtraInfo();
                if(bundle == null)
                    return false;
                String name = bundle.getString("name");
                String address = bundle.getString("address");
                String number = bundle.getString("number");
                LatLng location = new LatLng(39.113750,117.209560);
                double distance = DistanceUtil.getDistance(location,marker.getPosition());
                String dis = ((int)(distance * 100))/100.0 + "米";
                //第二个参数，因为不确定父容器是谁，所以写null，此时第三个参数默认为false
                View infoWindow = LayoutInflater.from(FindActivity.this).inflate(R.layout.infowindow_layout,null);
                TextView tvName = infoWindow.findViewById(R.id.tv_info_name);
                TextView tvAddress = infoWindow.findViewById(R.id.tv_info_address);
                TextView tvPhone = infoWindow.findViewById(R.id.tv_info_phone);
                TextView tvDistance = infoWindow.findViewById(R.id.tv_info_distance);
                tvName.setText(name);
                tvAddress.setText(address);
                tvPhone.setText(number);
                tvDistance.setText(dis);
                InfoWindow info = new InfoWindow(infoWindow,marker.getPosition(),-40);
                baiduMap.showInfoWindow(info);
                return true;
            }
        });
        if ("main".equals(from)) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                //判定权限
                int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
                if(permission != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE
                    },101);
                }else{
                    showTheLocation();
//                    showMyLocation();
                }
            }else {
//                showMyLocation();
                showTheLocation();
            }
        } else if ("detail".equals(from)) {
            showAddress();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 101:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    showMyLocation();
                    showTheLocation();
                }
                break;
        }
    }

    private void showTheLocation(){
        btSearch.setVisibility(View.VISIBLE);
        LatLng location = new LatLng(39.113750,117.209560);
        GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null && reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(FindActivity.this, "服务器繁忙，请稍后重试！", Toast.LENGTH_LONG).show();
                } else {
                    //Lat-->维度 Lng-->经度
                    LatLng location = reverseGeoCodeResult.getLocation();
                    //在location所对应的经纬度插上一个标志物
                    MarkerOptions option = new MarkerOptions();
                    option.position(location);
                    //图片可自定义
                    option.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_locate));
                    baiduMap.addOverlay(option);//覆盖物/标志物：
                    //3.移动屏幕的中心点到location所对应的位置
                    MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(location);
                    baiduMap.animateMapStatus(update);

                    //4.添加一个信息窗
                    TextView tv = new TextView(FindActivity.this);
                    tv.setText("小白楼");
                    tv.setPadding(8, 8, 8, 8);//数字代表像素
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                    tv.setBackgroundColor(Color.GRAY);
                    tv.setTextColor(Color.RED);
                    //第三个参数代表偏移量
                    InfoWindow infoWindow = new InfoWindow(tv, location, -40);
                    baiduMap.showInfoWindow(infoWindow);
                    String position = "纬度:" + location.latitude + ",经度:" + location.longitude;
                    Toast.makeText(FindActivity.this, position, Toast.LENGTH_LONG).show();
                }
            }
        });
        ReverseGeoCodeOption option = new ReverseGeoCodeOption();
        option.location(location);
        geoCoder.reverseGeoCode(option);
    }

    /**
     * 定位当前使用者的位置
     */
    private void showMyLocation() {
        btSearch.setVisibility(View.VISIBLE);
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        option.setScanSpan(0);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效，导航时可需要设置为非0

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.start();
        //mLocationClient为第二步初始化过的LocationClient对象
        //调用LocationClient的start()方法，便可发起定位请求
    }

    /**
     * 再百度地图上显示某地址
     * 把地址发送到百度服务器，去查该地址对应经纬度
     */
    private void showAddress() {
        btSearch.setVisibility(View.INVISIBLE);
        //1）根据地址查询出所对应的经纬度-->地理编码查询
        //1.1)根据经纬度反查具体地址，称为反向地理编码查询
        GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override//地理编码查询有结果后的回调
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null && geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(FindActivity.this, "服务器繁忙，请稍后重试！", Toast.LENGTH_LONG).show();
                } else {
                    //Lat-->维度 Lng-->经度
                    LatLng location = geoCodeResult.getLocation();
                    //在location所对应的经纬度插上一个标志物
                    MarkerOptions option = new MarkerOptions();
                    option.position(location);
                    //图片可自定义
                    option.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_locate));
                    baiduMap.addOverlay(option);//覆盖物/标志物：
                    //3.移动屏幕的中心点到location所对应的位置
                    MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(location);
                    baiduMap.animateMapStatus(update);

                    //4.添加一个信息窗
                    TextView tv = new TextView(FindActivity.this);
                    tv.setText(business.getAddress());
                    tv.setPadding(8, 8, 8, 8);//数字代表像素
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                    tv.setBackgroundColor(Color.GRAY);
                    tv.setTextColor(Color.RED);
                    //第三个参数代表偏移量
                    InfoWindow infoWindow = new InfoWindow(tv, location, -40);
                    baiduMap.showInfoWindow(infoWindow);
                    String position = "纬度:" + location.latitude + ",经度:" + location.longitude;
                    Toast.makeText(FindActivity.this, position, Toast.LENGTH_LONG).show();
                }
            }

            @Override//反向地理编码查询有结果时的回调
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
        //真正发起地理编码查询
        GeoCodeOption option = new GeoCodeOption();
        option.address("东城区东直门内大街233号");
        option.city("北京");
        geoCoder.geocode(option);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mLocationClient != null){
            mLocationClient.stop();
            mLocationClient.unRegisterLocationListener(myListener);
            mLocationClient = null;
        }

        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }

    /**
     * 附近的
     * @param v
     */
    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择");
        builder.setIcon(R.mipmap.ic_launcher);
        final String[] items = new String[]{"美食","麦当劳","银行","厕所","超市","电影院","火锅"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String item = items[which];
                searchNear(item);
            }
        });
        builder.create().show();
    }

    /**
     * 根据用户选择的“关键字”进行搜索
     * @param item
     */
    private void searchNear(String item) {
        //兴趣点（POI）搜索
        PoiSearch poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                //TODO
                if(poiResult == null || poiResult.error != SearchResult.ERRORNO.NO_ERROR){
                    int status = poiResult.status;
                    Toast.makeText(FindActivity.this,"您附近没有..."+status,Toast.LENGTH_LONG).show();
                }else{
                    List<PoiInfo> list = poiResult.getAllPoi();
                    baiduMap.clear();
                    LatLng loc = new LatLng(39.113750,117.209560);
                    MarkerOptions option = new MarkerOptions();
                    option.position(loc);
                    option.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_locate));
                    baiduMap.addOverlay(option);
                    for(PoiInfo poi:list){
                        LatLng location = poi.location;
                        MarkerOptions op = new MarkerOptions();
                        op.position(location);
                        op.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon));
                        Marker marker = (Marker) baiduMap.addOverlay(op);
                        Bundle bundle = new Bundle();
                        bundle.putString("name",poi.name);
                        bundle.putString("address",poi.address);
                        bundle.putString("number",poi.phoneNum);
                        marker.setExtraInfo(bundle);
                    }
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
        PoiNearbySearchOption option = new PoiNearbySearchOption();
        LatLng location = new LatLng(39.113750,117.209560);
        option.location(location);//搜索中心
        option.radius(3000);//搜索半径（米）
        option.keyword(item);//搜索关键字
        poiSearch.searchNearby(option);
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f
            String position = "latitude-->"+latitude+",longitude-->"+longitude;
            Toast.makeText(FindActivity.this, position, Toast.LENGTH_LONG).show();

            MarkerOptions option = new MarkerOptions();
            option.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_locate));
            LatLng latLng = new LatLng(0,0);
            option.position(latLng);
            baiduMap.addOverlay(option);
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
            baiduMap.animateMapStatus(update);

            int type = location.getLocType();
            Toast.makeText(FindActivity.this,"type="+type,Toast.LENGTH_LONG).show();
            if(type == 61 || type == 65 || type == 66 || type == 161){
                Toast.makeText(FindActivity.this,"定位成功！",Toast.LENGTH_LONG).show();
            }else{//返回值为167
                Toast.makeText(FindActivity.this,"定位失败！",Toast.LENGTH_LONG).show();
                //手动指定一个位置，潘家园建业苑写字楼
                double i = 116.465037;
                double j = 39.876425;
                //添加标志物
                MarkerOptions options = new MarkerOptions();
                option.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_locate));
                LatLng latLngs = new LatLng(j,i);
                MyApp.myLocation = latLngs;
                options.position(latLngs);
                baiduMap.addOverlay(options);
                MapStatusUpdate updates = MapStatusUpdateFactory.newLatLng(latLngs);
                baiduMap.animateMapStatus(updates);
            }
            TextView tv = new TextView(FindActivity.this);
            tv.setText(business.getAddress());
            tv.setPadding(8, 8, 8, 8);//数字代表像素
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            tv.setBackgroundColor(Color.GRAY);
            tv.setTextColor(Color.RED);
            //第三个参数代表偏移量
            InfoWindow infoWindow = new InfoWindow(tv, latLng, -40);
            baiduMap.showInfoWindow(infoWindow);

            //停止定位
            mLocationClient.stop();
            mLocationClient.unRegisterLocationListener(this);
            mLocationClient = null;
            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = location.getLocType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
        }
    }
}
