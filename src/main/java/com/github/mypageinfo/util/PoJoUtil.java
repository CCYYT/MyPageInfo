package com.github.mypageinfo.util;


import com.github.mypageinfo.verification.FieldNameException;
import com.github.mypageinfo.verification.FieldTypeException;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

public class PoJoUtil {

    private static final ThreadLocal<Map<String,Class<?>>> fieldTypeMapping = new ThreadLocal<>();

//    private static final Map<Class<?>, Predicate<String>> typeMap = new HashMap<>();
    private static final Map<Class<?>, Function<String,Object>> typeMap = new HashMap<>();

    static {
        typeMap.put(Integer.class,Integer::new);

        typeMap.put(Float.class,Float::new);

        typeMap.put(Double.class,Double::new);

        typeMap.put(String.class, s -> s);

        List<String> DATE_FORMATS = Arrays.asList(
                "yyyy-MM-dd", "dd/MM/yyyy", "dd-MM-yyyy", "MM/dd/yyyy", "MM-dd-yyyy", "yyyy/MM/dd"
        );
        typeMap.put(Date.class, s -> {
            for (String format : DATE_FORMATS) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                    return LocalDate.parse(s, formatter);
            }
            return null;
        });


    }

    public static Object testInstanceByType(Class<?> type,String value){
        return typeMap.get(type).apply(value);
    }

    public static void setFieldRulePoJoType(Class<?> type) {
        HashMap<String, Class<?>> map = new HashMap<>();
        for (Field field : type.getDeclaredFields()) {
            map.put(field.getName(),field.getType());
        }
        fieldTypeMapping.set(map);
    }

    public static void checkField(String field, String value) throws FieldNameException, FieldTypeException {

        Map<String, Class<?>> map = fieldTypeMapping.get();
        if (!map.containsKey(field))
            throw new FieldNameException("没有匹配的字段! "+field);
        try {
            typeMap.get(map.get(field)).apply(value);
        } catch (Exception e) {
            throw new FieldTypeException("字段类型不匹配!  字段名:"+field+"   期望的类型:"+map.get(field));
        }
    }

    public static Class<?> getFieldType(String fieldName) throws FieldNameException {
        Class<?> aClass = fieldTypeMapping.get().get(fieldName);
        if(aClass == null) throw new FieldNameException("没有匹配的字段! "+fieldName);
        return aClass;
    }
}
