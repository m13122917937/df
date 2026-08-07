-- 报价菜单重复诊断与清理（菜单名：报价单）
-- 第 1 部分（诊断）只会 SELECT，不会修改数据；确认结果后再执行第 2 部分。

-- ============ 1. 诊断：顶级“报价单”菜单 ============
SELECT `menu_id`, `menu_name`, `parent_id`, `order_num`,
       LENGTH(`menu_name`) AS name_len, `create_time`
FROM `sys_menu`
WHERE `parent_id` = 0
  AND `menu_name` = '报价单'
ORDER BY `menu_id`;

-- ============ 2. 诊断：所有报价相关菜单（含子菜单） ============
SELECT `menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `perms`
FROM `sys_menu`
WHERE `parent_id` = 0
  AND `menu_name` = '报价单'
   OR `parent_id` IN (
       SELECT `menu_id`
       FROM `sys_menu`
       WHERE `parent_id` = 0
         AND `menu_name` = '报价单'
   )
ORDER BY `parent_id`, `order_num`, `menu_id`;

-- ============ 3. 清理：保留最小 menu_id 的顶级菜单，删除其余重复顶级及其子菜单 ============
-- 确认上面 SELECT 结果只有一个是“要保留的”，再执行以下 DELETE。
-- 建议先备份 sys_menu 表（CREATE TABLE sys_menu_bak_20260807 AS SELECT * FROM sys_menu;）
DELETE FROM `sys_menu`
WHERE `menu_id` IN (
    SELECT `dup_id`
    FROM (
        SELECT `menu_id` AS `dup_id`
        FROM `sys_menu`
        WHERE `parent_id` = 0
          AND `menu_name` = '报价单'
          AND `menu_id` > (
              SELECT MIN(`menu_id`)
              FROM `sys_menu`
              WHERE `parent_id` = 0
                AND `menu_name` = '报价单'
          )
    ) AS dup_tmp
)
OR `parent_id` IN (
    SELECT `dup_id`
    FROM (
        SELECT `menu_id` AS `dup_id`
        FROM `sys_menu`
        WHERE `parent_id` = 0
          AND `menu_name` = '报价单'
          AND `menu_id` > (
              SELECT MIN(`menu_id`)
              FROM `sys_menu`
              WHERE `parent_id` = 0
                AND `menu_name` = '报价单'
          )
    ) AS dup_tmp
);
