/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.AshenAndroid.server.impl;

import android.util.Log;

import androidx.annotation.Nullable;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.android.AshenAndroid.server.HTTPResponse;
import com.android.AshenAndroid.utils.CallBackImpl;
import com.android.AshenAndroid.utils.AshenConst;

public class AshenResponse {

    private final Object value;
    private int httpStatus;

    public AshenResponse(@Nullable Object value) {
        if(value == null){
            value = new HashMap<String, String>(){
                {
                    put("error","返回值未配置");
                }
            };
            this.value = value;
        }else{
            if (value instanceof Throwable) {
                // 抛出异常的处理返回值
                final Object finalValue = value;
                value = new HashMap<String, String>(){
                    {
                        put("error","返回值未配置"+((Throwable) finalValue).getMessage());
                    }
                };
                this.value = value;

            }else {
                // 正常的返回值
                this.httpStatus = AshenConst.OK;
                this.value = value;

            }
        }

        CallBackImpl.test_done = false;
    }
    private static JSONObject formatException(Throwable error) throws JSONException {
        JSONObject result = new JSONObject();
        result.put("error", error.getMessage());
        result.put("message", error.getMessage());
        result.put("stacktrace", Log.getStackTraceString(error));
        return result;
    }

    public static Object formatNull(Object value) {
        return value == null ? JSONObject.NULL : value;
    }

    public void renderTo(HTTPResponse response) {
        response.setContentType("application/json");
//        response.setEncoding(StandardCharsets.UTF_8);
        response.setStatus(getHttpStatus());
        if (value instanceof JSONObject) {
            response.setContent((JSONObject) value);

        } else if (value instanceof Map) {
            response.setContent((Map<String, Object>) value);
        } else {
            try {
                JSONObject o = new JSONObject();
                o.put("string_value", (value instanceof Throwable)
                        ? formatException((Throwable) value)
                        : formatNull(value));
//            final String responseString = o.toString();
                response.setContent(o);
            } catch (Exception e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("error", "error 返回值错误");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                response.setContent(jsonObject);
                response.setStatus(AshenConst.EXCEPTION);
            }
        }

    }

    public int getHttpStatus() {
        return httpStatus;
    }

    @Nullable
    public Object getValue() {
        return value;
    }
}

