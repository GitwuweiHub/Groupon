package cn.w_wei.groupon.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import cn.w_wei.groupon.R;
import cn.w_wei.groupon.adapter.DealAdapter;
import cn.w_wei.groupon.bean.TuanBean;
import cn.w_wei.groupon.entity.City;
import cn.w_wei.groupon.entity.Province;
import cn.w_wei.groupon.util.BindView;
import cn.w_wei.groupon.util.ChooseArea;
import cn.w_wei.groupon.util.HttpUtil;
import cn.w_wei.groupon.util.KnifeUtil;
import cn.w_wei.groupon.util.OnClick;
import cn.w_wei.groupon.util.RetrofitClient;
import cn.w_wei.groupon.view.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    //头部编辑
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.menu_add)
    View menuAdd;

    //中段
    @BindView(R.id.lv_refresh)
    private PullToRefreshListView lvRefresh;
    ListView listView;
    List<TuanBean.Deal> datas;
    DealAdapter adapter;

    //脚部
    @BindView(R.id.rg_group)
    RadioGroup rgGroup;//用于控制FrameLayout，暂时不用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KnifeUtil.bind(this);
        initialListView();
        initialRadioGroup();
    }

    private void initialRadioGroup() {
        RadioButton radioButton = (RadioButton) rgGroup.getChildAt(0);
        radioButton.setChecked(true);
    }

    @OnClick(values = {R.id.tv_city})
    public void jumpToCity(View v){
        Intent intent = new Intent(this,CityActivity.class);
        startActivityForResult(intent,101);
    }
    //启动时，栈中第一个存的Activity是launcherActivity，
    //startActivity(参数一/参数二)，使用本应用的Activity做例子
    //参数一是告诉被启动的MainActivity启动时要入发起启动的FlashActivity的栈
    //MainActivity入栈时，FlashActivity执行了finish()方法，此时MainActivity位于栈底
    //MainActivity启动CityActivity时，同样原理，但是MainActivity没有执行finish()方法，此时仍然位于栈底
    //当CityActivity启动MainActivity时，自身执行了finish()方法，自身出栈，此时又入栈了一个MainActivity
    //此时栈里有两个MainActivity
    //方法一：重新启动即可。
    //方法二：把MainActivity设置成SingleActivity，当再次被启动时，位于栈底的它会把其上所有的Activity都finish掉，这种方法再一些情况下会有问题
    //方法三：如下所述。
    @Override//该方法在onResume方法之前执行,不用在这里执行刷新
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case 101:
                if(resultCode == 201){
                    String city = data.getStringExtra("city");
                    tvCity.setText(city);
                }
                break;
        }
    }

    @OnClick(values = {R.id.ll_search})
    private void jumpToSearch(View v){
        Toast.makeText(this,"打开搜索界面",Toast.LENGTH_LONG).show();
    }

    @OnClick(values = {R.id.iv_add})
    public void toggleMenu(View v){
        if(menuAdd.getVisibility() == View.VISIBLE){
            menuAdd.setVisibility(View.INVISIBLE);
        }else{
            menuAdd.setVisibility(View.VISIBLE);
        }
    }

    private void initialListView() {
        listView = lvRefresh.getRefreshableView();
        datas = new ArrayList<>();
        adapter = new DealAdapter(this,datas);
        //添加头部要在设置适配器之前
        LayoutInflater inflater = LayoutInflater.from(this);
        View listHeaderIcons = inflater.inflate(R.layout.include_viewpager_main,listView,false);
        View listHeaderRecommend = inflater.inflate(R.layout.header_list_recommend,listView,false);
        listView.addHeaderView(listHeaderIcons);
        listView.addHeaderView(listHeaderRecommend);

        listView.setAdapter(adapter);
        initialListHeaderIcons(listHeaderIcons);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,"第"+position+"个项目！",Toast.LENGTH_LONG).show();
            }
        });
        //添加下拉刷新时图标和文字显示,默认只有下拉，带2的为上拉和下拉刷新
        lvRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        datas.add(0,"新增内容");
//                        adapter.notifyDataSetChanged();
                        lvRefresh.onRefreshComplete();
                       refresh();
                    }
                },5000);
