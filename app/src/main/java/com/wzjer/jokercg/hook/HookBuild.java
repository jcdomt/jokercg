package com.wzjer.jokercg.hook;

import com.wzjer.jokercg.FakeSports;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class HookBuild extends XC_MethodHook {
    public Object Pacestr;
    public Object MinuteSpeed;
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        XposedBridge.log("hook build 方法");
        // 在方法执行前 hook 的逻辑
        //printWorld(param.args[0]);

        // 劫持创高体育核心类
        Class<?> sportBeanClass = XposedHelpers.findClass("net.crigh.cgsport.model.SportBean", param.thisObject.getClass().getClassLoader());
        FakeSports fake = new FakeSports();
        //Context context = global_context;
        // 生成虚假的运动数据
        Object fakedata = fake.MakeFake(param.args[0], sportBeanClass, param.thisObject.getClass().getClassLoader());
        //genData(param.args[0], fakedata);
        // 使用假数据强制覆盖运动记录类
        param.args[0] = fakedata;
        // 这两个值似乎会被二次覆盖，不过没事，我覆盖回去
        Pacestr = sportBeanClass.getField("paceStr").get(fakedata);
        MinuteSpeed = sportBeanClass.getField("minuteSpeedStr").get(fakedata);

        //printWorld(param.args[0]);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        // 在方法执行后 hook 的逻辑
        Class<?> sportBeanClass = XposedHelpers.findClass("net.crigh.cgsport.model.UploadJsonSports", param.thisObject.getClass().getClassLoader());
        sportBeanClass.getField("pace").set(param.thisObject, Pacestr);
        sportBeanClass.getField("minuteSpeed").set(param.thisObject, MinuteSpeed);
        String[] str = ((String)sportBeanClass.getField("beganPoint").get(param.thisObject)).split("\\|");
        double x = Double.parseDouble(str[0])+0.1;
        double y = Double.parseDouble(str[1])+0.1;
        DecimalFormat df = new DecimalFormat("##.######");
        sportBeanClass.getField("endPoint").set(param.thisObject, df.format(x)+"|"+df.format(y));
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
