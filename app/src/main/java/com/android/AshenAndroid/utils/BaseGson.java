package com.android.AshenAndroid.utils;

import android.net.Uri;

import com.android.AshenAndroid.utils.JsonDeserializer.UriDeserializer;
import com.android.AshenAndroid.utils.JsonDeserializer.UriSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class BaseGson {

    public static Gson getGson(){
        return new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriDeserializer())
                .registerTypeAdapter(Uri.class, new UriSerializer())
                .setLenient()// json宽松
                .enableComplexMapKeySerialization()//支持Map的key为复杂对象的形式
                .serializeNulls() //智能null
                .setPrettyPrinting()// 调教格式
                .create();
    }

}
