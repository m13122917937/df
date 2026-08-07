-- 报价模块菜单初始化：创建独立顶级菜单“报价管理”，并挂载两个子菜单。
-- 全部幂等，可重复执行。

-- 1. 创建顶级菜单“报价管理”
INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '报价管理', 0, 5, 'quote', NULL, '', 1, 0,
    'M', '0', '0', '', 'shopping', 'admin', NOW(), '报价模块（独立商品库与价格档位）'
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu`
    WHERE `parent_id` = 0
      AND `menu_name` = '报价管理'
      AND `del_flag` = '0'
);

-- 2. 挂载“商品库管理”子菜单
SET @quote_menu_id := (
    SELECT `menu_id`
    FROM `sys_menu`
    WHERE `menu_name` = '报价管理'
      AND `parent_id` = 0
      AND `del_flag` = '0'
    ORDER BY `menu_id`
    LIMIT 1
);

INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '商品库管理', @quote_menu_id, 1, 'product', 'quote/product/index', '', 1, 0,
    'C', '0', '0', 'quote:product:list', 'goods', 'admin', NOW(), '报价商品库管理'
WHERE @quote_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM `sys_menu`
      WHERE `parent_id` = @quote_menu_id
        AND `path` = 'product'
        AND `del_flag` = '0'
  );

-- 3. 挂载“价格档位管理”子菜单
INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '价格档位管理', @quote_menu_id, 2, 'tier', 'quote/tier/index', '', 1, 0,
    'C', '0', '0', 'quote:tier:list', 'tree-table', 'admin', NOW(), '报价价格档位管理'
WHERE @quote_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM `sys_menu`
      WHERE `parent_id` = @quote_menu_id
        AND `path` = 'tier'
        AND `del_flag` = '0'
  );
