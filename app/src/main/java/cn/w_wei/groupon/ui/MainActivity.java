package cn.w_wei.groupon.ui;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import cn.w_wei.groupon.R;
import cn.w_wei.groupon.util.BindView;
import cn.w_wei.groupon.util.HttpUtil;
import cn.w_wei.groupon.util.KnifeUtil;
import cn.w_wei.groupon.util.OnClick;
import cn.w_wei.groupon.util.RetrofitClient;
import cn.w_wei.groupon.view.CircleImageView;

public class MainActivity extends AppCompatActivity {
    //头部
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.menu_add)
    View menuAdd;

    //中段
    @BindView(R.id.lv_refresh)
    private PullToRefreshListView lvRefresh;
    ListView listView;
    List<String> datas;
    ArrayAdapter<String> adapter;

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
        Toast.makeText(this,"跳转到切换城市页面",Toast.LENGTH_LONG).show();
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
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,datas);

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
                        datas.add(0,"新增内容");
                        adapter.notifyDataSetChanged();
                        lvRefresh.onRefreshComplete();
                    }
                },3000);
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
        refresh();
    }

    /**
     * 向ListView中添加数据
     */
    private void refresh() {
        datas.add("aaa");
        datas.add("bbb");
        datas.add("ccc");
        datas.add("ddd");
        datas.add("eee");
        datas.add("fff");
        datas.add("ggg");
        datas.add("hhh");

        //1、发起一个请求，服务器响应
        //以GET方式发起请求
        //请求格式：http://xxx.xxx.com/xxx?key=14xxx&city=xxxutf8xxx
        //HttpClient(apache),android5.0过期，6.0之后废弃了
        //HttpURLConnection
        //Volley,不太适合大量数据规模操作
        //Retrofit+OKHttp

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
        HttpUtil.testRetrofit(this);
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
