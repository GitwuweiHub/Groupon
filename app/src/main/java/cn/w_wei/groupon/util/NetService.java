package cn.w_wei.groupon.util;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface NetService {

    /**
     * url中个性化的部分放到@GET注解中
     * @param appKey
     * @param sign
     * @param params 添加注解后会自动按要求格式添加数据，并自动转换为utf8格式字符串
     * @return
     */
    @GET("business/find_businesses")
    public Call<String> test(@Query("appkey") String appKey, @Query("sign") String sign, @QueryMap Map<String,String> params);

}
