package com.zo.platform.starter.console.runner;

import com.zo.platform.starter.console.constant.ConsoleConstant;
import com.zo.platform.starter.console.version.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.PriorityOrdered;

import java.util.Map;

/**
 * @author zhoump
 * @date 2026/11/6
 * @purpose
 */
public class ConsoleApplicationRunner implements ApplicationRunner, PriorityOrdered {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<String, Version> versionMap = applicationContext.getBeansOfType(Version.class);
        if (versionMap != null) {
            versionMap.forEach((str, version) -> System.out.println(version.toString()));
        }
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return ConsoleConstant.RUNNER_ORDER;
    }
}
