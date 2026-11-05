package com.jfinal.plugin.activerecord.lb;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 轮询负载
 * @author wenjin
 * @date 2023/10/16 16:09
 */
public class RollPollingLoadBalancer extends AbstractLoadBalancer{


    @Override
    public Connection getConnection(ExecutionInfo info) throws SQLException {
        return getSlaveConfigList().get(0).getConnection();
    }


}
