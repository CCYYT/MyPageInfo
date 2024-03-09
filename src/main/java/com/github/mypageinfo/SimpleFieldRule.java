package com.github.mypageinfo;


public class SimpleFieldRule extends FieldRule {

    private RuleItem ruleItem;

    private Sort sort;

    @Override
    public void checkField() {
        ruleItem.forEach(Condition::check);
        if(sort != null) sort.check();
    }

    public RuleItem getRuleItem() {
        return ruleItem;
    }

    public void setRuleItem(RuleItem ruleItem) {
        this.ruleItem = ruleItem;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }
}
