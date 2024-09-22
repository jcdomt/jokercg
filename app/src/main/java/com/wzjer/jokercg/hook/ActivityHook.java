package com.wzjer.jokercg.hook;

import android.app.Activity;
import android.content.Intent;

import com.wzjer.jokercg.XposedInit;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class ActivityHook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        Intent intent = (Intent) param.args[0];
        String originalActivity = "net.crigh.cgsport.ui.activity.FeedbackActivity";
        String myActivity = "com.wzjer.jokercg.JokerActivity";

        //XposedInit.global_context = ((Activity)param.thisObject).getBaseContext();

        // 判断是否是要替换的目标 Activity
        if (intent.getComponent() != null && originalActivity.equals(intent.getComponent().getClassName())) {
            //printWorld(intent);
            // 替换成我们自定义的 Activity
            intent.setClassName("com.wzjer.jokercg", myActivity);
        }
    }

    protected void printWorld(Object obj) {
        Class<?> clazz = obj.getClass();
        // 输出字段信息
        Field[] fields = clazz.getDeclaredFields();
        XposedBridge.log("Fields:");
        for (Field field : fields) {
            field.setAccessible(true); // 使私有字段可访问
            try {
                XposedBridge.log("\t" + field.getName() + ": " + field.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}