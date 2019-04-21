package cn.w_wei.groupon.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.w_wei.groupon.R;
import cn.w_wei.groupon.app.MyApp;
import cn.w_wei.groupon.bean.CityNameBean;
import cn.w_wei.groupon.util.BindView;
import cn.w_wei.groupon.util.KnifeUtil;
import cn.w_wei.groupon.util.OnClick;

public class CitySearchActivity extends AppCompatActivity {
    @BindView(R.id.et_search)
    private EditText etSearch;
    @BindView(R.id.lv_city)
    private ListView lvCity;
    ArrayAdapter<String> adapter;
    List<String> cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_search);
        KnifeUtil.bind(this);
        initListView();
        setListener();
    }

    private void setListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    cities.clear();
                    adapter.notifyDataSetChanged();
                }else{
                    searchCities(s.toString().toUpperCase());
                }
            }
        });
    }

    /**
     * 根据输入的内容，筛选符合的城市名称
     * @param s
     */
    private void searchCities(String s) {
        List<String> temps = new ArrayList<>();
        //中文,正则表达式 char 16bit 0-65535 \\uxxxx
        if(s.matches("[\\u4e00-\\u9fff]+")){//+表示多个字，不加的话只是一个字，+号可以换成{1，}
            for(CityNameBean bean:MyApp.cityNameBeanList){
                if(bean.getCityName().contains(s)){
                    temps.add(bean.getCityName());
                }
            }
        }else{//拼音
            for(CityNameBean bean:MyApp.cityNameBeanList){
                if(bean.getPyName().contains(s)){
                    temps.add(bean.getCityName());
                }
            }
        }
        if(temps.size() > 0){
            cities.clear();
            cities.addAll(temps);
            adapter.notifyDataSetChanged();
        }
    }

    private void initListView() {
        cities = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,cities);
        lvCity.setAdapter(adapter);
        lvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = getIntent();
                data.putExtra("city",adapter.getItem(position));
                setResult(301,data);
                finish();
            }
        });
    }

    @OnClick(values = {R.id.iv_back, R.id.iv_search})
    private void onSearch(View v){
        switch(v.getId()){
            case R.id.iv_back:
                break;
            case R.id.iv_search:
                break;
        }
    }


}
