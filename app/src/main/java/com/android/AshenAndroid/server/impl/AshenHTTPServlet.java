package com.android.AshenAndroid.server.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import com.android.AshenAndroid.server.HTTPRequest;
import com.android.AshenAndroid.server.HTTPResponse;
import com.android.AshenAndroid.server.HTTPServlet;
import com.android.AshenAndroid.utils.BaseGson;
import com.android.AshenAndroid.utils.AshenConst;
import com.android.AshenAndroid.utils.Util;
import com.android.AshenAndroid.utils.CallBackImpl;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import static com.android.AshenAndroid.utils.CallBackImpl.test_done;
import static com.android.AshenAndroid.utils.AshenConst.callbackMap;
import static com.android.AshenAndroid.utils.AshenConst.classObject;
import static com.android.AshenAndroid.utils.AshenConst.classObjectCallBack;
import static com.android.AshenAndroid.utils.AshenConst.classTypeObject;

public class AshenHTTPServlet implements HTTPServlet {

    @Override
    public void handleHttpRequest(HTTPRequest request, HTTPResponse response) {
        CallBackImpl.test_done = false;
        CallBackImpl.responseMap = new HashMap<>();
        String urlToMatchSections = getRequestUrlStr(request.uri());
        String testClassName = "";
        if (urlToMatchSections.equals("getAllInterface")) {
            // 展示给用户的接口
            Util.success(response, classObjectCallBack);
            return;
        } else if (urlToMatchSections.equals("getInterfaceParams")) {
            // 获取单个接口的参数  /getInterfaceParams?name=methodName
            LinkedHashMap param = request.data();
            LinkedList resList = new LinkedList();

            for (Map.Entry classIndex : classObjectCallBack.entrySet()) {
                LinkedList methodList = (LinkedList) classIndex.getValue();
                for (int methodIndex = 0; methodIndex < methodList.size(); methodIndex++) {
                    LinkedHashMap methodMap = (LinkedHashMap) methodList.get(methodIndex);
                    if (methodMap.containsKey(param.get("name"))) {
                        LinkedHashMap<String, LinkedHashMap> linkedHashMapLinkedHashMap = new LinkedHashMap<>();
                        linkedHashMapLinkedHashMap.put((String) classIndex.getKey(), methodMap);
                        resList.add(linkedHashMapLinkedHashMap);
                    }
                }
            }
            LinkedHashMap map = new LinkedHashMap();
            map.put(AshenConst.message, resList);
            Util.success(response, map);
            return;

        } else {
            // 遍历接口
            for (Map.Entry classEntry : classObject.entrySet()) {
                LinkedHashMap<String, Object> acParamMap = request.data();
                AshenConst.className = (String) classEntry.getKey();
                if (acParamMap.containsKey("testClassName")) {
                    testClassName = (String) acParamMap.get("testClassName");
                    acParamMap.remove("testClassName");
                }
                if (!testClassName.equals("")) {
                    if (!AshenConst.className.contains(testClassName)) {
                        continue;
                    }
                }

                LinkedList methodList = (LinkedList) classEntry.getValue();
                // 循环函数的数组
                for (int methodIndex = 0; methodIndex < methodList.size(); methodIndex++) {

                    LinkedHashMap<String, LinkedHashMap> methodMap = (LinkedHashMap<String, LinkedHashMap>) methodList.get(methodIndex);

                    // 判断接口名称是否一致
                    if (!methodMap.containsKey(urlToMatchSections)) {
                        continue;
                    }
                    try {
                        // 获取预期参数数量
                        LinkedHashMap<String, String> exParamMap = (LinkedHashMap) methodMap.get(urlToMatchSections);
                        int exParamsCount = Integer.decode(exParamMap.get("paramsCount"));

                        // 获取实际参数列表
                        int acParamsCount = acParamMap.size();
                        if (acParamMap.containsKey("callback")) {
                            acParamsCount -= 1;
                        }

                        // 判断参数列表是否一致
                        if (exParamsCount == acParamsCount) {
                            LinkedHashMap<String, String> currentMethodMap = methodMap.get(urlToMatchSections);
                            // 定义一个除了returnType的map
                            LinkedHashMap<String, String> methodParamMap = (LinkedHashMap<String, String>) currentMethodMap.clone();
                            methodParamMap.remove("returnType");
                            methodParamMap.remove("paramsCount");

                            // 获取预期的接口参数列表
                            LinkedList<String> exParamList = new LinkedList<String>();
                            for (Map.Entry entry : methodParamMap.entrySet()) {
                                String keyName = (String) entry.getKey();
                                exParamList.add(keyName);
                            }

                            // 获取实际的接口参数列表
                            LinkedList<String> acParamList = new LinkedList<String>();
                            for (Map.Entry entry : acParamMap.entrySet()) {
                                acParamList.add((String) entry.getKey());
                            }

                            // 开始比较参数,参数名称顺序一致才开始转换
                            boolean paramEqual = true;

                            for (int paramIndex = 0; paramIndex < acParamList.size(); paramIndex++) {
                                String exCurrentParamName = exParamList.get(paramIndex);
                                String acCurrentParamName = acParamList.get(paramIndex);

                                // 如果 key 的名称不一致,则直接判断为接口请求的不正确,跳过本次
                                if (!exCurrentParamName.equals(acCurrentParamName)) {
                                    paramEqual = false;
                                    break;
                                }
                            }

                            // 参数名称不一致,继续判断下一个接口
                            if (!paramEqual) {
                                continue;
                            }

                            // 判断请求参数中的 callback
                            boolean acCallBack = true;
                            if (acParamMap.containsKey("callback")) {
                                for (int paramIndex = 0; paramIndex < exParamList.size(); paramIndex++) {
                                    String exCurrentParamName = exParamList.get(paramIndex);
                                    String exCurrentParamType = (String) exParamMap.get(exCurrentParamName);
                                    String acCallBackStr = acParamMap.get("callback").toString().toLowerCase();
                                    if (exCurrentParamName.toLowerCase().contains("callback")) {
                                        if (!exCurrentParamType.toLowerCase().contains(acCallBackStr)) {
                                            acCallBack = false;
                                        }
                                    }
                                }
                            }

                            // 如果 callback 类型不匹配,则继续循环
                            if (!acCallBack) {
                                continue;
                            }

                            // 定义一个数组用来存放参数列表
                            Object[] requestParams = new Object[exParamList.size()];

                            // 影子 map, 用来指明参数类型
                            LinkedList<Map<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>> exParamTypeList = classTypeObject.get(AshenConst.className);
                            Map<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> exParamTypeMap = exParamTypeList.get(methodIndex);

                            // 重新写一次循环遍历参数,这样写可能会清晰一点,将参数循环塞入一个 object 的数组里
                            for (int paramIndex = 0; paramIndex < exParamList.size(); paramIndex++) {
                                String exCurrentParamName = exParamList.get(paramIndex);
                                String exCurrentParamType = exParamMap.get(exCurrentParamName);
                                // callback
                                if (exCurrentParamName.toLowerCase().contains("callback")) {
                                    if (callbackMap.containsKey(exCurrentParamType)) {
                                        requestParams[paramIndex] = callbackMap.get(exCurrentParamType);
                                        continue;
                                    } else {
                                        LinkedHashMap<String, Object> errorResp = new LinkedHashMap<>();
                                        errorResp.put("code", AshenConst.ERROR);
                                        errorResp.put(AshenConst.errorMessage, "没有找到 " + exCurrentParamType + " callback 类型");
                                        response.setContent(errorResp);
                                        response.end();
                                        return;
                                    }
                                }

                                Object acCurrentParam = acParamMap.get(exCurrentParamName);
                                // 获取参数的类型
//                                Class cls = Class.forName(exCurrentParamType);

                                Type type = (Type) exParamTypeMap.get(urlToMatchSections).get(exCurrentParamName).get("type");

                                // 如果不是基础类型,则开始使用 Gson 尝试转换
                                Gson paramGson = BaseGson.getGson();

                                String messageStr = paramGson.toJson(acCurrentParam);

                                // 判断参数是不是泛型 使用带有 callback 的 map 存放参数数据
                                Object o = paramGson.fromJson(messageStr, type);
                                if (o instanceof LinkedTreeMap) {
                                    HashMap<Object, Object> map = new HashMap((Map) o);
                                    requestParams[paramIndex] = map;
                                } else {
                                    requestParams[paramIndex] = o;
                                }

                            }

                            // 参数组装完毕,尝试 invoke 接口
                            Class<?> rClient = Class.forName(AshenConst.className);
                            Class<?>[] paramTypeList = new Class[methodParamMap.size()];
                            for (int paramIndex = 0; paramIndex < methodParamMap.size(); paramIndex++) {
                                String paramName = exParamList.get(paramIndex);
                                try {
                                    if (paramName.toLowerCase().contains("callback")) {
                                        // 处理 callback 泛型问题
                                        paramName = methodParamMap.get(paramName).split("<")[0];
                                        paramTypeList[paramIndex] = Class.forName(paramName);
                                    } else {
                                        paramTypeList[paramIndex] = Class.forName(methodParamMap.get(paramName));
                                    }
                                } catch (ClassNotFoundException e) {
                                    switch (methodParamMap.get(paramName)) {
                                        case "long":
                                            paramTypeList[paramIndex] = long.class;
                                            break;
                                        case "int":
                                            paramTypeList[paramIndex] = int.class;
                                            break;
                                        case "byte":
                                            paramTypeList[paramIndex] = byte.class;
                                            break;
                                        case "short":
                                            paramTypeList[paramIndex] = short.class;
                                            break;
                                        case "char":
                                            paramTypeList[paramIndex] = char.class;
                                            break;
                                        case "float":
                                            paramTypeList[paramIndex] = float.class;
                                            break;
                                        case "double":
                                            paramTypeList[paramIndex] = double.class;
                                            break;
                                        case "boolean":
                                            paramTypeList[paramIndex] = boolean.class;
                                            break;
                                        case "void":
                                            paramTypeList[paramIndex] = void.class;
                                            break;
                                        default:
                                            throw e;
                                    }
                                }

                            }

                            Method method = rClient.getMethod(urlToMatchSections, paramTypeList);

                            // 反射获取类的实例对象
                            Object classInstance = null;
                            if (!AshenConst.classRegistered.containsKey(AshenConst.className)) {
                                LinkedHashMap<String, Object> errorResp = new LinkedHashMap<>();
                                errorResp.put("code", AshenConst.ERROR);
                                errorResp.put(AshenConst.errorMessage, "未注册 AshenConst.className");
                                response.setContent(errorResp);
                                response.end();
                                return;
                            }
                            classInstance = AshenConst.classRegistered.get(AshenConst.className);

                            Object object = method.invoke(classInstance, requestParams);
                            if (currentMethodMap.get("returnType") != null && !currentMethodMap.get("returnType").equals(AshenConst.className) && !currentMethodMap.get("returnType").equals("void")) {
                                CallBackImpl.responseMap.put(AshenConst.message, Util.objectToMap(object));
                                test_done = true;
                            }
                            Util.success(response, Util.waiting_callback(request, CallBackImpl.responseMap));
                            return;
                        }

                    } catch (Exception e) {
                        LinkedHashMap<String, Object> errorResp = new LinkedHashMap<>();
                        errorResp.put("code", AshenConst.ERROR);
                        errorResp.put(AshenConst.errorMessage, e.toString());
                        response.setContent(errorResp);
                        response.end();
                        return;
                    }


                }
            }
        }
        Util.notFund(response, null);
    }

    private String getRequestUrlStr(String urlToMatch) {
        if (urlToMatch == null) {
            return null;
        }
        int qPos = urlToMatch.indexOf('?');
        if (qPos != -1) {
            urlToMatch = urlToMatch.substring(0, urlToMatch.indexOf("?"));
        }
        String[] uris = urlToMatch.split("/");
        return uris[1];
    }

}
