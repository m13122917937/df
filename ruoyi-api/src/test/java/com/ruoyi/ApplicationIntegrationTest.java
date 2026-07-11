package com.ruoyi;

import com.ruoyi.biz.mq.MsgClient;
import com.ruoyi.biz.order.ImeiBizService;
import com.ruoyi.capital.facade.ICompanyCapitalFacade;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.consts.WebConstants;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.kuaidi100.ExpressClient;
import com.ruoyi.kuaidi100.model.SubscribeExpressCode;
import com.ruoyi.kuaidi100.model.SubscribeExpressParam;
import com.ruoyi.order.facade.IImeiSkuRelFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.bo.ImeiSkuRelBO;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.BrandConsts;
import com.ruoyi.order.model.consts.ImeiConsts;
import com.ruoyi.order.model.query.ImeiSkuRelQuery;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.sn.SnQueryClient;
import com.ruoyi.sn.model.WarrantyResult;
import com.ruoyi.system.facade.ISysDictTypeFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
@SpringBootTest(classes = ApiApplication.class)
public class ApplicationIntegrationTest {


    @Autowired
    ExpressClient expressClient;
    @Autowired
    ISysDictTypeFacade dictTypeService;
    @Autowired
    private MsgClient msgClient;

    @Autowired
    ICompanyCapitalFacade companyCapitalFacade;

    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;

    @Autowired
    RuoYiConfig ruoYiConfig;

    @Autowired
    private ImeiBizService imeiBizService;

    @Autowired
    private SnQueryClient snQueryClient;

    @Autowired
    private IImeiSkuRelFacade imeiSkuRelFacade;

    @Autowired
    private IOrderFacade orderFacade;


    /**
     * 调试串码验证"型号不一致"问题
     *
     * 数据:
     *   SN: 3B166A00B9G00000
     *   IMEI: 861319085935155
     *   订单号: 1ccedea372919000
     *
     * 验证逻辑 (ImeiBizService.getImeiBO):
     *   1. 根据品牌决定使用SN还是IMEI查06API:
     *      - OPPO/VIVO/IQOO/一加 → 用IMEI查
     *      - 其他品牌 → 用SN查
     *   2. 06API返回model、activated等信息
     *   3. 按 warranty.getModel() 查 ImeiSkuRel 表:
     *      - 无记录 → 新建 WAIT 记录，不会报型号不一致
     *      - 有 OK 记录且 skuName/productName 匹配 → SUCCESS
     *      - 有 OK 记录但 skuName/productName 不匹配 → MODEL_NOT_CONSISTENT(3)
     */
    @Test
    public void debugImeiModelMismatch() throws Exception {
        String sn = "3B166A00B9G00000";
        String imei = "861319085935155";
        String orderCode = "1ccedea372919000";

        // ========== 第一步：查询订单信息 ==========
        OrderBO orderBO = orderFacade.getOne(
                new OrderQuery().setOrderCode(orderCode));
        if (orderBO == null) {
            System.err.println("订单不存在: " + orderCode);
            return;
        }
        System.out.println("=== 订单信息 ===");
        System.out.println("品牌: " + orderBO.getBrand());
        System.out.println("品类: " + orderBO.getCategory());
        System.out.println("商品名: " + orderBO.getProductName());
        System.out.println("规格: " + orderBO.getSkuName());

        // ========== 第二步：确定查询值 ==========
        String snQ = BrandConsts.SN_LIST.contains(orderBO.getBrand()) ? imei : sn;
        if (snQ == null || snQ.isBlank()) snQ = sn;
        System.out.println("\n=== 06API 查询 ===");
        System.out.println("查询值: " + snQ + " (品牌=" + orderBO.getBrand()
                + (snQ.equals(imei) ? ", 使用IMEI查询" : ", 使用SN查询") + ")");

        // ========== 第三步：调用06API查询 ==========
        WarrantyResult warranty = snQueryClient.query(snQ, orderBO.getBrand());
        System.out.println("06API返回: exits=" + warranty.getExits()
                + ", model=[" + warranty.getModel() + "]"
                + ", activated=" + warranty.getActivated()
                + ", description=" + warranty.getDescription());

        if (!Boolean.TRUE.equals(warranty.getExits())) {
            System.out.println("\n结论: 串码不存在(06API返回exits=false)，不会进入型号匹配逻辑");
            return;
        }

        // ========== 第四步：查 ImeiSkuRel 表 ==========
        System.out.println("\n=== ImeiSkuRel 匹配 ===");
        String model = warranty.getModel();
        System.out.println("按 model=[" + model + "] 查询 ImeiSkuRel...");

        ImeiSkuRelBO rel = imeiSkuRelFacade.getOne(
                new ImeiSkuRelQuery().setLimit(1)
                        .setStatus(ImeiConsts.ImeiRel.OK.getCode())
                        .setSnModel(model));

        if (rel == null) {
            System.out.println("未找到该model的已确认(OK)记录 → 会新建WAIT记录，不会报型号不一致");
        } else {
            System.out.println("找到已确认记录:");
            System.out.println("  rel.productName=" + rel.getProductName());
            System.out.println("  rel.skuName=" + rel.getSkuName());
            System.out.println("  rel.brand=" + rel.getBrand());
            System.out.println("  rel.category=" + rel.getCategory());
            System.out.println("  rel.snModel=" + rel.getSnModel());
            System.out.println("  rel.status=" + rel.getStatus());

            boolean nameMatch = java.util.Objects.equals(
                    cn.hutool.core.util.StrUtil.trim(rel.getSkuName()),
                    cn.hutool.core.util.StrUtil.trim(orderBO.getSkuName()))
                    && java.util.Objects.equals(
                    cn.hutool.core.util.StrUtil.trim(rel.getProductName()),
                    cn.hutool.core.util.StrUtil.trim(orderBO.getProductName()));

            System.out.println("\n型号匹配结果: " + (nameMatch ? "一致 ✓" : "不一致 ✗"));
            if (!nameMatch) {
                System.out.println("  订单商品名: [" + orderBO.getProductName() + "]");
                System.out.println("  记录商品名: [" + rel.getProductName() + "]");
                System.out.println("  订单规格:   [" + orderBO.getSkuName() + "]");
                System.out.println("  记录规格:   [" + rel.getSkuName() + "]");
            }
        }

        // ========== 第五步：检查是否有同model但不同状态的记录 ==========
        ImeiSkuRelBO anyRel = imeiSkuRelFacade.getOne(
                new ImeiSkuRelQuery().setLimit(1).setSnModel(model));
        if (anyRel != null && !java.util.Objects.equals(anyRel.getStatus(),
                rel != null ? rel.getStatus() : null)) {
            System.out.println("\n其他状态的记录: status=" + anyRel.getStatus());
        }
    }


//    @Test
//    public void throws ApiException {
//        fadadaBizService.xxx();
//    }

