package com.github.mypageinfo;


import com.github.mypageinfo.sql.SqlConverter;

import java.util.LinkedHashMap;

public abstract class Condition<V>
        extends LinkedHashMap<String,V>
        implements FieldCheck, SqlConverter {

}
