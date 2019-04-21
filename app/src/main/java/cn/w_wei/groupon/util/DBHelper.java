package cn.w_wei.groupon.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import cn.w_wei.groupon.bean.CityNameBean;

/**
 * ORM:o-->Object,R-->Relation,M-->Mapper
 * Object-->Java
 * Relation-->DB
 * Mapper-->映射
 *
 * 1.一个Java类对应了数据库中的一种数据表
 * Person 类 <----> Person 表
 * 2.该类中的属性对应数据表的字段
 * int age <----> integer age
 * String name <----> text name
 * 3.每一个类型的对象对应数据表中的一条数据记录
 * p1(25,""Olivia") <----> age:25,name:"Olivia"
 *
 * Created by w_wei on 2019.04.20
 */

public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static DBHelper INSTANCE;

    public static DBHelper getInstance(Context context){
        if(INSTANCE == null){
            synchronized (DBHelper.class){
                if(INSTANCE == null){
                    INSTANCE = new DBHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    private DBHelper(Context context) {
        super(context, "city.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        //在第一次创建city.db数据库时，该方法会被调用
        //创建存储数据的数据表
        try {
            TableUtils.createTableIfNotExists(connectionSource, CityNameBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            /**
             * 第一个参数：客户端与数据库的连接
             * 第二个参数：要删的表
             * 第三个参数：强行删除这张表
             */
            TableUtils.dropTable(connectionSource,CityNameBean.class,true);
            onCreate(sqLiteDatabase,connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }















    //-----------------------测试---------------------------
    public void onCreateTest(SQLiteDatabase db) {
        //创建存储数据的数据表
        String sql = "CREATE TABLE city(_id integer primary key autoincrement," +
                "name varchar(16) not null," +
                "pyName varchar(16) not null," +
                "flag Boolean not null)";
        db.execSQL(sql);
    }

    public void onUpgradeTest(SQLiteDatabase db, int oldVersion, int newVersion) {
        //删除重建原有内容，或修改原有内容
    }
}
