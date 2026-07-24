package com.jfinal.plugin.activerecord.lb;

import java.sql.Connection;

/**
 * hash负载
 * @author wenjin
 * @date 2023/10/16 16:09
 */
public class HashLoadBalancer extends AbstractLoadBalancer{


    @Override
    public Connection getConnection(ExecutionInfo info) {
        return null;

    }


}