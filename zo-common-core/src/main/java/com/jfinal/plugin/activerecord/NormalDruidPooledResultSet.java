package com.jfinal.plugin.activerecord;

import java.sql.ResultSet;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public class NormalDruidPooledResultSet extends BaseDruidPooledResultSet{

    public NormalDruidPooledResultSet(ResultSet rs, String sql) {
        super(rs,sql);
    }
}
