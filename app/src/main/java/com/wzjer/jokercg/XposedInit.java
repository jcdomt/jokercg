package com.wzjer.jokercg;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.DecimalFormat;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedInit implements IXposedHookLoadPackage {

    Context global_context;
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
                    XposedBridge.log("找到m433方法");
                }
            }



            XposedHelpers.findAndHookMethod(TextView.class, "setText", CharSequence.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String str = (String)param.args[0];
                    TextView view = (TextView) param.thisObject;
                    if (str.length() == 8 && view.getTextSize() > 50 && view.getCurrentTextColor() == -1) {
                        param.args[0] = "00:11:45";
                        global_context = view.getContext();
                    }
                }
            });


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

    public class HookBuild extends XC_MethodHook {
        public Object Pacestr;
        public Object MinuteSpeed;
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            XposedBridge.log("hook build 方法");
            // 在方法执行前 hook 的逻辑
            super.afterHookedMethod(param);
            //printWorld(param.args[0]);
            Class<?> sportBeanClass = XposedHelpers.findClass("net.crigh.cgsport.model.SportBean", param.thisObject.getClass().getClassLoader());
            FakeSports fake = new FakeSports();
            Context context = global_context;

            XposedBridge.log(context.getPackageName());

            int v = fake.validateKey(context);
            if (v <= 0) {
                Toast.makeText(context, "次数不足，请充值", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(context, "剩余"+String.valueOf(v)+"次", Toast.LENGTH_SHORT).show();
            }
            Object fakedata = fake.MakeFake(param.args[0], sportBeanClass, param.thisObject.getClass().getClassLoader());

            //genData(param.args[0], fakedata);
            param.args[0] = fakedata;
            Pacestr = sportBeanClass.getField("paceStr").get(fakedata);
            MinuteSpeed = sportBeanClass.getField("minuteSpeedStr").get(fakedata);

            //printWorld(param.args[0]);
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            printWorld(param.thisObject);
            // 在方法执行后 hook 的逻辑
            Class<?> sportBeanClass = XposedHelpers.findClass("net.crigh.cgsport.model.UploadJsonSports", param.thisObject.getClass().getClassLoader());
            sportBeanClass.getField("pace").set(param.thisObject, Pacestr);
            sportBeanClass.getField("minuteSpeed").set(param.thisObject, MinuteSpeed);
            String[] str = ((String)sportBeanClass.getField("beganPoint").get(param.thisObject)).split("\\|");
            double x = Double.parseDouble(str[0])+0.1;
            double y = Double.parseDouble(str[1])+0.1;
            DecimalFormat df = new DecimalFormat("##.######");
            sportBeanClass.getField("endPoint").set(param.thisObject, df.format(x)+"|"+df.format(y));
            printWorld(param.thisObject);
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
