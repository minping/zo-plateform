package com.jfinal.plugin.activerecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;

public class SqlParamLogProxy implements InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(SqlParamLogProxy.class);
    private PreparedStatement ps;
    private long start;
    private static boolean logOn = false;

    public SqlParamLogProxy(PreparedStatement ps) {
        this.ps = ps;
        this.start = System.currentTimeMillis();
    }

    public static void setLog(boolean on) {
        logOn = on;
    }

    public PreparedStatement prepareStatement() {
        Class clazz = this.ps.getClass();
        return (PreparedStatement) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{PreparedStatement.class}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (method.getName().equals("setObject")) {
                String info = "第" + args[0] + "个参数: " + args[1] + "\t";
                log.debug(info);
            } else if (method.getName().equals("close")) {
                log.debug("执行时间: " + (System.currentTimeMillis() - this.start) + "ms");
            }

            return method.invoke(this.ps, args);
        } catch (InvocationTargetException var5) {
            throw var5.getTargetException();
        }
    }
}