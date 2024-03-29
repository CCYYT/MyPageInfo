package com.github.mypageinfo;

import com.github.mypageinfo.util.PoJoUtil;

import java.util.List;

public class PageInfo<T> {
    private int page; //第几页
    private int pageSize =10; //每一页的大小
    private long totalPagesNum; //一共有多少页
    private long totalElementNum; //一共多少行
    private List<T> content; //结果字段
    private AbstractFieldRule fieldRule; //筛选条件

    private boolean isCheckField = false;


    public PageInfo(boolean skipFieldRule) {
        if(skipFieldRule) System.err.println("警告：跳过FieldRule检测,PageInfo中的FieldRule不可靠！ 会一定的风险");
        this.isCheckField = skipFieldRule;
    }

    public PageInfo(AbstractFieldRule fieldRule) {
        this.fieldRule = fieldRule;
    }

    //校验fieldRule中的字段 是否合法
    public void checkFieldRule(Class<?> ruleObject){
        PoJoUtil.setFieldRulePoJoType(ruleObject);
        fieldRule.checkField();
        this.isCheckField = true;
    }

    public boolean getIsCheckField(){
        return this.isCheckField;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalPagesNum() {
        return totalPagesNum;
    }

    public void setTotalPagesNum(long totalPagesNum) {
        this.totalPagesNum = totalPagesNum;
    }

    public long getTotalElementNum() {
        return totalElementNum;
    }

    public void setTotalElementNum(long totalElementNum) {
        this.totalElementNum = totalElementNum;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public AbstractFieldRule getFieldRule() {
        return fieldRule;
    }

    public void setFieldRule(AbstractFieldRule fieldRule) {
        this.fieldRule = fieldRule;
    }
}
