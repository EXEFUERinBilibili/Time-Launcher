package com.exefuer.timelauncher2;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private final String name;
    private final String packageName;
    private final Drawable icon;

    public AppInfo(String name, String packageName, Drawable icon) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }
}