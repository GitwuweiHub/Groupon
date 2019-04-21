package cn.w_wei.groupon.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import cn.w_wei.groupon.R;
import cn.w_wei.groupon.bean.TuanBean;
import cn.w_wei.groupon.util.HttpUtil;
import cn.w_wei.groupon.util.KnifeUtil;

public class DealAdapter extends MyBaseAdapter<TuanBean.Deal> {

    public DealAdapter(Context context, List<TuanBean.Deal> datas) {
        super(context, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.inflate_item_main,parent,false);
            holder = new ViewHolder(convertView);
            holder.ivPic = convertView.findViewById(R.id.iv_item_deal_image);
            holder.tvCount = convertView.findViewById(R.id.tv_item_deal_sellcount);
            holder.tvDetail = convertView.findViewById(R.id.tv_item_deal_detail);
            holder.tvDistance = convertView.findViewById(R.id.tv_item_deal_distance);
            holder.tvPrice = convertView.findViewById(R.id.tv_item_deal_price);
            holder.tvTitle = convertView.findViewById(R.id.tv_item_deal_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        TuanBean.Deal deal = datas.get(position);
        //TODO 呈现图片,也可用谷歌出品的GLIDE，特色为可加载GIF，比较老的universal-ImageLoader
//        HttpUtil.loadImage(deal.getS_image_url(),holder.ivPic);
        HttpUtil.displayImage(deal.getS_image_url(),holder.ivPic);
        holder.tvPrice.setText(deal.getCurrent_price()+"");
        holder.tvTitle.setText(deal.getTitle());
        holder.tvDetail.setText(deal.getDescription());
        Random random = new Random();
        int count = random.nextInt(2000) + 500;
        holder.tvCount.setText("已售"+count+"份");
        //TODO 距离
        return convertView;
    }

    private class ViewHolder{
        ImageView ivPic;
        TextView tvTitle;
        TextView tvDetail;
        TextView tvDistance;
        TextView tvPrice;
        TextView tvCount;
        public ViewHolder(View view){
            //这里也可以用KnifeUtil来绑定
            KnifeUtil.bind(this,view);
        }
    }
}
