package com.common.views;

import com.common.configuration.SpringContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.Map;

/**
 * @author zhoump
 * @date 2026/5/18
 * @purpose
 */
public class Mv extends ModelAndView {

    public static Mv create() {
        Mv mv = new Mv();
        //添加request、session对象
        mv.fluentAddObject("request", SpringContextHolder.request());
        //                .fluentAddObject("session", SpringContextHolder.sessionHolder());

        return mv;
    }

    private Mv() {
        super();
    }

    public Mv fluentSetViewName(String viewName) {
        super.setViewName(viewName);
        return this;
    }

    public Mv fluentSetView(View view) {
        super.setView(view);
        return this;
    }

    public Mv fluentSetStatus(HttpStatus status) {
        super.setStatus(status);
        return this;
    }

    public Mv fluentAddObject(String attributeName, Object attributeValue) {
        super.addObject(attributeName, attributeValue);
        return this;
    }

    public Mv fluentAddObject(Object attributeValue) {
        super.addObject(attributeValue);
        return this;
    }

    public Mv fluentAddAllObjects(Map<String, ?> modelMap) {
        super.addAllObjects(modelMap);
        return this;
    }
}
