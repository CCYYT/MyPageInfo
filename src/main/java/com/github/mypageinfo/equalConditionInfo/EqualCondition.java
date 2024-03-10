package com.github.mypageinfo.equalConditionInfo;


import com.github.mypageinfo.Condition;
import com.github.mypageinfo.sql.FieldNameConventionConverter;
import com.github.mypageinfo.sql.PrecompiledSql;
import com.github.mypageinfo.util.PoJoUtil;
import com.github.mypageinfo.verification.FieldNameException;
import com.github.mypageinfo.verification.FieldTypeException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;


public class EqualCondition
        extends Condition<String>{


    @Override
    public PrecompiledSql toSql(FieldNameConventionConverter converter) {
        LinkedHashMap<String,Class<?>> parameter = new LinkedHashMap<>();
        StringJoiner joiner =
                this.size() == 1 ?
                        new StringJoiner(" and "): new StringJoiner(" and "," ( "," ) ");
        for (Map.Entry<String, String> entry : this.entrySet()) {
            //改变字段命名规则
            String s = converter.convertToNewFieldNameFormat(entry.getKey());
            joiner.add(s + " = ?");
            try {
                parameter.put(entry.getValue(),PoJoUtil.getFieldType(entry.getKey()));
            } catch (FieldNameException e) {
                throw new RuntimeException("pageInfo.fieldRule.ruleItem.equal 中字段异常! \n"+e);
            }
        }
        return new PrecompiledSql(joiner.toString(),parameter);
    }

    @Override
    public void check() {
        this.forEach((field, value) -> {
            try {
                PoJoUtil.checkField(field, value);
            } catch (FieldNameException | FieldTypeException e) {
                throw new RuntimeException("pageInfo.fieldRule.ruleItem.equal 中字段异常! 异常键:"+field+" 异常值:"+value+"\n"+e);
            }
        });
    }

}
