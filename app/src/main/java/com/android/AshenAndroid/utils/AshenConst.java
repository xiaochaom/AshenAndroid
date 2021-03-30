package com.android.AshenAndroid.utils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class AshenConst {
    // http server 的端口
    public static int PORT_DEFAULT = 9999;

    public final static int OK = 200;
    public final static int EXCEPTION = 202;
    public final static int ERROR = 203;
    public final static int NOT_FOUND = 404;
    public final static int TIME_OUT = 300;

    public final static String errorCode = "errorCode";
    public final static String errorMessage = "errorMessage";
    public final static String message = "message";
    public final static String status = "status";
    public final static String code = "code";
    public static String fileSavePath = "";
    public static String className = "";

    // 内置的文件列表,用于文件上传等基础数据
    public final static String[] fileNameList = {"1080*1080.jpg"};

    public static LinkedHashMap<String, Object> callbackMap = new LinkedHashMap<>();

    public static LinkedHashMap<String, LinkedList<Map<String, LinkedHashMap<String, String>>>> classObjectCallBack = new LinkedHashMap<>();
    public static LinkedHashMap<String, LinkedList<Map<String, LinkedHashMap<String, String>>>> classObject = new LinkedHashMap<>();
    public static LinkedHashMap<String, LinkedList<Map<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>>> classTypeObject = new LinkedHashMap<>();

    public static LinkedHashMap<String, Object> classRegistered = new LinkedHashMap<>();

    public static LinkedList<String> built_in_interface = new LinkedList<>();
}
