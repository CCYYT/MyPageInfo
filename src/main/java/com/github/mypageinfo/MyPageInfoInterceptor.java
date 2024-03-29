package com.github.mypageinfo;

import com.github.mypageinfo.sql.PrecompiledSql;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


@Intercepts({
        @Signature(method = "prepare", type = StatementHandler.class, args = {Connection.class, Integer.class})
})
public class MyPageInfoInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        if (invocation.getTarget() instanceof StatementHandler) {
            RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
            Object o = handler.getBoundSql().getParameterObject();
            if(o instanceof Map){//方法中的参数是否为空
                Map parameterObject = (Map) o;
                if (parameterObject.containsKey("pageInfo")) {//方法中的参数是否包含PageInfo
                    //获取configuration
                    Configuration configuration = getConfiguration(handler, invocation);

                    //获取pageInfo对象
                    PageInfo pageInfo = (PageInfo) parameterObject.get("pageInfo");

                    //获取原始sql 并生成新的sql
                    BoundSql boundSql = handler.getBoundSql();
                    String sql = boundSql.getSql();
                    List<ParameterMapping> parameterMappings = new ArrayList<>();//方法中的属性
                    Map<String,Object> additionalParameters = new HashMap<>();//额外添加的属性
                    sql += analysisSql(pageInfo,parameterMappings,additionalParameters,configuration);

                    //生成一个新的BoundSql
                    BoundSql newBoundSql = new BoundSql(configuration,sql,parameterMappings,boundSql.getParameterObject());
                    additionalParameters.forEach((k, v) -> newBoundSql.setAdditionalParameter(k,v));

                    //将新生成的BoundSql 去替换invocation.target中的BoundSql
                    setBoundSql(handler,newBoundSql);
                }
            }
        }
        //返回执行结果
        return invocation.proceed();
    }

    /*
    * 获取到当前handler中的Configuration信息
    * 用的是反射 性能可能有问题
    * */
    private Configuration getConfiguration(RoutingStatementHandler handler,Invocation invocation) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = handler.getClass().getDeclaredField("delegate");
        declaredField.setAccessible(true);
        PreparedStatementHandler preparedStatementHandler = (PreparedStatementHandler) declaredField.get(invocation.getTarget());

        Field configurationField = preparedStatementHandler.getClass().getSuperclass().getDeclaredField("configuration");
        configurationField.setAccessible(true);
        return  (Configuration) configurationField.get(preparedStatementHandler);
    }

    /*
    * 用新的BoundSql 去替换掉 handler中的老BoundSql
    * 用的是反射 性能可能有问题
    * */
    private void setBoundSql(RoutingStatementHandler handler,BoundSql newBoundSql) throws NoSuchFieldException, IllegalAccessException {
        Field delegate = handler.getClass().getDeclaredField("delegate");
        delegate.setAccessible(true);
        Object o1 = delegate.get(handler);
        Field olderBoundSql = o1.getClass().getSuperclass().getDeclaredField("boundSql");
        olderBoundSql.setAccessible(true);
        olderBoundSql.set(o1,newBoundSql);

        DefaultParameterHandler parameterHandler = (DefaultParameterHandler) handler.getParameterHandler();
        Field pBoundSql = parameterHandler.getClass().getDeclaredField("boundSql");
        pBoundSql.setAccessible(true);
        pBoundSql.set(parameterHandler,newBoundSql);
    }


    /*
    * 根据PageInfo中的信息 生成对应的 Sql条件
    * 防sql注入
    * */
    public String analysisSql(PageInfo pageInfo, List<ParameterMapping> parameterMappings, Map<String,Object> additionalParameters, Configuration configuration){

        if (!pageInfo.getIsCheckField()) throw new RuntimeException("pageInfo 中未进行过字段检测 ，请运行PageInfo.checkFieldRule(); \n在执行sql前 你应该先执行一次PageInfo.checkFieldRule()");

        //所有条件的sql
        StringBuilder sqlStringBuilder = new StringBuilder();

        AbstractFieldRule fieldRule = pageInfo.getFieldRule();
        if(fieldRule != null){
            //1.解析fieldRule.RuleItem/RuleItems
            String ruleItemSql = analysisRuleItem(fieldRule, parameterMappings, additionalParameters, configuration);
            if(ruleItemSql != null)sqlStringBuilder.append(ruleItemSql); //拼接条件规则

            //2.解析sort
            Sort sort = fieldRule.getSort();
            if (sort != null) {
                StringJoiner sortJoiner = new StringJoiner(","," ORDER BY "," ");
                StringBuilder sortStringBuilder = new StringBuilder();
                sort.forEach(new BiConsumer<String, SortMode>() {
                    @Override
                    public void accept(String s, SortMode sortMode) {
                        sortStringBuilder.setLength(0); // 清空StringBuilder，准备下一次拼接
                        sortStringBuilder.append(s).append(" ").append(sortMode);
                        sortJoiner.add(sortStringBuilder);
                    }
                });
                //拼接排序规则
                sqlStringBuilder.append(sortJoiner);
            }
        }

        //拼接分页规则
        sqlStringBuilder.append(" LIMIT ").append(pageInfo.getPage()*pageInfo.getPageSize()).append(",").append(pageInfo.getPageSize());

        return sqlStringBuilder.toString();
    }

    /*
     * 解析fieldRule.RuleItem/RuleItems
     * 后续如果要 添加/解析 新的FieldRule 该方法可以优化为责任链
     * */
    private String analysisRuleItem(AbstractFieldRule fieldRule, List<ParameterMapping> parameterMappings, Map<String,Object> additionalParameters, Configuration configuration) {
        StringJoiner joiner = new StringJoiner(" OR "," WHERE "," ");
        if (fieldRule instanceof SimpleFieldRule){
            SimpleFieldRule fieldRule1 = (SimpleFieldRule) fieldRule;
            if(fieldRule1.getRuleItem() == null) return null;
            String s = toSql(fieldRule1.getRuleItem(), parameterMappings, additionalParameters, configuration);
            joiner.add(s);
        } else if (fieldRule instanceof MoreFieldRule) {
            MoreFieldRule fieldRule1 = (MoreFieldRule) fieldRule;
            if(fieldRule1.getRuleItems() == null) return null;
            for (RuleItem ruleItem : fieldRule1.getRuleItems()) {
                joiner.add(
                        toSql(ruleItem,parameterMappings,additionalParameters,configuration)
                );
            }
        }
        return joiner.toString();
    }

    //解析ruleItem中的Condition,生成sql、参数映射表
    private String toSql(RuleItem ruleItem, List<ParameterMapping> parameterMappings, Map<String,Object> additionalParameters, Configuration configuration){
        StringJoiner joiner = new StringJoiner(" and ","(",")");
        for (Condition<?> condition : ruleItem) {
            PrecompiledSql precompiledSql = condition.toSql(Config.getConverter());

            precompiledSql.getParameterMappings().forEach(new BiConsumer<String, Class<?>>() {
                @Override
                public void accept(String value, Class<?> aClass) {
                    parameterMappings.add(new ParameterMapping.Builder(configuration, value, Object.class).build());
                    additionalParameters.put(value, value);
                }
            });
            joiner.add(precompiledSql.getSql());
        }
        return joiner.toString();
    }
}
