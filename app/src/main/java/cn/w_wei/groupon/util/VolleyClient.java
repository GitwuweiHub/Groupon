package cn.w_wei.groupon.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import cn.w_wei.groupon.R;
import cn.w_wei.groupon.app.MyApp;
import cn.w_wei.groupon.bean.TuanBean;

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
    ImageLoader imageLoader;
    //3、构造器私有

    private VolleyClient(){
        queue = Volley.newRequestQueue(MyApp.CONTEXT);
        imageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
//            Map<String,Bitmap> cache = new HashMap<>();//可当临时缓存
            LruCache<String,Bitmap> cache = new LruCache<String,Bitmap>((int) (Runtime.getRuntime().maxMemory()/8)){
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getHeight()*value.getRowBytes();
                }
            };
            //LruCache内部就是用Map存的但它会自动检测所用内存剩余空间，根据最近最少使用的内容会被清楚掉
            @Override
            public Bitmap getBitmap(String s) {
                return cache.get(s);
            }
            //存图片
            @Override
            public void putBitmap(String s, Bitmap bitmap) {
                cache.put(s,bitmap);
            }
        });

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

    /**
     * 利用Volley去获取城市今日新增的团购信息
     * @param city 获取该城市名称的团购信息
     * @param listener
     */
    public void getDailyDeals(String city, final Response.Listener<String> listener){
        //1.获取新增团购的ID列表
        final Map<String,String> params = new HashMap<>();
        params.put("city",city);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
        params.put("date",date);
        final String url = HttpUtil.getURL("http://api.dianping.com/v1/deal/get_daily_new_id_list",params);
        Log.i("TAG","url-->"+url);
        final StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //appkey不可用，数据为模拟数据,也有可能没有新增
                s = "{\"status\":\"ok\",\"count\":309,\"id_list\":[\"1-33946\",\"1-4531\",\"1-4571\",\"1-5336\"]}";
                Log.i("TAG","s-->"+s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    Log.i("TAG","jsonObject-->"+jsonObject);
                    JSONArray jsonArray = jsonObject.getJSONArray("id_list");
                    int size = jsonArray.length();
                    Log.i("TAG","jsonArray-->"+jsonArray);
                    if(size > 40){
                        size = 40;
                    }
                    StringBuilder sb = new StringBuilder();
                    for(int i = 0;i < size;i++){
                        String id = jsonArray.getString(i);
                        sb.append(id);
                        sb.append(",");
                    }
                    if(sb.length() > 0) {
                        String idList = sb.substring(0, sb.length() - 1);
                        Log.i("TAG", "idList-->" + idList);
                        Map<String, String> params2 = new HashMap<>();
                        params2.put("deal_ids", idList);
                        String url2 = HttpUtil.getURL("http://api.dianping.com/v1/deal/get_batch_deals_by_id", params2);
                        //listener谁调用谁传入，目的是直接在activity中获得响应结果，节省步骤
                        StringRequest request2 = new StringRequest(url2, listener, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.i("TAG", "第二次网络请求的错误信息-->" + volleyError.getMessage());
                            }
                        });
                        queue.add(request2);
                    }else{
                        //该城市今日无新增团购
                        listener.onResponse(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("TAG","Volley模拟响应结果-->"+s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("TAG",volleyError.getMessage());
            }
        });
        queue.add(request);
    }

    /**
     * 显示网络中的一幅图片
     * @param url 图片在网络中的图片
     * @param imageView 显示图片的控件
     */
    public void loadImage(String url, ImageView imageView){
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.ic_edit_image_01,R.mipmap.ic_launcher);
        imageLoader.get(url,listener);
    }

    public void getDailyDeals2(String city, final Response.Listener<TuanBean> listener){
        //1.获取新增团购的ID列表
        final Map<String,String> params = new HashMap<>();
        params.put("city",city);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
        params.put("date",date);
        final String url = HttpUtil.getURL("http://api.dianping.com/v1/deal/get_daily_new_id_list",params);
        Log.i("TAG","url-->"+url);
        final StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //appkey不可用，数据为模拟数据,也有可能没有新增
                s = "{\"status\":\"ok\",\"count\":309,\"id_list\":[\"1-33946\",\"1-4531\",\"1-4571\",\"1-5336\"]}";
                Log.i("TAG","s-->"+s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    Log.i("TAG","jsonObject-->"+jsonObject);
                    JSONArray jsonArray = jsonObject.getJSONArray("id_list");
                    int size = jsonArray.length();
                    Log.i("TAG","jsonArray-->"+jsonArray);
                    if(size > 40){
                        size = 40;
                    }
                    StringBuilder sb = new StringBuilder();
                    for(int i = 0;i < size;i++){
                        String id = jsonArray.getString(i);
                        sb.append(id);
                        sb.append(",");
                    }
                    if(sb.length() > 0) {
                        String idList = sb.substring(0, sb.length() - 1);
                        Log.i("TAG", "idList-->" + idList);
                        Map<String, String> params2 = new HashMap<>();
                        params2.put("deal_ids", idList);
                        String url2 = HttpUtil.getURL("http://api.dianping.com/v1/deal/get_batch_deals_by_id", params2);
                        //listener谁调用谁传入，目的是直接在activity中获得响应结果，节省步骤
                        TuanBeanRequest request2 = new TuanBeanRequest(url2, listener, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.i("TAG", "第二次网络请求的错误信息-->" + volleyError.getMessage());
                            }
                        });
                        queue.add(request2);
                    }else{
                        //该城市今日无新增团购
                        listener.onResponse(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("TAG","Volley模拟响应结果-->"+s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("TAG",volleyError.getMessage());
            }
        });
        queue.add(request);
    }

    /**
     * 自定义请求类型
     */
    public class TuanBeanRequest extends Request<TuanBean>{

        Response.Listener<TuanBean> listener;

        public TuanBeanRequest(int method, String url, Response.ErrorListener listener) {
            super(method, url, listener);
        }

        public TuanBeanRequest(String url, Response.Listener listener, Response.ErrorListener errorListener){
            super(Method.GET,url, errorListener);
            this.listener = listener;
        }

        /**
         *
         * @param networkResponse 服务器响应的内容
         * @return
         */
        @Override
        protected Response<TuanBean> parseNetworkResponse(NetworkResponse networkResponse) {
            Gson gson = new Gson();
            String response = new String(networkResponse.data);
            TuanBean tuanBean = gson.fromJson(response,TuanBean.class);
            //自己组装一个Volley的Response对象作为方法的返回值
            Response<TuanBean> result = Response.success(tuanBean, HttpHeaderParser.parseCacheHeaders(networkResponse));
            return result;
        }

        /**
         * 调用访问成功以后的监听
         * @param tuanBean
         */
        @Override
        protected void deliverResponse(TuanBean tuanBean) {
            listener.onResponse(tuanBean);
        }
    }
}
