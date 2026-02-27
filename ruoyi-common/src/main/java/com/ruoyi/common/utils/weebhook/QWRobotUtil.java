//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ruoyi.common.utils.weebhook;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.ruoyi.common.utils.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
public class QWRobotUtil {
    private static final String MEDIA_UPLOAD_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/upload_media?key=%s&type=file";
    private static final String SEND_MESSAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=%s";
    private static final long MAX_FILE_SIZE = 20971520L;
    private static final long MAX_IMAGE_SIZE = 2097152L;
    public static final String AT_ALL = "@all";

    public static boolean atAll(final String secret) {
        return at(secret, "@all");
    }

    public static boolean at(final String secret, final String user) {
        String json = "{\"msgtype\": \"text\",\"text\": {\"mentioned_mobile_list\":[\"" + user + "\"]}}";
        return sendMsg(secret, json);
    }

    public static boolean at(final String secret, final List<String> user) {
        String mentioned_mobile_list = JacksonUtil.toJson(user);
        String json = "{\"msgtype\": \"text\",\"text\": {\"mentioned_mobile_list\":" + mentioned_mobile_list + "}}";
        return sendMsg(secret, json);
    }

    public static boolean sendTextMsg(final String secret, final String content) {
        String json = "{\"msgtype\": \"text\",\"text\": {\"content\": \"" + content + "\"}}";
        return sendMsg(secret, json);
    }

    public static boolean sendTextMsg(final String secret, final String content, final String atUser) {
        String json = "{\"msgtype\": \"text\",\"text\": {\"content\": \"" + content + "\", \"mentioned_mobile_list\":[\"" + atUser + "\"]}}";
        return sendMsg(secret, json);
    }

    public static boolean sendTextMsg(final String secret, final String content, final List<String> users) {
        String mentioned_mobile_list = JacksonUtil.toJson(users);
        String json = "{\"msgtype\": \"text\",\"text\": {\"content\": \"" + content + "\", \"mentioned_mobile_list\":" + mentioned_mobile_list + "}}";
        return sendMsg(secret, json);
    }

    public static boolean sendMarkdownMsg(final String secret, final String content) {
        String json = "{\"msgtype\": \"markdown\",\"markdown\": {\"content\": \"" + content + "\"}}";
        return sendMsg(secret, json);
    }

    public static boolean sendFileMsg(final String secret, final File file) {
        return sendFileMsg(secret, file, false);
    }

    public static boolean sendFileMsg(final String secret, final File file, final boolean needDeleted) {
        if (file.exists() && !file.isDirectory() && file.length() <= 20971520L) {
            String responseStr = HttpUtil.createPost(String.format("https://qyapi.weixin.qq.com/cgi-bin/webhook/upload_media?key=%s&type=file", secret)).contentType(ContentType.MULTIPART.toString()).form("media", file).execute().body();
            Map responseMap = JacksonUtil.parse(responseStr, Map.class);
            if (!responseMap.containsKey("media_id")) {
                log.error("[企微群机器人]消息发送文件异常:文件素材上传失败:{}", responseStr);
                return false;
            } else {
                String json = "{\"msgtype\": \"file\",\"file\": {\"media_id\": \"" + responseMap.get("media_id") + "\"}}";
                boolean sent = sendMsg(secret, json);
                if (needDeleted) {
                    file.delete();
                }

                return sent;
            }
        } else {
            log.error("[企微群机器人]消息发送文件异常:文件过大【{}】{}", file.getName(), file.length());
            return false;
        }
    }

    public static boolean sendImageMsg(final String secret, final File file) {
        return sendImageMsg(secret, file, false);
    }

