package com.ruoyi.user.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 用户企业对象 u_user_company
 *
 * @author ruoyi
 * @date 2025-09-06
 */
@Data
@Accessors(chain = true)
public class UserCompanyParam {
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
     * 创建时间
     */
    private Date createTime;


}
