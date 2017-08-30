package com.coolweather.android.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/8/21.
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String address , okhttp3.Callback callback) {

        //String TAG = "okHttp";

        OkHttpClient client = new OkHttpClient();
        //Log.d(TAG , "创建client成功——————");
        Request request = new Request.Builder().url(address).build();
        //Log.d(TAG , "发送请求成功————————");
        client.newCall(request).enqueue(callback);
        //Log.d(TAG , "——————————");
    }
}
