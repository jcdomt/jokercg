package com.wzjer.jokercg.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class tempHook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        XposedBridge.log("****************************************************");
        XposedBridge.log(param.args[0]+"    "+param.args[1]);
        //printWorld(param.args[0]);

    }

    @Override
    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        //XposedBridge.log("result:"+param.getResult().toString());
        //XposedBridge.log("***************************************************");
        //printWorld(param.args[0]);
    }
}
