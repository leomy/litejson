package com.leo.litejson.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author leo
 * @date 2019/4/3
 */
public final class UnsafeUtil {


    private static final Unsafe unsafe;

    static {
        Class<Unsafe> unsafeClass = Unsafe.class;
        Field unsafeField = null;
        try {
            unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error(e);
        }
    }

    public static Unsafe getUnsafe() {
        return unsafe;
    }
}
