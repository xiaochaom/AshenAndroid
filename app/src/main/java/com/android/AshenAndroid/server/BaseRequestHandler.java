package com.android.AshenAndroid.server;

import androidx.annotation.Nullable;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import com.android.AshenAndroid.server.impl.AshenResponse;


public abstract class BaseRequestHandler {
    public static Map<String, String> message_map = new HashMap<String, String>();
    private final String mappedUri;
    public BaseRequestHandler(String mappedUri) {
        this.mappedUri = mappedUri;
    }

    public String getMappedUri() {
        return mappedUri;
    }

    @Nullable
    public abstract AshenResponse handle(HTTPRequest request);

    protected AshenResponse safeHandle(HTTPRequest request) throws JSONException {
        return handle(request);
    }


}
