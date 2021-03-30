package com.android.AshenAndroid.server.impl;

import com.android.AshenAndroid.utils.BaseGson;
import com.google.gson.Gson;
import com.koushikdutta.async.http.Multimap;
import com.koushikdutta.async.http.body.AsyncHttpRequestBody;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.android.AshenAndroid.server.HTTPRequest;

public class AshenHTTPRequest implements HTTPRequest {

    private AsyncHttpServerRequest request;
    private Map<String, Object> data;
    private Map<String, Object> dataMap;

    public AshenHTTPRequest(AsyncHttpServerRequest request) {
        this.request = request;
        this.data = new HashMap<String, Object>();
        this.dataMap = this.data();
    }

    public static LinkedHashMap<String, Object> jsonToMap(String str_json) {
        LinkedHashMap<String, Object> res = new LinkedHashMap<>();
        try {
            Gson gson = BaseGson.getGson();
            res = gson.fromJson(str_json,LinkedHashMap.class);
        } catch (Exception e) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("RequestError", "Json 参数转化异常");
            return map;
        }
        return res;
    }

    @Override
    public String method() {
        return request.getMethod();
    }

    @Override
    public String uri() {
        return request.getPath();
    }


    @Override
    public String body() {
        return request.getBody().getContentType();
    }

    @Override
    public String header(String headerName) {
        return request.getHeaders().get(headerName);
    }


    @Override
    public LinkedHashMap<String, Object> data() {
        LinkedHashMap<String, Object> tmpMap = new LinkedHashMap<>();
        if (request.getMethod().equals("GET")) {
            for (String index : request.getQuery().keySet()) {
                tmpMap.put(index, request.getQuery().get(index).get(0));
                System.out.println(request.getQuery().get(index).get(0));
            }
            return tmpMap;
        } else if (request.getMethod().equals("POST")) {
            String contentType = request.getHeaders().get("Content-Type");
            if (contentType.equals("application/json")) {
                JSONObject params = (JSONObject) (request.getBody()).get();
                return jsonToMap(params.toString());
            } else {
                String params = ((AsyncHttpRequestBody<Multimap>) request.getBody()).get().toString();
                return jsonToMap(params);
            }
        } else {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("RequestError", "类型非 GET 或 POST 参数为非 Json");
            return map;
        }
    }

}
