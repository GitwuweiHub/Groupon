package cn.w_wei.groupon.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.w_wei.groupon.R;
import cn.w_wei.groupon.adapter.CityAdapter;
import cn.w_wei.groupon.app.MyApp;
import cn.w_wei.groupon.bean.CityNameBean;
import cn.w_wei.groupon.entity.City;
import cn.w_wei.groupon.entity.County;
import cn.w_wei.groupon.entity.Province;
import cn.w_wei.groupon.util.BindView;
import cn.w_wei.groupon.util.ChooseArea;
import cn.w_wei.groupon.util.DBUtil;
import cn.w_wei.groupon.util.KnifeUtil;
import cn.w_wei.groupon.util.OnClick;
import cn.w_wei.groupon.util.PinyinUtil;

import static cn.w_wei.groupon.util.ChooseArea.ADDRESS;

public class CityActivity extends AppCompatActivity {
    @BindView(R.id.tv_china)
    private TextView tvChina;
    @BindView(R.id.tv_foreign)
    private TextView tvForeign;
    @BindView(R.id.rv_recycler)
    private RecyclerView rvPosition;
    private CityAdapter adapter;

    private List<String> datas;
    private List<CityNameBean> cityNameBeans;
    private List<Province> provinces;
    private List<City> cities;
    private List<County> counties;

