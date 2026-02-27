package com.ruoyi.web.advice;

import cn.hutool.crypto.SecureUtil;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.common.utils.sign.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@ControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private static final Class<?>[] ENCRYPTED_TYPES = {AjaxResult.class, TableDataInfo.class};


    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (!isRestRequest(returnType)) {
            return false;
        }

        // 只有当返回类型是 AjaxResult 或 TableDataInfo 时才处理
        Class<?> rawType = getRawClass(returnType.getParameterType());
        for (Class<?> encryptedType : ENCRYPTED_TYPES) {
            if (encryptedType.isAssignableFrom(rawType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        if (body == null) {
            return body;
        }

        try {
            // 再次确认类型（防止泛型擦除等问题）
            boolean shouldEncrypt = false;
            for (Class<?> type : ENCRYPTED_TYPES) {
                if (type.isInstance(body)) {
                    shouldEncrypt = true;
                    break;
                }
            }
            if (!shouldEncrypt) {
                return body; // 不加密，直接返回
            }
            // 序列化为 JSON 字符串
            String json = JacksonUtil.toJson(body);
            return SecureUtil.aes(Base64.decode("jKvyoygIzfoMpkxGkyD3kA==")).encryptBase64( json);
        } catch (Exception e) {
            log.error("响应加密失败", e);
            throw new ServiceException("响应加密失败");
        }
    }

    /**
     * 获取原始类（处理代理、泛型等，简化版）
     */
    private Class<?> getRawClass(Class<?> clazz) {
        if (clazz.getName().contains("$$")) {
            // 处理 CGLIB 代理类（如 @Transactional 产生的代理）
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                return superClass;
            }
        }
        return clazz;
    }

    /**
     * 判断是否是 RESTful JSON 请求
     */
    private boolean isRestRequest(MethodParameter returnType) {
        // 方式1: 检查方法或类是否有 @ResponseBody / @RestController
        if (returnType.hasMethodAnnotation(ResponseBody.class)) {
            return true;
        }
        if (returnType.getContainingClass().isAnnotationPresent(RestController.class)) {
            return true;
        }

        return false;
    }
}