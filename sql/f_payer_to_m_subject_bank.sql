-- 银行明细表重命名：f_payer -> m_subject_bank，纳入主体基础数据(master)域。
-- 实体与代码已迁移至 com.ruoyi.master（类名 MasterSubjectBank）。
-- f_payer_config.payer_id 逻辑关联不变（按 id 值，无物理 FK）。
RENAME TABLE `f_payer` TO `m_subject_bank`;
