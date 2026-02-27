package com.ruoyi.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("u_user_company")
public class UserCompany {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /** $column.columnComment */
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
