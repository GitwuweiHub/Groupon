package cn.w_wei.groupon.util;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import cn.w_wei.groupon.config.Constant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * okio
 */
public class RetrofitClient {

    private static RetrofitClient INSTANCE;

    public static RetrofitClient getInstance(){
        if(INSTANCE == null){
            synchronized (RetrofitClient.class) {
                INSTANCE = new RetrofitClient();
            }
        }
        return INSTANCE;
    }


    private Retrofit retrofit;
    private NetService netService;

    private RetrofitClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASEURL)
                .addConverterFactory(ScalarsConverterFactory.create())
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
}
