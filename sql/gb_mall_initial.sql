-- 国补主题商城增量建表脚本。首期优惠仅代表平台活动优惠，不代表政府资格或核销结果。

CREATE TABLE IF NOT EXISTS gb_category (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父级ID',
    category_name VARCHAR(64) NOT NULL COMMENT '分类名称',
    icon_url VARCHAR(512) DEFAULT NULL COMMENT '图标地址',
    discount_rate DECIMAL(5,4) NOT NULL DEFAULT 0.0000 COMMENT '活动优惠比例',
    discount_cap_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '订单商品行优惠封顶金额',
    sale_provinces VARCHAR(2000) NOT NULL COMMENT '可售省份JSON',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0停用 1启用',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国补商城分类';

CREATE TABLE IF NOT EXISTS gb_banner (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    banner_name VARCHAR(64) NOT NULL COMMENT '轮播名称',
    image_url VARCHAR(512) NOT NULL COMMENT '图片地址',
    target_type VARCHAR(32) NOT NULL COMMENT '跳转类型',
    target_value VARCHAR(256) DEFAULT NULL COMMENT '跳转值',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0停用 1启用',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国补商城轮播图';

CREATE TABLE IF NOT EXISTS gb_product (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    product_code VARCHAR(64) NOT NULL COMMENT 'SPU编码',
    product_name VARCHAR(128) NOT NULL COMMENT '商品名称',
    subtitle VARCHAR(256) DEFAULT NULL COMMENT '副标题',
    main_image_url VARCHAR(512) DEFAULT NULL COMMENT '主图',
    detail_content MEDIUMTEXT COMMENT '图文详情',
    recommended TINYINT NOT NULL DEFAULT 0 COMMENT '是否推荐',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0下架 1上架',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_gb_product_code (product_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国补商城商品';

CREATE TABLE IF NOT EXISTS gb_product_sku (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sku_code VARCHAR(64) NOT NULL COMMENT 'SKU编码',
    spec_name VARCHAR(128) NOT NULL COMMENT '规格名称',
    original_price DECIMAL(12,2) NOT NULL COMMENT '原价',
    stock_quantity INT NOT NULL DEFAULT 0 COMMENT '库存，可为负数',
    sales_quantity INT NOT NULL DEFAULT 0 COMMENT '销量',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0下架 1上架',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_gb_sku_code (sku_code),
    KEY idx_gb_sku_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国补商城SKU';

CREATE TABLE IF NOT EXISTS gb_product_image (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    image_url VARCHAR(512) NOT NULL COMMENT '图片地址',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_gb_product_image_product (product_id, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国补商城商品轮播图';

CREATE TABLE IF NOT EXISTS gb_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_no VARCHAR(64) NOT NULL COMMENT '订单号',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    order_status VARCHAR(32) NOT NULL COMMENT '订单状态',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '商品总额',
    discount_amount DECIMAL(12,2) NOT NULL COMMENT '活动优惠额',
    pay_amount DECIMAL(12,2) NOT NULL COMMENT '实付金额',
    expire_time DATETIME NOT NULL COMMENT '待支付过期时间',
    paid_time DATETIME DEFAULT NULL COMMENT '支付成功时间',
    shipped_time DATETIME DEFAULT NULL COMMENT '发货时间',
    completed_time DATETIME DEFAULT NULL COMMENT '完成时间',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_gb_order_no (order_no),
    KEY idx_gb_order_member (member_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国补商城订单';

CREATE TABLE IF NOT EXISTS gb_member_address (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    receiver_name VARCHAR(64) NOT NULL COMMENT '收货人',
    receiver_phone VARCHAR(32) NOT NULL COMMENT '收货电话',
    province_name VARCHAR(64) NOT NULL COMMENT '省',
    city_name VARCHAR(64) NOT NULL COMMENT '市',
    district_name VARCHAR(64) NOT NULL COMMENT '区',
    detail_address VARCHAR(256) NOT NULL COMMENT '详细地址',
    default_address TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认地址',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_gb_member_address_member (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国补商城会员收货地址';

CREATE TABLE IF NOT EXISTS gb_order_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    product_name VARCHAR(128) NOT NULL COMMENT '商品名称快照',
    spec_name VARCHAR(128) NOT NULL COMMENT '规格快照',
    quantity INT NOT NULL COMMENT '购买数量',
    unit_price DECIMAL(12,2) NOT NULL COMMENT '原价快照',
    discount_rate DECIMAL(5,4) NOT NULL COMMENT '优惠比例快照',
    discount_cap_amount DECIMAL(12,2) NOT NULL COMMENT '优惠封顶快照',
    discount_amount DECIMAL(12,2) NOT NULL COMMENT '优惠金额快照',
    pay_amount DECIMAL(12,2) NOT NULL COMMENT '实付金额快照',
    PRIMARY KEY (id),
    UNIQUE KEY uk_gb_order_item_order (order_id),
    KEY idx_gb_order_item_sku (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国补商城订单商品';

CREATE TABLE IF NOT EXISTS gb_order_address (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    receiver_name VARCHAR(64) NOT NULL COMMENT '收货人',
    receiver_phone VARCHAR(32) NOT NULL COMMENT '收货电话',
    province_name VARCHAR(64) NOT NULL COMMENT '省',
    city_name VARCHAR(64) NOT NULL COMMENT '市',
    district_name VARCHAR(64) NOT NULL COMMENT '区',
    detail_address VARCHAR(256) NOT NULL COMMENT '详细地址',
    PRIMARY KEY (id),
    UNIQUE KEY uk_gb_order_address_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国补商城订单地址快照';

CREATE TABLE IF NOT EXISTS gb_payment (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    payment_no VARCHAR(64) NOT NULL COMMENT '支付单号',
    wechat_transaction_id VARCHAR(64) DEFAULT NULL COMMENT '微信交易号',
    amount DECIMAL(12,2) NOT NULL COMMENT '支付金额',
    payment_status VARCHAR(32) NOT NULL COMMENT '支付状态',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    paid_time DATETIME DEFAULT NULL COMMENT '支付时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_gb_payment_no (payment_no),
    UNIQUE KEY uk_gb_payment_transaction (wechat_transaction_id),
    UNIQUE KEY uk_gb_payment_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国补商城支付单';

CREATE TABLE IF NOT EXISTS gb_refund (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    refund_no VARCHAR(64) NOT NULL COMMENT '退款单号',
    amount DECIMAL(12,2) NOT NULL COMMENT '退款金额',
    refund_status VARCHAR(32) NOT NULL COMMENT '退款状态',
    reason VARCHAR(256) DEFAULT NULL COMMENT '退款原因',
    wechat_refund_id VARCHAR(64) DEFAULT NULL COMMENT '微信退款号',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    refund_time DATETIME DEFAULT NULL COMMENT '退款时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_gb_refund_no (refund_no),
    UNIQUE KEY uk_gb_refund_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国补商城退款单';

CREATE TABLE IF NOT EXISTS gb_stock_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    order_id BIGINT DEFAULT NULL COMMENT '订单ID',
    change_quantity INT NOT NULL COMMENT '库存变化量',
    after_quantity INT NOT NULL COMMENT '变化后库存',
    change_type VARCHAR(32) NOT NULL COMMENT '变化类型',
    remark VARCHAR(256) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_gb_stock_log_sku (sku_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国补商城库存流水';

CREATE TABLE IF NOT EXISTS gb_shipment (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    logistics_company VARCHAR(64) NOT NULL COMMENT '物流公司',
    tracking_no VARCHAR(64) NOT NULL COMMENT '运单号',
    shipped_time DATETIME NOT NULL COMMENT '发货时间',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_gb_shipment_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国补商城发货信息';

CREATE TABLE IF NOT EXISTS u_member_wechat_identity (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    channel VARCHAR(32) NOT NULL COMMENT '渠道 MP/MINIAPP',
    app_id VARCHAR(64) NOT NULL COMMENT '微信AppID',
    open_id VARCHAR(128) NOT NULL COMMENT 'OpenID',
    union_id VARCHAR(128) DEFAULT NULL COMMENT 'UnionID',
    identity_status VARCHAR(32) NOT NULL COMMENT '身份状态',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_u_member_wechat_open (channel, app_id, open_id),
    UNIQUE KEY uk_u_member_wechat_union (union_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员微信身份绑定';

CREATE TABLE IF NOT EXISTS u_member_wechat_identity_conflict (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    union_id VARCHAR(128) NOT NULL COMMENT '冲突UnionID',
    source_member_id BIGINT NOT NULL COMMENT '来源会员ID',
    target_member_id BIGINT NOT NULL COMMENT '目标会员ID',
    conflict_status VARCHAR(32) NOT NULL COMMENT '冲突状态',
    handle_remark VARCHAR(256) DEFAULT NULL COMMENT '处理备注',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_u_member_wechat_conflict_union (union_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员微信身份冲突';

-- 国补后台菜单：依赖部署环境已有的管理员角色授权流程，执行后需由管理员分配权限。
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type,
                      visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '国补', 0, 90, 'subsidy', NULL, '', 1, 0, 'M', '0', '0', '', 'shopping', 'admin', NOW(), '', NULL, '国补商城管理'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = 0 AND menu_name = '国补');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type,
                      visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '商城概览', root.menu_id, 1, 'overview', 'operationsManage/subsidy/index', '', 1, 0, 'C', '0', '0',
       'subsidy:overview:view', 'dashboard', 'admin', NOW(), '', NULL, '国补商城运营概览'
FROM sys_menu root
WHERE root.parent_id = 0 AND root.menu_name = '国补'
  AND NOT EXISTS (SELECT 1 FROM sys_menu child WHERE child.parent_id = root.menu_id AND child.path = 'overview');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type,
                      visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '商品管理', root.menu_id, 2, 'products', 'operationsManage/subsidy/products', '', 1, 0, 'C', '0', '0',
       'subsidy:product:list', 'shopping-bag', 'admin', NOW(), '', NULL, '国补商品管理'
FROM sys_menu root
WHERE root.parent_id = 0 AND root.menu_name = '国补'
  AND NOT EXISTS (SELECT 1 FROM sys_menu child WHERE child.parent_id = root.menu_id AND child.path = 'products');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type,
                      visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 'SKU 与库存', root.menu_id, 3, 'skus', 'operationsManage/subsidy/skus', '', 1, 0, 'C', '0', '0',
       'subsidy:sku:list', 'goods', 'admin', NOW(), '', NULL, '国补 SKU、库存及补货调整'
FROM sys_menu root
WHERE root.parent_id = 0 AND root.menu_name = '国补'
  AND NOT EXISTS (SELECT 1 FROM sys_menu child WHERE child.parent_id = root.menu_id AND child.path = 'skus');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type,
                      visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '分类管理', root.menu_id, 3, 'categories', 'operationsManage/subsidy/categories', '', 1, 0, 'C', '0', '0',
       'subsidy:category:list', 'tree-table', 'admin', NOW(), '', NULL, '国补商品分类管理'
FROM sys_menu root
WHERE root.parent_id = 0 AND root.menu_name = '国补'
  AND NOT EXISTS (SELECT 1 FROM sys_menu child WHERE child.parent_id = root.menu_id AND child.path = 'categories');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type,
                      visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '轮播图管理', root.menu_id, 4, 'banners', 'operationsManage/subsidy/banners', '', 1, 0, 'C', '0', '0',
       'subsidy:banner:list', 'image', 'admin', NOW(), '', NULL, '国补商城轮播图管理'
FROM sys_menu root
WHERE root.parent_id = 0 AND root.menu_name = '国补'
  AND NOT EXISTS (SELECT 1 FROM sys_menu child WHERE child.parent_id = root.menu_id AND child.path = 'banners');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type,
                      visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '订单管理', root.menu_id, 5, 'orders', 'operationsManage/subsidy/orders', '', 1, 0, 'C', '0', '0',
       'subsidy:order:list', 'list', 'admin', NOW(), '', NULL, '国补商城订单与发货管理'
FROM sys_menu root
WHERE root.parent_id = 0 AND root.menu_name = '国补'
  AND NOT EXISTS (SELECT 1 FROM sys_menu child WHERE child.parent_id = root.menu_id AND child.path = 'orders');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type,
                      visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '退款审核', root.menu_id, 6, 'refunds', 'operationsManage/subsidy/refunds', '', 1, 0, 'C', '0', '0',
       'subsidy:refund:list', 'money', 'admin', NOW(), '', NULL, '国补商城退款审核'
FROM sys_menu root
WHERE root.parent_id = 0 AND root.menu_name = '国补'
  AND NOT EXISTS (SELECT 1 FROM sys_menu child WHERE child.parent_id = root.menu_id AND child.path = 'refunds');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type,
                      visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '微信身份冲突', root.menu_id, 7, 'identity-conflicts', 'operationsManage/subsidy/identity-conflicts', '', 1, 0, 'C', '0', '0',
       'subsidy:identity-conflict:list', 'user', 'admin', NOW(), '', NULL, '微信 UnionID 身份冲突人工审阅'
FROM sys_menu root
WHERE root.parent_id = 0 AND root.menu_name = '国补'
  AND NOT EXISTS (SELECT 1 FROM sys_menu child WHERE child.parent_id = root.menu_id AND child.path = 'identity-conflicts');
