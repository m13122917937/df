package com.ruoyi;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ruoyi.biz.address.SmartParse;
import com.ruoyi.biz.address.domain.AddressInfo;
import com.ruoyi.biz.order.OrderAddressBizService;
import com.ruoyi.biz.pdd.PddBizService;
import com.ruoyi.capital.facade.ICompanyCapitalFacade;
import com.ruoyi.capital.model.consts.CompanyCapitalConsts;
import com.ruoyi.capital.model.param.CompanyCapitalLogParam;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.manager.RouteSubscribeManager;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.generator.domain.GenTable;
import com.ruoyi.generator.service.IGenTableService;
import com.ruoyi.job.*;
import com.ruoyi.product.facade.IProductSkuFacade;
import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.product.model.query.ProductSkuQuery;
import com.ruoyi.wangdian.param.Pager;
import com.ruoyi.wangdian.param.base.ProviderParams;
import com.ruoyi.wangdian.param.order.TradeQueryParams;
import com.ruoyi.wangdian.param.purchase.create.PurchaseDetailParam;
import com.ruoyi.wangdian.param.purchase.create.PurchaseOrderParam;
import com.ruoyi.wangdian.param.purchase.in.PurchaseOrderInParam;
import com.ruoyi.wangdian.param.purchase.in.StockinOrderHeader;
import com.ruoyi.wangdian.utils.WdtClient;
import com.ruoyi.web.form.order.AddressCompletedParams;
import com.ruoyi.web.form.order.OrderAddForm;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_PATTERN;
import static com.ruoyi.consts.DictEnum.WEB_HOOK_FOLLOW_ORDER;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration
@SpringBootTest(classes = AdminApplication.class)
public class ApplicationIntegrationTest {

    @Autowired
    ICompanyCapitalFacade companyCapitalFacade;

    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;

    @Autowired
    private IGenTableService genTableService;

    @Autowired
    DeliveryOrderJob deliveryOrderJob;

    @Test
    public void testApplicationStarts() {
        deliveryOrderJob.execute();
    }

    @Autowired
    OrderAddressBizService orderAddressBizService;

    @Autowired
    BillToDayJob billToDayJob;

    @Autowired
    WdtClient wdtClient;

    @Autowired
    ExpressErrorJob expressErrorJob;

    @Autowired
    OrderDownJob orderDownJob;


    @Autowired
    SmartParse smartParse;


