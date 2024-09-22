package com.wzjer.jokercg;


import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import android.content.Intent;
import android.content.IntentFilter;

import com.wzjer.jokercg.hook.staticData;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class JokerActivity extends AppCompatActivity {

    static Context global_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joker);

        global_context = getBaseContext();


        WebView webView = (WebView) findViewById(R.id.webview);


        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                encrypt(message);
                return true;
            }
        });

        encrypt("/api/l/v7/sportlist?xh=202321054021&type=1");

        //webView.loadUrl("file:///android_asset/index.html?id="+util.randomStr(10));

    }
    void encrypt(String str) {
        String[] args = new String[10];
        //args[0]="gz1ioA9FaxpyTvJodoj9zoRKtRSOlp9MtNBaXabinWJrGIdzNKqCfhh2iPhz5QZAvWaPOVE0yb9a+ULFN7l6YqxUcL3RRlty9PjD9LfhY5TBYCS1u6/ir4JDi7BxS8d0oV3dVNXhs0Eq14sUWbTkRjV7Q/sCOcZikiZJaLw06ElyMBTTfiCgPq19hLe8lGw8ZbhsftYLYRjG8jtvoQ8uZ3nAOLDyPjYSbiFyGrg8Bi3awLuihBrd3xIfO8z2dFLc6LcE5V2BmCY4kjthYB3l2j7M+FG+QOHXcpFKrxSHhPUJJcX2vM6DUSVAezntAqA1i4zRE6RaXydhiF9ldG6fCaRML2HLSg4FIcb5OfKzAbZn4XnB42mz+DPr0lkqbRoSCseByVdVaRiMAK7K7tNSuv/2tw033l7wmfZu2wW4cNPluRm86F+jPeY6RYQ7SOu+kh2OYLU04G4mYINaM9PNyDgxF4SUImxXOcRMGtNwUmnbKJ2QX6P6zU8XqO/BJqCPEXf5IrCvIQ9OLPfj5FGY2B0/bnPLJD0IiO812zLpael9mVcNRk7Z/9MGUCpOxV9oOWXb0fYEMYzl916iaRT8N+5obzSUhFC8uGjihD6Up2ek4ECx6Eno+u3rDKn5EF/MqoD6TrQKDDLVaM0/vckXtAM6hR9D0FGILAqd0KX4M7TLy1LV/na1FPEBLPKPpZyYoe7JFS75CoKWwFziQhiQNkb74ZJ3LGxZC7tPDd7Zi+ETfo5bEXa6IIPUgYHzxvcZKgYjbjcn3hxCmxs7eF4t6Y30d2IjY4sFE8NCHaCiqmCf4Sr9dYJxrGv51KPwb6UMG7N358R8CdyODKIO565cVYEWrx9eV4Gkd5AR8ZCcK8bPRjQT6+NH92e7JKYSGKOHwV1fis+6q+U3VVCcG6gWAj0s+LFaw1IJa8Gf3NdW9Zjbn+XgaCcDwg8nYn/6C/LaSdo0+y9Xw4CLXmRM0+KAgIOMXU8AMR09R1gc9a2dRLtw03zlVKoUgWJU9O3AP8Tlt4Jav1lL180ysobcAhqC5INYgqE8hwSaLKPyiLl8FXZ3WO0OyynUMQwKDMqQY49orEyhKU8oY4h/FKwKjR+dulhk6tvmMtdaWQxazoM3mBVCsaAz921GawDn+R9Tz5K/hv+GXHE2WoCS/lngRNc6GWA2MWkYdw3q0zBHwz1wD5JJHXQTc5/yuN1ZlM8Q2ax4AmgR82xtejcVSOBRjQVY7p7Yi4M1vkFW1h36/gE5ZfBHIKwb+/98ZWxskk+INdDjTNfZunEeJssrbpca3yEdPoYEJrArATiptGjgxhZ3jQNmy6zEu7mPqJvFDGlPjeLbI2IoSGiCb7V6VRnL41yuQyz7uT9SdK0g5sQH4mmD4K4FO7eKsMlqDSlGnN2ydAOrPxTK9wgN7Jzc/5LHcnTTfMQZ0W3MoeANEnkG5B/qg3gDER+JyMqlpSk6nrUN6NgV85DbtVjSiKRXVwQ4hh/VRUcYgYoTlcVVQjj9w6WmOgplQGZ+WCYNo4FZEwW5iRWFy1ik/VP3+3PKZZmQG7iaHkMjDnYdu08lRAjTbAnO3fS79hv+kIsz6f8wxeL89ERgWCqb2f3pow84VlWRDqL//EOkObLcltMK0WmK2etKD6E7uWrMR3GPV8lIN1AvSeE9txitSpdgVAZeehMIhJIDR4aM+MSRq49A1zbKwfv+SdrxLtBwM+BQFcjIBGx/78iIC9tMkFvaDVFqhKnGy6O+umWMfwBeiC4vGezP7fH1FJeYMCvCHj31vEvEMcaBRHWhCcr8j791nH+5BVOj0rlKeL0VE5Fl2aAx+0LGBJ4KhFOcN//OaTQqZIk9V1diU9Np";
        args[0]="AAAANMAeSQwm+/reHOd0hoafUOX0Lrd/MjZEXBRIKkZ8t2JGJ3qrtOJ0fH5+yrTF+7CnFg==";
        //args[1]="/api/l/v7/savesports?jsonsports=整个子串";
        args[1]=str;


        //util.writeFile(global_context,"aa.txt","123123123");

        Intent intent = new Intent("cgencrypt_send");
        intent.putExtra("a1", args[0]);
        intent.putExtra("a2", args[1]);
        global_context.sendBroadcast(intent);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cgencrypt_result");
        registerReceiver(new EncryptResultReceiver(), intentFilter);
    }

    private class EncryptResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();
            String r = intent.getExtras().getString("r");
            String token = intent.getExtras().getString("token");
            String client = intent.getExtras().getString("client");
            String imei = intent.getExtras().getString("imei");
            String ua = intent.getExtras().getString("ua");
            String v1 = intent.getExtras().getString("v1");

            String[] split = r.split("\\|");
            String sign = split[1];
            String time = split[3];

            writeFile(context,"aa.txt",r+"\n"+token+"\n"+client+"\n"+imei+"\n"+ua+"\n"+v1);
            //HttpUtils.http("https://ggtypt.njtech.edu.cn/cgapp-server/api/l/v7/sportlist?xh=202321054021&type=1",sign,time,v1,imei,ua,client,token);



            //XposedBridge.log("--------------------------------------------------------------");
            //XposedBridge.log(r+"\n"+token+"\n"+client+"\n"+imei+"\n"+ua+"\n"+v1);
            //XposedBridge.log("--------------------------------------------------------------");
        }


    }
    public boolean writeFile(Context context, String fileName, String data) {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data.getBytes());
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}