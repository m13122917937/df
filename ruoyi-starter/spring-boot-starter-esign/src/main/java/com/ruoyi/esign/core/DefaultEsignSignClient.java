package com.ruoyi.esign.core;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.esign.api.EsignSignApi;
import com.ruoyi.esign.exception.EsignException;
import com.ruoyi.esign.model.sign.CreateByFileRequest;
import com.ruoyi.esign.model.sign.CreateByFileResponse;
import com.ruoyi.esign.properties.EsignProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 默认e签宝合同签署客户端实现
 *
 * @author ruoyi
 */
public class DefaultEsignSignClient implements EsignSignApi {

    private static final Logger log = LoggerFactory.getLogger(DefaultEsignSignClient.class);

    private final EsignProperties properties;

    public DefaultEsignSignClient(EsignProperties properties) {
        this.properties = properties;
    }

    @Override
    public CreateByFileResponse createByFile(CreateByFileRequest request) {
        return executePost(properties.getCreateByFileUrl(), request, CreateByFileResponse.class);
    }

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
            log.error("e签宝合同签署API请求异常", e);
            throw new EsignException("API请求异常", e);
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
        if (result instanceof CreateByFileResponse
                && !Integer.valueOf(0).equals(((CreateByFileResponse) result).getCode())) {
            log.error("e签宝基于文件发起签署失败，code:{}, message:{}",
                    ((CreateByFileResponse) result).getCode(),
                    ((CreateByFileResponse) result).getMessage());
        }
    }
}
