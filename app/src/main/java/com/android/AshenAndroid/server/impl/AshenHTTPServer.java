package com.android.AshenAndroid.server.impl;

import android.util.Log;

import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import org.json.JSONObject;

import static com.android.AshenAndroid.utils.AshenConst.PORT_DEFAULT;

public class AshenHTTPServer implements HttpServerRequestCallback {

    private static final String TAG = "AshenHttpServer";

    private static AshenHTTPServer mInstance;

    AsyncHttpServer mServer = new AsyncHttpServer();


    public static AshenHTTPServer getInstance() {
        if (mInstance == null) {
            synchronized (AshenHTTPServer.class) {
                if (mInstance == null) {
                    mInstance = new AshenHTTPServer();
                }
            }
        }
        return mInstance;
    }

    public void start() {
        Log.d(TAG, "Starting http server...");
        mServer.get("[\\d\\D]*", this);
        mServer.post("[\\d\\D]*", this);
        mServer.listen(PORT_DEFAULT);
    }

    public void stop() {
        Log.d(TAG, "Stopping http server...");
        mServer.stop();
    }

    private void sendResponse(AsyncHttpServerResponse response, JSONObject json) {
        // Enable CORS
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.send(json);
    }


    @Override
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        AshenHTTPRequest rcHttpRequest = new AshenHTTPRequest(request);
        AshenHTTPResponse rcHttpResponse = new AshenHTTPResponse(response);
        AshenHTTPServlet rcHttpServlet = new AshenHTTPServlet();
        rcHttpServlet.handleHttpRequest(rcHttpRequest, rcHttpResponse);
        sendResponse(rcHttpResponse.getResponse(), rcHttpResponse.getContent());
    }

}