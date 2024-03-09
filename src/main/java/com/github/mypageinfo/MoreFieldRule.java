package com.github.mypageinfo;



import java.util.List;

public class MoreFieldRule extends FieldRule {
    private List<RuleItem> ruleItems;

    private Sort sort;

    @Override
    public void checkField() {
        for (RuleItem ruleItem : ruleItems) {
            ruleItem.forEach(Condition::check);
        }
        if(sort != null) sort.check();
    }

    public List<RuleItem> getRuleItems() {
        return ruleItems;
    }

    public void setRuleItems(List<RuleItem> ruleItems) {
        this.ruleItems = ruleItems;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }
}
