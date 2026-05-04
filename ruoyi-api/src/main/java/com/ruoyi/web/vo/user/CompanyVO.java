package com.ruoyi.web.vo.user;

import lombok.Data;

@Data

public class CompanyVO {


    /**
     * $column.columnComment
     */

    private Long id;

    /**
     * 企业名称
     */

    private String companyName;

    /**
     * 企业别名
     */

    private String nickName;


    private Boolean curr;


    private Integer owner;

    /**
     * 合同认证状态
     */
    private Integer contractAuthStatus;


    /**
     * 是否禁抢 ，0 未禁止 1 禁止
     */
    private Integer grabStatus;

}
