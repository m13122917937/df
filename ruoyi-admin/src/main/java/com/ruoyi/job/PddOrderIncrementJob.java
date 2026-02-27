package com.ruoyi.job;

import cn.hutool.core.util.StrUtil;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.api.pop.request.PddOrderNumberListIncrementGetRequest;
import com.pdd.pop.sdk.http.api.pop.response.PddOrderNumberListIncrementGetResponse;
import com.ruoyi.bill.constant.PayerConsts;
import com.ruoyi.bill.facade.IPayerConfigFacade;
import com.ruoyi.bill.model.bo.PayerConfigBO;
import com.ruoyi.bill.model.param.PayerConfigParam;
import com.ruoyi.bill.model.query.PayerConfigQuery;
import com.ruoyi.order.facade.IPddOrderIncrementFacade;
import com.ruoyi.order.model.param.PddOrderIncrementParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 拼多多增量订单同步定时任务
 *
 * @author ruoyi
 * @date 2025-02-08
 */
@Slf4j
@Component()
public class PddOrderIncrementJob {

    @Autowired
    PopClient popClient;

    @Autowired
    IPayerConfigFacade payerConfigFacade;

    @Autowired
    IPddOrderIncrementFacade pddOrderIncrementFacade;

    /**
     * 默认拉取最近多少秒的订单（24小时 = 86400秒）
     */
    private static final long DEFAULT_TIME_RANGE_SECONDS = 86400L;

    /**
     * 每页拉取数量
     */
    private static final int PAGE_SIZE = 100;

    /**
     * 执行拼多多订单增量同步任务
     */
    @Transactional
    public void execute() {
        log.info("===== 开始执行拼多多订单增量定时任务 =====");
        // 获取所有拼多多平台的配置
        List<PayerConfigBO> configList = payerConfigFacade.list(new PayerConfigQuery().setPlatform(PayerConsts.Platform.PDD.getMessage()), null);

        if (configList == null || configList.isEmpty()) {
            log.warn("未找到拼多多平台配置，任务结束");
            return;
        }

        log.info("找到{}个拼多多店铺配置", configList.size());

        // 遍历每个店铺配置，拉取增量订单
        for (PayerConfigBO payerConfig : configList) {
            try {
                syncIncrementOrders(payerConfig);
            } catch (Exception e) {
                log.error("处理店铺[{}]时发生异常", payerConfig.getKeyWord(), e);
            }
        }
        log.info("===== 结束执行拼多多订单增量定时任务 =====");
    }

    /**
     * 同步单个店铺的增量订单
     *
     * @param payerConfig 店铺配置
     */
    private void syncIncrementOrders(PayerConfigBO payerConfig) {
        if (StrUtil.isBlank(payerConfig.getToken())) {
            log.warn("店铺[{}]的token为空，跳过同步", payerConfig.getKeyWord());
            return;
        }

        log.info("开始同步店铺[{}]的增量订单", payerConfig.getKeyWord());

        // 计算时间范围
        long endTime = System.currentTimeMillis() / 1000; // 当前时间（秒级时间戳）
        long startTime = endTime - DEFAULT_TIME_RANGE_SECONDS; // 默认拉取最近24小时

        // 如果配置表中有上次同步时间，可以使用上次同步时间作为起始时间
        if (payerConfig.getLastSyncTime() != null) {
            startTime = payerConfig.getLastSyncTime();
        }
        //多多限制接口只能拉30分钟内的数据
        if ((endTime - startTime) > 1800) {
            endTime = startTime + 1800;
        }

        int page = 1;
        boolean hasNext = true;
        int totalSuccess = 0;
        int totalFail = 0;
        int totalOrder = 0;

        // 分页拉取订单数据
        while (hasNext) {
            try {
                // 构建请求参数
                PddOrderNumberListIncrementGetRequest request = buildRequest(startTime, endTime, page);

                // 调用拼多多API
                PddOrderNumberListIncrementGetResponse response = popClient.syncInvoke(request, payerConfig.getToken());

                // 检查响应
                if (response == null || response.getOrderSnIncrementGetResponse() == null) {
                    log.warn("店铺[{}]第{}页响应为空", payerConfig.getKeyWord(), page);
                    break;
                }

                // 获取订单列表
                List<PddOrderNumberListIncrementGetResponse.OrderSnIncrementGetResponseOrderSnListItem> orderSnList = response.getOrderSnIncrementGetResponse().getOrderSnList();
                if (orderSnList == null || orderSnList.isEmpty()) {
                    log.info("店铺[{}]第{}页无订单数据", payerConfig.getKeyWord(), page);
                    break;
                }

                log.info("店铺[{}]第{}页获取到{}条订单", payerConfig.getKeyWord(), page, orderSnList.size());

                // 处理每一条订单
                for (PddOrderNumberListIncrementGetResponse.OrderSnIncrementGetResponseOrderSnListItem orderInfo : orderSnList) {
                    try {
                        saveOrUpdateOrder(orderInfo, payerConfig, endTime);
                        totalSuccess++;
                    } catch (Exception e) {
                        totalFail++;
                        log.error("处理订单[{}]失败", orderInfo.getOrderSn(), e);
                    }
                }

                totalOrder += orderSnList.size();

                // 判断是否还有下一页
                hasNext = (response.getOrderSnIncrementGetResponse().getHasNext()) != null && response.getOrderSnIncrementGetResponse().getHasNext();
                ;
                if (hasNext) {
                    page++;
                }

            } catch (Exception e) {
                log.error("店铺[{}]拉取第{}页订单时发生异常", payerConfig.getKeyWord(), page, e);
                break;
            }
        }
        log.info("店铺[{}]增量订单同步完成，共处理{}条，成功{}条，失败{}条", payerConfig.getKeyWord(), totalOrder, totalSuccess, totalFail);

        payerConfigFacade.update(new PayerConfigParam().setLastSyncTime(endTime), new PayerConfigQuery().setId(payerConfig.getId()));
        log.info("店铺[{}]最后同步时间已更新为: {}", payerConfig.getKeyWord(), endTime);


    }

