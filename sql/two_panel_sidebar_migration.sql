-- ============================================================
-- 双栏 ERP 菜单迁移 SQL（修复版）
--
-- 三级结构：一级（模块）→ 二级（分类）→ 三级（页面）
-- 四个一级模块：经营分析（预留）、供应链、工具箱（预留）、系统
-- ============================================================

-- Step 1: 先查看现有菜单（不修改数据）
-- SELECT menu_id, menu_name, parent_id, menu_type, path FROM sys_menu WHERE parent_id = 0 ORDER BY order_num;


-- ============================================================
-- Step 2: 创建四个一级模块
-- ============================================================
-- NOTE: path 不能以 "/" 开头！getRouterPath() 会自动为 parent_id=0 的目录加上 "/"
-- 否则会产生 "//path" 导致 Vue Router 路由匹配失败
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
('经营分析', 0, 1, 'dashboard',   '', 1, 0, 'M', '0', '0', '', 'module-chart',       'admin', NOW(), '', NULL, ''),
('供应链',   0, 2, 'supplychain', '', 1, 0, 'M', '0', '0', '', 'module-supplychain', 'admin', NOW(), '', NULL, ''),
('工具箱',   0, 3, 'tools',       '', 1, 0, 'M', '0', '0', '', 'module-tools',       'admin', NOW(), '', NULL, ''),
('系统',     0, 4, 'sys',         '', 1, 0, 'M', '0', '0', '', 'module-system',      'admin', NOW(), '', NULL, '');


-- ============================================================
-- Step 3: 获取四个模块的 ID
-- ============================================================
SET @mod1 = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='经营分析' AND parent_id=0 LIMIT 1) t);
SET @mod2 = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='供应链'   AND parent_id=0 LIMIT 1) t);
SET @mod3 = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='工具箱'   AND parent_id=0 LIMIT 1) t);
SET @mod4 = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='系统'     AND parent_id=0 LIMIT 1) t);


-- ============================================================
-- Step 4: 创建二级分类（挂在模块下）
-- ============================================================

-- 供应链的二级分类
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
('订单管理', @mod2, 1, '/orders',     '', 1, 0, 'M', '0', '0', '', 'list',    'admin', NOW(), '', NULL, ''),
('售后订单', @mod2, 2, '/afterOrders', '', 1, 0, 'M', '0', '0', '', 'service', 'admin', NOW(), '', NULL, ''),
('财务管理', @mod2, 3, '/finance',    '', 1, 0, 'M', '0', '0', '', 'money',   'admin', NOW(), '', NULL, ''),
('仓库管理', @mod2, 4, '/warehouse',  '', 1, 0, 'M', '0', '0', '', 'nested',  'admin', NOW(), '', NULL, '');

-- 系统的二级分类
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
('用户管理', @mod4, 1, '/sys-user',    '', 1, 0, 'M', '0', '0', '', 'user',    'admin', NOW(), '', NULL, ''),
('系统配置', @mod4, 2, '/sys-config',  '', 1, 0, 'M', '0', '0', '', 'system',  'admin', NOW(), '', NULL, ''),
('系统监控', @mod4, 3, '/sys-monitor', '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', NOW(), '', NULL, ''),
('开发工具', @mod4, 4, '/sys-dev',     '', 1, 0, 'M', '0', '0', '', 'tool',    'admin', NOW(), '', NULL, '');


-- ============================================================
-- Step 5: 获取二级分类 ID
-- ============================================================
SET @cat_order   = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='订单管理' AND parent_id=@mod2 LIMIT 1) t);
SET @cat_after   = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='售后订单' AND parent_id=@mod2 LIMIT 1) t);
SET @cat_finance = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='财务管理' AND parent_id=@mod2 LIMIT 1) t);
SET @cat_wh      = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='仓库管理' AND parent_id=@mod2 LIMIT 1) t);

SET @cat_sysuser    = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='用户管理' AND parent_id=@mod4 LIMIT 1) t);
SET @cat_sysconfig  = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='系统配置' AND parent_id=@mod4 LIMIT 1) t);
SET @cat_sysmonitor = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='系统监控' AND parent_id=@mod4 LIMIT 1) t);
SET @cat_sysdev     = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='开发工具' AND parent_id=@mod4 LIMIT 1) t);


-- ============================================================
-- Step 6: 整理旧的顶级菜单 —— 找出所有 parent_id=0 的目录/Menu
--         统一挂到对应的二级分类下
-- ============================================================

-- 6a. 查出现在有哪些旧的顶级菜单（parent_id=0，排除自己创建的4个模块）
--     执行下面 SELECT 看看有哪些，作为参考：
--     SELECT menu_id, menu_name, menu_type, path FROM sys_menu
--     WHERE parent_id=0 AND menu_name NOT IN ('经营分析','供应链','工具箱','系统');


-- ------------------------------------------------------------------
-- 6b. 通用方法：用临时变量保存旧顶级菜单 ID，再UPDATE
--     下面列出了常见的若依系统菜单名，按实际做 UPDATE
-- ------------------------------------------------------------------

-- --- 订单相关 → 供应链-订单管理 ---
UPDATE sys_menu SET parent_id=@cat_order, update_time=NOW()
WHERE parent_id=0 AND menu_name IN ('代发订单','入仓订单','销售订单','拣货入仓','全部订单','集采','商品管理','产品映射');

-- --- 售后相关 → 供应链-售后订单 ---
UPDATE sys_menu SET parent_id=@cat_after, update_time=NOW()
WHERE parent_id=0 AND menu_name IN ('扣罚订单','扣罚费用','扣罚列表','销售退货','售后管理');

