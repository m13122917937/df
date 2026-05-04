package com.ruoyi.esign.core;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.esign.api.EsignFileApi;
import com.ruoyi.esign.exception.EsignException;
import com.ruoyi.esign.model.file.GetFileDownloadUrlRequest;
import com.ruoyi.esign.model.file.GetFileDownloadUrlResponse;
import com.ruoyi.esign.model.file.GetFileInfoRequest;
import com.ruoyi.esign.model.file.GetFileInfoResponse;
import com.ruoyi.esign.model.file.UploadFileRequest;
import com.ruoyi.esign.model.file.UploadFileResponse;
import com.ruoyi.esign.properties.EsignProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 默认e签宝文件服务客户端实现
 * 使用V3版本签名方式，签名算法参考EsignAuthApi
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
    public UploadFileResponse uploadFile(UploadFileRequest request) {
        String url = properties.getFileUploadUrl();
        return executePost(url, request, UploadFileResponse.class);
    }

    @Override
    public GetFileInfoResponse getFileInfo(GetFileInfoRequest request) {
        String url = properties.getFileGetInfoUrl();
        return executePost(url, request, GetFileInfoResponse.class);
    }

    @Override
    public GetFileDownloadUrlResponse getFileDownloadUrl(GetFileDownloadUrlRequest request) {
        String url = properties.getFileGetDownloadUrl();
        return executePost(url, request, GetFileDownloadUrlResponse.class);
    }

    /**
     * 使用V3版本请求签名鉴权发送POST请求
     * 签名规则参考：https://open.esign.cn/doc/opendoc/dev-guide3/tggw2e
     */
    @SuppressWarnings("unchecked")
    private <T> T executePost(String fullUrl, Object request, Class<T> responseClass) {
        String body = JacksonUtil.toJson(request);
        if (body == null) {
            throw new EsignException("序列化请求失败");
        }
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
                    .header("X-Tsign-Open-Ca-Timestamp", String.valueOf(System.currentTimeMillis()))
                    .header("X-Tsign-Open-Ca-Signature", signature)
                    .header("X-Tsign-Open-Auth-Mode", "Signature")
                    .timeout(properties.getReadTimeout().intValue());

            HttpResponse response = httpRequest.execute();

            if (!response.isOk()) {
                throw new EsignException("请求失败，请求响应：" + response.body());
            }

            String responseBody = response.body();
            T result = JacksonUtil.parse(responseBody, responseClass);

            logResponseError(result);
            return result;
        } catch (EsignException e) {
            throw e;
        } catch (Exception e) {
            log.error("e签宝文件API请求异常", e);
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
     * 生成请求签名（V3标准）
     * 签名规则：
     * 1. 拼接待签名字符串：HTTP_METHOD\naccept\ncontentMd5\ncontentType\ndate\nurl
     * 2. 使用HmacSHA256算法对拼接字符串进行签名
     * 3. 将签名结果进行Base64编码
     */
    private String generateSignature(String httpMethod, String url, String contentMd5,
            String accept, String contentType, String date) {
        StringBuilder sb = new StringBuilder();
        sb.append(httpMethod).append("\n")
          .append(accept).append("\n")
          .append(contentMd5).append("\n")
          .append(contentType).append("\n")
          .append(date).append("\n")
          .append(url);

        String message = sb.toString();
        log.debug("e签宝待签名字符串: {}", message);
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

    /**
     * 记录响应错误日志
     */
    private void logResponseError(Object result) {
        if (result instanceof UploadFileResponse && !Boolean.TRUE.equals(((UploadFileResponse) result).getSuccess())) {
            log.error("上传e签宝文件失败，code:{}, message:{}",
                    ((UploadFileResponse) result).getCode(), ((UploadFileResponse) result).getMessage());
        } else if (result instanceof GetFileInfoResponse && !Boolean.TRUE.equals(((GetFileInfoResponse) result).getSuccess())) {
            log.error("查询e签宝文件信息失败，code:{}, message:{}",
                    ((GetFileInfoResponse) result).getCode(), ((GetFileInfoResponse) result).getMessage());
        } else if (result instanceof GetFileDownloadUrlResponse && !Boolean.TRUE.equals(((GetFileDownloadUrlResponse) result).getSuccess())) {
            log.error("获取e签宝文件下载链接失败，code:{}, message:{}",
                    ((GetFileDownloadUrlResponse) result).getCode(), ((GetFileDownloadUrlResponse) result).getMessage());
        }
    }
}
