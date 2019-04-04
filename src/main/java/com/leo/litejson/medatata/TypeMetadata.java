package com.leo.litejson.medatata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leo
 * @date 2019/4/4
 */
public class TypeMetadata {

    public enum Type {

        /**
         * 原始boolean类型
         */
        Primitive_Boolean(boolean.class.getName(), 0x00),

        /**
         * 原始byte类型
         */
        Primitive_Byte(byte.class.getName(), 0x01),

        /**
         * 原始char类型
         */
        Primitive_Char(char.class.getName(), 0x02),

        /**
         * 原始shot类型
         */
        Primitive_Short(short.class.getName(), 0x03),

        /**
         * 原始int类型
         */
        Primitive_Int(int.class.getName(), 0x04),

        /**
         * 原始long类型
         */
        Primitive_Long(long.class.getName(), 0x05),

        /**
         * 原始float类型
         */
        Primitive_Float(float.class.getName(), 0x06),

        /**
         * 原始double类型
         */
        Primitive_Double(double.class.getName(), 0x07),

        /**
         * boolean包装类
         */
        Box_Boolean(Boolean.class.getTypeName(), 0x8),

        /**
         * byte包装类
         */
        Box_Byte(Byte.class.getTypeName(), 0x9),

        /**
         * char包装类
         */
        Box_Character(Character.class.getTypeName(), 0xA),

        /**
         * short包装类
         */
        Box_Short(Short.class.getTypeName(), 0xB),

        /**
         * int包装类
         */
        Box_Integer(Integer.class.getTypeName(), 0xC),

        /**
         * long包装类
         */
        Box_Long(Long.class.getTypeName(), 0xD),

        /**
         * float包装类
         */
        Box_Float(Float.class.getTypeName(), 0xE),

        /**
         * double包装类
         */
        Box_Double(Double.class.getTypeName(), 0xF),

        /**
         * String类
         */
        String(java.lang.String.class.getTypeName(), 0x10),
        /**
         * collection体系
         */
        Collection(java.util.Collection.class.getTypeName(), 0x11),

        /**
         * map体系
         */
        Map(java.util.Map.class.getTypeName(), 0x12),

        /**
         * map体系
         */
        Array("", 0x13),

        /**
         * 普通对象类,非包装类型、数组、Conllection、Map
         */
        Object(Object.class.getTypeName(), 0x14),

        /**
         * 空类型
         */
        Void(Void.class.getTypeName(), -1);

        /**
         * 类型名
         */
        public String name;

        /**
         * 类型标识
         */
        public int id;

        Type(String name, int id) {
            this.name = name;
            this.id = id;
        }
    }

    private static final Map<String, Type> TYPENAME_TO_FIELDTYPE;

    static {
        Type[] values = Type.values();
        TYPENAME_TO_FIELDTYPE = new HashMap<>((int) (values.length / 0.75) + 1);
        for (Type type : values) {
            if (type.name.equals("")) {
                continue;
            }
            TYPENAME_TO_FIELDTYPE.put(type.name, type);
        }
    }

    private Type type;

    private String fieldTypeName;

    /**
     * array aimension. if arrayAimension > 0, that's current type is array,
     * and fieldTypeName is the base types
     */
    private int arrayAimension;

    public TypeMetadata(Type type, String fieldTypeName) {
        if (type == Type.Array) {
            throw new RuntimeException("type must be not array type");
        }

        this.type = type;
        this.fieldTypeName = fieldTypeName;
        this.arrayAimension = 0;
    }

    public TypeMetadata(String fieldTypeName, int arrayAimension) {
        if (arrayAimension <= 0) {
            throw new RuntimeException("arrayAimension must be greater than zero");
        }

        this.type = Type.Array;
        this.fieldTypeName = fieldTypeName;
        this.arrayAimension = arrayAimension;
    }

    public static TypeMetadata typeof(Class klass, boolean skipCheckPrimitiveOrPrimitiveBox) {
        String typeName = klass.getName();

        if (!skipCheckPrimitiveOrPrimitiveBox) {
            Type type = getType(typeName);
            if (type != null) {
                return new TypeMetadata(type, typeName);
            }
        }


        long countArrayLeftSign = typeName.chars().filter(ch -> ch == '[').count();
        if (countArrayLeftSign > 0) {
            return new TypeMetadata(typeName.substring(0, typeName.indexOf('[')), (int) countArrayLeftSign);
        }

        Class[] interfaces = klass.getInterfaces();
        Class<Collection> collectionClass = Collection.class;
        Class<Map> mapClass = Map.class;
        for (Class anInterface : interfaces) {
            if (collectionClass.equals(anInterface)) {
                return new TypeMetadata(Type.Collection, typeName);
            }

            if (mapClass.equals(anInterface)) {
                return new TypeMetadata(Type.Map, typeName);
            }
        }

        return new TypeMetadata(Type.Object, typeName);
    }


    public static Type getType(String typeName) {
        return TYPENAME_TO_FIELDTYPE.get(typeName);
    }

    public static boolean isPrimitiveOrBox(Object data) {
        String typeName = data.getClass().getTypeName();
        TypeMetadata.Type type = TYPENAME_TO_FIELDTYPE.get(typeName);
        if (type == null) {
            return false;
        }
        int id = type.id;
        if (id < TypeMetadata.Type.Primitive_Boolean.id
                || id > TypeMetadata.Type.Box_Double.id) {
            return false;
        }

        return true;
    }

    public boolean isPrimitiveOrBox() {
        int id = type.id;
        if (id < TypeMetadata.Type.Primitive_Boolean.id
                || id > TypeMetadata.Type.Box_Double.id) {
            return false;
        }

        return true;
    }


    public boolean isString() {
        return Type.String == type;
    }


    public boolean isArray() {
        return TypeMetadata.Type.Array == type;
    }

    public boolean isCollection() {
        return TypeMetadata.Type.Collection == type;
    }


    public boolean isMap() {
        return TypeMetadata.Type.Map == type;
    }

    public boolean isObject() {
        return TypeMetadata.Type.Object == type;
    }

    public Type getType() {
        return type;
    }

    public String getFieldTypeName() {
        return fieldTypeName;
    }

    public int getArrayAimension() {
        return arrayAimension;
    }


}
