-- Persist the product and SKU recognized during an IMEI/SN model mismatch.
ALTER TABLE `o_imei`
    ADD COLUMN `recognized_product_name` VARCHAR(255) NULL DEFAULT NULL COMMENT '型号不一致时识别出的商品名称' AFTER `deleted`,
    ADD COLUMN `recognized_sku_name` VARCHAR(255) NULL DEFAULT NULL COMMENT '型号不一致时识别出的SKU规格' AFTER `recognized_product_name`;
