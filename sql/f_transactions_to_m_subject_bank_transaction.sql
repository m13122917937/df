-- 资金流水表重命名：f_transactions -> m_subject_bank_transaction，纳入主体基础数据(master)域。
-- account_id 逻辑关联 m_subject_bank.id（按 id 值，无物理 FK）。
-- 列结构不变。
RENAME TABLE `f_transactions` TO `m_subject_bank_transaction`;
