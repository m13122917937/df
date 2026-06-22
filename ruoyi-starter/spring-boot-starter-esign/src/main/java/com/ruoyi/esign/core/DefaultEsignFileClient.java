package com.ruoyi.esign.core;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.esign.api.EsignFileApi;
import com.ruoyi.esign.exception.EsignException;
import com.ruoyi.esign.model.file.CreateByDocTemplateRequest;
import com.ruoyi.esign.model.file.CreateByDocTemplateResponse;
import com.ruoyi.esign.model.file.PreviewFileDownloadUrlRequest;
import com.ruoyi.esign.model.file.PreviewFileDownloadUrlResponse;
import com.ruoyi.esign.model.file.SignFlowFileDownloadUrlRequest;
import com.ruoyi.esign.model.file.SignFlowFileDownloadUrlResponse;
import com.ruoyi.esign.properties.EsignProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 默认e签宝文件服务客户端实现
 *
 * @author ruoyi
 */
public class DefaultEsignFileClient implements EsignFileApi {

    private static final Logger log = LoggerFactory.getLogger(DefaultEsignFileClient.class);

    private final EsignProperties properties;

    public DefaultEsignFileClient(EsignProperties properties) {
        this.properties = properties;
    }

    @Override
    public SignFlowFileDownloadUrlResponse fileDownloadUrl(SignFlowFileDownloadUrlRequest request) {
        if (request == null || StrUtil.isBlank(request.getSignFlowId())) {
            throw new IllegalArgumentException("signFlowId不能为空");
        }
        Map<String, Object> body = new HashMap<>();
        putIfNotNull(body, "urlAvailableDate", request.getUrlAvailableDate());
        putIfNotNull(body, "internalUrl", request.getInternalUrl());
        putIfNotNull(body, "aesEncrypt", request.getAesEncrypt());
        putIfNotBlank(body, "rsaSecret", request.getRsaSecret());
        putIfNotBlank(body, "rsaSecretKey", request.getRsaSecretKey());
        return executePost(properties.getSignFlowFileDownloadUrl(request.getSignFlowId()), body,
                SignFlowFileDownloadUrlResponse.class);
    }

    @Override
    public PreviewFileDownloadUrlResponse previewFileDownloadUrl(PreviewFileDownloadUrlRequest request) {
        if (request == null || StrUtil.isBlank(request.getSignFlowId())) {
            throw new IllegalArgumentException("signFlowId不能为空");
        }
        if (StrUtil.isBlank(request.getDocFileId())) {
            throw new IllegalArgumentException("docFileId不能为空");
        }
        GetUrl getUrl = buildPreviewFileDownloadUrl(request);
        return executeGet(getUrl.requestUrl, getUrl.signatureUrl, PreviewFileDownloadUrlResponse.class);
    }

    @Override
    public CreateByDocTemplateResponse createByDocTemplate(CreateByDocTemplateRequest request) {
        return executePost(properties.getCreateByDocTemplateUrl(), request, CreateByDocTemplateResponse.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T executePost(String fullUrl, Object request, Class<T> responseClass) {
        String body = JacksonUtil.toJson(request);
        if (body == null) {
            throw new EsignException("序列化请求失败");
        }
        String accept = "application/json";
        String contentType = "application/json; charset=UTF-8";
        String date = formatDate();
        String pathUrl = extractPath(fullUrl);
        String contentMd5 = generateContentMd5(body);
        String signature = generateSignature("POST", pathUrl, contentMd5, accept, contentType, date);

        try {
            HttpResponse response = HttpRequest.post(fullUrl)
                    .body(body)
                    .header("Accept", accept)
                    .header("Date", date)
                    .contentType(contentType)
                    .header("Content-MD5", contentMd5)
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
            log.error("e签宝文件API请求异常", e);
            throw new EsignException("API请求异常", e);
        }
    }

    private <T> T executeGet(String requestUrl, String signatureUrl, Class<T> responseClass) {
        String accept = "application/json";
        String date = formatDate();
        String pathUrl = extractPath(signatureUrl);
        String signature = generateSignature("GET", pathUrl, "", accept, "", date);

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
            log.error("e签宝文件API请求异常", e);
            throw new EsignException("API请求异常", e);
        }
    }

    private GetUrl buildPreviewFileDownloadUrl(PreviewFileDownloadUrlRequest request) {
        UrlQuery requestQuery = new UrlQuery();
        StringBuilder signatureQuery = new StringBuilder();
        appendQueryParam(requestQuery, signatureQuery, "docFileId", request.getDocFileId());
        if (request.getUrlAvailableDate() != null) {
            appendQueryParam(requestQuery, signatureQuery, "urlAvailableDate", String.valueOf(request.getUrlAvailableDate()));
        }
        String baseUrl = properties.getPreviewFileDownloadUrl(request.getSignFlowId());
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

    private void putIfNotNull(Map<String, Object> body, String key, Object value) {
        if (value != null) {
            body.put(key, value);
        }
    }

    private void putIfNotBlank(Map<String, Object> body, String key, String value) {
        if (StrUtil.isNotBlank(value)) {
            body.put(key, value);
        }
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

    private String generateContentMd5(String body) {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        Digester digester = new Digester(DigestAlgorithm.MD5);
        byte[] md5Bytes = digester.digest(bytes);
        return Base64.encode(md5Bytes);
    }

    private String generateSignature(String httpMethod, String url, String contentMd5,
                                     String accept, String contentType, String date) {
        String message = httpMethod + "\n" + accept + "\n" + contentMd5 + "\n" + contentType + "\n" + date + "\n" + url;
        return Base64.encode(SecureUtil.hmacSha256(properties.getAppSecret().trim().getBytes(StandardCharsets.UTF_8))
                .digest(message.getBytes(StandardCharsets.UTF_8)));
    }

    private void logResponseError(Object result) {
        if (result instanceof SignFlowFileDownloadUrlResponse
                && !Integer.valueOf(0).equals(((SignFlowFileDownloadUrlResponse) result).getCode())) {
            log.error("获取e签宝已签署文件下载链接失败，code:{}, message:{}",
                    ((SignFlowFileDownloadUrlResponse) result).getCode(),
                    ((SignFlowFileDownloadUrlResponse) result).getMessage());
        } else if (result instanceof PreviewFileDownloadUrlResponse
                && !Integer.valueOf(0).equals(((PreviewFileDownloadUrlResponse) result).getCode())) {
            log.error("获取e签宝签署中文件预览下载链接失败，code:{}, message:{}",
                    ((PreviewFileDownloadUrlResponse) result).getCode(),
                    ((PreviewFileDownloadUrlResponse) result).getMessage());
        } else if (result instanceof CreateByDocTemplateResponse
                && !Integer.valueOf(0).equals(((CreateByDocTemplateResponse) result).getCode())) {
            log.error("e签宝填写模板生成文件失败，code:{}, message:{}",
                    ((CreateByDocTemplateResponse) result).getCode(),
                    ((CreateByDocTemplateResponse) result).getMessage());
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
