-- 吉客云物流更新延迟任务（每分钟执行一次）
INSERT INTO sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
SELECT '吉客云物流更新延迟任务', 'DEFAULT', 'jkyLogisticsTaskJob.execute()', '0 0/1 * * * ?', '3', '1', '1', 'admin', NOW(), '每分钟轮询 jky_logistics_task 表，执行到期的吉客云物流更新任务'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_job WHERE invoke_target = 'jkyLogisticsTaskJob.execute()'
);
