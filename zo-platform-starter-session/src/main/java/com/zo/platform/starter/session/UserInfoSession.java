package com.zo.platform.starter.session;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhoump
 * @date 2026/11/12
 * @purpose
 */
public class UserInfoSession {

    public static final String USER_KEY = "user_key";
    public static final String USER_ID_KEY = "user_id";
    public static final String USER_NAME_KEY = "user_name";

    ISessionHolder sessionHolder;

    @Autowired
    public void setSessionHolder(ISessionHolder sessionHolder) {
        this.sessionHolder = sessionHolder;
    }

    public String getUserId(){
        return (String) get(USER_ID_KEY);
    }

    public String getUserName(){
        return (String) get(USER_NAME_KEY);
    }

    public Object getUser(){
        return get(USER_KEY);
    }

    public void setUserId(String userId){
        put(USER_ID_KEY,userId);
    }

    public void setUserName(String userName){
        put(USER_NAME_KEY,userName);
    }

    public void setUser(Object user){
        put(USER_KEY,user);
    }

    public Object get(String key) {
        return sessionHolder.getAttribute(key);
    }

    public void put(String key, Object data) {
        if (data == null) {
            return;
        }
        sessionHolder.setAttribute(key, data);
    }
}
