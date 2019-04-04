package com.leo.litejson.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author leo
 * @date 2019/4/3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonIgnore {
    /**
     * ignore or not when serialization
     *
     * @return
     */
    boolean serializable() default false;

    /**
     * ignore or not when deserialization
     *
     * @return
     */
    boolean deserializable() default false;
}
