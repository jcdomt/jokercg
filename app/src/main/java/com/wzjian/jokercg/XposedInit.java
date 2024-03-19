package com.wzjian.jokercg;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedInit implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

//        if (!lpparam.packageName.equals("com.ang.target")) {
//            return;
//        }
        if (lpparam.packageName.equals("net.crigh.cgsport")) {
            XposedBridge.log("找到创高包");
            Class<?> clazz = XposedHelpers.findClass("net.crigh.cgsport.model.UploadJsonSports", lpparam.classLoader);

            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals("build")) {
                    XposedBridge.hookMethod(method,new HookBuild());
                    XposedBridge.log("找到build方法");
                }
            }

            //XposedHelpers.findAndHookMethod("net.crigh.cgsport.model.UploadJsonSports",lpparam.classLoader,"build",new HookBuild());
            //XposedHelpers.findAndHookMethod(clazz.getClass(), "build", new HookBuild());
            //XposedHelpers.findAndHookMethod(TextView.class, "setText", CharSequence.class, new SystemuiSetText());
        }
    }


    public class HookBuild extends XC_MethodHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            XposedBridge.log("hook build 方法");
            // 在方法执行前 hook 的逻辑
            super.afterHookedMethod(param);
            printWorld(param.args[0]);
            Class<?> sportBeanClass = XposedHelpers.findClass("net.crigh.cgsport.model.SportBean", param.thisObject.getClass().getClassLoader());
            FakeSports fake = new FakeSports();
            Object fakedata = fake.MakeFake(param.args[0], sportBeanClass, param.thisObject.getClass().getClassLoader());

            //genData(param.args[0], fakedata);
            param.args[0] = fakedata;

            printWorld(param.args[0]);
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            // 在方法执行后 hook 的逻辑
        }

        protected Object genData(Object origin, Object fake) {
            Class<?> originClass = origin.getClass();
            Class<?> FakeClass = fake.getClass();

            Field[] fakeFields = FakeClass.getDeclaredFields();
            Field[] originField = originClass.getDeclaredFields();

            for (Field field : fakeFields) {
                field.setAccessible(true); // 使私有字段可访问
                try {
                    if (field.get(fake).equals(null)) {
                        field.set(fake, originClass.getField(field.getName()).get(origin));
                    }
                    //XposedBridge.log("\t" + field.getName() + ": " + field.get(obj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }


            return fake;
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
}
