package com.leo.litejson.medatata;

import com.leo.litejson.utils.CacheUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author leo
 * @date 2019/4/3
 */
public class ClassMetadata {


    private Class classClass;

    private Map<Field, FieldMetadata> fieldToFieldMetadata;

    private Map<String, FieldMetadata> stringToFieldMetadata;

    private ClassMetadata() {
        fieldToFieldMetadata = new WeakHashMap();
        stringToFieldMetadata = new WeakHashMap<>();
    }

    /**
     * give a data not Class instance, return a ClassMetadata instance
     *
     * @param data
     * @return
     */
    public static ClassMetadata getClassMetadata(Object data) {
        Class<?> klass = data.getClass();
        ClassMetadata classMetadata = new ClassMetadata();
        Field[] declaredFields = klass.getDeclaredFields();
        boolean canCache = false;
        try {
            for (int i = 0, length = declaredFields.length; i < length; i++) {
                Field field = declaredFields[i];
                int modifiers = field.getModifiers();
                if ((modifiers & Modifier.TRANSIENT) == Modifier.TRANSIENT
                        || (modifiers & Modifier.STATIC) == Modifier.STATIC
                        || (modifiers & Modifier.FINAL) == Modifier.FINAL) {
                    continue;
                }
                // TODO parse @JsonIgnore
                // TODO parse @JsonProperty
                // TODO parse @JsonAlias
                String property = field.getName();
                String[] alias = {};
                field.setAccessible(true);
                TypeMetadata typeMetadata = TypeMetadata.typeof(field.getType(), false);
                FieldMetadata fieldMetadata = typeMetadata.isArray()
                        ? new FieldMetadata(field, property, typeMetadata.getFieldTypeName(), typeMetadata.getArrayAimension())
                        : new FieldMetadata(field, property, typeMetadata);
                classMetadata.putFieldMetadata(field, fieldMetadata);
                classMetadata.putFieldMetadata(alias, fieldMetadata);
                canCache = true;
            }
            classMetadata.classClass = klass;
            if (canCache) {
                CacheUtil.putClassMetadata(klass, classMetadata);
            }
            return classMetadata;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void putFieldMetadata(Field field, FieldMetadata fieldMetadata) {
        fieldToFieldMetadata.put(field, fieldMetadata);
        stringToFieldMetadata.put(fieldMetadata.getProperty(), fieldMetadata);
    }

    public void putFieldMetadata(String string, FieldMetadata fieldMetadata) {
        stringToFieldMetadata.put(string, fieldMetadata);
    }

    public void putFieldMetadata(String[] strings, FieldMetadata fieldMetadata) {
        for (String string : strings) {
            stringToFieldMetadata.put(string, fieldMetadata);
        }
    }

    public FieldMetadata getFieldMetadata(Field field) {
        return fieldToFieldMetadata.get(field);
    }

    public FieldMetadata putFieldMetadata(String string) {
        return stringToFieldMetadata.get(string);
    }

    public void addToJsonString(Object data, StringBuilder stringBuilder) {
        // TODO Unsafe replace StringBuilder

        stringBuilder.append("{");
        try {
            for (FieldMetadata fieldMetadata : fieldToFieldMetadata.values()) {
                fieldMetadata.addToJsonString(fieldMetadata.getField().get(data), stringBuilder);
                stringBuilder.append(',');
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("}");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
