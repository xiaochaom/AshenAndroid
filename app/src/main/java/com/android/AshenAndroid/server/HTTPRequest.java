package com.android.AshenAndroid.server;

//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;

import org.json.JSONException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface HTTPRequest {
    /**
     * Returns "GET", "POST", "PUT" or "DELETE".
     */
    String method();

    /**
     * Returns the request URI.
     */
    String uri();

    /**
     * Returns the full request body.
     */
    String body();

    /**
     * Gets the value of a given header.
     */
    String header(String name);

    /**
     * Returns additional data appended to the request.
     */
    LinkedHashMap<String, Object> data();

}
