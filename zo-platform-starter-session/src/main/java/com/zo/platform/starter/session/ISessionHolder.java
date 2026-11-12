package com.zo.platform.starter.session;

/**
 * @author zhoump
 * @date 2026/11/9
 * @purpose
 */
public interface ISessionHolder<M> {


    /**
     * 获取信息
     *
     * @param key
     * @return
     */
    Object getAttribute(String key);

    /**
     * 获取信息
     *
     * @param key
     * @param defaultValue
     * @return
     */
    Object getAttribute(String key, Object defaultValue);

    /**
     * 存放信息
     *
     * @param key
     * @param value
     * @return
     */
    M setAttribute(String key, Object value);

    /**
     * 移除key
     *
     * @param key
     * @return
     */
    M removeAttribute(String key);

    /**
     * 清空所有session数据
     *
     * @return
     */
    M clear();

    /**
     * 清空指定用户session数据
     * @param userId
     * @return
     */
    M clear(String userId);

    /**
     * 线程结束后，释放资源
     * @return
     */
    M release();

    /**
     * 创建新的token
     *
     * @param userId 用户唯一标识
     * @return
     */
    String newToken(String userId);

    /**
     * 获取token
     *
     * @return
     */
    String getToken();
}
