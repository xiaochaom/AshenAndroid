package com.android.AshenAndroid.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

public class ProperTies {
    private static String configPath = "config.properties";

    public static Properties getProperties(Context context) {

        Properties urlProps;
        Properties props = new Properties();
        try {

            File file = context.getFilesDir();
            if (!file.exists()){
                file.mkdirs();
            }

            File configFile = new File(String.format("%s/%s",file.getPath() , configPath ));
            if (!configFile.exists()){
                configFile.createNewFile();
            }
            props.load(context.openFileInput(configPath));

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        urlProps = props;
        return urlProps;
    }

    //保存配置文件
    public static void setProperties(Context context, String keyName, String keyValue) {
        Properties props = new Properties();
        try {
            props.load(context.openFileInput(configPath));
            props.setProperty(keyName, keyValue);
            FileOutputStream out = context.openFileOutput(configPath, Context.MODE_PRIVATE);
            props.store(out, null);
            out.close();


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("setPropertiesError", e.toString());
            Log.e("修改配置文件失败", e.toString());
        }
        Log.e("设置成功", "");
    }

    public static void writeConfig(Context context){

    }

}