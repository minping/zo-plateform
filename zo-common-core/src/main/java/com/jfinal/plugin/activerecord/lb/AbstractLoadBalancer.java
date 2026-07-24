package com.jfinal.plugin.activerecord.lb;

import com.jfinal.plugin.activerecord.Config;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */

public abstract class AbstractLoadBalancer {

    /**
     * 主库配置信息
     */
    @Getter
    @Setter
    Config mastConfig;

    /**
     * 从库配置信息
     */
    @Getter
    @Setter
    List<Config> slaveConfigList;

    @Getter
    @Setter
    SlaveProperties slaveProperties;

    /**
     * 权重范围
     * 例：weight = 10,20,30
     * 则：rangeWeightIndex = [1~10,11~30,31~60],  解释 rangeWeightIndex = [10,10+20,10+20+30]
     */
    @Getter
    int[] rangeWeightIndex;


    /**
     * 初始化
     * 计算权重分布
     */
    public void init(){
        initRangeWeight();
    }


    /**
     * 计算权重范围
     */
    private void initRangeWeight(){
        rangeWeightIndex = new int[slaveConfigList.size()];
        Integer[] slavePropertiesWeight = slaveProperties.getWeight();
        if(slavePropertiesWeight!=null){
            for (int i = 0; i < slavePropertiesWeight.length; i++) {
                if(i==0){
                    rangeWeightIndex[i] = slavePropertiesWeight[i];
                }else{
                    rangeWeightIndex[i] = rangeWeightIndex[i-1]+slavePropertiesWeight[i];
                }
            }
        }else{
            //默认权重一致
            for (int i = 0; i < slaveConfigList.size(); i++) {
                rangeWeightIndex[i] = (i==0 ? 10 : rangeWeightIndex[i-1]+10 );
            }
        }
    }

    /**
     * 1、判断是否有事务
     * 存在事务，走主事务，否则走 从库连接负载策略
     * 2、记录日志信息
     * @param info
     * @return
     * @throws SQLException
     */
    public Connection getCommonConnection(ExecutionInfo info) throws SQLException{
        if(mastConfig.isInTransaction()){
            //主库存在事务
            return mastConfig.getConnection();
        }
        return this.getConnection(info);
    }

    /**
     * 获取链接信息
     * @param info  执行信息
     */
    public abstract Connection getConnection(ExecutionInfo info) throws SQLException;

}
