-- 经营主体表：增加默认银行卡ID字段，关联 m_subject_bank.id。
-- 同步 upsert 不引用该列，故吉客云同步不会覆盖本地配置的默认卡。
ALTER TABLE `m_subject`
    ADD COLUMN `default_payer_id` bigint DEFAULT NULL COMMENT '默认银行卡ID，关联m_subject_bank.id'
    AFTER `tax_identify_number`;
