package com.android.AshenAndroid.utils;

import com.android.AshenAndroid.server.HTTPRequest;
import com.android.AshenAndroid.server.HTTPResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.android.AshenAndroid.utils.CallBackImpl.test_done;

public class Util {
    public static final int request_timeout = 10;
    private static final String time_out_key = "test_time_out";

    public static void notFund(HTTPResponse response, String message) {
        response.setStatus(AshenConst.NOT_FOUND).end();
        Map map = new HashMap();
        map.put("code", AshenConst.NOT_FOUND);
        if (message != null) {
            map.put("message", message);
        } else {
            map.put("message", "未找到匹配的接口");
        }
        response.setContent(map);
        response.end();
    }
    public static void success(HTTPResponse response, org.json.JSONObject object) {
        response.setContent(object);
        response.end();
    }

    public static void success(HTTPResponse response, Map object) {
        response.setContent(object);
        response.end();
    }

    public static org.json.JSONObject  waiting_callback(HTTPRequest request, Map<String, Object> map) {
        return waiting_callback(getTimeOut(request), map);
    }

    public static org.json.JSONObject waiting_callback(int timeout, Map<String, Object> map) {

        Date start_time = new Date(System.currentTimeMillis());
        Date end_time;
        long time_diff;
        while (true) {
            end_time = new Date(System.currentTimeMillis());

            time_diff = (end_time.getTime() - start_time.getTime()) / 1000;
            if (time_diff >= timeout) {
                map.put("code", AshenConst.TIME_OUT);
                org.json.JSONObject jsonObj = new org.json.JSONObject(map);
                return jsonObj;
            }
            if (test_done) {
                if (!map.containsKey("code")) {
                    map.put("code", AshenConst.OK);
                }
                if (!map.containsKey("type")) {
                    if ((int) map.get("code") == AshenConst.OK) {
                        map.put("type", "success");
                    } else {
                        map.put("type", "error");
                    }
                }

                map.put("time_diff(ms)", String.valueOf((end_time.getTime() - start_time.getTime())));
                org.json.JSONObject jsonObj = new org.json.JSONObject(map);
                return jsonObj;
            }
        }

    }

    public static int getTimeOut(HTTPRequest request) {
        final Map data = request.data();
        if (data.containsKey(time_out_key) && data.get(time_out_key) instanceof Integer && (Integer) data.get(time_out_key) > 0) {
            return (Integer) data.get(time_out_key);
        } else {
            return request_timeout;
        }
    }

    public static Map objectToMap(Object o) {
        Gson gson = BaseGson.getGson();
        String messageStr = gson.toJson(o);
        Map<String, Object> map;
        try {
            map = gson.fromJson(messageStr, new TypeToken<HashMap<String, Object>>() {
            }.getType());
        } catch (Exception e) {
            map = new LinkedHashMap<>();
            map.put(AshenConst.message, o);
        }

        return map;
    }
     public static LinkedList<Map> ObjectListToMapList(List o){
         LinkedList<Map> linkedList = new LinkedList<>();
         if (o != null){
             for (int index = 0;index < o.size();index ++){
                 linkedList.add(Util.objectToMap(o.get(index)));
             }
         }
         return linkedList;
     }

}
