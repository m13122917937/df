-- 吉客云经营主体全量同步任务：每 6 小时执行一次。
INSERT INTO `sys_job`
    (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`,
     `status`, `create_by`, `create_time`, `remark`)
SELECT
    '吉客云主体同步', 'DEFAULT', 'masterSubjectSyncJob.syncSubjects()', '0 0 0/6 * * ?', '3', '1',
    '0', 'admin', NOW(), '同步吉客云经营主体至 m_subject'
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_job`
    WHERE `invoke_target` = 'masterSubjectSyncJob.syncSubjects()'
);
