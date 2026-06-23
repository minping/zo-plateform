package com.common.filter;

import com.common.util.StrKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author zhoump
 * @date 2026/6/23
 * @purpose
 */
@Slf4j
public class LogFilter extends OncePerRequestFilter {
    private static final int MAX_OUTPUT_LENGTH_OF_PARA_VALUE = 1024;

    private final ThreadLocal<Common> startThreadLocal = new ThreadLocal<>();

    /**
     * 原来的interceptor日志记录转为filter实现，实现可以拦截到spring的异常
     *
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤器链
     * @throws ServletException servlet异常
     * @throws IOException      ioexception
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uuid = StrKit.uuid();
        startThreadLocal.set(new Common(System.currentTimeMillis(), uuid));
        ThreadContext.put("_log_id", uuid);
        String methodName = request.getMethod();
        String path = request.getRequestURI();
        String queryStr = request.getQueryString();
        StringBuffer sb = new StringBuffer("----------------------\tDSF-SPRINGBOOT " + uuid + " start\t--------------------\n\n");
        sb.append("\tUrl         : ").append(request.getRequestURL().toString());
        if (StrKit.isNotBlank(queryStr)) {
            sb.append("?").append(queryStr);
        }
        sb.append("\n").append("\tPath        : ").append(path).append("\n").append("\tMethod      : " + methodName).append("\n");
        Enumeration<String> e = request.getParameterNames();
        if (e.hasMoreElements()) {
            sb.append("\tParameter   : ");
            while (e.hasMoreElements()) {
                String name = e.nextElement();
                String[] values = request.getParameterValues(name);
                if (values.length == 1) {
                    sb.append(name).append("=");
                    if (values[0] != null && values[0].length() > MAX_OUTPUT_LENGTH_OF_PARA_VALUE) {
                        sb.append(values[0], 0, MAX_OUTPUT_LENGTH_OF_PARA_VALUE).append("...");
                    } else {
                        sb.append(values[0]);
                    }
                } else {
                    sb.append(name).append("[]={");
                    for (int i = 0; i < values.length; i++) {
                        if (i > 0) {
                            sb.append(",");
                        }
                        sb.append(values[i]);
                    }
                    sb.append("}");
                }
                sb.append("  ");
            }
            sb.append("\n");
        }
        log.debug(sb.toString());

        try {
            filterChain.doFilter(request, response);
        } finally {
            Object attribute = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
            if (attribute instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) attribute;
                log.debug("\n\n\tController  : {} \n",handlerMethod);
            }
            log.debug("总执行时间" + (System.currentTimeMillis() - startThreadLocal.get().getStart()) + "ms");
            log.debug("----------------------\tDSF-SPRINGBOOT " + startThreadLocal.get().getUuid() + " end\t\t--------------------\n");
            startThreadLocal.remove();
        }
    }

    /**
     * 跳过静态资源的日志打印
     *
     * @param request 请求
     * @return boolean
     * @throws ServletException servlet异常
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return new AntPathMatcher().match("/**/*.*", request.getServletPath());
    }

    private static class Common {
        private final long start;
        private final String uuid;

        public Common(long start, String uuid) {
            this.start = start;
            this.uuid = uuid;
        }

        public long getStart() {
            return start;
        }

        public String getUuid() {
            return uuid;
        }
    }
}
