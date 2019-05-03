package cn.w_wei.groupon.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import cn.w_wei.groupon.R;
import cn.w_wei.groupon.app.MyApp;
import cn.w_wei.groupon.bean.BusinessBean;
import cn.w_wei.groupon.util.DistanceUtil;
import cn.w_wei.groupon.util.HttpUtil;

public class BusinessAdapter extends MyBaseAdapter<BusinessBean.Business> {

    int[] stars;
    Random random;

    public BusinessAdapter(Context context, List<BusinessBean.Business> datas) {
        super(context, datas);
        init();
    }

    private void init() {
        stars = new int[]{R.drawable.movie_star10,R.drawable.movie_star20,
                R.drawable.movie_star30,R.drawable.movie_star35,
                R.drawable.movie_star40,R.drawable.movie_star45,
                R.drawable.movie_star50};
        random = new Random();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View v;
        if(convertView == null){
            v = inflater.inflate(R.layout.inflate_item_business,parent,false);
            holder = new ViewHolder(v);
            v.setTag(holder);
        }else{
            v = convertView;
            holder = (ViewHolder) v.getTag();
        }
        BusinessBean.Business item = getItem(position);
        //TODO 链接不可用
//        HttpUtil.loadImage(item.getPhoto_url(),holder.ivFood);
        String name = item.getName().substring(0,item.getName().indexOf("("));
        if(!TextUtils.isEmpty(item.getBranch_name())){//分店名字
            name = name + "(" + item.getBranch_name() + ")";
        }
        holder.tvShop.setText(name);
        int inx = random.nextInt(7);
        holder.ivStar.setImageResource(stars[inx]);
        int price = random.nextInt(100) + 50;
        holder.tvPrice.setText("¥"+price+"/人");
//        StringBuilder sb = new StringBuilder();
//        for(int i = 0;i < item.getRegions().size();i++){
//            if(i == 0){
//                sb.append(item.getRegions().get(i));
//            }else{
//                sb.append("/").append(item.getRegions().get(i));
//            }
//        }
//        sb.append(" ");
//        for(int i = 0;i < item.getCategories().size();i++){
//            if(i == 0){
//                sb.append(item.getCategories().get(i));
//            }else{
//                sb.append("/").append(item.getCategories().get(i));
//            }
//        }
//        holder.tvFood.setText(sb.toString());
        holder.tvFood.setText(item.getAddress());//仅用于测试
        //TODO holder.tvDistance 学完定位之后再回来设置
        String distance = random.nextInt(1000) + "米";
        if(MyApp.myLocation != null){
            distance = ""+DistanceUtil.getDistance(106.902100,39.444202,MyApp.myLocation.latitude,MyApp.myLocation.longitude);
            holder.tvDistance.setText(distance+"米");
        }else{
            Toast.makeText(MyApp.CONTEXT,"定位失败！",Toast.LENGTH_LONG).show();
        }
        return v;
    }

    private class ViewHolder{
        ImageView ivFood;
        TextView tvFood;
        ImageView ivStar;
        TextView tvShop;
        TextView tvDistance;
        TextView tvPrice;
        private ViewHolder(View itemView){
            ivFood = itemView.findViewById(R.id.iv_food);
            tvFood = itemView.findViewById(R.id.tv_food);
            ivStar = itemView.findViewById(R.id.iv_star);
            tvShop = itemView.findViewById(R.id.tv_shop);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
