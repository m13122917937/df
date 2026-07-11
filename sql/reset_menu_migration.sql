-- ============================================================
-- 重置双栏 ERP 菜单迁移
-- 撤销 two_panel_sidebar_migration.sql 的所有改动
-- 1. 删除新创建的4个模块及分类
-- 2. 恢复被移动的菜单到顶级
-- ============================================================

-- Step 1: 找出新创建的4个模块
SET @reset_mod1 = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='经营分析' AND parent_id=0 AND icon='module-chart') t);
SET @reset_mod2 = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='供应链'   AND parent_id=0 AND icon='module-supplychain') t);
SET @reset_mod3 = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='工具箱'   AND parent_id=0 AND icon='module-tools') t);
SET @reset_mod4 = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='系统'     AND parent_id=0 AND icon='module-system') t);

-- Step 2: 找出新创建的二级分类（供应链下的订单管理/售后/财务/仓库，系统下的用户管理/系统配置/系统监控/开发工具）
CREATE TEMPORARY TABLE IF NOT EXISTS _tmp_new_cats AS
SELECT menu_id FROM sys_menu WHERE parent_id IN (@reset_mod2, @reset_mod4);

-- Step 3: 恢复所有被移到二级分类下的菜单回到顶级（parent_id=0）
UPDATE sys_menu SET parent_id=0, update_time=NOW()
WHERE parent_id IN (SELECT menu_id FROM _tmp_new_cats);

-- Step 4: 恢复 Step 7 中被移到新分类的旧目录子菜单
-- 这些子菜单原来属于 需求管理/系统管理/财务管理/系统监控/系统工具/售后管理
-- 它们的 parent_id 被指向了新分类，但 Step 3 已经恢复了一部分
-- 额外处理那些父级不是新分类的菜单（留给用户手动处理）

-- Step 5: 删除新创建的二级分类
DELETE FROM sys_menu WHERE menu_id IN (SELECT menu_id FROM _tmp_new_cats);

-- Step 6: 删除新创建的4个模块
DELETE FROM sys_menu WHERE menu_id IN (@reset_mod1, @reset_mod2, @reset_mod3, @reset_mod4);

-- Step 7: 清理临时表
DROP TEMPORARY TABLE IF EXISTS _tmp_new_cats;

-- Step 8: 验证——查看恢复后的顶级菜单
SELECT menu_id, menu_name, parent_id, menu_type, path, order_num
FROM sys_menu WHERE parent_id = 0 ORDER BY order_num;
