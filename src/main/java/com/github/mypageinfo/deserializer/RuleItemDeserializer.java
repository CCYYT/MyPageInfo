package com.github.mypageinfo.deserializer;

import com.github.mypageinfo.Condition;
import com.github.mypageinfo.equalConditionInfo.EqualCondition;
import com.github.mypageinfo.inConditionInfo.InCondition;
import com.github.mypageinfo.likeConditionInfo.LikeCondition;
import com.github.mypageinfo.rangeConditionInfo.RangeCondition;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.mypageinfo.RuleItem;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class RuleItemDeserializer extends StdDeserializer<RuleItem> {
    public RuleItemDeserializer() {
        this(Condition.class);
    }

    protected RuleItemDeserializer(Class<?> vc) {
        super(vc);
    }

    protected RuleItemDeserializer(JavaType valueType) {
        super(valueType);
    }

    protected RuleItemDeserializer(StdDeserializer<?> src) {
        super(src);
    }


    private static final Map<String, Class<? extends Condition>> conditionMap = new HashMap<>();

    static {
        // 注册对应的 condition映射类型
        conditionMap.put("equal", EqualCondition.class);
        conditionMap.put("range", RangeCondition.class);
        conditionMap.put("in", InCondition.class);
        conditionMap.put("like", LikeCondition.class);
    }

    @Override
    public RuleItem deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        RuleItem ruleItem = new RuleItem();

        node.fields().forEachRemaining(new Consumer<Map.Entry<String, JsonNode>>() {
            @Override
            public void accept(Map.Entry<String, JsonNode> stringJsonNodeEntry) {
                String key = stringJsonNodeEntry.getKey();
                JsonNode value = stringJsonNodeEntry.getValue();
                try {
                    if (conditionMap.containsKey(key)){
                        Condition value1 = value.traverse(p.getCodec()).readValueAs(conditionMap.get(key));
                        ruleItem.add(value1);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return ruleItem;
    }

}
