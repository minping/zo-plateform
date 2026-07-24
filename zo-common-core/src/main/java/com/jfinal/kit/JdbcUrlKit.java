package com.jfinal.kit;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public class JdbcUrlKit {

    public static String filterUrlParams(String url, String... paramKeys) {
        if (!url.contains("?")) {
            return url;
        }
        String[] split = url.split("\\?");
        // 过滤参数
        List<String> filterKeys = Arrays.asList(paramKeys);
        List<String> paramList = Arrays.stream(split[1].split("&"))
                .filter(s -> !filterKeys.contains(s.split("=")[0]))
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder(split[0]);
        for (int i = 0; i < paramList.size(); i++) {
            String param = paramList.get(i);
            if (i == 0) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            sb.append(param);
        }
        return sb.toString();
    }


    /**
     * 截取jdbc url 参数
     * @param url
     * @param name
     * @return
     */
    public static String getParamByUrl(String url, String name) {
        String matchUrl = url+ "&";
        String pattern = "(\\?|&){1}#{0,1}" + name + "=[a-zA-Z0-9\\.]*(&{1})";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(matchUrl);
        if (m.find()) {
            return m.group(0).split("=")[1].replace("&", "");
        } else {
            return null;
        }
    }
}
