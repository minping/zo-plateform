package com.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhoump
 * @date 2026/5/18
 * @purpose
 */
public class ZoLog {

    private static final Logger logger = LoggerFactory.getLogger(ZoLog.class.getSimpleName());

    public ZoLog() {
    }

    public static void info(String msg, Object... args) {
        logger.info(msg, args);
    }

    public static void debug(String msg, Object... args) {
        logger.debug(msg, args);
    }

    public static void error(String msg, Object... args) {
        logger.error(msg, args);
    }
}
