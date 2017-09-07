package com.coolweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int anHour = 8 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent intent1 = new Intent(this , AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this , 0 , intent1 , 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP , triggerAtTime , pi);
        return super.onStartCommand(intent , flags , startId);
    }

    private void updateWeather() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherSring =preferences.getString("weather" , null);
        if (weatherSring != null) {
            Weather weather = Utility.handleWeatherResponse(weatherSring);
            String weatherId = weather.basic.weatherId;

            String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + weatherId + "&key= 9280912a44b04d5f9bf4fa18d987328b";
            HttpUtil.sendOkHttpRequest(weatherId, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();

                        editor.putString("weather" , responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    private void updateBingPic() {
        final String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();

                editor.putString("bing_pic" , bingPic);
                editor.apply();
            }
        });
    }
}
