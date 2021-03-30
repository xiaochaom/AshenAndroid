package com.android.AshenAndroid.server;

public interface HTTPServlet {
    void handleHttpRequest(HTTPRequest httpRequest, HTTPResponse httpResponse) throws Exception;
}
