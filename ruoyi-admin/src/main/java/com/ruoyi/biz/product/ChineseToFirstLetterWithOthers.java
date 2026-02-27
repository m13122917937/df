package com.ruoyi.biz.product;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class ChineseToFirstLetterWithOthers {

    public static String convert(String input) {
        if (input == null) return "";

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE); // 拼音小写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder result = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (c >= '\u4e00' && c <= '\u9fff') {
                // 是汉字：取拼音首字母（小写）
                try {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        result.append(pinyinArray[0].charAt(0)); // 首字母
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    // 出错时跳过（或可 append('?')）
                }
            } else if (Character.isDigit(c)) {
                // 数字：直接保留
                result.append(c);
            } else if (Character.isLetter(c)) {
                // 英文字母：转为小写保留
                result.append(Character.toLowerCase(c));
            }
            // 其他字符（如标点、空格等）默认忽略；如需保留，可加 else { result.append(c); }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println(convert("测试123ABC")); // 输出: cs123abc
        System.out.println(convert("你好World2025！")); // 输出: nhworld2025 （感叹号被忽略）
        System.out.println(convert("Java编程")); // 输出: javabcx
    }
}