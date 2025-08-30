package com.exefuer.timelauncher2;


import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import com.blankj.utilcode.util.AppUtils;

public class TimeLauncherApp extends Application {
    private static Context context;
    
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SharedPreferencesUtil.getInstance(context,"config");
    }
 
    public static Context getContext() {
        return context;
    }
    
}
