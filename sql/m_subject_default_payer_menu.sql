-- 主体银行卡管理按钮：挂到“主体配置”菜单（perms='master:subject:list'）下。
SET @subject_menu_id := (
    SELECT `menu_id`
    FROM `sys_menu`
    WHERE `perms` = 'master:subject:list'
      AND `del_flag` = '0'
    ORDER BY `menu_id`
    LIMIT 1
);

-- 查看主体银行卡列表
INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '主体银行卡查询', @subject_menu_id, 1, '', '', '', 1, 0,
    'F', '0', '0', 'master:subject:bank:list', '#', 'admin', NOW(), '查看主体下银行卡列表'
WHERE @subject_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM `sys_menu`
      WHERE `parent_id` = @subject_menu_id
        AND `perms` = 'master:subject:bank:list'
        AND `del_flag` = '0'
  );

-- 设置默认银行卡
INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '设置默认银行卡', @subject_menu_id, 2, '', '', '', 1, 0,
    'F', '0', '0', 'master:subject:setDefaultBank', '#', 'admin', NOW(), '设置主体默认银行卡'
WHERE @subject_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM `sys_menu`
      WHERE `parent_id` = @subject_menu_id
        AND `perms` = 'master:subject:setDefaultBank'
        AND `del_flag` = '0'
  );
