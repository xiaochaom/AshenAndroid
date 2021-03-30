package com.android.AshenAndroid.server;

import org.json.JSONObject;


import java.nio.charset.Charset;
import java.util.Map;

public interface HTTPResponse {

    HTTPResponse setStatus(int status);

    HTTPResponse setContentType(String mimeType);

    HTTPResponse setContent(byte[] data);

    HTTPResponse setContent(JSONObject message);


    HTTPResponse setContent(Map<String,Object> message);

    HTTPResponse setEncoding(Charset charset);

    HTTPResponse sendRedirect(String to);

    HTTPResponse sendTemporaryRedirect(String to);

    void end();

    boolean isClosed();
}
