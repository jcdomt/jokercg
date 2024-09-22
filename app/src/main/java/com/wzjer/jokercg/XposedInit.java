package com.wzjer.jokercg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import com.wzjer.jokercg.hook.*;
public class XposedInit implements IXposedHookLoadPackage {

    public static Context global_context;
    public static Method encryptMethod;
    public static Class<?> encryptClazz;
    public static ClassLoader classloader;
    public static Class<?> deviceClazz;
    public class EncryptReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            abortBroadcast();

            Object[] args = new Object[2];
            //args[0] = intent.getExtras().getString("a1");
            //args[1] = intent.getExtras().getString("a2");

            args[0] = staticData.secret;
            args[1] = "/api/l/v6/statistics?xh=zhe_ge_qi_shi_mei_yong_kan_header_de_shi&beginTime=1708272000000&endTime=1725119999000&type=TERM";
            String url = "https://ggtypt.njtech.edu.cn/cgapp-server"+args[1];


            Object result = XposedHelpers.callStaticMethod(encryptClazz,"cgapiEnrypt", args);
            String r = (String)result;

            String cli= (String) XposedHelpers.callStaticMethod(deviceClazz, "煻刊");
            String imei = (String) XposedHelpers.callStaticMethod(deviceClazz,"烊鐔铄皭恽偛");
            String v1 = (String) XposedHelpers.callStaticMethod(deviceClazz, "骡鮿繤溇霖恖");

            staticData.ua = "cgapp/2.9.5 (Linux; Android 14; Xiaomi/UKQ1.230804.001)";

            if (staticData.token.equals("")) return;

            Intent i = new Intent("cgencrypt_result");
            i.putExtra("r", (String) result);
            i.putExtra("token", staticData.token);
            i.putExtra("client", cli);
            i.putExtra("imei", imei);
            i.putExtra("ua", staticData.ua);
            i.putExtra("v1", v1);

            //util.writeFile(context,"abd.txt","result");

            String[] split = r.split("\\|");
            String sign = split[3];
            String time = split[1];
            new Thread(new Runnable(){
                @Override
                public void run() {
                    XposedBridge.log(HttpUtils.http(url,sign,time,v1,imei,staticData.ua,cli,staticData.token));
                }
            }).start();


            util.writeFile(context,"aa.txt",(String) result+"\n"+staticData.token+"\n"+cli+"\n"+imei+"\n"+staticData.ua+"\n"+v1);
            ContextWrapper c = new ContextWrapper(context);
            c.sendBroadcast(i);
        }

    }
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        classloader = lpparam.classLoader;


        // context 获取
        if (lpparam.packageName.equals("net.crigh.cgsport")) {
            deviceClazz = XposedHelpers.findClass("珂函筙.櫼螉彬彆.亏雄開邠煱嘂咔娡葘戭.阙蔬.熏燵婨左崘畱噛淜",classloader);
            //staticData.ua = (String) XposedHelpers.callStaticMethod(XposedHelpers.findClass("net.crigh.library.base.BaseApplication",classloader),"setUserAgent");


            XposedHelpers.findAndHookMethod(
                    "android.app.Application",
                    lpparam.classLoader,
                    "attach",
                    Context.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            // 在这里获取 Context
                            global_context = (Context) param.args[0];
                            IntentFilter intentFilter = new IntentFilter();
                            intentFilter.addAction("cgencrypt_send");
                            global_context.registerReceiver(new EncryptReceiver(), intentFilter);
                        }
                    }
            );


            Class<?> clazz = XposedHelpers.findClass("net.crigh.cgsport.model.UploadJsonSports", lpparam.classLoader);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals("build")) {
                    XposedBridge.hookMethod(method,new HookBuild());
                }
            }

            Class <?> HookActivityClazz = XposedHelpers.findClass("android.app.Activity", lpparam.classLoader);
            Method[] ActivityMethods = HookActivityClazz.getDeclaredMethods();
            for (Method method : ActivityMethods) {
                if (method.getName().equals("startActivityForResult")) {
                    XposedBridge.hookMethod(method,new ActivityHook());
                    //XposedBridge.log("找到startActivityForResult方法");
                }
            }

