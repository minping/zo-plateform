package com.jfinal.plugin.activerecord.dialect;

import com.alibaba.druid.DbType;
import com.jfinal.plugin.druid.DruidPlugin;
import org.springframework.core.Ordered;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public interface IMatchDatabase extends Ordered {

    /**
     * 是否匹配
     * @param druidPlugin
     * @return
     */
    boolean match(DruidPlugin druidPlugin);

    /**
     * 获取数据库dbType
     * @return
     */
    DbType getDbType();

    /**
     * 对比排序码，值越小优先级越高
     * @author wenjin
     * @date 2024/1/16 20:21
     * @return int
     */
    @Override
    default int getOrder() {
        return Integer.MAX_VALUE;
    }
}
