package com.android.AshenAndroid.server.impl;

import com.android.AshenAndroid.utils.BaseGson;
import com.google.gson.Gson;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Map;

import com.android.AshenAndroid.server.HTTPResponse;

public class AshenHTTPResponse implements HTTPResponse {
    private final AsyncHttpServerResponse response;
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_ENCODING = "Content-Encoding";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "location";
    private boolean closed;
    private Charset charset = Charset.forName("UTF-8");
    private static JSONObject jsonObject;


    public AshenHTTPResponse(AsyncHttpServerResponse response) {
        this.response = response;
        response.getHeaders().set(CONTENT_ENCODING, "identity");
    }

    public AsyncHttpServerResponse getResponse() {
        return this.response;
    }

    public HTTPResponse setStatus(int status) {
        response.code(status);
        return this;
    }


    public HTTPResponse setContentType(String mimeType) {
        response.getHeaders().set(CONTENT_TYPE, mimeType);
        return this;
    }

    public HTTPResponse setContent(byte[] data) {
        response.getHeaders().set(CONTENT_LENGTH, String.valueOf(data.length));
        String str = new String(data);
        response.send(str);
        return this;
    }

    public JSONObject getContent() {
        return jsonObject;
    }

    @Override
    public HTTPResponse setContent(JSONObject message) {
        jsonObject = message;
        return this;
    }


    @Override
    public HTTPResponse setContent(Map message) {
        Gson gson = BaseGson.getGson();
        String string = gson.toJson(message);
        JSONObject jo = null;
        try {
            jo = new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonObject = jo;

        return this;
    }

    public HTTPResponse setContent(String message) {
        setContent(message.getBytes(charset));
        return this;
    }

    public HTTPResponse sendRedirect(String to) {
        response.getHeaders().set(LOCATION, to);
        return this;
    }

    public HTTPResponse sendTemporaryRedirect(String to) {
        response.getHeaders().set(LOCATION, to);
        return this;
    }

    @Override
    public void end() {
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public HTTPResponse setEncoding(Charset charset) {
        this.charset = charset;
        return this;
    }
}
