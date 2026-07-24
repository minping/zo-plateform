package com.jfinal.plugin.constant;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public class DBConstant {

    public static final String VERSION_NAME = "dsfa-platform-starter-db";
    public static final String VERSION_CODE = "1.0.0";

    public static final int RUNNER_ORDER = 50;

    public static final String DATA_SOURCE_BEAN_SUFFIX = "DataSource";

    public static final String DEFAULT_DRIVER_CLASS_LOADER = "com.dsfa.platform.starter.db.jfinal.plugin.activerecord.driver.JarClassLoader";

    /**
     * 查询输出列 模式
     */
    public static final class DBColumnMapModel{
        /**
         * original 原样输出,与sql中定义的大小写保持一致
         */
        public static final String ORIGINAL = "original";

        /**
         * 全大写
         */
        public static final String UPPER = "upper";
        /**
         * 全小写
         */
        public static final String LOWER = "lower";
        /**
         * 根据数据库特性输出
         */
        public static final String DB = "db";
    }

    /**
     * 方言排序
     */
    public static final class DialectOrder{
        public static final int HUDI_ORDER = 10;
        public static final int STAR_ROCKS_ORDER = 20;
        public static final int ANSI_ORDER = 30;
        public static final int CK_ORDER = 40;
        public static final int DERBY_ORDER = 50;
        public static final int DM_ORDER = 60;
        public static final int GAUSS_ORDER = 70;
        public static final int HANA_ORDER = 80;
        public static final int HG_ORDER = 90;
        public static final int HIVE_ORDER = 100;
        public static final int KINGBASE_ORDER = 110;
        public static final int MYSQL_ORDER = 120;
        public static final int ORACLE_ORDER = 130;
        public static final int PG_ORDER = 140;
        public static final int ST_ORDER = 150;
        public static final int SQLITE3_ORDER = 160;
        public static final int SQLSERVER_ORDER = 170;
        public static final int VB_ORDER = 180;
        public static final int XH_ORDER = 190;
        public static final int XUGU_ORDER = 200;
        public static final int UX_ORDER = 210;
        public static final int SINO_ORDER = 220;
        public static final int POLAR_PG_ORDER = 230;
    }
}