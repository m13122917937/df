package com.ruoyi;

import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.kuaidi100.EExpressClient;
import com.ruoyi.kuaidi100.factory.DesensitizedOrderStrategyFactory;
import com.ruoyi.kuaidi100.model.e.EOrderConsts;
import com.ruoyi.kuaidi100.model.e.EOrderResult;
import com.ruoyi.kuaidi100.model.strategy.DesensitizedOrderRequest;
import com.ruoyi.kuaidi100.model.strategy.PlatformAuthInfo;
import com.ruoyi.kuaidi100.model.strategy.PlatformType;
import com.ruoyi.order.facade.IPddOrderIncrementFacade;
import com.ruoyi.order.model.bo.PddOrderIncrementBO;
import com.ruoyi.order.model.query.PddOrderIncrementQuery;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

/**
 * 拼多多订单打印面单测试用例
 *
 * @author ruoyi
 * @date 2025-02-10
 */
@Slf4j
@ContextConfiguration
@SpringBootTest(classes = AdminApplication.class)
public class PddOrderPrintTest {

    @Autowired
    private IPddOrderIncrementFacade pddOrderIncrementFacade;

    @Autowired
    private EExpressClient eExpressClient;

    /**
     * 测试：查询待打印的订单并打印面单
     * <p>
     * 注意：运行此测试前，需要确保：
     * 1. pdd_order_increment 表中有测试数据
     * 2. 快递100配置正确（application.yml 中配置了 kuaidi100.customer 和 kuaidi100.key）
     * 3. 如果使用脱敏订单，需要配置快递100的平台授权信息
     */
    @Test
    public void testPrintPddOrder() {
        // 1. 查询待打印的订单（查询最近5条记录）
        List<PddOrderIncrementBO> orderList = pddOrderIncrementFacade.list(new PddOrderIncrementQuery());

        if (orderList == null || orderList.isEmpty()) {
            log.warn("未找到待打印的订单，请先运行 PddOrderIncrementJob 同步订单数据");
            return;
        }

        log.info("查询到 {} 条订单", orderList.size());

        // 2. 遍历订单并打印面单
        for (PddOrderIncrementBO order : orderList) {
            try {
                printSingleOrder(order);
            } catch (Exception e) {
                log.error("打印订单[{}]失败: {}", order.getOrderSn(), e.getMessage(), e);
            }
        }
    }

    /**
     * 测试：打印指定订单号的面单
     */
    @Test
    public void testPrintSpecificOrder() {
        // 指定订单号
        String orderSn = "PDD202501011234567890"; // TODO: 替换为实际的订单号

        // 查询订单
        PddOrderIncrementBO order = pddOrderIncrementFacade.getByOrderSn(orderSn);

        if (order == null) {
            log.warn("未找到订单号[{}]的订单", orderSn);
            return;
        }

        // 打印面单
        printSingleOrder(order);
    }

    /**
     * 打印单个订单的面单
     *
     * @param order 订单信息
     */
    private void printSingleOrder(PddOrderIncrementBO order) {
        log.info("========================================");
        log.info("开始打印订单面单");
        log.info("订单号: {}", order.getOrderSn());
        log.info("收件人: {} {}", order.getReceiverName(), order.getReceiverPhone());
        log.info("收件地址: {} {} {} {}", order.getProvince(), order.getCity(), order.getDistrict(), order.getReceiverAddress());
        log.info("商品: {} x {}", order.getCargoName(), order.getCargoCount());
        log.info("快递公司: {}", order.getExpressCom());
        log.info("========================================");

        // 注意：这里需要实际的快递100客户端和策略工厂
        // 由于脱敏订单需要加密的收件人信息，而当前 pdd_order_increment 表中存储的是明文信息
        // 因此直接调用脱敏订单接口会失败

        // TODO: 这里需要先加密收件人信息，或者使用普通电子面单接口

        log.warn("当前订单[{}]使用的是明文收件人信息，无法直接调用脱敏订单接口", order.getOrderSn());
        log.info("如需测试脱敏订单打印，请：");
        log.info("1. 先调用快递100的加密接口加密收件人信息");
        log.info("2. 或者在 pdd_order_increment 表中添加加密字段并存储加密后的信息");
        log.info("3. 或者使用普通电子面单接口（非脱敏）");
    }

