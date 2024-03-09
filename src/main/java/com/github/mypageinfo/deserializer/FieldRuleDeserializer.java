package com.github.mypageinfo.deserializer;

import com.github.mypageinfo.FieldRule;
import com.github.mypageinfo.MoreFieldRule;
import com.github.mypageinfo.RuleItem;
import com.github.mypageinfo.SimpleFieldRule;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @program: IntelliJ IDEA
 * @description: FieldRule 反序列化器
 * @author: CCYT
 * @create: 2024-02-19 16:01
 **/
public class FieldRuleDeserializer extends StdDeserializer<FieldRule> {
    ObjectCodec codec;

    public FieldRuleDeserializer() {
        this(SimpleFieldRule.class);
    }

    protected FieldRuleDeserializer(Class<?> vc) {
        super(vc);
    }

    protected FieldRuleDeserializer(JavaType valueType) {
        super(valueType);
    }

    protected FieldRuleDeserializer(StdDeserializer<?> src) {
        super(src);
    }

    @Override
    public FieldRule deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        codec = p.getCodec();


        if (node.has("ruleItem")) {
            // 处理 "ruleItem" 的情况
            SimpleFieldRule simpleFieldRule = new SimpleFieldRule();
            simpleFieldRule.setRuleItem(
                    readValueAsRuleItem(node.get("ruleItem"))
            );
            return simpleFieldRule;

        } else if (node.has("ruleItems")) {
            // 处理 "ruleItems" 的情况
            MoreFieldRule moreFieldRule = new MoreFieldRule();
            ArrayList<RuleItem> ruleItems = new ArrayList<>();
            node.get("ruleItems")
                    .elements()
                    .forEachRemaining(
                            jsonNode -> ruleItems.add(readValueAsRuleItem(jsonNode))
                    );
            moreFieldRule.setRuleItems(ruleItems);

            return moreFieldRule;
        }
        return null;
    }

    private RuleItem readValueAsRuleItem(JsonNode jsonNode){
        try {
            return jsonNode.traverse(codec)
                    .readValueAs(RuleItem.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
