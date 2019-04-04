package com.leo.litejson;


import com.leo.litejson.medatata.ClassMetadata;
import com.leo.litejson.medatata.TypeMetadata;
import com.leo.litejson.utils.CacheUtil;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author leo
 * @date 2019/4/3
 */
public final class LiteJson {

    public static String toJsonString(Object data) {
        if (data == null) {
            throw new RuntimeException("args illegal. data must be can serializable object");
        }

        if (TypeMetadata.isPrimitiveOrBox(data)) {
            throw new RuntimeException("args illegal. data must be can serializable object");
        }
        TypeMetadata typeMetadata = TypeMetadata.typeof(data.getClass(), true);
        if (typeMetadata.isString()) {
            throw new RuntimeException("args illegal. data must be can serializable object");
        }

        StringBuilder stringBuilder = CacheUtil.getStringBuild();
        stringBuilder.setLength(0);

        return toJsonString(data, typeMetadata, stringBuilder);
    }

    private static String toJsonString(Object data, TypeMetadata typeMetadata, StringBuilder stringBuilder) {
        if (typeMetadata.isArray()) {
            stringBuilder.append("[");
            int length = ((Object[]) data).length;
            if (length == 0) {
                return stringBuilder.append("]").toString();
            }
            Object item = ((Object[]) data)[0];
            typeMetadata = TypeMetadata.typeof(item.getClass(), false);
            for (int i = 0; i < length; i++) {
                stringBuilder.append(toJsonString(((Object[]) data)[i], typeMetadata, stringBuilder)).append(",");
            }
            return stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("]").toString();
        }

        if (typeMetadata.isCollection()) {
            stringBuilder.append("[");
            if (((Collection) data).size() == 0) {
                return stringBuilder.append("]").toString();
            }

            Object item = ((Collection) data).iterator().next();
            typeMetadata = TypeMetadata.typeof(item.getClass(), false);
            for (Object c : ((Collection) data)) {
                stringBuilder.append(toJsonString(c, typeMetadata, stringBuilder)).append(",");
            }
            return stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("]").toString();
        }

        if (typeMetadata.isMap()) {
            stringBuilder.append("{");
            Set<Map.Entry> entrySet = ((Map) data).entrySet();
            if (entrySet.size() == 0) {
                return stringBuilder.append("}").toString();
            }
            Map.Entry item = entrySet.iterator().next();
            typeMetadata = TypeMetadata.typeof(item.getValue().getClass(), false);

            for (Map.Entry<Object, Object> entry : entrySet) {
                Object value = entry.getValue();
                stringBuilder.append("\"").append(entry.getKey().toString()).append("\":")
                        .append(toJsonString(value, typeMetadata, stringBuilder)).append(",");
            }

            return stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("}").toString();
        }

        if (typeMetadata.isObject()) {
            Class<?> klass = data.getClass();
            ClassMetadata classMetadata = CacheUtil.getClassMetadata(klass);
            if (classMetadata == null) {
                synchronized (klass) {
                    if (CacheUtil.getClassMetadata(klass) == null) {
                        classMetadata = ClassMetadata.getClassMetadata(data);
                    }
                }
            }
            classMetadata.addToJsonString(data, stringBuilder);
            return stringBuilder.toString();
        }

        throw new RuntimeException("UnKnown Type");
    }
}
