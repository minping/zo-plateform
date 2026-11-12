package com.zo.platform.starter.session.jwt;

import com.zo.platform.sdk.common.kit.StrKit;
import com.zo.platform.starter.session.AuthIgnore;
import com.zo.platform.starter.session.config.SessionConfig;
import com.zo.platform.starter.session.constant.SessionConstant;
import com.zo.platform.starter.session.ignore.HandlerInfo;
import com.zo.platform.starter.session.ignore.ITokenIgnore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author zhoump
 * @date 2026/11/10
 * @purpose
 */
@Slf4j
@Order(SessionConstant.INTERCEPTOR_ORDER)
public class JwtInterceptor implements HandlerInterceptor {
    JwtInfoSessionHolder jwtInfoSessionHolder;

    SessionConfig sessionConfig;

    ITokenIgnore tokenIgnore;

    private final static String DATA_VIEW_FILTER_KEY = "view";

    private final static String DATA_VIEW_FILTER_VALUE = "view_key";

    @Autowired
    public void setJwtInfoSessionHolder(JwtInfoSessionHolder jwtInfoSessionHolder) {
        this.jwtInfoSessionHolder = jwtInfoSessionHolder;
    }

    @Autowired
    public void setSessionConfig(SessionConfig sessionConfig) {
        this.sessionConfig = sessionConfig;
    }

    @Autowired
    public void setTokenIgnore(ObjectProvider<ITokenIgnore> tokenIgnoreProvider) {
        this.tokenIgnore = tokenIgnoreProvider.getIfAvailable();
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       log.info("handler类型：{}", handler.getClass().getName());
        // 忽略jwt验证
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 错误控制器直接放行
        if (handlerMethod.getBean() instanceof org.springframework.boot.web.servlet.error.ErrorController) {
            return true;
        }
        if (sessionConfig.isAuthIgnore()) {
            //全局忽略验证
            return true;
        }
        String header = request.getHeader(DATA_VIEW_FILTER_KEY);
        if (StrKit.equals(DATA_VIEW_FILTER_VALUE, header)) {
            return true;
        }

        //拿出url的白名单
        List<String> urlWhiteList = sessionConfig.getUrlWhiteList();
        for (String url : urlWhiteList) {
            //如果在白名单中直接放行
            if (request.getRequestURI().endsWith(url)) {
                return true;
            }
        }

        AuthIgnore authIgnore = AnnotationUtils.findAnnotation(
                handlerMethod.getMethod(), AuthIgnore.class);
        if (authIgnore == null) {
            authIgnore = AnnotationUtils.findAnnotation(
                    handlerMethod.getBeanType(), AuthIgnore.class);
        }
        if (authIgnore != null) {
            //优先级 method > class
            return true;
        }

        JwtBuildResult buildResult = (JwtBuildResult) request.getAttribute(SessionConstant.JWT_RESULT);
        String token = buildResult.getToken();
        HandlerInfo handlerInfo = new HandlerInfo(request, response, handlerMethod, token);

        if (tokenIgnore != null) {
            if (tokenIgnore.ignore(handlerInfo)) {
                //token验证忽略
                return true;
            }
        }

        if (buildResult.getJwtException() != null) {
            //没有忽略的token操作，判断是否token解析报错，解决token关闭浏览器未自动销毁的cookie.
            throw buildResult.getJwtException();
        }


        if (StrKit.isBlank(token)) {
            //没有找到token信息
            throw JwtException.create(JwtCode.NO_TOKEN);
        }
        return true;
    }
}
