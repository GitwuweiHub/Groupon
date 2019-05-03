package cn.w_wei.groupon.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.w_wei.groupon.R;
import cn.w_wei.groupon.adapter.CommentAdapter;
import cn.w_wei.groupon.bean.BusinessBean;
import cn.w_wei.groupon.bean.CommentBean;
import cn.w_wei.groupon.util.BindView;
import cn.w_wei.groupon.util.HttpUtil;
import cn.w_wei.groupon.util.KnifeUtil;
import cn.w_wei.groupon.util.OnClick;

public class DetailActivity extends AppCompatActivity {

    private BusinessBean.Business business;
    @BindView(R.id.lv_comment)
    private ListView lvComment;
    private List<CommentBean> datas;
    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        KnifeUtil.bind(this);
        business = (BusinessBean.Business) getIntent().getSerializableExtra("business");
        Log.i("TAG", "business-->"+business);
        initListView();
    }

    private void initListView() {
        View v01 = LayoutInflater.from(this).inflate(R.layout.inflate_item_business, lvComment, false);
        View v02 = LayoutInflater.from(this).inflate(R.layout.inflate_position_layout, lvComment, false);
        initHeaderInfo(v02);
        lvComment.addHeaderView(v01);
        lvComment.addHeaderView(v02);
        datas = new ArrayList<>();
        adapter = new CommentAdapter(this,datas);
        lvComment.setAdapter(adapter);
        //使用Volley访问网络
        HttpUtil.getCommentByVolley(business.getReview_list_url(), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.i("TAG","使用volley获得的响应-->"+s);
                Document document = Jsoup.parse(s);
                List<CommentBean> comments = new ArrayList<>();
                Elements elements = document.select("div[class=mod comment] li[data-id]");
                Log.i("TAG","elements-->"+elements);
                for(Element element:elements){
                    CommentBean comment = new CommentBean();
                    Element img = element.select("a[class=avatar J-avatar] img").get(0);
                    comment.setName(img.attr("alt"));
                    comment.setAvatar(img.attr("src"));
                    Elements spanElements = element.select("p[class=shop-info] span");
                    if(spanElements.size() > 0){

                    }else{

                    }
                    comment.setPrice("￥85/人");
                    Element spanElement = element.select("p[class=shop-info] span").get(0);
                    String rate = spanElement.attr("class").split("-")[3];
                    comment.setRating(rate);
                    Elements pElements = element.select("div[class=info J-info-shot clearfix] p[class=desc]");
                    comment.setContent("不知道说什么");
                    Elements imgElements = element.select("div[class=photos] img");
                    int size = imgElements.size();
                    if(size > 3){
                        size = 3;
                    }
                    String[] imgs = new String[size];
                    for(int i = 0;i < size;i++) {
                        imgs[i] = imgElements.get(i).attr("src");
                    }
                    comment.setImgs(imgs);//长度可能为零
                    Elements timeElements = element.select("div[class=misc-info] span[time]");
                    if(timeElements.size()>0) {
                        comment.setDate(timeElements.get(0).text());
                    }
                    comments.add(comment);
                }
                adapter.addAll(comments,true);
                adapter.notifyDataSetChanged();
                Log.i("TAG","使用volley访问网络结果-->共"+comments.size()+"人的评论内容："+comments);
            }
        });
        //使用Jsoup访问网络，并调用字写回调方法
        HttpUtil.getComment(business.getReview_list_url(), new HttpUtil.OnResponseListener<Document>() {
            @Override
            public void onResponse(Document document) {
                //1)解析
                List<CommentBean> comments = new ArrayList<>();
                Elements elements = document.select("div[class=mod comment] li[data-id]");
                Log.i("TAG","elements-->"+elements);
                for(Element element:elements){
                    CommentBean comment = new CommentBean();
                    Element img = element.select("a[class=avatar J-avatar] img").get(0);
                    comment.setName(img.attr("alt"));
                    comment.setAvatar(img.attr("src"));
                    Elements spanElements = element.select("p[class=shop-info] span");
                    if(spanElements.size() > 0){

                    }else{

                    }
                    comment.setPrice("￥85/人");
                    Element spanElement = element.select("p[class=shop-info] span").get(0);
                    String rate = spanElement.attr("class").split("-")[3];
                    comment.setRating(rate);
                    Elements pElements = element.select("div[class=info J-info-shot clearfix] p[class=desc]");
                    comment.setContent("不知道说什么");
                    Elements imgElements = element.select("div[class=photos] img");
                    int size = imgElements.size();
                    if(size > 3){
                        size = 3;
                    }
                    String[] imgs = new String[size];
                    for(int i = 0;i < size;i++) {
                        imgs[i] = imgElements.get(i).attr("src");
                    }
                    comment.setImgs(imgs);//长度可能为零
                    Elements timeElements = element.select("div[class=misc-info] span[time]");
                    if(timeElements.size()>0) {
                        comment.setDate(timeElements.get(0).text());
                    }
                    comments.add(comment);
                }
                Log.i("TAG","共"+comments.size()+"人的评论内容："+comments);
                //2)放到ListView中呈现
            }
        });
    }

    private void initHeaderInfo(View v02) {
        TextView tvLocate = v02.findViewById(R.id.tv_locate);
        tvLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this,FindActivity.class);
                intent.putExtra("business",business);
                intent.putExtra("from","detail");
                startActivity(intent);
            }
        });
        TextView tvTelphone = v02.findViewById(R.id.tv_phone);
    }

    @OnClick(values = {R.id.iv_back, R.id.iv_share, R.id.iv_favorite, R.id.iv_more})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_share:
                break;
            case R.id.iv_favorite:
                break;
            case R.id.iv_more:
                break;
        }
    }
}
