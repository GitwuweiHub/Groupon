package cn.w_wei.groupon.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.w_wei.groupon.R;
import cn.w_wei.groupon.bean.CommentBean;
import cn.w_wei.groupon.util.HttpUtil;

public class CommentAdapter extends MyBaseAdapter<CommentBean>{

    private int[] resIds = new int[]{R.drawable.movie_star10,R.drawable.movie_star20,
                                    R.drawable.movie_star30,R.drawable.movie_star35,
                                    R.drawable.movie_star40,R.drawable.movie_star45,
                                    R.drawable.movie_star50};
    private String[] ratings = new String[]{"10","20","30","35","40","45","50"};

    public CommentAdapter(Context context, List<CommentBean> datas) {
        super(context, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        final ViewHolder holder;
        if(convertView == null){
            v = inflater.inflate(R.layout.inflate_comment_layout,parent,false);
            holder = new ViewHolder(v);
            //控件层级树观察者,里面所有控件摆放好以后，会触发这个监听器
            holder.llContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    //重新计算每一个Imageview的大小，再按照计算好的重新摆放
                    int width = holder.llContainer.getWidth();
                    //把3dp转化成相应的像素值
                    int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,3,context.getResources().getDisplayMetrics());
                    int size = (width - 2 * margin)/3;
                    for(int i = 0;i < holder.llContainer.getChildCount();i++){
                        ImageView iv = (ImageView) holder.llContainer.getChildAt(i);
                        //容器是谁，就跟谁要边距
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size,size);
                        if(i != 0) {
                            params.setMargins(margin, 0, 0, 0);
                        }
                        iv.setLayoutParams(params);
                    }
                }
            });
            v.setTag(holder);
        }else{
            v = convertView;
            holder = (ViewHolder) v.getTag();
        }
        CommentBean comment = getItem(position);
        holder.tvUsername.setText(comment.getName());
        holder.tvDate.setText(comment.getDate());
        holder.tvPrice.setText(comment.getPrice());
        int resId = resIds[0];
        String rating = comment.getRating();
        for(int i = 0;i < ratings.length;i++){
            if(rating.contains(ratings[i])){
                resId = resIds[i];
                break;
            }
        }
        holder.ivRating.setImageResource(resId);
        holder.tvContent.setText(comment.getContent());
        HttpUtil.loadImage(comment.getAvatar(),holder.ivAvatar);

        //TODO 加评论图片
        String[] imgs = comment.getImgs();
        ImageView[] ivs = new ImageView[3];
        ivs[0] = holder.ivImg01;
        ivs[1] = holder.ivImg02;
        ivs[2] = holder.ivImg03;
        for(int i = 0;i < ivs.length;i++){
            ivs[i].setVisibility(View.GONE);
        }

        for(int i = 0;i < imgs.length;i++){
            ivs[i].setVisibility(View.VISIBLE);
            HttpUtil.loadImage((imgs[i]),ivs[i]);
        }

        return v;
    }

    private class ViewHolder{
        ImageView ivAvatar;
        TextView tvUsername;
        TextView tvDate;
        ImageView ivRating;
        TextView tvPrice;
        TextView tvContent;
        ImageView ivImg01;
        ImageView ivImg02;
        ImageView ivImg03;
        LinearLayout llContainer;

        public ViewHolder(View itemView){
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvUsername = itemView.findViewById(R.id.tv_comment_item_username);
            tvDate = itemView.findViewById(R.id.tv_comment_item_date);
            ivRating = itemView.findViewById(R.id.iv_comment_item_rating);
            tvPrice = itemView.findViewById(R.id.tv_comment_item_price);
            tvContent = itemView.findViewById(R.id.tv_comment_item_content);
            ivImg01 = itemView.findViewById(R.id.iv_comment_item_img01);
            ivImg02 = itemView.findViewById(R.id.iv_comment_item_img02);
            ivImg03 = itemView.findViewById(R.id.iv_comment_item_img03);
            llContainer = itemView.findViewById(R.id.ll_comment_item);
        }
    }
}
