package com.common.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author zhoump
 * @date 2026/5/18
 * @purpose
 */
@Getter
public class WebProperties {
    /** 错误静态页面路径 */
    @Value("${dsfa.web.errorUrl:/errors/500}")
    String errorUrl;
}
