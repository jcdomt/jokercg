package com.wzjer.jokercg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtils {

    private static final String TAG = "HttpUtils";




    static public String http(String url,String sign, String timestamp, String v1, String imei, String ua, String cli, String cgAuthorization) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("cgAuthorization",cgAuthorization)
                .addHeader("client",cli)
                .addHeader("sign",sign)
                .addHeader("timestamp",timestamp)
                .addHeader("v1",v1)
                .addHeader("imei",imei)
                .addHeader("User-Agent",ua)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                return responseBody;
                // 处理响应数据
            } else {
                // 处理错误情况
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return url;
    }


    public static String getData(String urlString) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new HttpTask(urlString));
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        } finally {
            executor.shutdown();
        }
    }

    private static class HttpTask implements Callable<String> {

        private final String urlString;

        public HttpTask(String urlString) {
            this.urlString = urlString;
        }

        @Override
        public String call() throws Exception {
            StringBuilder result = new StringBuilder();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result.toString();
        }
    }
}