    @Test
    public void x1111xx() {

        for (int i = 0; i < 1000; i++) {
            TradeQueryParams tradeQueryParams = new TradeQueryParams();
            DateTime endTime = DateUtil.offsetHour(DateUtil.date(), -i);;
            DateTime startTime = DateUtil.offsetHour(endTime, -1);
            tradeQueryParams.setStatus("110");
            tradeQueryParams.setStartTime( DateUtil.format(startTime, DatePattern.NORM_DATETIME_PATTERN));
            tradeQueryParams.setEndTime(DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN) );
            Pager pager = new Pager();
            pager.setPageNo(0);
            pager.setPageSize(200);
            pager.setCalcTotal(0);

            wdtClient.orderList(tradeQueryParams , pager);

        }




    }


    @Autowired
    PddBizService pddBizService;

    @Autowired
    PddOrderIncrementJob pddOrderIncrementJob;

    @Test
    public void dg1dfg() throws Exception {


    }


    @Test
    public void dgdfg() throws Exception {
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
        pddOrderIncrementJob.execute();
    }


    @Test
    public void testExpress() throws IOException, InterruptedException {
//        expressErrorJob.execute();
        // orderDownJob.execute();
        // 创建采购单
        PurchaseOrderParam build = PurchaseOrderParam.builder().purchase_no("1").expect_warehouse_no("dfc").purchaserer_name("李权").receive_warehouse_nos("dfc").provider_no("FY1")
                .purchase_details(List.of(PurchaseDetailParam.builder().spec_no("X35V03-375QSD-160512").num(1).build())).build();
//        wdtClient.purchaseOrderCreate(build);
        // 创建入库单
//        StockinOrderHeader.builder().logistics_code()
//        PurchaseOrderInParam orderInParam = PurchaseOrderInParam.builder().stockin_order().build();
//        wdtClient.purchaseOrderIn(orderInParam);
    }

    @Test
    public void testOrder() throws IOException {
        String uncollectedKey = DictUtils.getDictValue(WEB_HOOK_FOLLOW_ORDER.getValue(), WEB_HOOK_FOLLOW_ORDER.getLabel());
        System.out.println(uncollectedKey);
        ProviderParams params = ProviderParams.builder().provider_name("test001").provider_no("测试").build();
        wdtClient.createProvider(params);

    }

    @Test
    public void xxx() {
        billToDayJob.execute();
    }

    @Test
    public void xxxxx() {
        //收件人：杨程杰[9923] 17283244643 河南省南阳市方城县法院极兔速递[9923]
        AddressCompletedParams addressCompletedParams = new AddressCompletedParams();
        addressCompletedParams.setAddressCompletedParams(new ArrayList<>() {{
            add(new AddressCompletedParams.AddressCompletedParam().setAddressee("杨程杰[9923]").setPhone("17283244643").setReceivingAddress("河南省南阳市方城县法院极兔速递[9923]").setOriginalOrderId("250912-557171361353798"));
        }});
        orderAddressBizService.parse(addressCompletedParams);
    }

    @Test
    public void testHealthEndpoint() throws IOException {

        String[] tableNames = {"f_deduction"};
        genTableService.truncateTable();

        List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames);
        genTableService.importGenTable(tableList, "2121");
        byte[] data = genTableService.downloadCode(tableNames);
        IOUtils.write(data, new FileOutputStream("C:\\Users\\lenovo\\Desktop\\z.zip"));
        ZipUtil.unzip(new File("C:\\Users\\lenovo\\Desktop\\z.zip"), new File("C:\\Users\\lenovo\\Desktop\\z"));
    }

    @Test
    public void testApiEndpoint() {

//        CompanyCapitalLogParam capitalDetailParam = new CompanyCapitalLogParam().setOutAmount(BigDecimal.ZERO)
//                .setOrderNo("12121").setCompanyId(1L).setAddAmount(new BigDecimal("100"))
//                .setRemark("充值").setType(CompanyCapitalConsts.LogTypes.CHARGE.getCode());
//        companyCapitalFacade.changeAvailable(capitalDetailParam);

//        companyCapitalFacade.freezeAndUnFreeze(new CompanyCapitalLogParam().setOrderNo("1212221").setCompanyId(1L)
//                .setOutAmount(new BigDecimal("-100"))
//                .setRemark("12").setType(CompanyCapitalConsts.LogTypes.ORDER.getCode()));

//        companyCapitalFacade.freezeAndUnFreeze(new CompanyCapitalLogParam().setOrderNo("1212221").setCompanyId(1L)
//                .setAddAmount(new BigDecimal("100"))
//                .setRemark("12").setType(CompanyCapitalConsts.LogTypes.ORDER.getCode()));

        RouteSubscribeBO one = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setId(1L));
        System.out.println(one);

    }

    @Autowired
    DataSource dataSource;

    @Test
    public void testJdbcJson() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT detail_info FROM e_route_subscribe WHERE id = ?");
        ps.setLong(1, 1L);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Object obj = rs.getObject("detail_info");
            System.out.println(obj.getClass()); // 应该是 String，不是 byte[]
            System.out.println(obj);
        }
    }

    @Autowired
    IProductSkuFacade productSkuFacade;

    @Test
    public void xx() {
        String brand = "苹果";

//        String ss = "{status: 3, brand: \"\", provinceList: [], orderCode: \"\", purchaseType: 2}";
//        HttpResponse authorization = HttpRequest.post("https://api.xingshiapp.cn/search/demand/drop/shipping/hang/orders?pageNum=1&pageSize=100").body(ss)
//                .header("accept", "application/json, text/plain, */*")
//                .header(  "accept-language", "zh-CN,zh;q=0.9,en;q=0.8,zh-HK;q=0.7,zh-TW;q=0.6,tr;q=0.5")
//               .header(   "authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ0b2tlbiI6IjE3NTkwMjYyMzkyMzQ5Mjg2NjYiLCJsb2dpbl91c2VyIjoiWDc0SDQzUUdYUyIsInBsYXRmb3JtIjoib3BlcmF0aW9uIiwidmVyc2lvbiI6M30.MSJ7NlaK_f4QK2kiqhidnuOUa1mZ4V4E-YT1jRE44BE")
//                .header(  "content-type", "application/json;charset=UTF-8")
//               .header(   "env", "gray")
//                .header(  "pageid", "15010103")
//                .header(  "plat-form", "operation-web")
//                .header(  "priority", "u=1, i")
//                .header(  "pvid", "6267907a1c4aeb5e_mgklakgc_qrn0")
//                .header(  "sec-ch-ua-mobile", "?0")
//                .header(  "sec-ch-ua-platform", "\"Windows\"")
//                .header(  "sec-fetch-dest", "empty")
//                .header(  "sec-fetch-mode", "cors")
//                .header(  "sec-fetch-site", "same-site")
//                .execute();
        String body = FileUtil.readString("C:\\Users\\lenovo\\Desktop\\123.txt", Charset.forName("utf-8"));
        Map parse = JacksonUtil.parse(body, Map.class);
        Map data = (Map) parse.get("data");
        List<Map> row = (List<Map>) data.get("rows");
//        String sss = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImxvZ2luX3VzZXJfa2V5IjoiMzVjYWQ4NDMtMjY5MS00MTYxLTk2ODAtOTU3MWVmNTZiZmRmIn0.ANiunanWfbW-_pjVPA3qpqkhwGqM8bNltJov-zEVFPuPnG93hCLlKfY3nKXhnVBaooXdi7CNMcoHUdKbpQCGAg";
        for (Map map : row) {
            OrderAddForm orderAddForm = new OrderAddForm();
            orderAddForm.setShopName((String) (map.get("orderSource")));
//            orderAddForm.setCategory((String) map.get("category"));
            orderAddForm.setOriginalOrderId((String) (map.get("originalOrderId")));
            orderAddForm.setErpOrderId((String) (map.get("originalOrderId")));
            try {
                orderAddForm.setErpTradeTime(DateUtil.parse((String) (map.get("gjCreateTime")), NORM_DATETIME_PATTERN));
                orderAddForm.setLastShippingTime(DateUtil.parse((String) (map.get("lastShippingTime")), NORM_DATETIME_PATTERN));

                orderAddForm.setOriginalOrderId((String) map.get("originalOrderId"));
                orderAddForm.setPlatform((String) map.get("platform"));
//                orderAddForm.setProductName((String) map.get("commonName"));
                orderAddForm.setQuantity(((Integer) map.get("quantity")).longValue());
//                orderAddForm.setProvince(((Integer) map.get("provinceId")).longValue());
//                orderAddForm.setCity(((Integer) map.get("cityId")).longValue());
                orderAddForm.setShopName((String) map.get("orderSource"));
                orderAddForm.setSkuCode((String) map.get("skuCode"));
//                orderAddForm.setSkuName((String) map.get("productModel"));
//                orderAddForm.setBrand((String) map.get("brand"));
//                ProductSkuBO one = productSkuFacade.getOne(new ProductSkuQuery().setProductName(orderAddForm.getProductName()).setSpecName(orderAddForm.getSkuName()));
//                orderAddForm.setSkuCode(one.getSkuCode());

                String authorization1 = HttpRequest.post("http://127.0.0.1:7772/order/new/add").body(JacksonUtil.toJson(orderAddForm)).execute().body();
                System.out.println(authorization1);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}