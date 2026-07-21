-- 企业微信用户同步任务。连接参数位于 application-dev.yml / application-prod.yml 的 wechat.wecom。
-- 默认停用；确认 WECOM_CORP_ID、WECOM_CONTACT_SECRET、WECOM_AGENT_ID 后在“定时任务”页面启用。
DELETE FROM sys_job WHERE job_name = '企业微信用户同步';

INSERT INTO sys_job
    (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
VALUES
    ('企业微信用户同步', 'DEFAULT', 'weComUserSyncJob.syncUsers()', '0 0 * * * ?', '3', '1', '1', 'admin', NOW(),
     '按手机号同步企业微信通讯录；admin 跳过，离职成员逻辑停用');
