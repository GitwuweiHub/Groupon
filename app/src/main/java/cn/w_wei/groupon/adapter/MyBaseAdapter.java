package cn.w_wei.groupon.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class MyBaseAdapter<T> extends BaseAdapter {

    private Context context;
    public LayoutInflater inflater;
    public List<T> datas;

    public MyBaseAdapter(Context context,List<T> datas){
        this.context = context;
        this.datas = datas;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }
    //JDK1.5之后才可以返回T
    @Override
    public T getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 追加一个新数据
     * @param t
     */
    public void add(T t){
        datas.add(t);
        notifyDataSetChanged();
    }

    /**
     * 追加或替换数据
     * @param list
     * @param isClear
     */
    public void addAll(List<T> list,boolean isClear){
        if(isClear){
            datas.clear();
        }
        Log.i("TAG","datas-->"+datas);
        Log.i("TAG","list-->"+list);
        datas.addAll(list);
        notifyDataSetChanged();
    }

    public void removeAll(){
        datas.clear();
        notifyDataSetChanged();
    }

    public void remove(T t){
        datas.remove(t);
        notifyDataSetChanged();
    }

    public abstract View getView(int position, View convertView, ViewGroup parent);
}
