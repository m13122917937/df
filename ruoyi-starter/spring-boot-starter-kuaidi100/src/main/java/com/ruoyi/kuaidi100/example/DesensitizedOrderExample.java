package com.ruoyi.kuaidi100.example;

import com.ruoyi.kuaidi100.EExpressClient;
import com.ruoyi.kuaidi100.model.e.EOrderResult;
import com.ruoyi.kuaidi100.model.strategy.DesensitizedOrderRequest;
import com.ruoyi.kuaidi100.model.strategy.PlatformAuthInfo;
import com.ruoyi.kuaidi100.model.strategy.PlatformType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 脱敏订单打印使用示例
 *
 * <p>演示如何使用策略模式提交不同平台的脱敏订单
 *
 * @author kuaidi100
 */
@Component
public class DesensitizedOrderExample {

    @Autowired
    private EExpressClient eExpressClient;

    /**
     * 示例1：提交拼多多脱敏订单
     */
    public void submitPinduoduoOrder() {
        // 1. 构建平台授权信息
        PlatformAuthInfo authInfo = PlatformAuthInfo.builder()
                .partnerId("your_partner_id")
                .partnerKey("your_partner_key")
                .partnerName("your_partner_name")
                .net("your_net")
                .build();

        // 2. 构建脱敏订单请求
        DesensitizedOrderRequest request = DesensitizedOrderRequest.builder()
                .platformType(PlatformType.PINDUODUO)
                .authInfo(authInfo)
                .thirdOrderId("PDD202501011234567890")
                .expressCom("yunda")
                // 收件人加密信息（注意：这些数据应该是已经加密过的）
                .recipientInfo(DesensitizedOrderRequest.RecipientEncryptedInfo.builder()
                        .name("~AgAAAAK6MjwFdMbadQAimOGCTHMINDd7/W2xXlWtlao=~4~")  // 加密姓名
                        .mobile("$AgAAAAK6MjwGdMbadQCrzhfdOcmsmHeJ69OhLqUjh80=$4$")  // 加密电话
                        .province("广东省")
                        .city("深圳市")
                        .district("南山区")
                        .addr("~AgAAAAK6MjwIdMbadQG53SyjOLyy04ZtKOi1EBOn5YQC/uQ1PceHzGU+Fp/SjVHcpR6fyzXzvnsLY4NoveXR7JgfAK1rUayLlT79BDdjydA=~4~")  // 加密地址
                        .detailAddrEnc("~AgAAAAK6MjwIdMbadQG53SyjOLyy04ZtKOi1EBOn5YQC/uQ1PceHzGU+Fp/SjVHcpR6fyzXzvnsLY4NoveXR7JgfAK1rUayLlT79BDdjydA=~4~")  // 加密详细地址
                        .build())
                // 寄件人信息
                .senderInfo(DesensitizedOrderRequest.SenderInfo.builder()
                        .name("张三")
                        .mobile("13800138000")
                        .province("广东省")
                        .city("广州市")
                        .district("白云区")
                        .addr("白云大道123号")
                        .build())
                // 货物信息
                .cargoInfo(DesensitizedOrderRequest.CargoInfo.builder()
                        .name("手机")
                        .count("1")
                        .unit("件")
                        .weight("0.5")
                        .build())
                .weight("0.5")
                .quantity("1")
                .build();

        // 3. 提交订单
        EOrderResult result = eExpressClient.submitDesensitizedOrder(request);
        System.out.println("拼多多订单提交结果：" + result);
    }

