-- 报价模块菜单初始化：顶级菜单“报价单”及四个子菜单（菜单名均为 4 字）。
-- 全部幂等，可重复执行；兼容无 del_flag 字段的 sys_menu 表。

-- 1. 创建顶级菜单“报价单”
INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '报价单', 0, 5, 'quote', NULL, '', 1, 0,
    'M', '0', '0', '', 'shopping', 'admin', NOW(), '报价模块'
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu`
    WHERE `parent_id` = 0
      AND `menu_name` = '报价单'
);

-- 2. 定位顶级菜单 ID
SET @quote_menu_id := (
    SELECT `menu_id`
    FROM `sys_menu`
    WHERE `menu_name` = '报价单'
      AND `parent_id` = 0
    ORDER BY `menu_id`
    LIMIT 1
);

-- 3. 挂载“品牌管理”子菜单
INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '品牌管理', @quote_menu_id, 1, 'brand', 'quote/brand/index', '', 1, 0,
    'C', '0', '0', 'quote:brand:list', 'shopping', 'admin', NOW(), '报价品牌'
WHERE @quote_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM `sys_menu`
      WHERE `parent_id` = @quote_menu_id
        AND `path` = 'brand'
  );

-- 4. 挂载“品类管理”子菜单
INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '品类管理', @quote_menu_id, 2, 'category', 'quote/category/index', '', 1, 0,
    'C', '0', '0', 'quote:category:list', 'tree-table', 'admin', NOW(), '报价品类'
WHERE @quote_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM `sys_menu`
      WHERE `parent_id` = @quote_menu_id
        AND `path` = 'category'
  );

-- 5. 挂载“商品管理”子菜单
INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '商品管理', @quote_menu_id, 3, 'product', 'quote/product/index', '', 1, 0,
    'C', '0', '0', 'quote:product:list', 'goods', 'admin', NOW(), '报价商品库'
WHERE @quote_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM `sys_menu`
      WHERE `parent_id` = @quote_menu_id
        AND `path` = 'product'
  );

-- 6. 挂载“报价更新”子菜单
INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '报价更新', @quote_menu_id, 4, 'price', 'quote/price/index', '', 1, 0,
    'C', '0', '0', 'quote:product:list', 'money', 'admin', NOW(), '报价每日价格维护'
WHERE @quote_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM `sys_menu`
      WHERE `parent_id` = @quote_menu_id
        AND `path` = 'price'
  );

-- 7. 挂载“客户层级”子菜单
INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '客户层级', @quote_menu_id, 5, 'customer-level', 'quote/customerLevel/index', '', 1, 0,
    'C', '0', '0', 'quote:customerLevel:list', 'people', 'admin', NOW(), '报价客户层级设置'
WHERE @quote_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM `sys_menu`
      WHERE `parent_id` = @quote_menu_id
        AND `path` = 'customer-level'
  );