    private DBUtil dbUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        KnifeUtil.bind(this);
        dbUtil = new DBUtil(this);
        initialPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        //优先从内存缓存中读取城市数据
        //缓存也不能滥用，比如实时性很强的数据，写在缓存里不好
        Log.i("TAG", "MyApp.cityNameBeanList.size()-->" + MyApp.cityNameBeanList);
        if (MyApp.cityNameBeanList != null && MyApp.cityNameBeanList.size() > 0) {
            //不能用等号，不然会指向同一个地址
            cityNameBeans.addAll(MyApp.cityNameBeanList);
            adapter.notifyDataSetChanged();
            Log.i("TAG", "城市数据是从缓存中加载的！");
            return;
        }
        //从数据库中读取城市数据
        List<CityNameBean> list = dbUtil.query();
        if(list != null && list.size() > 0){
            cityNameBeans.addAll(list);
            MyApp.cityNameBeanList = list;
            adapter.notifyDataSetChanged();
            Log.i("TAG","城市数据从数据库中加载！");
            Toast.makeText(this,"城市数据从数据库中加载！",Toast.LENGTH_LONG).show();
            return;
        }
        indexOne = 0;
        indexTwo = 0;
        datas.clear();
        cityNameBeans.clear();
        queryProvince();
        ChooseArea.getInstance().queryFromServer();
    }

    private void initialPosition() {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvPosition.setLayoutManager(manager);
        rvPosition.setFitsSystemWindows(true);
        datas = new ArrayList<>();
        cityNameBeans = new ArrayList<>();
        adapter = new CityAdapter(this, cityNameBeans);
        rvPosition.setAdapter(adapter);
        View headerView = LayoutInflater.from(this).inflate(R.layout.just_for_test,rvPosition,false);
        adapter.addHeaderView(headerView);
        //设置下划线，也可以加在item布局中
        rvPosition.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(new CityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position, CityNameBean bean) {
//                String result = cityNameBeans.get(position).getCityName() + "==" + bean.getCityName();
//                Toast.makeText(CityActivity.this, result, Toast.LENGTH_LONG).show();
                String city = bean.getCityName();
                Intent intent = getIntent();
                intent.putExtra("city",city);
                setResult(201,intent);//同一个Activity，不同的操作，可能会返回不同的结果，可用resultCode来区别一下
//                finishActivity(101);
                finish();
            }
        });
    }

    private void queryProvince() {
        queryFromServer(ADDRESS, 0, "province");
    }

    private void queryCity(int provinceId) {
        String url = ADDRESS + "/" + provinceId;
        queryFromServer(url, provinceId, "city");
    }

    private void queryCounty(int provinceId, int cityId) {
        String url = ADDRESS + "/" + provinceId + "/" + cityId;
        queryFromServer(url, cityId, "county");
    }

    private int indexOne = 0;
    private int indexTwo = 0;

    private void queryFromServer(String url, final int id, final String result) {
        ChooseArea.getInstance().queryFromServer(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if ("province".equals(result)) {
                    provinces = ChooseArea.getInstance().handleProvince(s);
                    if (provinces != null) {
                        indexOne += provinces.size();
                        for (int i = 0; i < provinces.size(); i++) {
                            queryCity(provinces.get(i).getProcinceId());
                        }
                    }
                } else if ("city".equals(result)) {
                    cities = ChooseArea.getInstance().handleCity(s, id);
                    if (cities != null) {
                        indexTwo++;
                        indexOne += cities.size();
                        for (int i = 0; i < cities.size(); i++) {
//                            datas.add(cities.get(i).getCityName());
                            queryCounty(cities.get(i).getProvinceId(),cities.get(i).getCityId());
                        }
                    }
                } else if ("county".equals(result)) {
                    counties = ChooseArea.getInstance().handleCounty(s, id);
                    if (counties != null) {
                        indexTwo++;
                        for (int i = 0; i < counties.size(); i++) {
                            datas.add(counties.get(i).getCountName());
                        }
                        compareOneAndTwo();
                    }
                }
            }
        });
    }

    private void compareOneAndTwo() {
        if (indexTwo == indexOne) {
            for (String name : datas) {
                CityNameBean bean = new CityNameBean();
                bean.setCityName(name);
                bean.setPyName(PinyinUtil.getPinyin(name));
                bean.setLetter(PinyinUtil.getLetter(name));
                cityNameBeans.add(bean);
//                Log.i("TAG",bean.toString());
            }
            //按字典顺序进行比较，第二个参数为比较器，顺序有错的地方是因为多音字
            Collections.sort(cityNameBeans, new Comparator<CityNameBean>() {
                @Override
                public int compare(CityNameBean o1, CityNameBean o2) {
                    //返回字符串的比较结果，字符串实现了Comparable接口
                    return o1.getPyName().compareTo(o2.getPyName());
                }
            });
            //对数据源进行分组，同组的第一个数据为false,其它数据为true
            char c = cityNameBeans.get(0).getLetter();
            char p;
            for (int i = 1; i < cityNameBeans.size(); i++) {
                p = cityNameBeans.get(i).getLetter();
                if (p == c) {
                    cityNameBeans.get(i).setFlag(true);
                } else {
                    c = p;
                }
            }
            Log.i("TAG", cityNameBeans.toString());
            //将数据缓存起来
            Log.i("TAG", "数据从网络中加载");
            //将数据缓存起来
            MyApp.cityNameBeanList.addAll(cityNameBeans);
            //向数据库中写入城市数据
            //密集型操作要放在工作线程中比较好
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    long start = System.currentTimeMillis();
                    dbUtil.insertBatch(cityNameBeans);
                    long end = System.currentTimeMillis();
                    Log.i("TAG","向数据库中写数据耗时-->"+String.valueOf(end - start));
                }
            }.start();
            Log.i("TAG", "缓存中的数据-->" + MyApp.cityNameBeanList.toString());
            adapter.notifyDataSetChanged();
        }
    }

    @OnClick(values = {R.id.tv_china, R.id.tv_foreign, R.id.iv_back, R.id.ll_search})
    private void setHeaderCity(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_china:
                //显示国内的城市信息
                setTvChina(true);
                setTvForeign(false);
                refresh();
                break;
            case R.id.tv_foreign:
                //显示国外的城市信息
                setTvChina(false);
                setTvForeign(true);
                cityNameBeans.clear();
                adapter.notifyDataSetChanged();
                break;
            case R.id.ll_search:
                Intent intent = new Intent(this,CitySearchActivity.class);
                startActivityForResult(intent,201);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case 201:
                if(resultCode == 301){
                    setResult(201,data);
                    finish();
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
