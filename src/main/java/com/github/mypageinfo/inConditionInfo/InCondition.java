package com.github.mypageinfo.inConditionInfo;


import com.github.mypageinfo.Condition;
import com.github.mypageinfo.sql.FieldNameConventionConverter;
import com.github.mypageinfo.sql.PrecompiledSql;
import com.github.mypageinfo.util.PoJoUtil;
import com.github.mypageinfo.verification.FieldNameException;
import com.github.mypageinfo.verification.FieldTypeException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class InCondition
        extends Condition<ArrayList<String>> {
    @Override
    public void check() {
        this.forEach(new BiConsumer<String, ArrayList<String>>() {
            @Override
            public void accept(String k, ArrayList<String> values) {
                for (String v : values) {
                    try {
                        PoJoUtil.checkField(k, v);
                    } catch (FieldNameException | FieldTypeException e) {
                        throw new RuntimeException("pageInfo.fieldRule.ruleItem.in 中字段异常! 异常键:"+k+" 异常值:"+v+"\n"+e);
                    }
                }
            }
        });
    }

    @Override
    public PrecompiledSql toSql(FieldNameConventionConverter converter) {
        LinkedHashMap<String,Class<?>> parameter = new LinkedHashMap<>();
        StringJoiner joiner =
                this.size() == 1 ?
                        new StringJoiner(" and "): new StringJoiner(" and "," ( "," ) ");
        this.forEach(new BiConsumer<String, ArrayList<String>>() {
            @Override
            public void accept(String s, ArrayList<String> values) {
                String k = converter.convertToNewFieldNameFormat(s);
                StringJoiner joiner1 = new StringJoiner(" , ",k+" in ( "," ) ");
                for (String v : values) {
                    joiner1.add("?");
                    try {
                        parameter.put(v,PoJoUtil.getFieldType(k));
                    } catch (FieldNameException e) {
                        throw new RuntimeException("pageInfo.fieldRule.ruleItem.in 中字段异常! \n"+e);
                    }
                }
                joiner.add(joiner1.toString());

            }
        });
        return new PrecompiledSql(joiner.toString(),parameter);
    }
}
