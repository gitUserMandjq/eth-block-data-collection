package com.eth.framework.base.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 对象管理，后面换成redis
 */
public class ObjectManageUtils {
    public static Map<String, Object> map = new HashMap<>();
    public static void setValue(String key, Object o){
        map.put(key, o);
    }
    public static Object getValue(String key){
        Object o = map.get(key);
        return o;
    }
    public static boolean containsValue(String key){
        return map.containsValue(key);
    }
    public static void remove(String key){
        map.remove(key);
    }
}
