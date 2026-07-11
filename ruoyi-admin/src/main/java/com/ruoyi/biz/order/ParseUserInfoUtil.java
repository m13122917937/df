package com.ruoyi.biz.order;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUserInfoUtil {

    // 匹配中国省级行政区开头的关键字（用于定位地址起始位置）
    private static final String PROVINCE_PATTERN =
            "(?:北京市|天津市|河北省|山西省|内蒙古自治区|辽宁省|吉林省|黑龙江省|上海市|江苏省|浙江省|安徽省|福建省|江西省|山东省|河南省|湖北省|湖南省|广东省|广西壮族自治区|海南省|重庆市|四川省|贵州省|云南省|西藏自治区|陕西省|甘肃省|青海省|宁夏回族自治区|新疆维吾尔自治区)";

    // 结果封装类
    public static class ParsedAddress {
        private final String name;
        private final String phone;
        private final String address;

        public ParsedAddress(String name, String phone, String address) {
            this.name = name;
            this.phone = phone;
            this.address = address;
        }

        // Getters
        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }

        @Override
        public String toString() {
            return String.format("姓名: %s, 电话: %s, 地址: %s", name, phone, address);
        }
    }

    // 主解析方法
    public static ParsedAddress parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        // 第一步：匹配姓名 + 手机号（含可选后缀）
        // 姓名：非空格字符序列
        // 手机号：11位数字，可选 -4位数字
        Pattern pattern = Pattern.compile(
                "^(\\S+)\\s+(1[3-9]\\d{9}(?:-\\d{4})?)\\s+(.+)$"
        );
        Matcher matcher = pattern.matcher(line.trim());

        if (!matcher.matches()) {
            // 如果标准格式不匹配，尝试 fallback：找手机号位置
            return fallbackParse(line);
        }

        String name = matcher.group(1);
        String phone = matcher.group(2);
        String address = matcher.group(3);

        // 验证地址是否以省级关键词开头（防止手机号误判）
        if (!address.matches("^" + PROVINCE_PATTERN + ".*")) {
            // 如果不以省开头，说明可能手机号没匹配全，尝试 fallback
            return fallbackParse(line);
        }

        return new ParsedAddress(name, phone, address);
    }

    // 备用解析：当主正则失败时，通过查找手机号定位
    private static ParsedAddress fallbackParse(String line) {
        // 查找 11 位手机号（13-19开头）
        Pattern phonePattern = Pattern.compile("(1[3-9]\\d{9}(?:-\\d{4})?)");
        Matcher phoneMatcher = phonePattern.matcher(line);

        if (!phoneMatcher.find()) {
            return null; // 无法找到手机号
        }

        int phoneStart = phoneMatcher.start();
        int phoneEnd = phoneMatcher.end();

        // 姓名 = 手机号前的所有非空格内容（trim）
        String name = line.substring(0, phoneStart).trim();
        // 手机号
        String phone = phoneMatcher.group(1);
        // 地址 = 手机号后的内容（trim）
        String address = line.substring(phoneEnd).trim();

        return new ParsedAddress(name, phone, address);
    }

}
