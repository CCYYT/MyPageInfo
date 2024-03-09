package com.github.mypageinfo;


import com.github.mypageinfo.sql.FieldNameConventionConverter;
import com.github.mypageinfo.util.NameConventionConverter;

public class Config {

    private final static FieldNameConventionConverter converter = new NameConventionConverter();

    public static FieldNameConventionConverter getConverter() {
        return converter;
    }
}
