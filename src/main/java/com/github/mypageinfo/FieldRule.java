package com.github.mypageinfo;

import com.github.mypageinfo.deserializer.FieldRuleDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FieldRuleDeserializer.class)
public abstract class FieldRule {

    //校验FieldRule中的字段 是否合法
    public abstract void checkField();
}
