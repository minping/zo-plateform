package com.jfinal.plugin.activerecord.exception;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public enum Code {
    SLAVE_SIZE_COMP_WEIGHT_SIZE_ERROR(23001, "从库数量与权重配置数量不一致"),
    UPDATE_COLUMN_SET_PARSE_ERROR(23002,"SQL SET 参数解析异常"),
    UPDATE_COLUMN_WHERE_PARSE_ERROR(23003,"SQL WHERE 参数解析异常"),
    UN_SUPPORT_DATABASE(23004,"未支持的数据库类型"),
    DRIVER_CLASSLOADER_ERROR(23005,"驱动加载异常");


    private int code;
    private String msg;

    Code(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public void doThrow() {
        throw DBException.create(this);
    }
}
