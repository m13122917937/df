package com.ruoyi.user.model.query;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 会员企业对象 u_member_company
 *
 * @author ruoyi
 * @date 2025-09-06
 */
@Data
@Accessors(chain = true)
public class MemberCompanyQuery {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 企业id
     */
    private Long companyId;

    /**
     * 是否负责人
     */
    private Integer owner;

    /**
     * 合同签署权限状态（0:未授权 1:已授权）
     */
    private Integer contractSignAuthStatus;

    /**
     * 创建时间
     */
    private Date createTime;


}
