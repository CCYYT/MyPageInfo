package com.github.mypageinfo.likeConditionInfo;


import com.github.mypageinfo.Condition;
import com.github.mypageinfo.sql.FieldNameConventionConverter;
import com.github.mypageinfo.sql.PrecompiledSql;
import com.github.mypageinfo.util.PoJoUtil;
import com.github.mypageinfo.verification.FieldNameException;
import com.github.mypageinfo.verification.FieldTypeException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class LikeCondition
        extends Condition<String> {
    @Override
    public void check() {
        this.forEach((k, v) -> {
            try {
                PoJoUtil.checkField(k, v.replace("%","").replace("_",""));
            } catch (FieldNameException | FieldTypeException e) {
                throw new RuntimeException("pageInfo.fieldRule.ruleItem.like 中字段异常! 异常键:"+k+" 异常值:"+v+"\n"+e);
            }
        });
    }

    @Override
    public PrecompiledSql toSql(FieldNameConventionConverter converter) {
        LinkedHashMap<String,Class<?>> parameter = new LinkedHashMap<>();
        StringJoiner joiner =
                this.size() == 1 ?
                        new StringJoiner(" and "): new StringJoiner(" and "," ( "," ) ");
        for (Map.Entry<String, String> entry : this.entrySet()) {
            //改变字段命名规则
            String k = converter.convertToNewFieldNameFormat(entry.getKey());
            joiner.add(k + " like ?");
            try {
                parameter.put(entry.getValue(),PoJoUtil.getFieldType(entry.getKey()));
            } catch (FieldNameException e) {
                throw new RuntimeException("pageInfo.fieldRule.ruleItem.like 中字段异常! \n"+e);
            }
        }
        return new PrecompiledSql(joiner.toString(),parameter);
    }
}