//                refresh();
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             *
             * @param view
             * @param firstVisibleItem
             * @param visibleItemCount
             * @param totalItemCount
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem == 0){
                    tvCity.setVisibility(View.VISIBLE);
                    ivAdd.setVisibility(View.VISIBLE);
                }else{
                    tvCity.setVisibility(View.GONE);
                    ivAdd.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initialListHeaderIcons(View listHeaderIcons) {
        final ViewPager viewPager = listHeaderIcons.findViewById(R.id.vp_home);
        PagerAdapter adapter = new PagerAdapter() {

            int[] resIds = new int[]{
                    R.layout.icons_list_1,
                    R.layout.icons_list_2,
                    R.layout.icons_list_3
            };

            @Override
            public int getCount() {
                return 10000;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                int layoutId = resIds[position % 3];
                View v = LayoutInflater.from(MainActivity.this).inflate(layoutId,viewPager,false);
                container.addView(v);
                return v;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(5001);

        final ImageView[] ivDots = new ImageView[]{
                listHeaderIcons.findViewById(R.id.iv_dot01),
                listHeaderIcons.findViewById(R.id.iv_dot02),
                listHeaderIcons.findViewById(R.id.iv_dot03)
        };

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                //这里控制指示器
                for(ImageView imageView:ivDots){
                    imageView.setImageResource(R.drawable.banner_dot);
                }
                ivDots[i%3].setImageResource(R.drawable.banner_dot_pressed);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("TAG","onResume()");
        refresh();
    }

    /**
     * 向ListView中添加数据
     */
    private void refresh() {
//        datas.add("aaa");
//        datas.add("bbb");
//        datas.add("ccc");
//        datas.add("ddd");
//        datas.add("eee");
//        datas.add("fff");
//        datas.add("ggg");
//        datas.add("hhh");

        //1、发起一个请求，服务器响应
        //以GET方式发起请求
        //请求格式：http://xxx.xxx.com/xxx?key=14xxx&city=xxxutf8xxx
        //HttpClient(apache),android5.0过期，6.0之后废弃了
        //HttpURLConnection
        //Volley,不太适合大量数据规模操作
  /*      HttpUtil.getDailyDealsByVolley(tvCity.getText().toString(), new Response.Listener<TuanBean>() {
            @Override
            public void onResponse(TuanBean tuanBean) {
                Log.i("TAG","Volley团购详情："+tuanBean.toString());
                if(tuanBean != null) {
                    List<TuanBean.Deal> deals = tuanBean.getDeals();
                    Log.i("TAG", "deals-->" + deals);
                    if (deals == null) {
                        deals = new ArrayList<>();
                    }
                    //将deals放到适配器中
                    adapter.addAll(deals, true);
                }else{
                    Toast.makeText(MainActivity.this,"今日无新增内容！",Toast.LENGTH_LONG).show();
                }
                lvRefresh.onRefreshComplete();

            }
        });*/
        //Retrofit+OKHttp
      /*  HttpUtil.getDailyDealsByRetrofit2(tvCity.getText().toString(), new Callback<TuanBean>() {
            @Override
            public void onResponse(Call<TuanBean> call, Response<TuanBean> response) {
                if(response != null){
                    TuanBean tuanBean = response.body();
//                    Toast.makeText(MainActivity.this,deals,Toast.LENGTH_LONG).show();
//                    Gson gson = new Gson();
//                    TuanBean tuanBean = gson.fromJson(deals,TuanBean.class);
                    List<TuanBean.Deal> list = tuanBean.getDeals();
                    adapter.addAll(list,true);
                }else{
                    Toast.makeText(MainActivity.this,"今日无新增内容！",Toast.LENGTH_LONG).show();
                }
                lvRefresh.onRefreshComplete();
            }

            @Override
            public void onFailure(Call<TuanBean> call, Throwable throwable) {
                Log.i("TAG",throwable.getMessage());
                lvRefresh.onRefreshComplete();
            }
        });*/
      HttpUtil.getDailyDealsByRetrofit3(tvCity.getText().toString(), new Callback<TuanBean>() {
          @Override
          public void onResponse(Call<TuanBean> call, Response<TuanBean> response) {

          }

          @Override
          public void onFailure(Call<TuanBean> call, Throwable throwable) {

          }
      });
        //2、根据服务器响应的内容进行解析
        //JSON字符串/XML文档-->使用更广泛
        //解析JSON字符串：
        //JSONLib(JsonObject是其中一个类)
        //GSON
        //fastJson
        //Jackson
        //解析XML文档
        //XMLPull
        //SAX

        //3、将解析结果放到View中显示
        //放到ListView中显示需要适配器、条目布局

//        HttpUtil.testHttpURLConnection();
//        HttpUtil.testVolley(this);
//        HttpUtil.testRetrofit();
//        HttpUtil.testRetrofit(this);

    }

    boolean flag = true;
    @Override
    public void onBackPressed() {
        if(flag){
            Toast.makeText(this,"再按退出！",Toast.LENGTH_LONG).show();
            flag = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    flag = true;
                }
            },3000);
        }else{
            super.onBackPressed();
        }
    }
}
