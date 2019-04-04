package com.github.dadiyang.jvmsandbox.module.util;

import com.alibaba.fastjson.JSON;

/**
 * 摘要工具类
 *
 * @author dadiyang
 * date 2018/11/23
 */
public class SummaryUtil {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final int MAX_STRING_LENGTH = 150;

    /**
     * 将对象序列化后取摘要
     *
     * @param obj     需要被摘要的类
     * @param fullMsg 是否使用全信息
     * @return 摘要
     */
    public static String summary(Object obj, boolean fullMsg) {
        String argsString = JSON.toJSONStringWithDateFormat(obj, DATE_FORMAT);
        if (fullMsg) {
            return argsString;
        }
        if (argsString.length() > MAX_STRING_LENGTH) {
            // 参数的简单摘要
            argsString = argsString.substring(0, MAX_STRING_LENGTH) + "...";
        }
        return argsString;
    }


}