    /**
     * 构建请求参数
     *
     * @param startTime 开始时间（秒级时间戳）
     * @param endTime   结束时间（秒级时间戳）
     * @param page      页码
     * @return 请求对象
     */
    private PddOrderNumberListIncrementGetRequest buildRequest(long startTime, long endTime, int page) {
        PddOrderNumberListIncrementGetRequest request = new PddOrderNumberListIncrementGetRequest();
        request.setStartUpdatedAt(startTime);
        request.setEndUpdatedAt(endTime);
        request.setOrderStatus(5);        // 1:待发货
        request.setRefundStatus(5);       // 0:无售后
        request.setIsLuckyFlag(0);
        request.setPage(page);
        request.setPageSize(PAGE_SIZE);
        request.setUseHasNext(true);      // 使用hasNext判断是否有下一页
        return request;
    }

    /**
     * 保存或更新订单
     *
     * @param orderInfo    拼多多订单信息
     * @param payerConfig  店铺配置
     * @param endUpdatedAt 数据抓取结束时间
     */
    private void saveOrUpdateOrder(PddOrderNumberListIncrementGetResponse.OrderSnIncrementGetResponseOrderSnListItem orderInfo, PayerConfigBO payerConfig, Long endUpdatedAt) {
        // 构建订单参数
        PddOrderIncrementParam param = buildOrderParam(orderInfo, payerConfig);
        if (Objects.isNull(param)) {
            return;
        }
        // 保存或更新（根据订单号判断是否存在）
        pddOrderIncrementFacade.saveOrUpdate(param);

        log.debug("订单[{}]处理成功", orderInfo.getOrderSn());
    }

    /**
     * 构建订单参数对象
     *
     * @param orderInfo   拼多多订单信息
     * @param payerConfig 店铺配置
     * @return 订单参数
     */
    private PddOrderIncrementParam buildOrderParam(PddOrderNumberListIncrementGetResponse.OrderSnIncrementGetResponseOrderSnListItem orderInfo, PayerConfigBO payerConfig) {

        if (StrUtil.isBlank(orderInfo.getReceiverAddress())) {
            return null;
        }

        PddOrderIncrementParam param = new PddOrderIncrementParam();

        // ========== 订单基本信息 ==========
        param.setOrderSn(orderInfo.getOrderSn());
        param.setPayerConfigId(payerConfig.getId());
        param.setPayerName(payerConfig.getKeyWord());

        // ========== 收件人原始信息 ==========
        param.setReceiverName(orderInfo.getReceiverName());
        param.setReceiverPhone(orderInfo.getReceiverPhone());
        param.setReceiverAddress(orderInfo.getReceiverAddress());
        param.setProvince(orderInfo.getProvince());
        param.setCity(orderInfo.getCity());
        param.setDistrict(orderInfo.getTown());

        // ========== 订单标签 ==========
        param.setOrderTag(buildOrderTag(orderInfo.getOrderTagList()));

        // ========== 商品信息（从拼多多API读取）==========
        PddOrderNumberListIncrementGetResponse.OrderSnIncrementGetResponseOrderSnListItemItemListItem orderItem = orderInfo.getItemList().get(0);
        // 商品名称（如果没有获取到，使用默认值）
        param.setCargoName(orderItem.getGoodsName());
        // 商品数量（如果没有获取到，默认为1）
        param.setCargoCount(orderItem.getGoodsCount().intValue());
        // 商品单位（默认为"件"）
        param.setCargoUnit("件");
        // 商品重量（如果没有获取到，使用默认值1.0）
        param.setCargoWeight("1.0");

        // ========== 快递公司编码（默认使用顺丰，可根据配置修改）==========
        param.setExpressCom("shunfeng");

        return param;
    }

    /**
     * 构建订单标签
     *
     * @param orderTagList 订单标签列表
     * @return 订单标签（如果包含微派服务则返回"微派"，否则返回null或空）
     */
    private Integer buildOrderTag(List<PddOrderNumberListIncrementGetResponse.OrderSnIncrementGetResponseOrderSnListItemOrderTagListItem> orderTagList) {
        if (orderTagList == null || orderTagList.isEmpty()) {
            return null;
        }

        // 遍历标签列表，查找 has_weipai_service:1
        for (PddOrderNumberListIncrementGetResponse.OrderSnIncrementGetResponseOrderSnListItemOrderTagListItem tag : orderTagList) {
            if (Objects.equals("has_weipai_service", tag.getName()) && Objects.equals(1, tag.getValue())) {
                return 1;
            }
        }
        return null;
    }
}
