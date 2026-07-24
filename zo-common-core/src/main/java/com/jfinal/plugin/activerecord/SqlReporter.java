package com.jfinal.plugin.activerecord;

import com.jfinal.plugin.activerecord.dialect.Dialect;
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
    private Dialect dialect;
    /**
     * 是否开启加密
     */
    private boolean enableEncrypt;
    /**
     * configName
     * 通过xyzr_开头，为resource资源ID
     */
    private String configName;
    private static boolean logOn = false;

    SqlReporter(Connection conn, Dialect dialect, boolean enableEncrypt, String configName) {
        this.conn = conn;
        this.dialect = dialect;
        this.enableEncrypt = enableEncrypt;
        this.configName = configName;
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
                return new PreparedStatementLogProxy(((PreparedStatement) method.invoke(conn,args)),(String)args[0]).prepareStatement();
            }
            return method.invoke(conn, args);

        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
