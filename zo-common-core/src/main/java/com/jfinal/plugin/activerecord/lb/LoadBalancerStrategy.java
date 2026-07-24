package com.jfinal.plugin.activerecord.lb;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public enum LoadBalancerStrategy {

    /**
     * 随机
     */
    RANDOM,
    /**
     * 轮询
     */
    ROLL_POLLING,
    /**
     * hash值
     */
    HASH;
}
