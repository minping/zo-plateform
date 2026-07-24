package com.jfinal.plugin.activerecord.exception;

import com.common.util.Result;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public class ZoException  extends RuntimeException {

    private Result result = Result.me().error();

    public ZoException() {

    }

    public ZoException(String message) {
        super(message);
        this.setMessage(message);
    }

    public static ZoException create(int state, String message) {
        return new ZoException(message).setState(state);
    }


    public Result error() {
        return result;
    }

    public ZoException setData(Object data) {
        error().setData(data);
        return this;
    }

    public ZoException setMessage(String msg) {
        error().setMessage(msg);

        return this;
    }

    public ZoException setState(int state) {
        error().setState(state);
        return this;
    }


}
