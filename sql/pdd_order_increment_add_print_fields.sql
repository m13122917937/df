-- =============================================
-- 拼多多增量订单表：添加快递100脱敏订单打印相关字段
-- 执行日期: 2025-02-10
-- 说明: 根据快递100拼多多脱敏订单打印文档添加所需字段
-- =============================================

-- 添加拼多多脱敏订单打印相关字段
ALTER TABLE `pdd_order_increment`
    ADD COLUMN `encrypted_name` VARCHAR(255) NULL COMMENT '收件人加密姓名' AFTER `order_tag`,
    ADD COLUMN `encrypted_mobile` VARCHAR(255) NULL COMMENT '收件人加密电话' AFTER `encrypted_name`,
    ADD COLUMN `province` VARCHAR(50) NULL COMMENT '收件人省份' AFTER `encrypted_mobile`,
    ADD COLUMN `city` VARCHAR(50) NULL COMMENT '收件人城市' AFTER `province`,
    ADD COLUMN `district` VARCHAR(50) NULL COMMENT '收件人区/县' AFTER `city`,
    ADD COLUMN `encrypted_addr` VARCHAR(500) NULL COMMENT '收件人加密详细地址' AFTER `district`,
    ADD COLUMN `detail_addr_enc` VARCHAR(500) NULL COMMENT '收件人加密详细地址（完整版，用于detailAddrEnc）' AFTER `encrypted_addr`,
    ADD COLUMN `express_com` VARCHAR(50) NULL COMMENT '快递公司编码（用于打印面单）' AFTER `detail_addr_enc`,
    ADD COLUMN `cargo_name` VARCHAR(200) NULL COMMENT '商品名称（用于打印面单）' AFTER `express_com`,
    ADD COLUMN `cargo_count` INT NULL COMMENT '商品数量（用于打印面单）' AFTER `cargo_name`,
    ADD COLUMN `cargo_unit` VARCHAR(20) NULL COMMENT '商品单位（用于打印面单）' AFTER `cargo_count`,
    ADD COLUMN `cargo_weight` VARCHAR(20) NULL COMMENT '商品重量（用于打印面单）' AFTER `cargo_unit`,
    ADD COLUMN `partner_id` VARCHAR(100) NULL COMMENT '合作伙伴ID' AFTER `cargo_weight`,
    ADD COLUMN `partner_key` VARCHAR(200) NULL COMMENT '合作伙伴密钥' AFTER `partner_id`,
    ADD COLUMN `partner_name` VARCHAR(100) NULL COMMENT '合作伙伴名称' AFTER `partner_key`,
    ADD COLUMN `net` VARCHAR(50) NULL COMMENT '网络标识' AFTER `partner_name`;

-- 添加索引以提升查询性能
ALTER TABLE `pdd_order_increment`
    ADD INDEX `idx_order_sn` (`order_sn`) COMMENT '订单号索引',
    ADD INDEX `idx_payer_config_id` (`payer_config_id`) COMMENT '付款主体ID索引',
    ADD INDEX `idx_express_com` (`express_com`) COMMENT '快递公司编码索引';

-- =============================================
-- 字段说明
-- =============================================
-- 1. 加密字段（encrypted_name, encrypted_mobile, encrypted_addr, detail_addr_enc）
--    - 这些字段用于存储加密后的收件人信息
--    - 加密格式需符合快递100拼多多脱敏订单打印规范
--    - 电话加密格式：$...=4$
--    - 姓名加密格式：~...=~4~
--    - 地址加密格式：~...=~4~
--
-- 2. 地址拆分字段（province, city, district）
--    - 将完整地址拆分为省、市、区三级
--    - 用于构建快递100面单请求
--
-- 3. 快递和商品信息（express_com, cargo_name, cargo_count, cargo_unit, cargo_weight）
--    - express_com: 快递公司编码，如 yunda（圆通）、shentong（申通）等
--    - cargo_*: 商品相关信息，用于打印面单时显示
--
-- 4. 平台授权信息（partner_id, partner_key, partner_name, net）
--    - 快递100第三方平台授权信息
--    - 需在快递100平台完成拼多多平台授权后获取
--    - 这些信息用于调用快递100脱敏订单打印接口
--
-- =============================================
-- 使用示例
-- =============================================
--
-- 1. 更新平台授权信息（每个店铺只需配置一次）
-- UPDATE pdd_order_increment
-- SET partner_id = 'your_partner_id',
--     partner_key = 'your_partner_key',
--     partner_name = 'your_partner_name',
--     net = 'your_net'
-- WHERE payer_config_id = 1;
--
-- 2. 查询需要打印的订单
-- SELECT * FROM pdd_order_increment
-- WHERE express_com IS NOT NULL
--   AND partner_id IS NOT NULL
-- ORDER BY create_time DESC
-- LIMIT 100;
--
-- 3. 结合快递100策略模式使用
-- DesensitizedOrderRequest request = DesensitizedOrderRequest.builder()
--     .platformType(PlatformType.PINDUODUO)
--     .authInfo(PlatformAuthInfo.builder()
--         .partnerId(order.getPartnerId())
--         .partnerKey(order.getPartnerKey())
--         .partnerName(order.getPartnerName())
--         .net(order.getNet())
--         .build())
--     .thirdOrderId(order.getOrderSn())
--     .expressCom(order.getExpressCom())
--     .recipientInfo(...)
--     .build();
--
-- =============================================