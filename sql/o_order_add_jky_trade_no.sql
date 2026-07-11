-- 订单表增加吉客云唯一单号（jy开头）
ALTER TABLE `o_order`
    ADD COLUMN `jky_trade_no` VARCHAR(64) NULL DEFAULT NULL COMMENT '吉客云唯一单号（jy开头）' AFTER `erp_order_id`;
