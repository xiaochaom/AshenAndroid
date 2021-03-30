package com.android.AshenAndroid.utils.JsonDeserializer;

import android.net.Uri;

import com.android.AshenAndroid.utils.BaseGson;
import com.android.AshenAndroid.utils.Util;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class UriSerializer implements JsonSerializer<Uri> {

    @Override
    public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
        Gson gson = BaseGson.getGson();
        return gson.toJsonTree(Util.objectToMap(src));
    }
}
