package com.shanmingc.yi.network;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.shanmingc.yi.activity.BoardActivity;
import okhttp3.*;

import java.io.IOException;
import java.util.*;

public class RequestProxy implements Callback {

    private static RequestProxy proxy = new RequestProxy();

    private boolean isDone;

    private boolean isFailed;

    private boolean isExecute;

    private String response;

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

        /*OkHttpClient client = new OkHttpClient();
        Response response = null;

        try {
            response = client.newCall(request).execute();
        } catch (Exception e) {
            Log.d(TAG, "response failed");
        }

        Map<String, Object> map = new HashMap<>();



        if(response == null) {
            Log.d(TAG, "response == null");
            return null;
        }
        try {
            Gson gson = new Gson();
            map = gson.fromJson(response.body().string(), new TypeToken<Map<String, Object>>() {
            }.getType());
        } catch (Exception e) {
            Log.d(TAG, "parse error");
        }

        return map;*/

        Map<String, Object> map = new HashMap<>();

        proxy.request(request);
        try {
            while (!proxy.isDone()) {
                Thread.sleep(100);
            }

            String response = proxy.response();

            Gson gson = new Gson();
            map = gson.fromJson(response, new TypeToken<Map<String, Object>>(){}.getType());
        } catch (Exception e) {
            BoardActivity.logResponse(TAG, map, "illegal state exception");
            e.printStackTrace();
        }
        return map;
    }

    public static List<Map<String, Object>> waitForResponseList(Request request) {
        List<Map<String, Object>> res = new ArrayList<>();
        Map<String, Object> map = null;

        proxy.request(request);
        try {
            while (!proxy.isDone()) {
                Thread.sleep(100);
            }

            String response = proxy.response();

            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonElements = jsonParser.parse(response).getAsJsonArray();
            for(JsonElement element : jsonElements) {
                map = gson.fromJson(element, new TypeToken<Map<String, Object>>(){}.getType());
                res.add(map);
            }

        } catch (Exception e) {
            BoardActivity.logResponse(TAG, map, "illegal state exception");
            e.printStackTrace();
        }

        return res;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        call.cancel();
        isFailed = true;
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        this.response = response.body().string();
        isDone = true;
    }

    public String response() throws RuntimeException {
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
