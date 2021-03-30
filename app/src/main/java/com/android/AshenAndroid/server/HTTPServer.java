package com.android.AshenAndroid.server;

public interface HTTPServer {
    void handleHttpRequest(HTTPRequest IHttpRequest, HTTPResponse httpResponse) throws Exception;
}
