package com.ruoyi.esign.core;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.esign.api.EsignAuthApi;
import com.ruoyi.esign.exception.EsignException;
import com.ruoyi.esign.model.v3.OrgAuthUrlRequest;
import com.ruoyi.esign.properties.EsignProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 默认e签宝认证客户端实现（V3版本）
 * 只实现：获取企业认证授权链接
 *
 * @author ruoyi
 */
public class DefaultEsignAuthClient implements EsignAuthApi {

    private static final Logger log = LoggerFactory.getLogger(DefaultEsignAuthClient.class);

    private final EsignProperties properties;

    public DefaultEsignAuthClient(EsignProperties properties) {
        this.properties = properties;
    }

    @Override
    public String createOrgAuthUrl(OrgAuthUrlRequest request) {
        String url = properties.getCreateOrgAuthUrlV3();
        return extractRedirectUrl(postWithSignature(url, request));
    }

    /**
     * 使用请求签名鉴权发送POST请求并返回结果Map
     * 签名规则参考：https://open.esign.cn/doc/opendoc/dev-guide3/tggw2e
     */
    private Map<String, Object> postWithSignature(String fullUrl, Object request) {
        String body = JacksonUtil.toJson(request);
        if (body == null) {
            throw new EsignException("序列化请求失败");
        }
        String timestamp = String.valueOf(System.currentTimeMillis());
        String accept = "application/json";
        String contentType = "application/json; charset=UTF-8";
        // 生成RFC 1123格式的Date头
        String date = formatDate();
        // 提取路径部分用于签名计算（签名只需要路径，不需要host）
        String pathUrl = extractPath(fullUrl);
        String contentMd5 = generateContentMd5(body);
        String signature = generateSignature("POST", pathUrl, contentMd5, accept, contentType, date);

        try {
            HttpRequest httpRequest = HttpRequest.post(fullUrl)
                    .body(body)
                    .header("Accept", accept)
                    .header("Date", date)
                    .contentType(contentType)
                    .header("Content-MD5", contentMd5)
                    .header("X-Tsign-Open-App-Id", properties.getAppId().trim())
                    .header("X-Tsign-Open-Ca-Timestamp", timestamp)
                    .header("X-Tsign-Open-Ca-Signature", signature)
                    .header("X-Tsign-Open-Auth-Mode", "Signature")
                    .timeout(properties.getReadTimeout().intValue());

            HttpResponse response = httpRequest.execute();

            if (!response.isOk()) {
                throw new EsignException("请求失败，请求响应：" + response.body());
            }

            return JacksonUtil.parse(response.body(), Map.class);
        } catch (EsignException e) {
            throw e;
        } catch (Exception e) {
            log.error("e签宝API请求异常", e);
            throw new EsignException("API请求异常", e);
        }
    }

    /**
     * 格式化Date为RFC 1123格式 (GMT时区)
     * 示例: Thu, 11 Jul 2015 15:32:24 GMT
     */
    private String formatDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss 'GMT'",
                Locale.US
        );
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date());
    }

    /**
     * 从完整URL中提取路径部分（去除scheme和host）
     */
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

    /**
     * 提取跳转链接
     */
    private String extractRedirectUrl(Map<String, Object> result) {
        checkApiResultCode(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        log.info("e签宝返回data: {}", data);
        return (String) data.get("authUrl");
    }

    /**
     * 检查API返回码
     */
    private void checkApiResultCode(Map<String, Object> result) {
        Object codeObj = result.get("code");
        if (codeObj != null && !"0".equals(String.valueOf(codeObj))) {
            String message = String.valueOf(result.get("message"));
            log.error("e签宝API调用失败，code:{}, message:{}", codeObj, message);
            throw new EsignException(String.valueOf(codeObj), message);
        }
    }

    /**
     * 生成Content-MD5
     * 对body进行MD5计算后，将二进制结果进行Base64编码
     */
    private String generateContentMd5(String body) {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        Digester digester = new Digester(DigestAlgorithm.MD5);
        byte[] md5Bytes = digester.digest(bytes);
        return Base64.encode(md5Bytes);
    }

    /**
     * 生成请求签名
     * 签名规则（V3标准）：
     * 1. 拼接待签名字符串：HTTP_METHOD\naccept\ncontentMd5\ncontentType\ndate\nurl
     * 2. 使用HmacSHA256算法对拼接字符串进行签名
     * 3. 将签名结果进行Base64编码
     */
    private String generateSignature(String httpMethod, String url, String contentMd5,
            String accept, String contentType, String date) {
        // 按照标准格式拼接待签名字符串
        StringBuilder sb = new StringBuilder();
        sb.append(httpMethod).append("\n")
          .append(accept).append("\n")
          .append(contentMd5).append("\n")
          .append(contentType).append("\n")
          .append(date).append("\n")
          .append(url);

        String message = sb.toString();
        log.debug("e签宝待签名字符串: {}", message);
        // appSecret去除前后空格
        String appSecret = properties.getAppSecret().trim();
        return doSignatureBase64(message, appSecret);
    }

    /**
     * 使用HmacSHA256计算签名并进行Base64编码
     */
    private String doSignatureBase64(String message, String secret) {
        byte[] hmac = SecureUtil.hmacSha256(secret.getBytes(StandardCharsets.UTF_8))
                .digest(message.getBytes(StandardCharsets.UTF_8));
        return Base64.encode(hmac);
    }



}