    public static boolean sendImageMsg(final String secret, final File file, final boolean needDeleted) {
        if (file.exists() && !file.isDirectory() && file.length() <= 2097152L) {
            try {
                byte[] fileBytes = FileUtils.readFileToByteArray(file);
                String base64 = Base64.getEncoder().encodeToString(fileBytes);
                String md5 = DigestUtil.md5Hex(fileBytes);
                String json = "{\"msgtype\": \"image\",\"image\": {\"base64\": \"" + base64 + "\", \"md5\":\"" + md5 + "\"}}";
                boolean sent = sendMsg(secret, json);
                if (needDeleted) {
                    boolean delete = file.delete();
                    if (!delete) {
                        log.error("[企微群机器人]消息发送图片文件删除失败:{}", file.getName());
                    }
                }

                return sent;
            } catch (IOException e) {
                log.error("[企微群机器人]消息发送图片消息文件读取异常", e);
                return false;
            }
        } else {
            log.error("[企微群机器人]消息发送图片异常:文件过大【{}】{}", file.getName(), file.length());
            return false;
        }
    }

    public static boolean sendMsg(final String secret, final String requestBody) {
        log.info("[企微群机器人]消息发送请求:{}", requestBody);
        HttpRequest httpRequest = HttpUtil.createPost(String.format("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=%s", secret)).body(requestBody).timeout(5000).contentType(ContentType.JSON.toString());

        try {
            boolean var4;
            try (HttpResponse response = httpRequest.execute()) {
                log.info("[企微群机器人]消息发送结果[{}]:{}", response.body());

                var4 = response.getStatus() == 200;
            }
            return var4;
        } catch (Exception e) {
            log.error("[企微群机器人]消息发送异常[{}]:{} ", new Object[]{secret, requestBody, e});
            return false;
        }
    }

    public static boolean sendImageAndTextMsg(final String secret, final String title, final String description, final String url, final String picture) {
        String json = "{\n    \"msgtype\": \"news\",\n    \"news\": {\n       \"articles\" : [\n           {\n               \"title\" : \"" + title + "\",\n               \"description\" : \"" + description + "\",\n               \"url\" : \"" + url + "\",\n               \"picurl\" : \"" + picture + "\"\n           }\n        ]\n    }\n}";
        return sendMsg(secret, json);
    }

    public static class MarkdownBuilder {
        private String title;
        private final StringBuilder content = new StringBuilder();

        public static MarkdownBuilder create() {
            return new MarkdownBuilder();
        }

        public static String colorText(final String text, final String color) {
            return String.format("<font color='%s'>%s</font>", color, text);
        }

        public static String boldText(final String text) {
            return String.format("** %s **", text);
        }

        public static String link(final String text, final String link) {
            return String.format("[%s](%s)", text, link);
        }

        public MarkdownBuilder title(final String title) {
            this.title = title;
            return this;
        }

        public MarkdownBuilder addContentWithBoldKey(final String key, final String value) {
            this.addContent(boldText(key), value);
            return this;
        }

        public MarkdownBuilder addContentWithBoldValue(final String key, final String value) {
            this.addContent(key, boldText(value));
            return this;
        }

        public MarkdownBuilder addContentWithBold(final String key, final String value) {
            this.addContent(boldText(key), boldText(value));
            return this;
        }

        public MarkdownBuilder addContentWithColor(final String key, final String value, final String color) {
            this.addContent(key, colorText(value, color));
            return this;
        }

        public MarkdownBuilder addContentWithLink(final String key, final String text, final String link) {
            this.addContent(key, link(text, link));
            return this;
        }

        public MarkdownBuilder addContent(final String key, final String value) {
            this.content.append(">").append(key).append("：").append(value).append("\n");
            return this;
        }

        public MarkdownBuilder addContentWithColorAndBold(final String key, final String value, final String color) {
            this.addContent(boldText(key), colorText(boldText(value), color));
            return this;
        }

        public MarkdownBuilder addContent(final String key) {
            this.content.append(">").append(key).append("\n");
            return this;
        }

        public String build() {
            return String.format("# %s \n %s", StrUtil.blankToDefault(this.title, "无标题内容"), this.content);
        }

        public String toString() {
            return this.build();
        }
    }
}
