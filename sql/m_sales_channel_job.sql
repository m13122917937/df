-- 吉客云销售渠道同步任务：每日 02:30 执行一次。
INSERT INTO `sys_job`
    (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`,
     `status`, `create_by`, `create_time`, `remark`)
SELECT
    '吉客云渠道同步', 'DEFAULT', 'masterSalesChannelSyncJob.syncSalesChannels()', '0 30 2 * * ?', '3', '1',
    '0', 'admin', NOW(), '同步吉客云销售渠道至 m_sales_channel'
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_job`
    WHERE `invoke_target` = 'masterSalesChannelSyncJob.syncSalesChannels()'
);
