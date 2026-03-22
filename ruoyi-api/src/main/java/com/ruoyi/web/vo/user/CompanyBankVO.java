package com.ruoyi.web.vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data

public class CompanyBankVO {


    /**
     * 主键
     */

    private Long id;

    /**
     * 企业主体name
     */

    private String nickName;
    /**
     * 企业别称
     */

    private String companyName;
    /**
     * 银行开户名
     */

    private String accountBankName;
    /**
     * 开户银行
     */

    private String accountBank;
    /**
     * 银行账号
     */

    private String bankAccount;
    /**
     * 注册省
     */

    private Long province;

    /**
     * 注册省
     */

    private String provinceName;

    /**
     * 注册市
     */

    private Long city;

    /**
     * 注册市
     */

    private String cityName;
    /**
     * 是否默认 0 否 1 是
     */

    private Integer defaulted;
    /**
     * 是否启用：0-禁用；1-启用
     */

    private Integer valid;
    /**
     * 创建时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建人
     */

    private String createByName;
    /**
     * 修改时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    /**
     * 修改人
     */
    private Long updateBy;

    /**
     * 修改人
     */

    private String updateByName;
}
