package com.jfinal.plugin.activerecord;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;

@Slf4j
public class SqlReporter implements InvocationHandler {

    private Connection conn;
    private static boolean logOn = false;

    SqlReporter(Connection conn) {
        this.conn = conn;
    }

    public static void setLog(boolean on) {
        SqlReporter.logOn = on;
    }

    @SuppressWarnings("rawtypes")
    Connection getConnection() {
        Class clazz = conn.getClass();
        return (Connection) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{Connection.class}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (method.getName().equals("prepareStatement")) {
                if(!SqlLog.isDisableLog()){
                    String info = "Sql: " + args[0];
                    log.debug(info);
                }
                return new SqlParamLogProxy(((PreparedStatement) method.invoke(conn, args))).prepareStatement();
            }
            return method.invoke(conn, args);

        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}

