package cn.w_wei.groupon.util;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import cn.w_wei.groupon.bean.CityNameBean;

public class DBUtil {

    private  DBHelper dbHelper;
    //只要哪个列的的值唯一且非空，它就可以作为主键列
    //第二参数：充当主键列的类型,此时要在类中响应属性上加上注解的值id = true
    private Dao<CityNameBean,String> dao;

    //在构造器中为dbHelper赋值
    public DBUtil(Context context){
        dbHelper = DBHelper.getInstance(context);
        try {
            dao = dbHelper.getDao(CityNameBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(CityNameBean bean){
        try {
            dao.createIfNotExists(bean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertAll(List<CityNameBean> list){
        for(CityNameBean bean:list){
            insert(bean);
        }
    }

    //.bat批处理文件，现在往数据库插一批数据
    public void insertBatch(final List<CityNameBean> list){
        //建立连接后，一次性将数据全部写入后，再断开连接
        //Callable可看作Runnable的增强版，区别在于前者有返回值，后者没有
        try {
            dao.callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() {
                    for(CityNameBean bean:list){
                        insert(bean);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<CityNameBean> query(){
        List<CityNameBean> list;
        try {
            list = dao.queryForAll();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询数据库时出现异常");
        }
    }
}
