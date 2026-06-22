-- 二销验证状态同步定时任务（每 5 分钟执行一次）
INSERT INTO sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
SELECT '二销验证状态同步', 'DEFAULT', 'secondSaleVerifyJob.execute()', '0 0/5 * * * ?', '3', '1', '1', 'admin', NOW(), '扫描发货中且子状态为平台二销验证中的订单，调用淘宝接口同步验证结果'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_job WHERE invoke_target = 'secondSaleVerifyJob.execute()'
);
