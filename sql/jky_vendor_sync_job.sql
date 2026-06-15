INSERT INTO sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
SELECT '吉客云供应商编码同步', 'DEFAULT', 'jkyVendorSyncJob.execute()', '0 0 2 * * ?', '3', '1', '1', 'admin', NOW(), '同步吉客云供应商编码到本地企业外部编码'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_job WHERE invoke_target = 'jkyVendorSyncJob.execute()'
);
