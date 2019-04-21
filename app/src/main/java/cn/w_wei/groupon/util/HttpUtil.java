package cn.w_wei.groupon.util;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.w_wei.groupon.R;
import cn.w_wei.groupon.app.MyApp;
import cn.w_wei.groupon.bean.TuanBean;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 网络访问工具类
 *
 * 符合大众点评服务器要求的地址
 * http://网址部分?参数1=值1&参数2=值2&参数3=值3&参数4=值4
 *
 * http://api.dianping.com/v1/business/find_businesses?c
 * appkey=49814079&sign=生成的签名&city=%xx%xx%xx&category=%xx%xx%xx%xx(中文要转成utf8格式)
 *
 * 请求地址中签名的生成：
 * 利用appkey，Appsecret，以及其他访问参数（例如city，category）
 * 首先将上述拼成一个字符串
 * 例如：49814079category美食city上海90e3438a41d646848033b6b9d461ed54
 * 将拼接好的字符串进行转码（转码算法SHA1算法，无论字符串多长都能转成40个字符，而且转换是单向不可逆的，仅有SHA1字符串无法还原）
 * 转码后就得到了签名
 *
 */
public class HttpUtil {
    public static final String APPKEY = "49814079";
    public static final String APPSECRET = "90e3438a41d646848033b6b9d461ed54";

    /**
     * 获得满足大众点评服务器要求的请求路径
     * @return
     */
    public static String getURL(String url,Map<String,String> params){
        String result = "";
        String sign = getSign(APPKEY,APPSECRET,params);
        String query = getQuery(APPKEY,sign,params);
        result = url + "?" + query;
        return result;
    }

    /**
     * 获取请求地址中的签名
     * @param appkey
     * @param appsecret
     * @param params
     * @return
     */
    public static String getSign(String appkey, String appsecret, Map<String,String> params) {
        StringBuilder stringBuilder = new StringBuilder();
        //对参数名按字典顺序进行排序
        String[] keyArray = params.keySet().toArray(new String[0]);
        Arrays.sort(keyArray);
        //拼接有序的参数名-值对
        stringBuilder.append(appkey);
        for(String key:keyArray){
            stringBuilder.append(key).append(params.get(key));
        }
        String codes = stringBuilder.append(appsecret).toString();
        //在纯Java环境中，利用Codec对字符串进行SHA1转码采用如下方式
//        String sign = org.apache.commons.codec.digest.DigestUtils.shaHex(codes).toUpperCase();
        //在安卓环境中，利用Codec对字符串进行SHA1转码采用如下方式
        String sign = new String(Hex.encodeHex(DigestUtils.sha(codes))).toUpperCase();
        return sign;
    }

    /**
     * 获取请求地址中的参数部分
     * @param appkey
     * @param sign
     * @param params
     * @return
     */
    private static String getQuery(String appkey, String sign, Map<String,String> params) {
        try {//ctrl+alt+t
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("appkey=").append(appkey).append("&sign=").append(sign);
            for(Map.Entry<String,String> entry:params.entrySet()){
                stringBuilder.append('&').append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(),"utf8"));
            }
            String queryString = stringBuilder.toString();
            return queryString;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //扔yichang,异常可作为方法返回值的一种
            throw new RuntimeException("使用了不正确的字符集名称");
        }
    }
    public static void testHttpURLConnection(){
        //获取符合大众点评要求的请求地址
        Map<String,String> params = new HashMap<>();
        params.put("city","北京");
        params.put("category","美食");
        final String url = getURL("http://api.dianping.com/v1/business/find_businesses",params);
        Log.i("TAG","生成的网络请求地址是："+url);
        new Thread(){
            @Override
            public void run() {
                try {
                    super.run();
                    URL u = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) u.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);//该方法可写可不写，默认是true
                    connection.setDoOutput(true);//该方法默认是false
                    connection.connect();
                    int statusCode = connection.getResponseCode();
                    Log.i("TAG","statusCode="+statusCode);
                    InputStream is = connection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is,"utf-8");
                    BufferedReader reader = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while((line = reader.readLine()) != null){
                        sb.append(line);
                    }
                    is.close();
                    String response = sb.toString();
                    Log.i("TAG","HttpURLConnection获得的服务器响应"+response);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Deprecated
    public static void testVolley(){
        //1、请求队列,也可用Application
        RequestQueue queue = Volley.newRequestQueue(MyApp.CONTEXT);
        //2、请求对象
        //获取符合大众点评要求的请求地址
        Map<String,String> params = new HashMap<>();
        params.put("city","北京");
        params.put("category","美食");
        String url = getURL("http://api.dianping.com/v1/business/find_businesses",params);
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

    public static void testVolley(Context context){
        VolleyClient.getInstance().test();
    }

    @Deprecated
    public static void testRetrofit(){
        //1、创建Retrofit对象,baseUrl在2.0之后要加斜杠
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.dianping.com/v1/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        //2、创建接口的实现类对象
        NetService service = retrofit.create(NetService.class);
        Map<String,String> params = new HashMap<>();
        params.put("city","北京");
        params.put("category","美食");
        String sign = getSign(APPKEY,APPSECRET,params);
        //3、获得请求对象
        Call<String> call = service.test(HttpUtil.APPKEY,sign,params);
        //4、将请求队列放到请求队列中
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                String string = response.body();
                Log.i("TAG","利用Retrofit获得的网络请求："+string);
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Log.i("tAG",throwable.getMessage());
            }
        });
    }

    public static void testRetrofit(Context context){
        RetrofitClient.getInstance().test();
    }

    public static void getDailyDealsByVolley(String city,Response.Listener<TuanBean> listener){
//        VolleyClient.getInstance().getDailyDeals(city,listener);
     VolleyClient.getInstance().getDailyDeals2(city,listener);
    }

    public static void getDailyDealsByRetrofit(String city,Callback<String> callback){
        RetrofitClient.getInstance().getDailyDeals(city,callback);
    }

    public static void getDailyDealsByRetrofit2(String city,Callback<TuanBean> callback){
        RetrofitClient.getInstance().getDailyDeals2(city,callback);
    }

    public static void getDailyDealsByRetrofit3(String city,Callback<TuanBean> callback){
        RetrofitClient.getInstance().getDailyDeals3(city,callback);
    }

    public static void loadImage(String url, ImageView imageView){
        VolleyClient.getInstance().loadImage(url,imageView);
    }

    public static void displayImage(String url,ImageView imageView){
        Picasso.with(MyApp.CONTEXT).load(url).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(imageView);

    }


}
