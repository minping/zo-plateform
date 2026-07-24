package com.jfinal.plugin.activerecord;

import com.jfinal.kit.SyncWriteMap;

import java.util.Map;

/**
 * @author zhoump
 * @date 2026/6/24
 * @purpose
 */
public interface IDbProFactory {

    Map<String, DbPro> DB_PRO_CACHE_MAP = new SyncWriteMap<>(32, 0.25F);

    /**
     * 获取DbPro
     * @param configName
     * @return
     */
    DbPro getDbPro(String configName);

    /**
     * 移除config
     * @param configName
     */
    default void remove(String configName){
        DB_PRO_CACHE_MAP.remove(configName);
    }

    IDbProFactory defaultDbProFactory = (configName) -> {
        DbPro result = DB_PRO_CACHE_MAP.get(configName);
        if(result == null){
            Config config = DbKit.getConfig(configName);
            if (config == null) {
                throw new IllegalArgumentException("Config not found by configName: " + configName);
            }
            result = new DbPro(configName);
            DB_PRO_CACHE_MAP.put(configName, result);
        }
        return result;
    };
}
