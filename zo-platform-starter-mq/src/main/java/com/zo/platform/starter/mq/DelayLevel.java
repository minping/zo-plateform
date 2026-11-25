package com.zo.platform.starter.mq;

/**
 * @author zhoump
 * @date 2026/11/25
 * 延迟等级
 * 默认等级 messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
 */
public enum DelayLevel {
    /**
     * 不延迟
     */
    _0(0),

    /**
     * 默认延迟1s
     */
    _1(1),

    /**
     * 默认延迟5s
     */
    _2(2),

    /**
     * 默认延迟10s
     */
    _3(3),

    /**
     * 默认延迟30s
     */
    _4(4),

    /**
     * 默认延迟1m
     */
    _5(5),

    /**
     * 默认延迟2m
     */
    _6(6),

    /**
     * 默认延迟3m
     */
    _7(7),

    /**
     * 默认延迟4m
     */
    _8(8),

    /**
     * 默认延迟5m
     */
    _9(9),

    /**
     * 默认延迟6m
     */
    _10(10),

    /**
     * 默认延迟7m
     */
    _11(11),

    /**
     * 默认延迟8m
     */
    _12(12),

    /**
     * 默认延迟9m
     */
    _13(13),

    /**
     * 默认延迟10m
     */
    _14(14),

    /**
     * 默认延迟20m
     */
    _15(15),

    /**
     * 默认延迟30m
     */
    _16(16),

    /**
     * 默认延迟1h
     */
    _17(17),

    /**
     * 默认延迟2h
     */
    _18(18);

    int level;

    DelayLevel (int level) {
        this.level = level;
    }

    public int value() {
        return this.level;
    }
}
