package com.leo.litejson.utils;


import com.leo.litejson.medatata.ClassMetadata;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author leo
 * @date 2019/4/3
 */
public class CacheUtil {

    private static final ThreadLocal<StringBuilder> THREAD_LOCAL_STRING_BUILDER = ThreadLocal.withInitial(() -> new StringBuilder());

    /**
     *
     */
    private static final ConcurrentHashMap<Class, ClassMetadata> CACHE_CLASS_INFO = new ConcurrentHashMap();

    public static ClassMetadata getClassMetadata(Class klass) {
        return CACHE_CLASS_INFO.get(klass);
    }

    public static ClassMetadata putClassMetadata(Class klass, ClassMetadata classMetadata) {
        // TODO there are a variabele named threshold represent that the class and classMetadata can be cache or not be
        return CACHE_CLASS_INFO.put(klass, classMetadata);
    }

    public static StringBuilder getStringBuild() {
        return THREAD_LOCAL_STRING_BUILDER.get();
    }
}
