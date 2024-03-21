package com.github.mypageinfo;


public class SimpleFieldRule extends AbstractFieldRule {

    private RuleItem ruleItem;

    @Override
    public void checkRuleItem() {
        if(ruleItem != null)ruleItem.forEach(Condition::check);
    }

    public RuleItem getRuleItem() {
        return ruleItem;
    }

    public void setRuleItem(RuleItem ruleItem) {
        this.ruleItem = ruleItem;
    }

}
