package com.android.AshenAndroid.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class CallBackImpl {
    public static Map<String, Object> responseMap;
    public static boolean test_done;

    /**
     * public static Callback callback = new Callback() {
     *
     *         @Override
     *         public void onError(String message, ErrorCode code) {
     *             responseMap.put(AshenConst.errorCode, code.getValue());
     *             responseMap.put(AshenConst.errorMessage, Util.objectToMap(message));
     *             responseMap.put(AshenConst.code, AshenConst.ERROR);
     *             test_done = true;
     *         }
     *
     *
     *         @Override
     *         public void onSuccess(String message) {
     *             responseMap.put(AshenConst.message, Util.objectToMap(message));
     *             test_done = true;
     *         }
     *
     *         @Override
     *         public void onProgress(String message, int progress) {
     *
     *         }
     *     };
     */

}
