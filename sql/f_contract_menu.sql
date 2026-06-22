-- 合同管理菜单（请先确认 financialManage 父菜单 menu_id，并替换 @parentId 与新菜单 menu_id；以下示例假设父菜单 menu_id=2000，按钮使用自增）
-- 1. 父菜单：财务管理（如果已存在则跳过）
-- INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
-- VALUES (2000, '财务管理', 0, 50, 'financialManage', null, 1, 0, 'M', '0', '0', '', 'money', 'admin', NOW(), '', NULL, '财务管理目录');

-- 2. 合同管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('合同管理', 2000, 20, 'contract', 'financialManage/contract/index', 1, 0, 'C', '0', '0', 'finance:contract:list', 'documentation', 'admin', NOW(), '', NULL, '合同管理菜单');

SET @contractMenuId := LAST_INSERT_ID();

-- 3. 合同管理按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
  ('合同查询', @contractMenuId, 1, '', null, 1, 0, 'F', '0', '0', 'finance:contract:list',   '#', 'admin', NOW(), '', NULL, ''),
  ('合同新增', @contractMenuId, 2, '', null, 1, 0, 'F', '0', '0', 'finance:contract:add',    '#', 'admin', NOW(), '', NULL, ''),
  ('合同修改', @contractMenuId, 3, '', null, 1, 0, 'F', '0', '0', 'finance:contract:edit',   '#', 'admin', NOW(), '', NULL, ''),
  ('合同删除', @contractMenuId, 4, '', null, 1, 0, 'F', '0', '0', 'finance:contract:remove', '#', 'admin', NOW(), '', NULL, ''),
  ('发起签署', @contractMenuId, 5, '', null, 1, 0, 'F', '0', '0', 'finance:contract:launch', '#', 'admin', NOW(), '', NULL, ''),
  ('撤销签署', @contractMenuId, 6, '', null, 1, 0, 'F', '0', '0', 'finance:contract:cancel', '#', 'admin', NOW(), '', NULL, ''),
  ('同步状态', @contractMenuId, 7, '', null, 1, 0, 'F', '0', '0', 'finance:contract:sync',   '#', 'admin', NOW(), '', NULL, '');

-- 4. 合同状态同步定时任务（quartz）
INSERT INTO sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
VALUES ('合同状态同步', 'DEFAULT', 'contractStatusSyncJob.execute()', '0 */30 * * * ?', '3', '1', '0', 'admin', NOW(), '每30分钟兜底同步合同签署状态');
