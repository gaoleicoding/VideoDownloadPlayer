package com.android.singledownload;

import android.app.Application;
import android.content.Context;

public class ContextUtil {
    private static Context mContext;

    public static Context getAppContext() {
        if (mContext == null) {
            synchronized (ContextUtil.class) {
                if (mContext == null) {
                    try {
                        Application application = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null);
                        mContext = application.getApplicationContext();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mContext;
    }
}