    /**
     * 测试：使用模拟数据调用快递100脱敏订单接口
     * <p>
     * 此测试使用硬编码的测试数据，可以直接调用快递100接口
     * 运行前需要配置正确的快递100授权信息
     */
    @Test
    public void testPrintWithMockData() {

        // 构建模拟的脱敏订单请求
        DesensitizedOrderRequest request = DesensitizedOrderRequest.builder().platformType(PlatformType.PINDUODUO).printType(EOrderConsts.PrintType.IMAGE.getPrintType()).thirdOrderId("260209-477165033063732").expressCom("shunfeng")
                // 注意：这里的加密信息需要从快递100平台获取或调用加密接口生成
                .recipientInfo(DesensitizedOrderRequest.RecipientEncryptedInfo.builder()
                        .name("~AgAAAAPwd2wFpiY+7gAqCyS9PAH53oRHwbnEjj1HTn4=~3~")  // 示例加密姓名
                        .mobile("$AgAAAAPwd2wGpiY+7gBJZCoISh6afvCbbVi4k2flLY0=$3$")  // 示例加密电话
                        .province("江西省")
                        .city("赣州市")
                        .district("上犹县")
                        .addr("粤海街道科技园")
                        .addr("~AgAAAAPwd2wIpiY+7gHGos64SD/xGDz9IMBwRlr1ubKIsqVFuYPJi2lSCNWLHyR2HtKdYdZvs4KkMYwhclDvww==~3~")
                        .detailAddrEnc("~AgAAAAPwd2wIpiY+7gHGos64SD/xGDz9IMBwRlr1ubKIsqVFuYPJi2lSCNWLHyR2HtKdYdZvs4KkMYwhclDvww==~3~")  // 示例加密详细地址
                        .build())
                .cargoInfo(DesensitizedOrderRequest.CargoInfo.builder()
                        .name("测试商品")
                        .count("1")
                        .unit("件")
                        .weight("1.0")
                        .build())
                .senderInfo(DesensitizedOrderRequest.SenderInfo.builder()
                        .mobile("13122917937").name("001").addr("上海市宝山区顾北路东方帕提欧10号楼1301").build())
                .authInfo(PlatformAuthInfo.builder()
                        .partnerId("5797481480").net("pinduoduoWx").build())
                .build();

        EOrderResult result = eExpressClient.submitDesensitizedOrder(request);

        log.info("请求参数: {}", request);
        log.info("响应参数: {}", JacksonUtil.toJson(result));
    }

//    /**
//     * 测试：使用快递100平台授权信息调用脱敏订单接口
//     * <p>
//     * 配置说明：
//     * 1. 在 application.yml 中配置快递100基本信息：
//     * fy:
//     * express:
//     * customer: your_customer_code  // 快递100 customer
//     * key: your_api_key             // 快递100 key
//     * <p>
//     * 2. 在快递100平台完成拼多多平台授权：
//     * - 登录 https://api.kuaidi100.com
//     * - 进入"第三方平台账号授权"
//     * - 选择"拼多多"平台进行授权
//     * - 获取授权信息：partnerId, partnerKey, partnerName, net
//     * <p>
//     * 3. 运行测试前，请替换下面的授权信息
//     */
//    @Test
//    public void testPrintWithRealAuth() {
//        log.info("========================================");
//        log.info("开始测试快递100脱敏订单接口");
//        log.info("========================================");
//
//        // TODO: 替换为实际的快递100平台授权信息
//        String partnerId = "your_partner_id";
//        String partnerKey = "your_partner_key";
//        String partnerName = "your_partner_name";
//        String net = "your_net";
//
//        // 检查是否已配置授权信息
//        if ("your_partner_id".equals(partnerId)) {
//            log.warn("⚠️ 请先配置正确的快递100平台授权信息");
//            log.info("");
//            log.info("📖 配置步骤：");
//            log.info("1. 登录快递100控制台：https://api.kuaidi100.com");
//            log.info("2. 进入\"第三方平台账号授权\"");
//            log.info("3. 选择\"拼多多\"平台进行授权");
//            log.info("4. 授权成功后获取以下信息：");
//            log.info("   - partnerId (合作伙伴ID)");
//            log.info("   - partnerKey (合作伙伴密钥)");
//            log.info("   - partnerName (合作伙伴名称)");
//            log.info("   - net (网络标识)");
//            log.info("5. 在此测试方法中替换上述配置");
//            log.info("");
//            log.info("💡 注意：");
//            log.info("- 此测试使用模拟的加密数据");
//            log.info("- 实际使用时，需要使用真实的拼多多加密收件人信息");
//            log.info("- 加密格式：电话 $...$4$, 姓名 ~...~4~, 地址 ~...~4~");
//            return;
//        }
//
//        // 构建平台授权信息
//        DesensitizedOrderRequest.PlatformAuthInfo authInfo =
//                DesensitizedOrderRequest.PlatformAuthInfo.builder()
//                        .partnerId(partnerId)
//                        .partnerKey(partnerKey)
//                        .partnerName(partnerName)
//                        .net(net)
//                        .build();
//
//        // 构建脱敏订单请求
//        DesensitizedOrderRequest request = DesensitizedOrderRequest.builder()
//                .platformType(PlatformType.PINDUODUO)
//                .authInfo(authInfo)
//                .thirdOrderId("TEST_ORDER_" + System.currentTimeMillis())
//                .expressCom("shunfeng")  // 顺丰速运
//                .recipientInfo(DesensitizedOrderRequest.RecipientEncryptedInfo.builder()
//                        .name("~AgAAAAK6MjwFdMbadQAimOGCTHMINDd7/W2xXlWtlao=~4~")  // 示例加密姓名
//                        .mobile("$AgAAAAK6MjwGdMbadQCrzhfdOcmsmHeJ69OhLqUjh80=$4$")  // 示例加密电话
//                        .province("广东省")
//                        .city("深圳市")
//                        .district("南山区")
//                        .addr("粤海街道科技园")
//                        .detailAddrEnc("~AgAAAA...=~4~")  // 示例加密详细地址
//                        .build())
//                .senderInfo(DesensitizedOrderRequest.SenderInfo.builder()
//                        .name("发件人姓名")
//                        .mobile("13800138000")
//                        .province("广东省")
//                        .city("深圳市")
//                        .district("福田区")
//                        .addr("发件人详细地址")
//                        .build())
//                .cargoInfo(DesensitizedOrderRequest.CargoInfo.builder()
//                        .name("测试商品")
//                        .count("1")
//                        .unit("件")
//                        .weight("1.0")
//                        .build())
//                .build();
//
//        log.info("请求参数：");
//        log.info("  平台: {}", request.getPlatformType().getName());
//        log.info("  订单号: {}", request.getThirdOrderId());
//        log.info("  快递公司: {}", request.getExpressCom());
//        log.info("  收件人(加密): {}", request.getRecipientInfo().getName());
//        log.info("  电话(加密): {}", request.getRecipientInfo().getMobile());
//        log.info("  地址: {}{}{}{}", request.getRecipientInfo().getProvince(),
//                request.getRecipientInfo().getCity(), request.getRecipientInfo().getDistrict(),
//                request.getRecipientInfo().getAddr());
//        log.info("  商品: {} x{}", request.getCargoInfo().getName(), request.getCargoInfo().getCount());
//
//        try {
//            log.info("");
//            log.info("正在调用快递100脱敏订单接口...");
//
//            // 调用快递100接口
//            EOrderResult result = eExpressClient.submitDesensitizedOrder(request);
//
//            // 输出结果
//            log.info("");
//            log.info("========================================");
//            log.info("面单打印结果");
//            log.info("========================================");
//
//            if (result != null) {
//                log.info("返回码: {}", result.getCode());
//                log.info("返回消息: {}", result.getMessage());
//
//                if (result.isSuccess()) {
//                    log.info("");
//                    log.info("✅ 面单打印成功！");
//                    log.info("快递单号: {}", result.getKuaidiNum());
//                    log.info("订单ID: {}", result.getOrderId());
//                    log.info("");
//                    log.info("💡 提示：");
//                    log.info("- 可以使用快递单号在快递公司官网查询物流信息");
//                    log.info("- 订单ID可用于后续的取消订单操作");
//                } else {
//                    log.error("");
//                    log.error("❌ 面单打印失败");
//                    log.error("错误码: {}", result.getCode());
//                    log.error("错误信息: {}", result.getMessage());
//                    log.info("");
//                    log.info("💡 常见错误：");
//                    log.info("- 1001: 授权信息错误，请检查 partnerId、partnerKey 等");
//                    log.info("- 1002: 加密格式错误，请确保加密数据格式正确");
//                    log.info("- 1003: 参数缺失，请检查必填参数是否完整");
//                }
//            } else {
//                log.error("");
//                log.error("❌ 接口返回结果为空");
//                log.info("请检查网络连接和快递100配置");
//            }
//
//        } catch (Exception e) {
//            log.error("");
//            log.error("调用快递100接口异常: {}", e.getMessage(), e);
//            log.info("");
//            log.info("💡 请检查：");
//            log.info("1. application.yml 中是否配置了 fy.express.customer 和 fy.express.key");
//            log.info("2. 网络连接是否正常");
//            log.info("3. 快递100平台授权信息是否正确");
//        }
//    }
}