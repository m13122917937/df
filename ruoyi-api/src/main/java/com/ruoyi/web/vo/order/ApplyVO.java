package com.ruoyi.web.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 【请填写功能名称】对象 o_apply
 *
 * @author ruoyi
 * @date 2025-09-23
 */
@Data
@Accessors(chain = true)
public class ApplyVO {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */

    private Long id;

    /**
     * 申请企业
     */

    private Long companyId;


    private String companyName;
    /**
     * 申请类型(1立即毁单 2免责毁单 4修改物流单号申请 )
     */

    private Long type;
    /**
     * 审批状态 1 申请中 2 已拒绝 3 已审批
     */

    private Long status;
    /**
     * 申请理由
     */

    private String remark;
    /**
     * 创建人
     */

    private Long createBy;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    private Date createTime;

    /**
     * 拒绝理由
     */

    private String refuseRemark;




}
