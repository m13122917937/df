package com.ruoyi.jky.util;

import com.ruoyi.jky.model.JkyResponse;

import java.util.Objects;

/**
 * 吉客云 API 响应校验工具。
 */
public class JkyResponseUtil {

    /**
     * 判断 Jky API 响应是否成功（code == 200 且 result 非空）。
     */
    public static boolean isSuccess(JkyResponse<?> response) {
        return response != null && Objects.equals(response.getCode(), 200);
    }

    /**
     * 安全提取 Jky API 响应中的数据（data 部分）。
     */
    public static <T> T getData(JkyResponse<T> response) {
        if (response == null || response.getResult() == null) {
            return null;
        }
        return response.getResult().getData();
    }

    private JkyResponseUtil() {
    }
}
