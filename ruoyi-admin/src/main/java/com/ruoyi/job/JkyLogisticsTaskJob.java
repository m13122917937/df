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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
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
            LogisticsUpdateParam.LogisticsUpdateItem item = new LogisticsUpdateParam.LogisticsUpdateItem();
            item.setOrderNo(task.getErpOrderId() != null ? task.getErpOrderId() : task.getOrderCode());
            item.setLogisticNo(task.getLogisticsNo());
            item.setLogisticName(task.getLogisticsName());
            item.setLogisticCode(task.getLogisticsCode());
            LogisticsUpdateParam param = new LogisticsUpdateParam();
            param.setBizdata(Collections.singletonList(item));
            JkyResponse<List<LogisticsUpdateRep>> response = jkyTemplate.updateLogisticsInfo(param);

            List<LogisticsUpdateRep> dataList = JkyResponseUtil.getData(response);
            LogisticsUpdateRep first = CollectionUtil.isNotEmpty(dataList) ? dataList.get(0) : null;

            if (first == null || !Boolean.TRUE.equals(first.getIsSuccess())) {
                String errorMsg = first != null ? first.getError() : "响应数据为空";
                log.error("订单号：{}，吉客云物流更新失败：{}", task.getOrderCode(), errorMsg);
                jkyLogisticsTaskFacade.update(
                        new JkyLogisticsTaskParam()
                                .setStatus(2)
                                .setErrorMsg(errorMsg)
                                .setRetryCount(task.getRetryCount() != null ? task.getRetryCount() + 1 : 1)
                                .setUpdateTime(DateUtil.date()),
                        new JkyLogisticsTaskQuery().setId(task.getId()));
                return;
            }

            jkyLogisticsTaskFacade.update(
                    new JkyLogisticsTaskParam().setStatus(1).setUpdateTime(DateUtil.date()),
                    new JkyLogisticsTaskQuery().setId(task.getId()));
            log.info("订单号：{}，吉客云物流更新成功", task.getOrderCode());
        } catch (Exception e) {
            log.error("订单号：{}，吉客云物流更新失败：{}", task.getOrderCode(), e.getMessage(), e);
            jkyLogisticsTaskFacade.update(
                    new JkyLogisticsTaskParam()
                            .setStatus(2)
                            .setErrorMsg(e.getMessage())
                            .setRetryCount(task.getRetryCount() != null ? task.getRetryCount() + 1 : 1)
                            .setUpdateTime(DateUtil.date()),
                    new JkyLogisticsTaskQuery().setId(task.getId()));
        }
    }
}
