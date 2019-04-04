package com.leo.litejson.medatata;

import com.leo.litejson.utils.UnsafeUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author leo
 * @date 2019/4/3
 */
public class FieldMetadata {


    private Field field;
    private Class fieldClass;
    private long offset;

    private String property;
    private TypeMetadata typeMetadata;
    private TypeMetadata.Type type;
    private String fieldTypeName;
    private int fieldTypeId;

    /**
     * array aimension. if arrayAimension > 0, that's current filed is array.
     * and fieldTypeName is the base types
     */
    private int arrayAimension;

    /**
     * ths constructor could not use if type is array type.
     * if you want a constructor function which type is array, you should be use others constructor
     *
     * @param field
     * @param property
     * @param typeMetadata
     * @see com.leo.litejson.medatata.FieldMetadata#FieldMetadata(Field, String, String, int)
     */
    public FieldMetadata(Field field, String property, TypeMetadata typeMetadata) {
        if (typeMetadata.getType() == TypeMetadata.Type.Array) {
            throw new RuntimeException("type must be not array type");
        }

        this.field = field;
        this.fieldClass = field.getType();
        offset = UnsafeUtil.getUnsafe().objectFieldOffset(field);

        this.property = property;
        this.typeMetadata = typeMetadata;
        this.type = typeMetadata.getType();
        this.fieldTypeName = type.name;
        this.fieldTypeId = type.id;

        this.arrayAimension = 0;
        field.setAccessible(true);
    }

    /**
     * ths constructor could use if type is array type
     *
     * @param field
     * @param property
     * @param fieldTypeName
     * @param arrayAimension
     * @see com.leo.litejson.medatata.FieldMetadata#FieldMetadata(Field, String, TypeMetadata)
     */
    public FieldMetadata(Field field, String property, String fieldTypeName, int arrayAimension) {
        if (arrayAimension <= 0) {
            throw new RuntimeException("arrayAimension must be greater than zero");
        }

        this.field = field;
        this.fieldClass = field.getType();
        offset = UnsafeUtil.getUnsafe().objectFieldOffset(field);

        this.property = property;
        this.typeMetadata = new TypeMetadata(fieldTypeName, arrayAimension);
        this.type = TypeMetadata.Type.Array;
        this.fieldTypeName = fieldTypeName;
        this.fieldTypeId = TypeMetadata.Type.Array.id;

        this.arrayAimension = arrayAimension;

        field.setAccessible(true);
    }

    public static void addToJsonString_0(Object data, FieldMetadata fieldMetadata, TypeMetadata typeMetadata, StringBuilder stringBuilder) {
        if (fieldMetadata != null) {
            stringBuilder.append("\"").append(fieldMetadata.property).append("\":");
        }

        if (data == null) {
            stringBuilder.append("null");
            return;
        }

        switch (typeMetadata.getType()) {
            case Primitive_Boolean:
            case Primitive_Byte:
            case Primitive_Short:
            case Primitive_Int:
            case Primitive_Long:
            case Primitive_Float:
            case Primitive_Double:
                stringBuilder.append(data);
                break;
            case Primitive_Char:
                stringBuilder.append("\"").append(data).append("\"");
                break;
            case Box_Character:
            case String:
                if (data == null) {
                    stringBuilder.append(data);
                } else {
                    stringBuilder.append("\"").append(data).append("\"");
                }
                break;
            case Box_Byte:
            case Box_Boolean:
            case Box_Short:
            case Box_Integer:
            case Box_Long:
            case Box_Float:
            case Box_Double:
                stringBuilder.append(data.toString());
                break;
            case Array:
                stringBuilder.append("[");
                Object[] array = (Object[]) data;
                int length = array.length;
                if (length == 0) {
                    stringBuilder.append("]");
                    break;
                }
                Object item = array[0];
                typeMetadata = TypeMetadata.typeof(item.getClass(), true);
                for (int i = 0; i < length; i++) {
                    addToJsonString_0(array[i], null, typeMetadata, stringBuilder);
                    stringBuilder.append(",");
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("]");
                break;
            case Collection:
                stringBuilder.append("[");
                if (((Collection) data).size() == 0) {
                    stringBuilder.append("]");
                    break;
                }

                item = ((Collection) data).iterator().next();
                typeMetadata = TypeMetadata.typeof(item.getClass(), false);
                for (Object c : ((Collection) data)) {
                    addToJsonString_0(c, null, typeMetadata, stringBuilder);
                    stringBuilder.append(",");
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("]");
                break;
            case Map:
                stringBuilder.append("{");
                Set<Map.Entry> entrySet = ((Map) data).entrySet();
                if (entrySet.size() == 0) {
                    stringBuilder.append("}");
                    break;
                }
                item = entrySet.iterator().next();
                typeMetadata = TypeMetadata.typeof(((Map.Entry) item).getValue().getClass(), false);

                for (Map.Entry<Object, Object> entry : entrySet) {
                    Object value = entry.getValue();
                    stringBuilder.append("\"").append(entry.getKey().toString()).append("\":");
                    addToJsonString_0(value, null, typeMetadata, stringBuilder);
                    stringBuilder.append(",");
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("}");
                break;
            case Object:
                ClassMetadata.getClassMetadata(data).addToJsonString(data, stringBuilder);
                break;
            default:
                stringBuilder.append("null");
                break;
        }
    }

    public void addToJsonString(Object data, StringBuilder stringBuilder) {
        addToJsonString_0(data, this, typeMetadata, stringBuilder);
    }

    public Field getField() {
        return field;
    }

    public long getOffset() {
        return offset;
    }

    public String getProperty() {
        return property;
    }
}
