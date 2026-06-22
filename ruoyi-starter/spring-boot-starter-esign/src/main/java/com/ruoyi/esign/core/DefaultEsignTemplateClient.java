package com.ruoyi.esign.core;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.esign.api.EsignTemplateApi;
import com.ruoyi.esign.exception.EsignException;
import com.ruoyi.esign.model.template.DocTemplateInfoRequest;
import com.ruoyi.esign.model.template.DocTemplateInfoResponse;
import com.ruoyi.esign.model.template.DocTemplatesRequest;
import com.ruoyi.esign.model.template.DocTemplatesResponse;
import com.ruoyi.esign.properties.EsignProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 默认e签宝合同模板客户端实现。
 *
 * @author ruoyi
 */
public class DefaultEsignTemplateClient implements EsignTemplateApi {

    private static final Logger log = LoggerFactory.getLogger(DefaultEsignTemplateClient.class);

    private final EsignProperties properties;

    public DefaultEsignTemplateClient(EsignProperties properties) {
        this.properties = properties;
    }

    @Override
    public DocTemplatesResponse docTemplates(DocTemplatesRequest request) {
        GetUrl getUrl = buildDocTemplatesUrl(request);
        return executeGet(getUrl.requestUrl, getUrl.signatureUrl, DocTemplatesResponse.class);
    }

    @Override
    public DocTemplateInfoResponse docTemplateInfo(DocTemplateInfoRequest request) {
        if (request == null || StrUtil.isBlank(request.getDocTemplateId())) {
            throw new IllegalArgumentException("docTemplateId不能为空");
        }
        String url = properties.getDocTemplateInfoUrl(request.getDocTemplateId());
        return executeGet(url, url, DocTemplateInfoResponse.class);
    }

    private <T> T executeGet(String requestUrl, String signatureUrl, Class<T> responseClass) {
        String accept = "application/json";
        String date = formatDate();
        String pathUrl = extractPath(signatureUrl);
        String signature = generateSignature("GET", pathUrl, accept, date);

        try {
            HttpResponse response = HttpRequest.get(requestUrl)
                    .header("Accept", accept)
                    .header("Date", date)
                    .header("X-Tsign-Open-App-Id", properties.getAppId().trim())
                    .header("X-Tsign-Open-Ca-Timestamp", String.valueOf(System.currentTimeMillis()))
                    .header("X-Tsign-Open-Ca-Signature", signature)
                    .header("X-Tsign-Open-Auth-Mode", "Signature")
                    .timeout(properties.getReadTimeout().intValue())
                    .execute();
            if (!response.isOk()) {
                throw new EsignException("请求失败，请求响应：" + response.body());
            }
            T result = JacksonUtil.parse(response.body(), responseClass);
            logResponseError(result);
            return result;
        } catch (EsignException e) {
            throw e;
        } catch (Exception e) {
            log.error("e签宝合同模板API请求异常", e);
            throw new EsignException("API请求异常", e);
        }
    }

    private GetUrl buildDocTemplatesUrl(DocTemplatesRequest request) {
        UrlQuery requestQuery = new UrlQuery();
        StringBuilder signatureQuery = new StringBuilder();
        if (request != null) {
            if (request.getPageNum() != null) {
                appendQueryParam(requestQuery, signatureQuery, "pageNum", String.valueOf(request.getPageNum()));
            }
            if (request.getPageSize() != null) {
                appendQueryParam(requestQuery, signatureQuery, "pageSize", String.valueOf(request.getPageSize()));
            }
        }
        String baseUrl = properties.getDocTemplatesUrl();
        if (signatureQuery.length() == 0) {
            return new GetUrl(baseUrl, baseUrl);
        }
        String requestUrl = baseUrl + "?" + requestQuery.build(StandardCharsets.UTF_8);
        String signatureUrl = baseUrl + "?" + signatureQuery;
        return new GetUrl(requestUrl, signatureUrl);
    }

    private void appendQueryParam(UrlQuery requestQuery, StringBuilder signatureQuery, String key, String value) {
        requestQuery.add(key, value);
        if (signatureQuery.length() > 0) {
            signatureQuery.append("&");
        }
        signatureQuery.append(key).append("=").append(value);
    }

    private String formatDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date());
    }

    private String extractPath(String fullUrl) {
        int pathStart = fullUrl.indexOf("//");
        if (pathStart >= 0) {
            pathStart += 2;
            int pathEnd = fullUrl.indexOf('/', pathStart);
            if (pathEnd >= 0) {
                return fullUrl.substring(pathEnd);
            }
        }
        return fullUrl;
    }

    private String generateSignature(String httpMethod, String url, String accept, String date) {
        String message = httpMethod + "\n" + accept + "\n\n\n" + date + "\n" + url;
        return Base64.encode(SecureUtil.hmacSha256(properties.getAppSecret().trim().getBytes(StandardCharsets.UTF_8))
                .digest(message.getBytes(StandardCharsets.UTF_8)));
    }

    private void logResponseError(Object result) {
        if (result instanceof DocTemplatesResponse
                && !Integer.valueOf(0).equals(((DocTemplatesResponse) result).getCode())) {
            log.error("查询e签宝合同模板列表失败，code:{}, message:{}",
                    ((DocTemplatesResponse) result).getCode(),
                    ((DocTemplatesResponse) result).getMessage());
        } else if (result instanceof DocTemplateInfoResponse
                && !Integer.valueOf(0).equals(((DocTemplateInfoResponse) result).getCode())) {
            log.error("查询e签宝合同模板控件详情失败，code:{}, message:{}",
                    ((DocTemplateInfoResponse) result).getCode(),
                    ((DocTemplateInfoResponse) result).getMessage());
        }
    }

    private static class GetUrl {
        private final String requestUrl;
        private final String signatureUrl;

        private GetUrl(String requestUrl, String signatureUrl) {
            this.requestUrl = requestUrl;
            this.signatureUrl = signatureUrl;
        }
    }
}