    /**
     * 示例2：提交抖音脱敏订单
     */
    public void submitDouyinOrder() {
        PlatformAuthInfo authInfo = PlatformAuthInfo.builder()
                .partnerId("your_partner_id")
                .partnerKey("your_partner_key")
                .partnerName("your_partner_name")
                .net("your_net")
                .build();

        DesensitizedOrderRequest request = DesensitizedOrderRequest.builder()
                .platformType(PlatformType.DOUYIN)
                .authInfo(authInfo)
                .thirdOrderId("DY202501011234567890")
                .expressCom("yunda")
                .recipientInfo(DesensitizedOrderRequest.RecipientEncryptedInfo.builder()
                        .name("##N+06KglUtfp/I/Vp6XdLbHJ9pfiJRW15YIOY2AeklPpHpQkTQyD6hV4VcGO2WcZgXFlM6eFc7rya/LkYrf3X3sEK5rodZ2Gr5ilcZigo_CgYIASAHKAESPgo8ThN5zZXaW+iMWid5gmZZZ8TUfmakRVo4oaUw0umP2foh/zFyimMcSDN/AsoYgmr5VZHQGYZh8fiZNL4rGgA=#1##")
                        .mobile("$$MrwRxZLQ6eyoiBb0hHAVwa3PY8Tn2Lc6f1tJWkxjvlfrxNCR5Fb9NGm2l82YUKWKYvrl9OUs1NJO0m3abAAeBBRMePnnFHDCnHrN3HlZ5z9Eino=_CgYIASAHKAESPgo8VDWCrMBRiJhedYjhAiEkaBHRzPv0qV7ZKkdBS/NTTFIG5/R4GpXRomylvsbhkyYa8H/Ct3qSyv6clV3lGgA=$1$$")
                        .province("广东省")
                        .city("深圳市")
                        .district("南山区")
                        .addr("##fLm0CXukwu35yzQXuls0et8ZInKeT4X0kmO/kr3TfV41ZDfpRi16nui9qXSj9X+Wuz1w/q2qctG0YSVhheEPc5JIs6LPIyQe+vWm2DyAv8mrCeDG+NEFqw3ZXtexQ8c+8Cz6N2ZxwYk8Gs7qTjpdBPWD9La3H1Ev_CgYIASAHKAESPgo88I2EinrcLxopENPGWaZwj0Cm8QP69eeT7hMQKb3JEmyc8B1j1zdYtcL836uO7oEz3zLSM2aKKAzwTprKGgA=#1##")
                        .detailAddrEnc("##fLm0CXukwu35yzQXuls0et8ZInKeT4X0kmO/kr3TfV41ZDfpRi16nui9qXSj9X+Wuz1w/q2qctG0YSVhheEPc5JIs6LPIyQe+vWm2DyAv8mrCeDG+NEFqw3ZXtexQ8c+8Cz6N2ZxwYk8Gs7qTjpdBPWD9La3H1Ev_CgYIASAHKAESPgo88I2EinrcLxopENPGWaZwj0Cm8QP69eeT7hMQKb3JEmyc8B1j1zdYtcL836uO7oEz3zLSM2aKKAzwTprKGgA=#1##")
                        .build())
                .senderInfo(DesensitizedOrderRequest.SenderInfo.builder()
                        .name("张三")
                        .mobile("13800138000")
                        .province("广东省")
                        .city("广州市")
                        .district("白云区")
                        .addr("白云大道123号")
                        .build())
                .cargoInfo(DesensitizedOrderRequest.CargoInfo.builder()
                        .name("手机")
                        .count("1")
                        .unit("件")
                        .weight("0.5")
                        .build())
                .build();

        EOrderResult result = eExpressClient.submitDesensitizedOrder(request);
        System.out.println("抖音订单提交结果：" + result);
    }

