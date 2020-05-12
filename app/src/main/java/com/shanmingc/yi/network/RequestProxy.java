package com.shanmingc.yi.network;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestProxy implements Callback {

    private static RequestProxy proxy = new RequestProxy();

    private boolean isDone;

    private boolean isFailed;

    private boolean isExecute;

    private Response response;

    public static final String TAG = "RequestProxy";

    public static RequestProxy getInstance() {
        proxy.isExecute = false;
        return proxy;
    }

    synchronized private void request(Request req) {
        isExecute = true;
        isDone = false;
        isFailed = false;
        OkHttpClient client = new OkHttpClient();
        client.newCall(req).enqueue(this);
    }

    public static Map<String, Object> waitForResponse(Request request) {
        RequestProxy proxy = RequestProxy.getInstance();

        Map<String, Object> map = new HashMap<>();

        proxy.request(request);
        try {
            while (!proxy.isDone()) {
                Thread.sleep(100);
            }
            Response response = proxy.response();

            Gson gson = new Gson();
            map = gson.fromJson(response.body().string(), new TypeToken<Map<String, Object>>(){}.getType());
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return map;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        call.cancel();
        isFailed = true;
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        this.response = response;
        isDone = true;
    }

    public Response response() throws RuntimeException {
        if(!isExecute) {
            throw new RuntimeException("Request proxy not send a request.");
        }
        return response;
    }

    public boolean isDone() throws RuntimeException {
        if(!isExecute) {
            throw new RuntimeException("Request proxy not send a request.");
        }
        return isDone;
    }

    public boolean isFailed() throws RuntimeException {
        if(!isExecute) {
            throw new RuntimeException("Request proxy not send a request.");
        }
        return isFailed;
    }
}
