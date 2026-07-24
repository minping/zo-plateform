package com.jfinal.plugin.activerecord.lb;


import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 加权随机负载
 * @author wenjin
 * @date 2023/10/16 16:09
 */
@Slf4j
public class RandomLoadBalancer extends AbstractLoadBalancer{


    @Override
    public Connection getConnection(ExecutionInfo info)  throws SQLException {
        int[] rangeWeightIndex = getRangeWeightIndex();
        int offset = ThreadLocalRandom.current().nextInt(rangeWeightIndex[rangeWeightIndex.length-1])+1;
        for (int i = 0; i < rangeWeightIndex.length; i++) {
            int min = 1;
            int max = rangeWeightIndex[i];
            if(i>0){
                min = rangeWeightIndex[i-1]+1;
            }
            if(min<=offset && offset<=max){
                return getSlaveConfigList().get(i).getConnection();
            }
        }
        //以下代码永远都不会执行
        log.error("##############RandomLoadBalancer arithmetic exception#################");
        return getSlaveConfigList()
                .get(ThreadLocalRandom.current().nextInt(rangeWeightIndex.length))
                .getConnection();
    }


}
