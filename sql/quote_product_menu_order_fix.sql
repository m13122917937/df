-- 修正“报价单”子菜单显示顺序：品牌管理、品类管理、商品管理、报价更新。
-- 幂等：按 path 更新 order_num，可重复执行。

UPDATE `sys_menu` m
JOIN (
    SELECT `menu_id`
    FROM `sys_menu`
    WHERE `parent_id` = 0
      AND `menu_name` = '报价单'
) q ON m.parent_id = q.menu_id
SET m.order_num = 1
WHERE m.path = 'brand';

UPDATE `sys_menu` m
JOIN (
    SELECT `menu_id`
    FROM `sys_menu`
    WHERE `parent_id` = 0
      AND `menu_name` = '报价单'
) q ON m.parent_id = q.menu_id
SET m.order_num = 2
WHERE m.path = 'category';

UPDATE `sys_menu` m
JOIN (
    SELECT `menu_id`
    FROM `sys_menu`
    WHERE `parent_id` = 0
      AND `menu_name` = '报价单'
) q ON m.parent_id = q.menu_id
SET m.order_num = 3
WHERE m.path = 'product';

UPDATE `sys_menu` m
JOIN (
    SELECT `menu_id`
    FROM `sys_menu`
    WHERE `parent_id` = 0
      AND `menu_name` = '报价单'
) q ON m.parent_id = q.menu_id
SET m.order_num = 4
WHERE m.path = 'price';