    @Test
    public void test() {

        RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setOrderCode("1bedb6539ed90000"));

        SubscribeExpressCode subscribeExpressCode = expressClient.subscribeExpress(new SubscribeExpressParam().setExpressNo(routeSubscribeBO.getLogisticsNo())
                .setExpressCode(routeSubscribeBO.getLogisticsCode()).setOrderId(routeSubscribeBO.getOrderCode()).setCellphone(routeSubscribeBO.getPhone())
                .setExpressCallBackUrl(String.format(WebConstants.EXPRESS_NOTIFY_URL, ruoYiConfig.getHost())));

        System.out.println(subscribeExpressCode);
    }


    @Test
    public void erwer() {

        // 订阅快递信息
        SubscribeExpressCode subscribeExpressCode = expressClient.subscribeExpress(new SubscribeExpressParam().setExpressNo("SF3272987496015")
                .setExpressCode("sf").setOrderId("1ba824eb6f119000").setCellphone("18739934509")
                .setExpressCallBackUrl(String.format(WebConstants.EXPRESS_NOTIFY_URL, "https://api.wujievip.cn")));
        System.out.println(subscribeExpressCode);
        //  扣保证金
//        companyCapitalFacade.freezeAndUnFreeze(new CompanyCapitalLogParam().setCompanyId(1L).setCreateTime(DateUtil.date()).setOutAmount(new BigDecimal(-50))
//                .setOrderNo("123123123").setType(CompanyCapitalConsts.LogTypes.ORDER.getCode()));


//        companyCapitalFacade.freezeAndUnFreeze(new CompanyCapitalLogParam().setCompanyId(1L).setUpdateTime(DateUtil.date()).setAddAmount(new BigDecimal(50))
//                .setOrderNo("123123123").setType(CompanyCapitalConsts.LogTypes.ORDER.getCode()));

    }


    @Test
    public void x() {
        SubscribeExpressParam subscribeExpressParam = new SubscribeExpressParam();
        subscribeExpressParam.setExpressCode("shunfeng");
        subscribeExpressParam.setExpressNo("SF0257574715016");
        subscribeExpressParam.setCellphone("13122917937");
        subscribeExpressParam.setExpressCallBackUrl("13122917937");
        SubscribeExpressCode subscribeExpressCode = expressClient.subscribeExpress(subscribeExpressParam);
        System.out.println(subscribeExpressCode);

    }

}
