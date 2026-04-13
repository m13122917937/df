package com.ruoyi;

import cn.hutool.http.HttpUtil;

public class as {

    public static void main(String[] args) {


        String post = HttpUtil.post("http://localhost:7772/order/new/list?pageNum=1&pageSize=30", "{\n" +
                "    \"province\": \"\",\n" +
                "    \"brand\": \"\",\n" +
                "    \"orderCodeList\": [],\n" +
                "    \"originalOrderId\": \"\",\n" +
                "    \"category\": \"\",\n" +
                "    \"shopName\": \"\",\n" +
                "    \"productNameLike\": \"\",\n" +
                "    \"skuNameLike\": \"\",\n" +
                "    \"status\": 1\n" +
                "}");
        System.out.println( post );
    }
}
