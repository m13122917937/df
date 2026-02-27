package com.ruoyi.biz.express;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.biz.express.bean.ExpressCallbackStatusCode;
import com.ruoyi.biz.express.bean.ExpressUtils;
import com.ruoyi.biz.express.bean.LogisticsStateParam;
import com.ruoyi.biz.express.impl.*;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.kuaidi100.model.SubscribePushData;
import com.ruoyi.kuaidi100.model.SubscribePushResult;
import com.ruoyi.kuaidi100.model.consts.LogisticsStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 切换不同状态Context
 *
 * @author nlsm
 */
@Slf4j
public class LogisticsContext {

    /** 物流物流状态接口 */
    private final LogisticsState logisticsState;

    /** 物流信息状态Param */
    private final LogisticsStateParam STATE_PARAM = new LogisticsStateParam();

    /**
     * 获取物流状态Bean
     *
     * @param statusCode 监控状态
     * @param lastResult 物流信息
     * @param autoSales 是否需要自动退货追单
     */
    public LogisticsContext(String statusCode, SubscribePushResult lastResult, Boolean autoSales) {
        STATE_PARAM.setState(lastResult.getState());
        STATE_PARAM.setLogisticsNo(lastResult.getNu());
        STATE_PARAM.setRouteInfo(lastResult.getRouteInfo());
        // 判断是否有退货节点
        // 判断是否需要自动退货追单
        Boolean salesNode = Boolean.FALSE;
        if (Boolean.TRUE.equals(autoSales)) {
            salesNode = this.salesNode(lastResult.getData());
        }
        STATE_PARAM.setSalesReturn(salesNode);
        // 赋值执行状态处理
        this.logisticsState = this.getState(statusCode,  salesNode);
        // 赋值物流时间
        setLogisticsDate(lastResult);
    }

    /**
     * 赋值物流时间
     * @param lastResult 物流信息
     */
    private void setLogisticsDate(SubscribePushResult lastResult) {
        // 赋值查找签收时间和揽收时间
        for (SubscribePushData ex : lastResult.getData()) {
            // 正常签收和退回签收都是签收
            if (LogisticsStatus.signed(ex.getStatusCode()) || LogisticsStatus.rejected(ex.getStatusCode())) {
                STATE_PARAM.setSignDate(DateUtil.parse(ex.getFtime()));
            }
            // 取出揽收时间
            if (LogisticsStatus.collected(ex.getStatusCode())) {
                STATE_PARAM.setCollectDate(DateUtil.parse(ex.getFtime()));
            }
        }

        // 兜底,如果上述没有找到揽收时间,则取最后一条数据
        if (Objects.isNull(STATE_PARAM.getCollectDate()) && CollUtil.isNotEmpty(lastResult.getData())) {
            int size = lastResult.getData().size();
            SubscribePushData subscribePushData = lastResult.getData().get(size - 1);
            STATE_PARAM.setCollectDate(DateUtil.parse(subscribePushData.getFtime()));
        }
        // 兜底, 如果签收状态,则取第一条数据为签收时间
        if (Objects.isNull(STATE_PARAM.getSignDate())) {
            if (LogisticsStatus.signed(lastResult.getState()) || LogisticsStatus.rejected(lastResult.getState())) {
                // 取出最后一条数据为签收时间
                SubscribePushData subscribePushData = lastResult.getData().get(0);
                STATE_PARAM.setSignDate(DateUtil.parse(subscribePushData.getFtime()));
            }
        }
    }

    /**
     * 判断是否有退货节点
     * @param data 路由节点集合
     * @return true退货 false没有退货
     */
    private Boolean salesNode(List<SubscribePushData> data) {
        if (CollUtil.isNotEmpty(data)) {
            Set<String> contextSet = data.stream().map(SubscribePushData::getContext).collect(Collectors.toSet());
            return CollUtil.contains(contextSet, c -> c.contains(ExpressUtils.LOGISTICS_SALES_NODE_STR));
        }
        return false;
    }

    /**
     * 物流状态处理实体获取
     *
     * @param statusCode  跟踪状态
     * @param salesReturn 是否退单
     * @return 物流实体
     */
    private LogisticsState getState(String statusCode, Boolean salesReturn) {
        // 监控中
        if (Objects.equals(ExpressCallbackStatusCode.POLLING_STATUS.getCode(), statusCode)) {
            if (Boolean.TRUE.equals(salesReturn)) {
                // 拦截到有退单，走签收异常逻辑
                return SpringUtils.getBean(RejectStateService.class);
            }
            // 将异常订单转到正常
            return SpringUtils.getBean(AnomalyStateService.class);
        }
        // 推送结束
        if (Objects.equals(ExpressCallbackStatusCode.SHUTDOWN_STATUS.getCode(), statusCode)) {
            if (LogisticsStatus.rejected(STATE_PARAM.getState())) {
                // 拒签状态
                RejectStateService stateService = SpringUtils.getBean(RejectStateService.class);
                return stateService;
            } else if (LogisticsStatus.signed(STATE_PARAM.getState())) {
                // 签收状态
                return SpringUtils.getBean(SignStateService.class);
            } else if (LogisticsStatus.knotty(STATE_PARAM.getState())) {
                // 疑难状态
                return SpringUtils.getBean(KnottyStateService.class);
            }
        }
        // 推送终止
        if (Objects.equals(ExpressCallbackStatusCode.ABORT_STATUS.getCode(), statusCode)) {
            // 3天查询无记录”或“60天无变化
            return SpringUtils.getBean(AbortStateService.class);
        }
        return null;
    }

    /**
     * 执行操作
     * @param orderCode 订单主键
     */
    public void doAction(String orderCode) {
        if (Objects.isNull(this.logisticsState)) {
            log.info("没有找到对应的状态实现-----orderId={}, no={}, state={}", orderCode, STATE_PARAM.getLogisticsNo(), STATE_PARAM.getState());
            return;
        }
        STATE_PARAM.setOrderId(orderCode);
        logisticsState.doAction(STATE_PARAM);
    }

}
