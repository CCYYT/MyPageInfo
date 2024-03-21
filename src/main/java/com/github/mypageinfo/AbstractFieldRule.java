package com.github.mypageinfo;

import com.github.mypageinfo.deserializer.FieldRuleDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FieldRuleDeserializer.class)
public abstract class AbstractFieldRule {

    private Sort sort;

    //校验FieldRule中的字段 是否合法
    public void checkField(){
        checkRuleItem();
        if(sort != null) sort.check();
    }
    public abstract void checkRuleItem();


    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }
}
