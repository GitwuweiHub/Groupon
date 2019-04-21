package cn.w_wei.groupon.util;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.w_wei.groupon.adapter.DealAdapter;

public class KnifeUtil {

    public static void bind(Activity activity){
        setBindView(activity);
        setOnClick(activity);
    }

    public static void bind(Object obj,View v){

    }

    private static void setBindView(Activity activity) {
        Class clz = activity.getClass();
        Field[] fields = clz.getDeclaredFields();
        for(Field field:fields){
            if(field.isAnnotationPresent(BindView.class)){
                BindView inject = field.getAnnotation(BindView.class);
                int value = inject.value();
                field.setAccessible(true);
                try {
                    field.set(activity,activity.findViewById(value));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void setOnClick(final Activity activity){
        Class clz = activity.getClass();
        Method[] methods = clz.getDeclaredMethods();//用于获得当前类中的方法，而getMethods();则获得父类中所有方法，不包含当前类中私有方法
        Log.i("TAG","methods="+methods);
        for(final Method method:methods){
            method.setAccessible(true);
            Log.i("TAG","method="+method);
            if(method.isAnnotationPresent(OnClick.class)){
                OnClick click = method.getAnnotation(OnClick.class);
                int[] values = click.values();
                for(int value:values){
                    Log.i("TAG","value="+value);
                    if(value != -1){
                        View v = activity.findViewById(value);
                        Log.i("TAG","view="+v);
                        if(v == null)
                            return;
                        Log.i("TAG","view是否为空");
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    method.invoke(activity,v);//可用于为方法绑定多个控件，而invoke(activity);则绑定单个控件
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        }
    }
}
