package com.github.mypageinfo;

import com.github.mypageinfo.deserializer.RuleItemDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.HashSet;

@JsonDeserialize(using = RuleItemDeserializer.class)
public class RuleItem
        extends HashSet<Condition<?>> {
}
