package com.github.mypageinfo.sql;


import java.util.LinkedHashMap;

public class PrecompiledSql {
    private String sql;

    private LinkedHashMap<String,Class<?>> parameterMappings;

    public PrecompiledSql(String sql, LinkedHashMap<String, Class<?>> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public LinkedHashMap<String, Class<?>> getParameterMappings() {
        return parameterMappings;
    }

    public void setParameterMappings(LinkedHashMap<String, Class<?>> parameterMappings) {
        this.parameterMappings = parameterMappings;
    }
}
