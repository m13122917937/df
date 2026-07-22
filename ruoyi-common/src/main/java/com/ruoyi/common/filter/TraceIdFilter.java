package com.ruoyi.common.filter;

import cn.hutool.core.util.IdUtil;
import org.slf4j.MDC;

import javax.servlet.*;
import java.io.IOException;

/**
 * 为每个 HTTP 请求生成 traceId 并注入 MDC，用于链路追踪。
 */
public class TraceIdFilter implements Filter {

    private static final String TRACE_ID = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            MDC.put(TRACE_ID, IdUtil.fastSimpleUUID());
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID);
        }
    }
}
