package com.jfinal.plugin.activerecord;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;

@Slf4j
public class SqlParamLogProxy implements InvocationHandler {

    private PreparedStatement ps;
    private long start;
    private static boolean logOn = false;

    public SqlParamLogProxy(PreparedStatement ps) {
        this.ps = ps;
        start = System.currentTimeMillis();
    }


    public static void setLog(boolean on) {
        SqlParamLogProxy.logOn = on;
    }

    @SuppressWarnings("rawtypes")
    public PreparedStatement prepareStatement() {
        Class clazz = ps.getClass();
        return (PreparedStatement) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{PreparedStatement.class}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if(!SqlLog.isDisableLog()){
                if (method.getName().startsWith("set") && args.length>1) {
                    String info = "第" + args[0] + "个参数: " + args[1] + "\t";
                    log.debug(info);
                } else if (method.getName().equals("close")) {
                    log.debug("执行时间: " + (System.currentTimeMillis() - start) + "ms\n\n");
                }
            }
            return method.invoke(ps, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}