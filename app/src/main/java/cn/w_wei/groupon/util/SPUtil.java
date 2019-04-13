package cn.w_wei.groupon.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import cn.w_wei.groupon.config.Constant;

/**
 * 对偏好文件的访问
 * 1、Context的getSharedPreferences(文件名，模式);
 *      存放用户个人设置
 * 2、Activity的getPreference(模式);获取以preference_activity名的偏好设置文件
 *      存放于界面相关的设置
 * 3、PreferenceManager的getDefaultSharedPreferences(Context);
 *      获取preference_包名 偏好设置文件
 *      模式 Context_MODE_PRIVATE
 *      存放全局设置
 * created by w_wei on 2019-04-12
 */
public class SPUtil {

    private SharedPreferences sp;

    //通过构造器重载，以不同的方式来获得偏好设置文件

    public SPUtil (Context context,String name){
        sp = context.getSharedPreferences(name,Context.MODE_PRIVATE);
    }

    public SPUtil(Context context){
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isFirst(){

        return sp.getBoolean(Constant.FIRST,true);
    }

    public void setFirst(boolean flag){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constant.FIRST,flag);
        editor.commit();
    }
}
