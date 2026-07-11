package com.ruoyi.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.consts.AdminRedisKey;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.param.logistics.LogisticsUpdateParam;
import com.ruoyi.jky.rep.logistics.LogisticsUpdateRep;
import com.ruoyi.jky.util.JkyResponseUtil;
import com.ruoyi.order.facade.IJkyLogisticsTaskFacade;
import com.ruoyi.order.model.bo.JkyLogisticsTaskBO;
import com.ruoyi.order.model.param.JkyLogisticsTaskParam;
import com.ruoyi.order.model.query.JkyLogisticsTaskQuery;
import com.ruoyi.biz.bill.BillBizService;
import com.ruoyi.mapper.jky.JkyLogisticsConvert;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.query.OrderQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 吉客云物流更新延迟任务——每分钟轮询待处理的物流更新任务。
 */
@Slf4j
@Component("jkyLogisticsTaskJob")
public class JkyLogisticsTaskJob {

    @Autowired
    private IJkyLogisticsTaskFacade jkyLogisticsTaskFacade;

    @Autowired
    private JkyTemplate jkyTemplate;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IOrderFacade orderFacade;

    @Autowired
    private BillBizService billBizService;

    /**
     * 每分钟执行一次，轮询待处理的物流更新任务并执行。
     */
    public void execute() {
        redisCache.tryLockRun(AdminRedisKey.Jky.LOGISTICS_TASK_LOCK, 5L, TimeUnit.MINUTES, "吉客云物流更新任务", this::doSync);
    }

    private void doSync() {
        try {
            List<JkyLogisticsTaskBO> taskList = jkyLogisticsTaskFacade.list(
                    new JkyLogisticsTaskQuery()
                            .setStatus(0)
                            .setLeExecuteTime(DateUtil.date()));
            if (CollectionUtil.isEmpty(taskList)) {
                return;
            }
            for (JkyLogisticsTaskBO task : taskList) {
                processTask(task);
            }
        } catch (Exception e) {
            log.error("吉客云物流更新任务执行异常", e);
        }
    }

    private void processTask(JkyLogisticsTaskBO task) {
        try {
            LogisticsUpdateParam param = new LogisticsUpdateParam();
            param.setBizdata(Collections.singletonList(JkyLogisticsConvert.INSTANCE.toItem(task)));

            JkyResponse<List<LogisticsUpdateRep>> response = jkyTemplate.updateLogisticsInfo(param);

            List<LogisticsUpdateRep> dataList = JkyResponseUtil.getData(response);
            LogisticsUpdateRep first = CollectionUtil.isNotEmpty(dataList) ? dataList.get(0) : null;

            if (first == null || !Boolean.TRUE.equals(first.getIsSuccess())) {
                String errorMsg = first != null ? first.getError() : "响应数据为空";
                log.error("订单号：{}，吉客云物流更新失败：{}", task.getOrderCode(), errorMsg);
                jkyLogisticsTaskFacade.update(
                        new JkyLogisticsTaskParam().setStatus(2).setErrorMsg(errorMsg).setUpdateTime(DateUtil.date()).setRetryCount(task.getRetryCount() != null ? task.getRetryCount() + 1 : 1),
                        new JkyLogisticsTaskQuery().setId(task.getId()));
                return;
            }

            jkyLogisticsTaskFacade.update(
                    new JkyLogisticsTaskParam().setStatus(1).setUpdateTime(DateUtil.date()),
                    new JkyLogisticsTaskQuery().setId(task.getId()));
            log.info("订单号：{}，吉客云物流更新成功", task.getOrderCode());

            tryGenerateBill(task);
        } catch (Exception e) {
            log.error("订单号：{}，吉客云物流更新失败：{}", task.getOrderCode(), e.getMessage(), e);
            jkyLogisticsTaskFacade.update(
                    new JkyLogisticsTaskParam().setStatus(2).setErrorMsg(e.getMessage()).setRetryCount(task.getRetryCount() != null ? task.getRetryCount() + 1 : 1).setUpdateTime(DateUtil.date()),
                    new JkyLogisticsTaskQuery().setId(task.getId()));
        }
    }

    /**
     * 代发订单物流同步成功后，按供应商账期出账。
     */
    private void tryGenerateBill(JkyLogisticsTaskBO task) {
        try {
            OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(task.getOrderCode()));
            if (Objects.nonNull(orderBO)
                    && Objects.equals(OrderConsts.OrderType.O2O.getCode(), orderBO.getOrderType())
                    && Objects.equals(OrderConsts.OrderStatus.DELIVERY_END.getCode(), orderBO.getStatus())) {
                billBizService.generateBill(orderBO);
            }
        } catch (Exception e) {
            log.error("订单号：{}，出账失败：{}", task.getOrderCode(), e.getMessage(), e);
        }
    }
}
