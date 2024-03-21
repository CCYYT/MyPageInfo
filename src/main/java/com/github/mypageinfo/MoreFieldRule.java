package com.github.mypageinfo;



import java.util.List;

public class MoreFieldRule extends AbstractFieldRule {
    private List<RuleItem> ruleItems;

    @Override
    public void checkRuleItem() {
        if(ruleItems != null) {
            for (RuleItem ruleItem : ruleItems) {
                ruleItem.forEach(Condition::check);
            }
        }
    }

    public List<RuleItem> getRuleItems() {
        return ruleItems;
    }

    public void setRuleItems(List<RuleItem> ruleItems) {
        this.ruleItems = ruleItems;
    }

}
