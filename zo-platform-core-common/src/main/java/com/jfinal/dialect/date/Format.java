package com.jfinal.dialect.date;

import cn.hutool.core.util.ReUtil;

import java.util.List;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public class Format {

    public static String analysis(List<Patten> pattenList, String source){
        for (int i = 0; i < pattenList.size(); i++) {
            Patten patten = pattenList.get(i);
            source = ReUtil.replaceAll(source,patten.getSource(),patten.getConvert());
        }
        return source;
    }

}
