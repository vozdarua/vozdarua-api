package io.vozdarua.utils;

public class VozDaRuaUtils {
    public static <T> T verifyNull(T oldObj, T newObj) {
        return newObj != null ? newObj : oldObj;
    }
}
