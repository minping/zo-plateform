package com.zo.platform.starter.exception;

import com.zo.platform.sdk.common.Result;

/**
 * @author zhoump
 * @date 2026/11/4
 * @purpose
 */
public class RedisOperationException extends RuntimeException {

    private Result result = Result.me().error();

    public RedisOperationException() {
    }

    public RedisOperationException(String message) {
        super(message);
        this.setMessage(message);
    }

    public RedisOperationException(String message,Exception e) {
        super(message.concat(":")+e.getMessage());
        this.setMessage(message.concat(":")+e.getMessage());
    }

    public static ZoException create(int state, String message) {
        return new ZoException(message).setState(state);
    }


    public Result error() {
        return result;
    }

    public RedisOperationException setData(Object data) {
        error().setData(data);
        return this;
    }

    public RedisOperationException setMessage(String msg) {
        error().setMessage(msg);

        return this;
    }

    public RedisOperationException setState(int state) {
        error().setState(state);
        return this;
    }

}