-- --- 财务相关 → 供应链-财务管理 ---
UPDATE sys_menu SET parent_id=@cat_finance, update_time=NOW()
WHERE parent_id=0 AND menu_name IN ('今日应付','收款主体配置','付款配置','合同管理','企业管理','交易流水','财务管理');

-- --- 仓库相关 → 供应链-仓库管理 ---
UPDATE sys_menu SET parent_id=@cat_wh, update_time=NOW()
WHERE parent_id=0 AND menu_name IN ('仓库管理','拣货入仓');

-- --- 用户相关 → 系统-用户管理 ---
UPDATE sys_menu SET parent_id=@cat_sysuser, update_time=NOW()
WHERE parent_id=0 AND menu_name IN ('用户管理','角色管理','部门管理','岗位管理','字典管理');

-- --- 配置相关 → 系统-系统配置 ---
UPDATE sys_menu SET parent_id=@cat_sysconfig, update_time=NOW()
WHERE parent_id=0 AND menu_name IN ('菜单管理','参数配置','通知公告');

-- --- 监控相关 → 系统-系统监控 ---
UPDATE sys_menu SET parent_id=@cat_sysmonitor, update_time=NOW()
WHERE parent_id=0 AND menu_name IN ('在线用户','定时任务','服务监控','缓存监控','操作日志','登录日志','系统监控');

-- --- 工具相关 → 系统-开发工具 ---
UPDATE sys_menu SET parent_id=@cat_sysdev, update_time=NOW()
WHERE parent_id=0 AND menu_name IN ('代码生成','表单构建','系统工具');


-- ============================================================
-- Step 7: 处理旧顶级菜单下的子菜单
--         如果旧的顶级菜单（如"系统管理"、"需求管理"）还有其他子菜单
--         没被上面的 UPDATE 覆盖，这里统一处理
-- ============================================================

-- 7a. 把旧的"需求管理"下的子菜单全部移到供应链-订单管理下
UPDATE sys_menu AS child
JOIN (SELECT menu_id FROM sys_menu WHERE menu_name='需求管理' AND parent_id=0 LIMIT 1) AS old
SET child.parent_id = @cat_order
WHERE child.parent_id = old.menu_id;

-- 7b. 把旧的"系统管理"下的子菜单移到系统模块
UPDATE sys_menu AS child
JOIN (SELECT menu_id FROM sys_menu WHERE menu_name='系统管理' AND parent_id=0 LIMIT 1) AS old
SET child.parent_id = @mod4
WHERE child.parent_id = old.menu_id;

-- 7c. 把旧的"财务管理"下的子菜单移到供应链-财务管理
UPDATE sys_menu AS child
JOIN (SELECT menu_id FROM sys_menu WHERE menu_name='财务管理' AND parent_id=0 LIMIT 1) AS old
SET child.parent_id = @cat_finance
WHERE child.parent_id = old.menu_id;

-- 7d. 把旧的"系统监控"下的子菜单移到系统-系统监控
UPDATE sys_menu AS child
JOIN (SELECT menu_id FROM sys_menu WHERE menu_name='系统监控' AND parent_id=0 LIMIT 1) AS old
SET child.parent_id = @cat_sysmonitor
WHERE child.parent_id = old.menu_id;

-- 7e. 把旧的"系统工具"下的子菜单移到系统-开发工具
UPDATE sys_menu AS child
JOIN (SELECT menu_id FROM sys_menu WHERE menu_name='系统工具' AND parent_id=0 LIMIT 1) AS old
SET child.parent_id = @cat_sysdev
WHERE child.parent_id = old.menu_id;

-- 7f. 把旧的"售后管理"下的子菜单移到供应链-售后管理
UPDATE sys_menu AS child
JOIN (SELECT menu_id FROM sys_menu WHERE menu_name='售后管理' AND parent_id=0 LIMIT 1) AS old
SET child.parent_id = @cat_after
WHERE child.parent_id = old.menu_id;


-- ============================================================
-- Step 8: 删除已经变空的旧顶级目录
--         只删 parent_id=0 且无子菜单的旧目录
--         用临时表保存待删 ID，避免 "Can't specify target table" 报错
-- ============================================================

-- 找出所有旧的顶级目录（不在4个新模块中），且没有子菜单
CREATE TEMPORARY TABLE IF NOT EXISTS _tmp_empty_roots AS
SELECT m.menu_id
FROM sys_menu m
WHERE m.parent_id = 0
  AND m.menu_name NOT IN ('经营分析','供应链','工具箱','系统')
  AND NOT EXISTS (
    SELECT 1 FROM (SELECT parent_id FROM sys_menu) x WHERE x.parent_id = m.menu_id
  );

-- 删除这些空目录
DELETE FROM sys_menu WHERE menu_id IN (SELECT menu_id FROM _tmp_empty_roots);

-- 清理临时表
DROP TEMPORARY TABLE IF EXISTS _tmp_empty_roots;


-- ============================================================
-- Step 9: 验证最终结构
-- ============================================================
SELECT
  m1.menu_name AS '一级模块',
  m2.menu_name AS '二级分类',
  m3.menu_name AS '三级页面',
  m3.path       AS '路由地址'
FROM sys_menu m1
LEFT JOIN sys_menu m2 ON m2.parent_id = m1.menu_id
LEFT JOIN sys_menu m3 ON m3.parent_id = m2.menu_id
WHERE m1.parent_id = 0
  AND m1.menu_name IN ('经营分析','供应链','工具箱','系统')
ORDER BY m1.order_num, m2.order_num, m3.order_num;

-- 检查是否还有漏网的顶级菜单
SELECT menu_id, menu_name, menu_type, path AS '未归类的顶级菜单'
FROM sys_menu
WHERE parent_id = 0
  AND menu_name NOT IN ('经营分析','供应链','工具箱','系统');
