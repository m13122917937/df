-- 经营主体表移除冗余字段
-- 这些字段吉客云接口不返回，无用数据
ALTER TABLE m_subject
    DROP COLUMN country_name,
    DROP COLUMN province_name,
    DROP COLUMN city_name,
    DROP COLUMN town_name,
    DROP COLUMN address,
    DROP COLUMN contact_name,
    DROP COLUMN contact_phone,
    DROP COLUMN is_blockup;
