package com.shanmingc.yi.network;

import okhttp3.OkHttpClient;

import java.util.concurrent.Callable;

public class Request implements Callable<String> {

    private okhttp3.Request request;

    public Request(okhttp3.Request request) {
        this.request = request;
    }

    @Override
    public String call() throws Exception {
        OkHttpClient client = new OkHttpClient();
        return client.newCall(request).execute().body().string();
    }
}
