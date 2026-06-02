ALTER TABLE u_member_company
    ADD COLUMN contract_sign_auth_status TINYINT NOT NULL DEFAULT 0 COMMENT '合同签署权限状态（0:未授权 1:已授权）';
