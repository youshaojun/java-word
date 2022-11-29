package com.xm.word.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.nio.charset.Charset;

/**
 * @author yousj
 * @since 2022-06-06
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XssUtil {

    public static String parserUrl(String url) {
        if (StrUtil.isBlank(url)) {
            return "";
        }
        try {
            URI.create(url);
        } catch (Exception ex) {
            return HttpUtil.encodeParams(url, Charset.defaultCharset());
        }
        return url;
    }

}
