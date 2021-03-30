package com.android.AshenAndroid.utils;


public class RequestThread extends Thread {

    public void run() {
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String result = SendHttpRequest.sendGet("https://www.baidu.com", "");
        }
    }
}
