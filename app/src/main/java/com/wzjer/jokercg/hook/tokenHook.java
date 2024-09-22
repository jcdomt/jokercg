package com.wzjer.jokercg.hook;

import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import com.wzjer.jokercg.XposedInit;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class tokenHook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        // public static String m5619(TreeMap<String, String> treeMap, String str, String str2, String str3) {
        // 参数map url_path currentTimeMillis url_host+path

//        XposedBridge.log(param.args[1]+"****"+param.args[2]+"****"+param.args[3]);
//        TreeMap<String, String> t = (TreeMap<String, String>)param.args[0];
//        String integ = null;Iterator iter = t.entrySet().iterator();
//        while(iter.hasNext()) {
//            Map.Entry entry = (Map.Entry)iter.next();String key = (String)entry.getKey();integ = (String)entry.getValue();
//            XposedBridge.log(key+": "+integ);
//        }
    }
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        String data = (String)param.getResult();
        if (((String) param.args[0]).equals("TOKEN") && staticData.token.equals("")) {
            XposedBridge.log("创高TOKEN获取成功:"+data);
            staticData.token = data;


            Looper.prepare();
            Toast.makeText(XposedInit.global_context, "成功获取 TOKEN 了", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }

        if (((String) param.args[0]).equals("SECRET") && staticData.secret.equals("")) {
            String token = (String)param.getResult();
            XposedBridge.log("创高SECRET获取成功:"+data);
            staticData.secret = data;


            Looper.prepare();
            Toast.makeText(XposedInit.global_context, "成功获取 SECRET 了", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }


    }
}
