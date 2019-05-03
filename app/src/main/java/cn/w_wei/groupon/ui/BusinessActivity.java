package cn.w_wei.groupon.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.w_wei.groupon.R;
import cn.w_wei.groupon.adapter.BusinessAdapter;
import cn.w_wei.groupon.app.MyApp;
import cn.w_wei.groupon.bean.BusinessBean;
import cn.w_wei.groupon.bean.DistrictBean;
import cn.w_wei.groupon.util.BindView;
import cn.w_wei.groupon.util.HttpUtil;
import cn.w_wei.groupon.util.KnifeUtil;
import cn.w_wei.groupon.util.OnClick;
import cn.w_wei.groupon.util.SPUtil;
import cn.w_wei.groupon.view.BannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessActivity extends AppCompatActivity {
    @BindView(R.id.iv_loading)
    private ImageView ivLoading;
    @BindView(R.id.lv_meishi)
    private PullToRefreshListView rvRefresh;
    @BindView(R.id.tv_china)
    private TextView tvChina;
    @BindView(R.id.tv_foreign)
    private TextView tvForeign;
    private BusinessAdapter adapter;
    private List<BusinessBean.Business> datas;
    private AnimationDrawable drawable;

    @BindView(R.id.district_layout)
    private View districtLayout;
    @BindView(R.id.tv_business)
    private TextView tvBusiness;
    @BindView(R.id.tv_subway)
    private TextView tvSubway;
    @BindView(R.id.lv_business)
    private ListView lvBusiness;
    @BindView(R.id.lv_subway)
    private ListView lvSubway;
    @BindView(R.id.tv_business_1)
    private TextView tvRegion;

    private List<String> leftDatas;
    private List<String> rightDatas;

    private ArrayAdapter<String> leftAdapter;
    private ArrayAdapter<String> rightAdapter;

    private SPUtil spUtil;
    private String city;
    private List<DistrictBean.City.District> districtList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        KnifeUtil.bind(this);
        spUtil = new SPUtil(this);
        initListView();
    }

    private void initListView() {
        final ListView listView = rvRefresh.getRefreshableView();

        listView.setEmptyView(ivLoading);//有listview自动管理ivLoading的可见性，没有数据或没有绑定adapter时为可见，有数据时为不可见

        datas = new ArrayList<>();
        adapter = new BusinessAdapter(this,datas);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BusinessBean.Business business;
                //判断是否移除了头部，所有下拉刷新都自带一个头部，所以...
                if(spUtil.isCloseBanner()){
                    business = adapter.getItem(position);
                }else{
                    business = adapter.getItem(position-2);
                }
                Intent intent = new Intent(BusinessActivity.this,DetailActivity.class);
                intent.putExtra("business",business);
                startActivity(intent);
            }
        });

        if(!spUtil.isCloseBanner()) {
            int[] ids = new int[]{R.drawable.ic_adv01, R.drawable.ic_adv02};
//        ids = null;
            final BannerView banner = new BannerView(this, ids);
            banner.setOnCloseBannerListener(new BannerView.OnCloseBannerListener() {
                @Override
                public void onClose() {
                    spUtil.setCloseBanner(true);
                    listView.removeHeaderView(banner);
                }
            });
            listView.addHeaderView(banner);
        }

        rvRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                },3000);
            }
        });

        leftDatas = new ArrayList<>();
        leftAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,leftDatas);
        lvBusiness.setAdapter(leftAdapter);

        rightDatas = new ArrayList<>();
        rightAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,rightDatas);
        lvSubway.setAdapter(rightAdapter);

    }

    private void refresh() {
        if(MyApp.region == null){
            tvRegion.setText("全部");
        }else{
            tvRegion.setText(MyApp.region);
        }
        Intent intent = getIntent();
        city = intent.getStringExtra("city");
        //引用其它项目时，除了可以引用代码外，还可以引用其它所有资源，包括jar包和图片等
        ivLoading.setImageResource(R.drawable.refreshing_anim);
        drawable = (AnimationDrawable) ivLoading.getDrawable();
        drawable.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drawable.stop();
                HttpUtil.getFoodsByRetrofit(city, MyApp.region, new Callback<BusinessBean>() {
                    @Override
                    public void onResponse(Call<BusinessBean> call, Response<BusinessBean> response) {
                        BusinessBean businessBean = response.body();
                        Toast.makeText(BusinessActivity.this,"查询到的商户数据-->" + businessBean,Toast.LENGTH_SHORT).show();
                        Log.i("TAG","查询到的商户数据-->" + businessBean);
                        List<BusinessBean.Business> list = businessBean.getBusinesses();
                        if(list != null){
                            datas.addAll(0,list);
                            adapter.notifyDataSetChanged();
                        }else{
                            addData();
                        }
                        rvRefresh.onRefreshComplete();
                    }

                    @Override
                    public void onFailure(Call<BusinessBean> call, Throwable throwable) {

                    }
                });

                HttpUtil.getDistricsByRetrofit(city, new Callback<DistrictBean>() {
                    @Override
                    public void onResponse(Call<DistrictBean> call, Response<DistrictBean> response) {
                        DistrictBean districtBean = response.body();
                        if(districtBean.getCities() != null) {
                            districtList = districtBean.getCities().get(0).getDistricts();
                            List<String> districtNames = new ArrayList<>();
                            for (int i = 0; i < districtNames.size(); i++) {
                                DistrictBean.City.District district = districtList.get(i);
                                districtNames.add(district.getDistrict_name());
                            }
                            leftDatas.clear();
                            leftDatas.addAll(districtNames);
                            leftAdapter.notifyDataSetChanged();
                            //这里由于neighborhoods和下面那一个指向的是同一个地址，所以每次都将会添加："全部" + districtName
                            //先修改为如下语句，以便每次都会创建一个对象以便复制右侧数据，
                            //缺点是每次点击都会创建一个堆区域，有点浪费空间
                            List<String> neighborhoods = new ArrayList<>(districtList.get(0).getNeighborhoods());
                            String districtName = districtList.get(0).getDistrict_name();
                            neighborhoods.add(0, "全部" + districtName);
                            rightDatas.clear();
                            rightDatas.addAll(neighborhoods);
                            rightAdapter.notifyDataSetChanged();

                            lvBusiness.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    List<String> neighborhoods = new ArrayList<>(districtList.get(position).getNeighborhoods());
                                    String districtName = districtList.get(position).getDistrict_name();
                                    neighborhoods.add(0, "全部" + districtName);
                                    rightDatas.clear();
                                    rightDatas.addAll(neighborhoods);
                                    rightAdapter.notifyDataSetChanged();
                                }
                            });

                            lvSubway.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String region = rightAdapter.getItem(position);
                                    if(position ==0){
                                        region = region.substring(2);
                                    }
                                    //TODO 当页面销毁时，记住本次选择以便在下次进入时，直接打开当前页面
                                    tvRegion.setText(region);
                                    MyApp.region = region;
                                    districtLayout.setVisibility(View.INVISIBLE);
                                    adapter.removeAll();//写在这里可以让网络加载时，把吃包子的动画显示出来
                                    HttpUtil.getFoodsByRetrofit(city, region, new Callback<BusinessBean>() {
                                        @Override
                                        public void onResponse(Call<BusinessBean> call, Response<BusinessBean> response) {
                                            BusinessBean businessBean = response.body();
                                            List<BusinessBean.Business> list = businessBean.getBusinesses();
                                            adapter.addAll(list,true);
                                        }

                                        @Override
                                        public void onFailure(Call<BusinessBean> call, Throwable throwable) {

                                        }
                                    });
                                }
                            });
                        }else{
                            //TODO 模拟假数据
                            Log.i("TAG","districtBean-->"+ districtBean);
                            leftDatas.clear();
                            leftDatas.add("热门商区");
                            Random random = new Random();
                            for(int i = 0;i < 9;i++){
                                String left = "";
                                for(int j = 0;j < random.nextInt(6)+1;j++){
                                    left += 'A';
                                }
                                leftDatas.add(left);
                            }
                            leftAdapter.notifyDataSetChanged();

                            rightDatas.clear();
                            rightDatas.add("热门商区");
                            for(int i = 0;i < 9;i++){
                                String right = "";
                                for(int j = 0;j < random.nextInt(6)+1;j++){
                                    right += 'B';
                                }
                                rightDatas.add(right);
                            }
                            rightAdapter.notifyDataSetChanged();

                            lvBusiness.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    rightDatas.clear();
                                    rightDatas.add("热门商区");
                                    Random random = new Random();
                                    for(int i = 0;i < 9;i++){
                                        String right = "";
                                        for(int j = 0;j < random.nextInt(6)+1;j++){
                                            right += 'B';
                                        }
                                        rightDatas.add(right);
                                    }
                                    rightAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<DistrictBean> call, Throwable throwable) {

                    }
                });

            }
        },5000);
    }
    private void addData() {
        BusinessBean.Business item;
        item = new BusinessBean.Business();
        item.setName("1618清真公馆(用于测试)");
        item.setBranch_name("马场道店");
        item.setAddress("牛肉馅饼");
        item.setReview_list_url("http://www.dianping.com/shop/108109792");
        datas.add(item);
        item = new BusinessBean.Business();
        item.setName("1618清真公馆(用于测试)");
        item.setBranch_name("马场道店");
        item.setAddress("牛肉馅饼");
        item.setReview_list_url("http://www.dianping.com/shop/108109792");
        datas.add(item);
        item = new BusinessBean.Business();
        item.setName("1618清真公馆(用于测试)");
        item.setBranch_name("马场道店");
        item.setAddress("牛肉馅饼");
        item.setReview_list_url("http://www.dianping.com/shop/108109792");
        datas.add(item);
        item = new BusinessBean.Business();
        item.setName("1618清真公馆(用于测试)");
        item.setBranch_name("马场道店");
        item.setAddress("牛肉馅饼");
        item.setReview_list_url("http://www.dianping.com/shop/108109792");
        datas.add(item);
        item = new BusinessBean.Business();
        item.setName("1618清真公馆(用于测试)");
        item.setBranch_name("马场道店");
        item.setAddress("牛肉馅饼");
        item.setReview_list_url("http://www.dianping.com/shop/108109792");
        datas.add(item);
        item = new BusinessBean.Business();
        item.setName("1618清真公馆(用于测试)");
        item.setBranch_name("马场道店");
        item.setAddress("牛肉馅饼");
        item.setReview_list_url("http://www.dianping.com/shop/108109792");
        datas.add(item);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvChina.setText("商户");
        tvForeign.setText("闪惠团购");
        refresh();
    }

    @OnClick(values = {R.id.tv_china, R.id.tv_foreign, R.id.iv_back,R.id.tv_business_1})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_china:
                //显示国内的城市信息
                setTvChina(true);
                setTvForeign(false);
                break;
            case R.id.tv_foreign:
                //显示国外的城市信息
                setTvChina(false);
                setTvForeign(true);
                break;
            case R.id.tv_business_1:
                if(districtLayout.getVisibility() != View.VISIBLE){
                    districtLayout.setVisibility(View.VISIBLE);
                }else{
                    districtLayout.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    private void setTvChina(boolean flag) {
        if (flag) {
            tvChina.setBackgroundResource(R.drawable.city_china_red);
            tvChina.setTextColor(Color.parseColor("#ffffff"));
        } else {
            tvChina.setBackgroundResource(R.drawable.city_china_white);
            tvChina.setTextColor(Color.parseColor("#232323"));
        }
    }

    private void setTvForeign(boolean flag) {
        if (flag) {
            tvForeign.setBackgroundResource(R.drawable.city_foreign_red);
            tvForeign.setTextColor(Color.parseColor("#ffffff"));
        } else {
            tvForeign.setBackgroundResource(R.drawable.city_foreign_white);
            tvForeign.setTextColor(Color.parseColor("#232323"));
        }
    }
}
