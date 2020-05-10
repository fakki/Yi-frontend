package com.shanmingc.yi.network;

import okhttp3.*;

import java.io.IOException;

public class RequestProxy implements Callback {

    private static RequestProxy proxy = new RequestProxy();

    private boolean isDone;

    private boolean isFailed;

    private boolean isExecute;

    private Response response;

    public static RequestProxy getInstance() {
        proxy.isExecute = false;
        return proxy;
    }

    synchronized public void request(Request req) {
        isExecute = true;
        isDone = false;
        isFailed = false;
        OkHttpClient client = new OkHttpClient();
        client.newCall(req).enqueue(this);
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
