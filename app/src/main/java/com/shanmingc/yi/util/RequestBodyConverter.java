package com.shanmingc.yi.util;

import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.util.Map;

public class RequestBodyConverter {

    public static RequestBody mapToRequestBody(Map<String, String> params) {
        Gson gson = new Gson();
        String data = gson.toJson(params);
        MediaType JSON = MediaType.parse("application/json");
        return RequestBody.create(JSON, data);
    }
}
