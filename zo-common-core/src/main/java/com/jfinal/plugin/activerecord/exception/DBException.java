package com.jfinal.plugin.activerecord.exception;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public class DBException extends ZoException {
    private Code code;


    private DBException(String message) {
        super(message);
    }

    public Code getCode() {
        return code;
    }

    public static DBException create(Code code) {
        DBException DBException = (DBException) new DBException(code.getMsg()).setState(code.getCode());
        DBException.code = code;
        return DBException;
    }
}
