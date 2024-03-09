package com.github.mypageinfo;


import com.github.mypageinfo.util.PoJoUtil;
import com.github.mypageinfo.verification.FieldNameException;

import java.util.LinkedHashMap;

public class Sort
        extends LinkedHashMap<String,SortMode>
        implements FieldCheck{

    @Override
    public void check() {
            this.keySet().forEach(fieldName -> {
                try {
                    PoJoUtil.getFieldType(fieldName);
                } catch (FieldNameException e) {
                    throw new RuntimeException("pageInfo.fieldRule.sort 中字段异常! "+e);
                }
            });
    }
}
enum SortMode{
    asc,
    desc
}
