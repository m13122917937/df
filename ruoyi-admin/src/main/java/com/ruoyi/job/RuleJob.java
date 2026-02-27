package com.ruoyi.job;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.order.model.query.RuleQuery;
import com.ruoyi.rule.facade.IRuleFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component("ruleJob")
public class RuleJob {

    @Autowired
    IRuleFacade ruleFacade;

    /**
     * 每日清除规则的定时任务
     */
    @Transactional
    public void execute() {
        log.info("开始执行清除规则定时任务");

        DateTime dateTime = DateUtil.yesterday().setField(DateField.HOUR, 18).setField(DateField.MINUTE, 0);

        ruleFacade.delete(new RuleQuery().setGtCreateTime(dateTime));

        log.info("结束执行清除规则定时任务");
    }

}
