package com.github.mypageinfo.sql;

public interface SqlConverter {
    PrecompiledSql toSql(FieldNameConventionConverter converter);
}