    /**
     * 示例3：提交快手脱敏订单
     */
    public void submitKuaishouOrder() {
        PlatformAuthInfo authInfo = PlatformAuthInfo.builder()
                .partnerId("your_partner_id")
                .partnerKey("your_partner_key")
                .partnerName("your_partner_name")
                .net("your_net")
                .build();

        DesensitizedOrderRequest request = DesensitizedOrderRequest.builder()
                .platformType(PlatformType.KUAISHOU)
                .authInfo(authInfo)
                .thirdOrderId("KS202501011234567890")
                .expressCom("yunda")
                .recipientInfo(DesensitizedOrderRequest.RecipientEncryptedInfo.builder()
                        .name("#aYUfZile#Ci1tZXJjaGFudC5vcGVuLnNlY3JldC5rZXkua3M2NDkwODEyOTc4NDQ0NjQyMTgSIOKcWztpYSpaTq11Cp11zmDObNe9eu3VYZfW6VBFXDTcGhKshLB07Uzy/CvuBE9bj3C7hUciID5qIOnT7c/nfhCyoaCB0VTrwo4SZsgcqohha5Qtuv18KAUwAQ==&Ci1tZXJjaGFudC5vcGVuLnNlY3JldC5rZXkua3M2NDkwODEyOTc4NDQ0NjQyMTgSMHnnAveIhZ7HwCr5eJbcf63Kj7fRDkRehISS489TB3SdPK1jzX9Wdad2icwMSJ/o+RoS5vZo7at9XZdPFvIjv/lgi7H7IiCNRqixfOfZJbdoV+J8Z9ERYtF9erEpeEbWPXRpv1kNJSgFMAE=#1##")
                        .mobile("$odMHb6BGqeJkEqkNoNTdfyoXdqOaFiDxHohBIWBom4M=$Ci1tZXJjaGFudC5vcGVuLnNlY3JldC5rZXkua3M2NDkwODEyOTc4NDQ0NjQyMTgSICBZ27H1fwuCozjE6GHOJNCUPpjzYRmFqq+TFdbu2ER3GhICu0DaU0XezGbk6UOn3iNFJrAiIG0KT6MCjELash0cy3edgjuHmGLF0agctZ3NgvaN/ZSWKAUwAQ==&Ci1tZXJjaGFudC5vcGVuLnNlY3JldC5rZXkua3M2NDkwODEyOTc4NDQ0NjQyMTgSMPwht2YV6hrokZh1HpOn/EhibeorheKZQfAYzfry6LQL3WOGWodSRPK7EhAoczky7RoSJHJGtusVhTZO7YNcF2W6loQAIiAXKke9OBANJ1NYoNdObd2RA2lu26+THvFchsDNvSjukygFMAE=$1$$")
                        .province("广东省")
                        .city("深圳市")
                        .district("南山区")
                        .street("粤海街道")  // 快手需要街道信息
                        .addr("~4ZIlCF4TRCihgCsI4ZIlCF4TukhyDEOI~Ci1tZXJjaGFudC5vcGVuLnNlY3JldC5rZXkua3M2NDkwODEyOTc4NDQ0NjQyMTgSMDaZx7Qn73skKnMl6QgGQK2k0De/g0BCZOZJBmJN4Qr+xtZDEvsdFWdg8hHMLivSYxoSRZuvzpEyLU49SQXTqEJouleEIiDlcWQEfAGQm0rHsczwjFBpWwFva6stoL4TtKfvAiQgXygFMAE=&Ci1tZXJjaGFudC5vcGVuLnNlY3JldC5rZXkua3M2NDkwODEyOTc4NDQ0NjQyMTgSMLPNJii8BAi9NGRBeT9hXfaIuXTvMsJSxIMhoimTb3E0vPe+9ciFDANlzOuDo1WjmBoS8Q6ReyhN/OXe2ik4umdb4sRAIiBoLKSlqzotpdyBO1jHn4xlMgvpu99hBoXjJ59eY0IMxigFMAE=~1~~")
                        .detailAddrEnc("~4ZIlCF4TRCihgCsI4ZIlCF4TukhyDEOI~Ci1tZXJjaGFudC5vcGVuLnNlY3JldC5rZXkua3M2NDkwODEyOTc4NDQ0NjQyMTgSMDaZx7Qn73skKnMl6QgGQK2k0De/g0BCZOZJBmJN4Qr+xtZDEvsdFWdg8hHMLivSYxoSRZuvzpEyLU49SQXTqEJouleEIiDlcWQEfAGQm0rHsczwjFBpWwFva6stoL4TtKfvAiQgXygFMAE=&Ci1tZXJjaGFudC5vcGVuLnNlY3JldC5rZXkua3M2NDkwODEyOTc4NDQ0NjQyMTgSMLPNJii8BAi9NGRBeT9hXfaIuXTvMsJSxIMhoimTb3E0vPe+9ciFDANlzOuDo1WjmBoS8Q6ReyhN/OXe2ik4umdb4sRAIiBoLKSlqzotpdyBO1jHn4xlMgvpu99hBoXjJ59eY0IMxigFMAE=~1~~")
                        .build())
                .senderInfo(DesensitizedOrderRequest.SenderInfo.builder()
                        .name("张三")
                        .mobile("13800138000")
                        .province("广东省")
                        .city("广州市")
                        .district("白云区")
                        .addr("白云大道123号")
                        .build())
                .cargoInfo(DesensitizedOrderRequest.CargoInfo.builder()
                        .name("手机")
                        .count("1")
                        .unit("件")
                        .weight("0.5")
                        .build())
                .build();

        EOrderResult result = eExpressClient.submitDesensitizedOrder(request);
        System.out.println("快手订单提交结果：" + result);
    }

    /**
     * 示例4：提交菜鸟淘宝脱敏订单
     */
    public void submitTaobaoOrder() {
        PlatformAuthInfo authInfo = PlatformAuthInfo.builder()
                .partnerId("your_partner_id")
                .partnerKey("your_partner_key")
                .partnerName("your_partner_name")
                .net("your_net")
                .oaid("tb_oaid_1234567890")  // 菜鸟淘宝需要 oaid
                .build();

        DesensitizedOrderRequest request = DesensitizedOrderRequest.builder()
                .platformType(PlatformType.TAOBAO)
                .authInfo(authInfo)
                .thirdOrderId("TB202501011234567890")
                .expressCom("yunda")
                // 菜鸟淘宝使用脱敏数据，不需要加密
                .recipientInfo(DesensitizedOrderRequest.RecipientEncryptedInfo.builder()
                        .name("锦*_")  // 脱敏姓名
                        .mobile("138****_25")  // 脱敏电话
                        .province("广东省")
                        .city("深圳市")
                        .district("南山区")
                        .addr("北太**街道知春号锦秋国际韵达")  // 脱敏地址
                        // 注意：菜鸟淘宝不需要 detailAddrEnc
                        .build())
                .senderInfo(DesensitizedOrderRequest.SenderInfo.builder()
                        .name("张三")
                        .mobile("13800138000")
                        .province("广东省")
                        .city("广州市")
                        .district("白云区")
                        .addr("白云大道123号")
                        .build())
                .cargoInfo(DesensitizedOrderRequest.CargoInfo.builder()
                        .name("手机")
                        .count("1")
                        .unit("件")
                        .weight("0.5")
                        .build())
                .build();

        EOrderResult result = eExpressClient.submitDesensitizedOrder(request);
        System.out.println("菜鸟淘宝订单提交结果：" + result);
    }
}