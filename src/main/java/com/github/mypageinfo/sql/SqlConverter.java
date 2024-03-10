package com.github.mypageinfo.sql;

public interface SqlConverter {
    //FieldNameConventionConverter 字段名装换器
    PrecompiledSql toSql(FieldNameConventionConverter converter);
}
