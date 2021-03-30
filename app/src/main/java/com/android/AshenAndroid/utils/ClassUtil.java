package com.android.AshenAndroid.utils;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * 工具类
 */
public class ClassUtil {

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static LinkedHashMap<String, LinkedList<Map<String, LinkedHashMap<String, String>>>> getClassObjectNotCallBack(Object obj) {
        Class c = obj.getClass();
        // 待返回的 类对象
        LinkedHashMap<String, LinkedList<Map<String, LinkedHashMap<String, String>>>> classObj = new LinkedHashMap();
        LinkedList<Map<String, LinkedHashMap<String, String>>> methodList = new LinkedList();
        for (Method m : c.getMethods()) {
            LinkedHashMap<String, LinkedHashMap<String, String>> methodObj = new LinkedHashMap();
            LinkedHashMap<String, String> paramObj = new LinkedHashMap<>();
            for (Parameter p : m.getParameters()) {
                Type type = p.getParameterizedType();
                paramObj.put(p.getName(), type.getTypeName());
            }
            methodObj.put(m.getName(), paramObj);
            methodList.add(methodObj);
        }
        classObj.put(c.getName(), methodList);
        return classObj;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static LinkedHashMap<String, LinkedList<Map<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>>> getClassTypeObject(Object obj) {
        Class c = obj.getClass();
        // 待返回的 类对象
        LinkedHashMap<String, LinkedList<Map<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>>> classObj = new LinkedHashMap();
        LinkedList<Map<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>> methodList = new LinkedList();
        for (Method m : c.getMethods()) {
            LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> methodObj = new LinkedHashMap();
            LinkedHashMap<String, LinkedHashMap<String, Object>> paramObj = new LinkedHashMap<>();
            Class returnType = m.getReturnType();  // 获取函数返回
            // returnType 的 map
            LinkedHashMap<String, Object> returnTypeMap = new LinkedHashMap<>();
            returnTypeMap.put("typeName", returnType.getName());
            paramObj.put("returnType", returnTypeMap);
            int paramsCount = 0;
            for (Parameter p : m.getParameters()) {
                if (!p.getName().toLowerCase().contains("callback")) {
                    paramsCount += 1;
                }

                // 参数的 map
                LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
                paramMap.put("typeName", p.getType().getName());

                Type type = p.getParameterizedType();
                paramMap.put("type", type);
                paramObj.put(p.getName(), paramMap);

            }
            // paramsCount map
            LinkedHashMap<String, Object> paramsCountMap = new LinkedHashMap<>();
            returnTypeMap.put("typeName", String.valueOf(paramsCount));
            paramObj.put("paramsCount", paramsCountMap);
            methodObj.put(m.getName(), paramObj);
            methodList.add(methodObj);
        }
        classObj.put(c.getName(), methodList);
        return classObj;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static LinkedHashMap<String, LinkedList<Map<String, LinkedHashMap<String, String>>>> getClassObject(Object obj) {
        Class c = obj.getClass();
        // 待返回的 类对象
        LinkedHashMap<String, LinkedList<Map<String, LinkedHashMap<String, String>>>> classObj = new LinkedHashMap();
        LinkedList<Map<String, LinkedHashMap<String, String>>> methodList = new LinkedList();
        for (Method m : c.getMethods()) {
            LinkedHashMap<String, LinkedHashMap<String, String>> methodObj = new LinkedHashMap();
            LinkedHashMap<String, String> paramObj = new LinkedHashMap<>();
            Class returnType = m.getReturnType();  // 获取函数返回
            // returnType 的 map
            paramObj.put("returnType", returnType.getName());

            int paramsCount = 0;
            for (Parameter p : m.getParameters()) {
                if (!p.getName().toLowerCase().contains("callback")) {
                    paramsCount += 1;
                } else {
                    Type type = p.getParameterizedType();
                    paramObj.put(p.getName(), type.getTypeName());
                    continue;
                }

                // 参数的 map
                paramObj.put(p.getName(), p.getType().getName());
            }
            // paramsCount map
            paramObj.put("paramsCount", String.valueOf(paramsCount));
            methodObj.put(m.getName(), paramObj);

            methodList.add(methodObj);
        }
        classObj.put(c.getName(), methodList);
        return classObj;
    }

}