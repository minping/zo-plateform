package com.zo.platform.starter.session.ignore;

/**
 * @author zhoump
 * @date 2026/11/10
 * @purpose
 */
public interface ITokenIgnore {

    /**
     * token忽略
     * @param handlerInfo
     * @return
     */
    Boolean ignore(HandlerInfo handlerInfo);
}
