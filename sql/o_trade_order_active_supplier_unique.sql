-- 执行前检查：结果必须为空；若存在记录，应先核实并失效多余的成交记录。
SELECT order_id, COUNT(*) AS active_count
FROM o_trade_order
WHERE status = 2
GROUP BY order_id
HAVING COUNT(*) > 1;

-- 一个内部订单只允许保留一条 status = 2 的有效成交记录。
-- 非有效状态生成 NULL，唯一索引允许保留多条历史成交轨迹。
ALTER TABLE o_trade_order
    ADD COLUMN active_order_id VARCHAR(50)
        GENERATED ALWAYS AS (CASE WHEN status = 2 THEN order_id ELSE NULL END) STORED
        COMMENT '有效成交订单ID',
    ADD UNIQUE KEY uk_o_trade_order_active_order_id (active_order_id);
