package cn.w_wei.groupon.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import cn.w_wei.groupon.app.MyApp;

public class VolleyClient {
    //单例模式要确保对象只创建一次
    //1、声明一个私有的静态的属性
    private static VolleyClient INSTANCE;
//    private static VolleyClient INSTANCE = new VolleyClient();
    //2、声明一个公有的静态的获取1中属性的方法,
    //单线程时没有问题，多线程时需要同步
    //1、对下列方法加同步2、在上述创建属性即被实例化3、对代码块加锁
    //原则上尽量延迟初始化，即采用第一种方法
    public static VolleyClient getInstance(){
        if(INSTANCE == null){
            synchronized (VolleyClient.class){
                if(INSTANCE == null) {
                    INSTANCE = new VolleyClient();
                }
            }
        }
        return INSTANCE;
    }

    RequestQueue queue;
    //3、构造器私有

    private VolleyClient(){
        queue = Volley.newRequestQueue(MyApp.CONTEXT);
    }

    private VolleyClient(Context context){
        queue = Volley.newRequestQueue(context);
    }

    public void test(){
        //获取符合大众点评要求的请求地址
        Map<String,String> params = new HashMap<>();
        params.put("city","北京");
        params.put("category","美食");
        String url = HttpUtil.getURL("http://api.dianping.com/v1/business/find_businesses",params);
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.i("tAG","利用Volley获取的服务器响应内容："+s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("TAG",volleyError.getMessage());
            }
        });
        //3、请求对象放到请求队列中
        queue.add(request);
    }
}
