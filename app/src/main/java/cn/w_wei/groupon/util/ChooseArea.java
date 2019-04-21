package cn.w_wei.groupon.util;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cn.w_wei.groupon.app.MyApp;
import cn.w_wei.groupon.entity.City;
import cn.w_wei.groupon.entity.County;
import cn.w_wei.groupon.entity.Province;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChooseArea {
    public static final String ADDRESS = "http://guolin.tech/api/china";
    private static ChooseArea INSTANCE;

    private List<Province> provinces;
    private List<City> cities;
    private List<County> counties;

    private RequestQueue queue;
    //使用Retrofit进行网络访问
    private Retrofit retrofit;
    private NetService netService;

    private ChooseArea(){
        queue = Volley.newRequestQueue(MyApp.CONTEXT);
        retrofit = new Retrofit.Builder()
                .baseUrl("http://guolin.tech/api/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        netService = retrofit.create(NetService.class);
    }

    public static ChooseArea getInstance(){
        if(INSTANCE == null){
            synchronized (ChooseArea.class){
                if(INSTANCE == null){
                    INSTANCE = new ChooseArea();
                }
            }
        }
        return INSTANCE;
    }

    public List<Province> handleProvince(String s) {
        provinces = new ArrayList<>();
        if(TextUtils.isEmpty(s)){
            return null;
        }else{
            try {
                JSONArray jsonArray = new JSONArray(s);
                for(int i = 0;i < jsonArray.length();i++){
                    Province province = new Province();
                    province.setProvinceName(jsonArray.getJSONObject(i).getString("name"));
                    province.setProcinceId(jsonArray.getJSONObject(i).getInt("id"));
                    provinces.add(province);
                }
                return provinces;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public List<City> handleCity(String s, int provinceId) {
        cities =  new ArrayList<>();
        if(TextUtils.isEmpty(s)){
            return null;
        }else{
            try {
                JSONArray jsonArray = new JSONArray(s);
                for(int i = 0;i < jsonArray.length();i++){
                    City city = new City();
                    city.setCityName(jsonArray.getJSONObject(i).getString("name"));
                    city.setCityId(jsonArray.getJSONObject(i).getInt("id"));
                    city.setProvinceId(provinceId);
                    cities.add(city);
                }
                return cities;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public List<County> handleCounty(String s, int cityId) {
        counties = new ArrayList<>();
        if(TextUtils.isEmpty(s)){
            return null;
        }else{
            Log.i("TAG","全国某个城市下的县城-->"+s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                for(int i = 0;i < jsonArray.length();i++){
                    County county = new County();
                    county.setCountName(jsonArray.getJSONObject(i).getString("name"));
                    county.setCityId(cityId);
                    counties.add(county);
                }
                return counties;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void queryFromServer(String url, Response.Listener listener){
        StringRequest request = new StringRequest(url, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MyApp.CONTEXT,volleyError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        queue.add(request);
    }

    public void queryFromServer(){
        Call<String> call = netService.getProvinces();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.i("TAG","利用Retrofit查询省份结果-->"+response.body());
                handleProvince(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Log.i("TAG",throwable.getMessage());
            }
        });
    }

}
