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

        StringJoiner joiner = new StringJoiner(" and "," where "," ");

        if (!pageInfo.getIsCheckField()) throw new RuntimeException("pageInfo 中未进行过字段检测 ，请运行PageInfo.checkFieldRule(); \n在执行sql前 你应该先执行一次PageInfo.checkFieldRule()");

        FieldRule fieldRule = pageInfo.getFieldRule();
        if (fieldRule instanceof SimpleFieldRule){
            SimpleFieldRule fieldRule1 = (SimpleFieldRule) fieldRule;
            toSql(joiner,fieldRule1.getRuleItem(),parameterMappings,additionalParameters,configuration);

        } else if (fieldRule instanceof MoreFieldRule) {
            MoreFieldRule fieldRule1 = (MoreFieldRule) fieldRule;
            for (RuleItem ruleItem : fieldRule1.getRuleItems()) {
                toSql(joiner,ruleItem,parameterMappings,additionalParameters,configuration);
            }
        }
        return joiner.toString();
    }

    //解析ruleItem中的Condition,生成sql、参数映射表，并添加到joiner中
    private void toSql(StringJoiner joiner, RuleItem ruleItem, List<ParameterMapping> parameterMappings, Map<String,Object> additionalParameters, Configuration configuration){
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
    }
}
