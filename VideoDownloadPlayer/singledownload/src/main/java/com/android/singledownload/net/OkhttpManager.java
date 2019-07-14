package com.android.singledownload.net;

import okhttp3.OkHttpClient;

public class OkhttpManager {

    public OkHttpClient mClient;//OKHttpClient;

    private OkhttpManager(){
        mClient=new OkHttpClient.Builder().build();
    }
    public static OkhttpManager getInstance() {

        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static OkhttpManager instance = new OkhttpManager();

    }

}
