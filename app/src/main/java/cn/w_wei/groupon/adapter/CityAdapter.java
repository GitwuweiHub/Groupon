package cn.w_wei.groupon.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.List;

import cn.w_wei.groupon.R;
import cn.w_wei.groupon.bean.CityNameBean;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> implements SectionIndexer {
    private Context context;
    private List<CityNameBean> datas;
    private LayoutInflater layoutInflater;
    //为RecyclerView添加一个头部视图
    private View headerView;

    private static final int HEADER = 0;
    private static final int ITEM = 1;

    private OnItemClickListener listener;

    //写在方法中时，可以在需要时设置监听，写在构造方法中却必须要实现
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public CityAdapter(Context context,List<CityNameBean> datas){
        this.context = context;
        this.datas = datas;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    /**
     * 获取某个分组的起始位置
     * @param sectionIndex
     * @return
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        for(int i = 0;i < datas.size();i++){
            if(datas.get(i).getLetter() == sectionIndex){
                return i;
            }
        }
        //只要返回一个数据源中不存在的下标值即可
        //TODO 这个值如何返回，当前数据源（datas）没有一个数据属于传入的sectionIndex，程序才会执行到这里
        return datas.size();
    }

    /**
     * 第position位置上的数据所属分组是什么
     * @param position
     * @return
     */
    @Override
    public int getSectionForPosition(int position) {
        return datas.get(position).getLetter();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPosition;
        TextView tvLetter;
        @Nullable//即使为空，此时也不会报错
        ImageView ivTest;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //这里可以用KnifeUtil进行绑定，代码暂时还没写
            tvPosition = itemView.findViewById(R.id.tv_position);
            tvLetter = itemView.findViewById(R.id.tv_letter);
            ivTest = itemView.findViewById(R.id.iv_test);
        }
    }

    /**
     *
     * @param viewGroup
     * @param i 添加到recyclerview条目的类型，调用getItemViewType方法来得到所需的返回值
     * @return
     */
    @Override
    public CityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        if(this.headerView != null && i == HEADER){
            v = headerView;
        }else {
            v = layoutInflater.inflate(R.layout.inflate_item_city, viewGroup, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if(this.headerView != null){
            if(position == 0){
                return HEADER;
            }else{
                return ITEM;
            }
        }
        return super.getItemViewType(position);
    }

    private char c;
    @Override
    public void onBindViewHolder(@NonNull CityAdapter.ViewHolder viewHolder, final int j) {
        if(headerView != null && j ==0){
            return;
        }
        int k = headerView != null ? j - 1 : j;

        final int i = k;
        //自定义头部，在条目中添加即可，在position为0时，显示，其它为GONE
//        if(i == 0){
//            viewHolder.ivTest.setVisibility(View.VISIBLE);
//        }else{
//            viewHolder.ivTest.setVisibility(View.GONE);
//        }
        viewHolder.tvPosition.setText(datas.get(i).getCityName());
        //自写分组方法
//        if(datas.get(i).isFlag()){
//            viewHolder.tvLetter.setVisibility(View.GONE);
//        }else{
//            //这里跟ListView，gridView一样，存在布局复用，提高效率的做法，所以必须加上这一行，重新让它显示出来
//            viewHolder.tvLetter.setVisibility(View.VISIBLE);
//        }
        c = datas.get(i).getLetter();
        String str = String.valueOf(c);
        viewHolder.tvLetter.setText(str);

        //i这个位置的数据是不是该数据所属分组的起始位置
        if(i == getPositionForSection(getSectionForPosition(i))){
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
        }else{
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        View itemView = viewHolder.itemView;
        if(this.listener != null){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,i,datas.get(i));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public interface OnItemClickListener{
        void onItemClick(View itemView,int position,CityNameBean bean);
    }

    public void addHeaderView(View headerView){
        Log.i("TAG","该方法被调用了几次？"+"headerView-->"+headerView);
        if(this.headerView == null) {
            this.headerView = headerView;
            notifyItemChanged(0);
        }else{
//            throw new RuntimeException("不允许添加多个头部！");

        }
    }
    public View getHeaderView(){
        return headerView;
    }
}
