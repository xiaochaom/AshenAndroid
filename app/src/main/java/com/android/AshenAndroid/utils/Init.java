package com.android.AshenAndroid.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.AshenAndroid.server.BaseRequestHandler;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import static com.android.AshenAndroid.utils.AshenConst.built_in_interface;
import static com.android.AshenAndroid.utils.AshenConst.callbackMap;
import static com.android.AshenAndroid.utils.AshenConst.classObjectCallBack;
import static com.android.AshenAndroid.utils.AshenConst.classObject;
import static com.android.AshenAndroid.utils.AshenConst.classTypeObject;
import static com.android.AshenAndroid.utils.AshenConst.classRegistered;
import static com.android.AshenAndroid.utils.AshenConst.fileNameList;
import static com.android.AshenAndroid.utils.AshenConst.fileSavePath;

public class Init {

    @RequiresApi(api = Build.VERSION_CODES.P)
    public Init(Context context) {
        Intent intent = new Intent(context, AshenService.class);
        context.startService(intent);
        initFile(context);
        new RequestThread().start();
        initCallback();
        initClassRegister();
        initActivityBtn();
    }


    public static void initFile(Context context){
        File file1 = new File(fileSavePath);
        File[] files = file1.listFiles();
        List list = new LinkedList();
        if (files != null) {
            for (int file_index = 0; file_index < files.length; file_index++) {
                list.add(files[file_index].getName());
            }
        }

        // 备用发送文件的文件存到设备上®
        for (int file_index = 0; file_index < fileNameList.length; file_index++) {
            try {
                if (!list.contains(fileNameList[file_index])) {
                    InputStream is = context.getAssets().open(fileNameList[file_index]);

                    File file = new File(fileSavePath, fileNameList[file_index]);

                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int byteCount;
                    while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                        fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                    }
                    fos.flush();//刷新缓冲区
                    is.close();
                    fos.close();
                }
            } catch (IOException e) {
                e.toString();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void initClassRegister() {
        // 只需给 classRegistered put 就可以,key 为包路径,value 为调用 api 的 SDK 实例,单例类为 instance,例如 Gson
//        classRegistered.put("com.xxx.xx.xx", Class.getInstance());
        classRegistered.put("com.google.gson.Gson", new Gson());

        for (Map.Entry classEntry : classRegistered.entrySet()) {
            Object o = classEntry.getValue();
            classObjectCallBack.putAll(ClassUtil.getClassObjectNotCallBack(o));
            classObject.putAll(ClassUtil.getClassObject(o));
            classTypeObject.putAll(ClassUtil.getClassTypeObject(o));
        }
    }

    public void initCallback() {
        // 包路径$callback实例
//        callbackMap.put("com.xxx.xx.Class$Callback", CallBackImpl.callback);
        }

    public void initActivityBtn(){
        built_in_interface.add("getAllInterface");
        built_in_interface.add("getInterfaceParams?name=sendMessage");
    }
}
