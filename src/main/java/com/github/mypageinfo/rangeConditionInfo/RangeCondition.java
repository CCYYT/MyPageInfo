package com.github.mypageinfo.rangeConditionInfo;


import com.github.mypageinfo.Condition;
import com.github.mypageinfo.sql.FieldNameConventionConverter;
import com.github.mypageinfo.sql.PrecompiledSql;
import com.github.mypageinfo.util.PoJoUtil;
import com.github.mypageinfo.verification.FieldNameException;
import com.github.mypageinfo.verification.FieldTypeException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.BiConsumer;

public class RangeCondition
        extends Condition<RangeValue> {

    @Override
    public PrecompiledSql toSql(FieldNameConventionConverter converter) {
        LinkedHashMap<String,Class<?>> parameter = new LinkedHashMap<>();
        StringJoiner joiner = new StringJoiner(" and "," ( "," ) ");
        for (Map.Entry<String, RangeValue> entry : this.entrySet()) {
            RangeValue rangeValue = entry.getValue();
            //改变字段命名规则
            String k = converter.convertToNewFieldNameFormat(entry.getKey());
            rangeValue.forEach(
                    (rangeMode, v) -> {
                        switch (rangeMode) {
                            case lte:
                                joiner.add(k + " <= ?");
                                break;
                            case gte:
                                joiner.add(k + " >= ?");
                                break;
                        }
                        try {
                            parameter.put(v, PoJoUtil.getFieldType(entry.getKey()));
                        } catch (FieldNameException e) {
                            throw new RuntimeException("pageInfo.fieldRule.ruleItem.range 中字段异常! \n"+e);
                        }
                    });
        }
        return new PrecompiledSql(joiner.toString(),parameter);
    }

    @Override
    public void check() {
        this.forEach(new BiConsumer<String, RangeValue>() {
            @Override
            public void accept(String k, RangeValue rangeValue) {
                rangeValue.forEach(new BiConsumer<RangeMode, String>() {
                    @Override
                    public void accept(RangeMode rangeMode, String v) {
                        try {
                            PoJoUtil.checkField(k, v);
                        } catch (FieldNameException | FieldTypeException e) {
                            throw new RuntimeException("pageInfo.fieldRule.ruleItem.range 中字段异常! 异常键:"+k+" 异常值:"+v+"\n"+e);
                        }
                    }
                });
            }
        });
    }
}
