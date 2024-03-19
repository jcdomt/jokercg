package com.wzjian.jokercg;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class FakeSports {
    public Object MakeFake(Object obj, Class<?> bean, ClassLoader classLoader) {
        try {
            double TARGET_KM = 2;

            double odometer = (TARGET_KM + Math.random() / 3);
            double minutes = 5 * TARGET_KM + Math.random() * TARGET_KM * 3;
            String activeTime = "00:" + padZero(minutes) + ":" + padZero((minutes % 1) * 60);
            double calorie = Math.round(320 / 30 * minutes * 10.0) / 10.0;
            double speed = odometer / minutes * 60;
            String avgSpeed = String.valueOf(Math.round(speed * 100.0) / 100.0);
            String maxSpeed = String.valueOf(Math.round((speed + 0.5 + Math.random() * 0.7) * 100.0) / 100.0);
            String minSpeed = String.valueOf(Math.round((speed - 0.5 - Math.random() * 0.7) * 100.0) / 100.0);
            long now = System.currentTimeMillis();
            // 计算 beginTime
            Calendar calBegin = Calendar.getInstance();
            calBegin.setTimeInMillis((long) (now - minutes * 60000 - calBegin.getTimeZone().getOffset(now)));
            String beginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calBegin.getTime()).replace('T', ' ');

            // 计算 endTime
            Calendar calEnd = Calendar.getInstance();
            calEnd.setTimeInMillis(now - calEnd.getTimeZone().getOffset(now));
            String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calEnd.getTime()).replace('T', ' ');
//            String beginTime = (System.currentTimeMillis() - minutes * 60000 - TimeZone.getDefault().getOffset(System.currentTimeMillis()));
//            String endTime = (new Date(now - new Date().getTimezoneOffset() * 60000)).toISOString().split('.')[0].replace('T', ' ');
            int isValid = 1;
            String isValidReason = "";
            int stepCount = (int) Math.round(3000 + Math.random() * 1000);
            double stepMinute = stepCount / minutes;
            double minutesPerKM = minutes / odometer;
            String lastOdometerTime = "00:" + padZero(minutesPerKM + Math.random() * 1.5) + ":" + padZero(Math.random() * 60);

            String avgPace = padZero(minutesPerKM) + "'" + padZero(Math.random() * 60) + "''";

            Class<?> cpacestrClass = XposedHelpers.findClass("net.crigh.cgsport.model.Cpacestr", classLoader);
            ArrayList paceStr = new ArrayList<Object>();
            for (int i = 1; i <= TARGET_KM; i++) {
                Object cpacestrObject = cpacestrClass.newInstance();
                cpacestrClass.getField("sportId").set(cpacestrObject, "");
                cpacestrClass.getField("km").set(cpacestrObject, String.valueOf(i));
                cpacestrClass.getField("t").set(cpacestrObject, String.valueOf(Math.round((speed + Math.random() * (i < minutes / 2 ? 1 : -1)) * 100.0) / 100.0));
                paceStr.add(cpacestrObject);
            }

            Class<?> miniteSpeedClazz = XposedHelpers.findClass("net.crigh.cgsport.model.CminuteSpeedstr", classLoader);
            ArrayList minuteSpeed = new ArrayList<Object>();
            for (int i = 1; i <= minutes; i++) {
                Object miniteSpeedObject = miniteSpeedClazz.newInstance();
                miniteSpeedClazz.getField("sportId").set(miniteSpeedObject, "");
                miniteSpeedClazz.getField("min").set(miniteSpeedObject, String.valueOf(i));
                miniteSpeedClazz.getField("v").set(miniteSpeedObject, String.valueOf((speed + Math.random() * (i < minutes / 2 ? 1 : -1) * 100.0) / 100.0));
                minuteSpeed.add(miniteSpeedObject);
            }

            bean.getField("dlTime").set(obj, activeTime);
            bean.getField("startDate").set(obj, beginTime);
            bean.getField("endDate").set(obj, endTime);
            bean.getField("cal").set(obj, calorie);
            bean.getField("maxSpeedPerHour").set(obj, maxSpeed);
            bean.getField("minSpeedPerHour").set(obj, minSpeed);
            bean.getField("result").set(obj, isValid);
            bean.getField("isValidReason").set(obj, isValidReason);
            bean.getField("totalStep").set(obj, stepCount);
            bean.getField("avgSpeed").set(obj, avgSpeed);

            DecimalFormat df = new DecimalFormat("#.#");
            
            String formattedValue = df.format(odometer);
            bean.getField("dllc").set(obj, formattedValue);

            bean.getField("avgPace").set(obj, avgPace);
            bean.getField("paceStr").set(obj, paceStr);
            bean.getField("minuteSpeedStr").set(obj, minuteSpeed);
            bean.getField("totalStep").set(obj, stepCount);

            formattedValue = df.format(stepMinute);
            bean.getField("stepMinute").set(obj, formattedValue);

            bean.getField("lastOdometerTime").set(obj, lastOdometerTime);
            bean.getField("alreadyPassPoint").set(obj, "1;2;3");

            XposedBridge.log("假数据生成完毕");
            return obj;

        } catch (Exception exception) {
            XposedBridge.log(exception.toString());
        }

        XposedBridge.log("NULL?");
        return null;

    }


    private String padZero(double num)  {
        int intValue = (int) num; // 将double转换为int，截断小数部分
        if (intValue < 10) {
            return "0" + intValue;
        }
        return String.valueOf(intValue);
    }
}
