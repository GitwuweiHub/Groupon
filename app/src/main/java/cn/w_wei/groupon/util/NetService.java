package cn.w_wei.groupon.util;

import java.util.Map;

import cn.w_wei.groupon.bean.BusinessBean;
import cn.w_wei.groupon.bean.DistrictBean;
import cn.w_wei.groupon.bean.TuanBean;
import cn.w_wei.groupon.entity.Province;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface NetService {

    /**
     * url中个性化的部分放到@GET注解中
     * 拼接格式为 baseUrl + 个性化部分 + ? + appkey= + 值 + & + sign = 值 + & + xxx= + 值 + & + ......
     * @param appKey
     * @param sign
     * @param params 添加注解后会自动按要求格式添加数据，并自动转换为utf8格式字符串
     * @return
     */
    @GET("business/find_businesses")
    public Call<String> test(@Query("appkey") String appKey, @Query("sign") String sign, @QueryMap Map<String,String> params);

    @GET("deal/get_daily_new_id_list")
    public Call<String> getDailyIds(@Query("appkey") String appKey,@Query("sign") String sign ,@QueryMap Map<String,String> params);



    @GET("deal/get_daily_new_id_list")
    public Call<String> getDailyIds2(@QueryMap Map<String,String> params);

    @GET("deal/get_batch_deals_by_id")
    public Call<TuanBean> getDailyDeals3(@QueryMap Map<String,String> params);




    @GET("deal/get_batch_deals_by_id")
    public Call<String> getDailyDeals(@Query("appkey") String appKey,@Query("sign") String sign ,@QueryMap Map<String,String> params);

    @GET("deal/get_batch_deals_by_id")
    public Call<TuanBean> getDailyDeals2(@Query("appkey") String appKey,@Query("sign") String sign ,@QueryMap Map<String,String> params);

    @GET("china")
    public Call<String> getProvinces();

    @GET("business/find_businesses")
    public Call<BusinessBean> getFoods(@QueryMap Map<String,String> params);

    @GET("metadata/get_regions_with_businesses")
    public Call<DistrictBean> getDistricts(@QueryMap Map<String,String> params);

}
