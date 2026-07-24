package com.jfinal.plugin.activerecord.lb;

import com.jfinal.plugin.activerecord.Config;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
@Getter
@Setter
@NoArgsConstructor
public class SlaveConfig {


    /**
     * 主库配置信息
     */
    private Config masterConfig;

    /**
     * 从库配置信息
     */
    private List<Config> slaveConfigList;
    /**
     * 负载策略
     */
    private LoadBalancerStrategy strategy;

    private AbstractLoadBalancer loadBalancer;

    /**
     * 配置中心 配置信息
     */
    private SlaveProperties slaveProperties;


    /**
     * 获取从库连接
     * @return
     * @throws SQLException
     */
    public Connection getConnection(String sql) throws SQLException {
        //是否存在事务
        ExecutionInfo executionInfo = new ExecutionInfo();
        executionInfo.setSql(sql);
        return loadBalancer().getCommonConnection(executionInfo);
    }

    /**
     * 获取负载策略
     * @return
     */
    private AbstractLoadBalancer loadBalancer(){
        if(loadBalancer==null){
            synchronized (SlaveConfig.class){
                if(loadBalancer==null){
                    switch (strategy){
                        case RANDOM:
                            loadBalancer = new RandomLoadBalancer();
                            break;
                        case HASH:
                            loadBalancer = new HashLoadBalancer();
                            break;
                        case ROLL_POLLING:
                        default:
                            //默认走轮询策略，之后补充支持自定义策略二开方式。
                            loadBalancer = new RollPollingLoadBalancer();
                    }
                    loadBalancer.setSlaveConfigList(this.slaveConfigList);
                    loadBalancer.setMastConfig(masterConfig);
                    loadBalancer.setSlaveProperties(slaveProperties);
                    loadBalancer.init();
                }
            }
        }
        return loadBalancer;
    }


}