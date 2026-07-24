package com.jfinal.plugin.activerecord;


import com.common.util.StrKit;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * sql 参数输出代理类
 *
 * @author wenjin
 */
@Slf4j
public class PreparedStatementLogProxy implements InvocationHandler {

    private PreparedStatement ps;
    private long start;
    private static boolean logOn = false;
    private String sql;

    public PreparedStatementLogProxy(PreparedStatement ps,String sql) {
        this.ps = ps;
        start = System.currentTimeMillis();
        this.sql = sql;
    }


    public static void setLog(boolean on) {
        PreparedStatementLogProxy.logOn = on;
    }

    @SuppressWarnings("rawtypes")
    public PreparedStatement prepareStatement() {
        Class clazz = ps.getClass();
        return (PreparedStatement) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{PreparedStatement.class}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            String methodName = method.getName();
            if(!SqlLog.isDisableLog()){
                if (methodName.startsWith("set") && args.length>1) {
                    String info = "第" + args[0] + "个参数: " + args[1] + "\t";
                    log.debug(info);
                } else if (methodName.equals("close")) {
                    log.debug("执行时间: " + (System.currentTimeMillis() - start) + "ms\n\n");
                }
            }

            Object invoke = method.invoke(ps, args);
            if(StrKit.equals(methodName,"executeQuery")){
                invoke = new NormalDruidPooledResultSet((ResultSet) invoke,this.sql);
            }
            return invoke;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
