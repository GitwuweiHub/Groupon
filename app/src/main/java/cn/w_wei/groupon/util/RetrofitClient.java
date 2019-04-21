package cn.w_wei.groupon.util;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.w_wei.groupon.app.MyApp;
import cn.w_wei.groupon.bean.TuanBean;
import cn.w_wei.groupon.config.Constant;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * okio
 */
public class RetrofitClient {

    private static RetrofitClient INSTANCE;

    public static RetrofitClient getInstance(){
        if(INSTANCE == null){
            synchronized (RetrofitClient.class) {
                if(INSTANCE == null) {
                    INSTANCE = new RetrofitClient();
                }
            }
        }
        return INSTANCE;
    }


    private Retrofit retrofit;//默认在retrofit创建时会自己创建OkHttpClient

    private OkHttpClient okHttpClient;

    private NetService netService;

    private RetrofitClient(){
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new MyOkHttpInterceptor())
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASEURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)//加上这句后就会用新创建的okHttpClient,添加了拦截器
                .build();
        netService = retrofit.create(NetService.class);
    }

    public void test(){
        Map<String,String> params = new HashMap<>();
        params.put("city","北京");
        params.put("category","美食");
        String sign = HttpUtil.getSign(HttpUtil.APPKEY,HttpUtil.APPSECRET,params);
        Call<String> call = netService.test(HttpUtil.APPKEY,sign,params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String str = response.body();
                Log.i("TAG","Retrofit获得的网络响应"+str);
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Log.i("TAG",throwable.getMessage());
            }
        });
    }

    public void getDailyDeals(String city,final Callback<String> callback2){
        Map<String,String> params = new HashMap<>();
        params.put("city",city);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
        params.put("date",date);
        String sign = HttpUtil.getSign(HttpUtil.APPKEY,HttpUtil.APPSECRET,params);
        Call<String> ids = netService.getDailyIds(HttpUtil.APPKEY,sign,params);
        ids.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    Log.i("TAG",response.body());
                    String status = jsonObject.getString("status");
                    if("ERROR".equals(status)){
                        String error = jsonObject.getJSONObject("error").getString("errorMessage");
                        Response<String> response2 = Response.success(error);
                        callback2.onResponse(null, response2);
                    }else {
                        JSONArray jsonArray = jsonObject.getJSONArray("id_list");
                        int size = jsonArray.length();
                        if (size > 40) {
                            size = 40;
                        }
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < size; i++) {
                            sb.append(jsonArray.getString(i));
                            sb.append(",");
                        }
                        if (sb.length() > 0) {
                            String idList = sb.substring(0, sb.length() - 1);
                            Map<String, String> params2 = new HashMap<>();
                            params2.put("deal_ids", idList);
                            String sign2 = HttpUtil.getSign(HttpUtil.APPKEY, HttpUtil.APPSECRET, params2);
                            Call<String> call2 = netService.getDailyDeals(HttpUtil.APPKEY, sign2, params2);
                            call2.enqueue(callback2);
                        } else {
                            callback2.onResponse(null, null);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {

            }
        });
    }

    public void getDailyDeals2(String city,final Callback<TuanBean> callback2){
        Map<String,String> params = new HashMap<>();
        params.put("city",city);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
        params.put("date",date);
        String sign = HttpUtil.getSign(HttpUtil.APPKEY,HttpUtil.APPSECRET,params);
        Call<String> ids = netService.getDailyIds(HttpUtil.APPKEY,sign,params);
        ids.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    Log.i("TAG",response.body());
                    String status = jsonObject.getString("status");
                    if("ERROR".equals(status)){
                        String error = jsonObject.getJSONObject("error").getString("errorMessage");
                        Response<String> response2 = Response.success(error);
                        callback2.onResponse(null, null);
                    }else {
                        JSONArray jsonArray = jsonObject.getJSONArray("id_list");
                        int size = jsonArray.length();
                        if (size > 40) {
                            size = 40;
                        }
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < size; i++) {
                            sb.append(jsonArray.getString(i));
                            sb.append(",");
                        }
                        if (sb.length() > 0) {
                            String idList = sb.substring(0, sb.length() - 1);
                            Map<String, String> params2 = new HashMap<>();
                            params2.put("deal_ids", idList);
                            String sign2 = HttpUtil.getSign(HttpUtil.APPKEY, HttpUtil.APPSECRET, params2);
                            Call<TuanBean> call2 = netService.getDailyDeals2(HttpUtil.APPKEY, sign2, params2);
                            call2.enqueue(callback2);
                        } else {
                            callback2.onResponse(null, null);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {

            }
        });
    }

    public void getDailyDeals3(String city,final Callback<TuanBean> callback2){
        Map<String,String> params = new HashMap<>();
        params.put("city",city);
        params.put("date",new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()));
        Call<String> idCall = netService.getDailyIds2(params);
        idCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String error = jsonObject.getJSONObject("error").getString("errorMessage");
                    Toast.makeText(MyApp.CONTEXT,error,Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {

            }
        });
    }

    /**
     * OKHTTP的拦截器
     */
    public class MyOkHttpInterceptor implements Interceptor{

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            //修改原有请求地址
            Request request = chain.request();
            //原有请求路径
            HttpUrl url = request.url();
            //取出原有请求路径的参数
            HashMap<String,String> params = new HashMap<>();
            //原有请求路径中请求参数的名称
            Set<String> set = url.queryParameterNames();
            for(String key:set){
                params.put(key,url.queryParameter(key));
            }

            String sign = HttpUtil.getSign(HttpUtil.APPKEY,HttpUtil.APPSECRET,params);
            //拿到url的字符串形式
            String urlStr = url.toString();
            Log.i("TAG","原始请求路径-->"+urlStr);

            StringBuilder sb = new StringBuilder(urlStr);
            sb.append("&").append("appkey=").append(HttpUtil.APPKEY);
            sb.append("&").append("sign=").append(sign);
            Log.i("TAG","新的请求路径-->"+sb.toString());

            Request newRequest = new Request.Builder().url(sb.toString()).build();

            return chain.proceed(newRequest);
        }
    }

}