//            Class<?> clazz1 = XposedHelpers.findClass("net.crigh.cgsport.model.Prejudgement", lpparam.classLoader);
//            Method[] methods1 = clazz1.getDeclaredMethods();
//            for (Method method : methods1) {
//                if (method.getName().equals("getIsvalid")) {
//                    XposedBridge.hookMethod(method,new HookGetIsvalid());
//                    XposedBridge.log("找到getIsvalid方法");
//                }
//            }


            Class<?> targetClass = XposedHelpers.findClass("珂函筙.櫼螉彬彆.彐叐凴瀱砰漆摯.饙旒賽.亏雄開邠煱嘂咔娡葘戭", lpparam.classLoader);
            XposedHelpers.setStaticObjectField(targetClass, "z", "0");

            Class<?> clazz2 = XposedHelpers.findClass("net.crigh.cgsport.cgactivity.room.RoomSportActivity", lpparam.classLoader);
            Method[] methods2 = clazz2.getDeclaredMethods();
            Method uploadDataFunc = null;
            Method m433Func = null;
            for (Method method : methods2) {
                if (method.getName().equals("uploadData")) uploadDataFunc = method;
                if (method.getName().equals("箃報敚翀茲绡謨谉恗袼澬礲")) {
                    m433Func = method;
                    XposedBridge.hookMethod(method,new HookM433(uploadDataFunc));
                    //XposedBridge.log("找到m433方法");
                }
            }

            // 这个是用于获取 HTTP 的 token 的
            Class <?> tokenClazz = XposedHelpers.findClass("縰丒鰬湓篚爀槽蟡塱溹斠.彐叐凴瀱砰漆摯.櫼螉彬彆.彐叐凴瀱砰漆摯.梍遴蟪迳庘橞燶蟸廮", lpparam.classLoader);
            Method[] tokenMethods = tokenClazz.getDeclaredMethods();
            for (Method method : tokenMethods) {
                if (method.getName().equals("昚娍小肂卽韫飰")) {
                    XposedBridge.hookMethod(method, new tokenHook());
                    XposedBridge.log("成功劫持 TOKEN 函数");
                }
            }


            XposedHelpers.findAndHookMethod(TextView.class, "setText", CharSequence.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String str = (String)param.args[0];
                    if (str == null) return;
                    TextView view = (TextView) param.thisObject;
                    if (str.length() == 8 && view.getTextSize() > 50 && view.getCurrentTextColor() == -1) {
                        param.args[0] = "00:11:45";
                        //global_context = view.getContext();
                    }
                }
            });



            Class<?> clazz_temp = XposedHelpers.findClass("net.crigh.api.encrypt.ChingoEncrypt", lpparam.classLoader);
            encryptClazz = clazz_temp;
            Method[] methods_temp = clazz_temp.getDeclaredMethods();
            for (Method method : methods_temp) {
                if (method.getName().equals("cgapiEnrypt")) {
                    encryptMethod = method;
                    XposedBridge.hookMethod(method,new tempHook());
                    //XposedBridge.log("找到cgapiEnrypt方法");
                }
            }

//            Class<?> targetClass = XposedHelpers.findClass("java.lang.Integer", lpparam.classLoader);
//            methods = targetClass.getDeclaredMethods();
//            for (Method method : methods) {
//                if (method.getName().equals("parseInt")) {
//                    XposedBridge.hookMethod(method,new HookParseInt());
//                    XposedBridge.log("找到parseInt方法");
//                }
//            }
//            XposedHelpers.findAndHookMethod(targetClass,"",new HookParseInt());

            //XposedHelpers.findAndHookMethod("net.crigh.cgsport.model.UploadJsonSports",lpparam.classLoader,"build",new HookBuild());
            //XposedHelpers.findAndHookMethod(clazz.getClass(), "build", new HookBuild());
            //XposedHelpers.findAndHookMethod(TextView.class, "setText", CharSequence.class, new SystemuiSetText());
        }
    }
    final TextView[] clock = new TextView[1];

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

    public class HookGetIsvalid extends XC_MethodHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            param.setResult(1);
        }
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            param.setResult(1);
        }
    }

    public class HookM433 extends XC_MethodHook {
        public Method uploadFunc;

        HookM433() {}
        HookM433(Method _uploadFunc) {
            uploadFunc = _uploadFunc;
        }
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            XposedBridge.log("跳过时间检查");
            uploadFunc.invoke(param.thisObject);
            param.setResult(0);
        }
    }
    public class HookParseInt extends XC_MethodHook {

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            if (param.args[0].equals("2")) {
                param.args[0] = "0";
            }
        }
    }


}
