package com.ruoyi.web.vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("企业银行卡信息")
public class CompanyBankVO {


    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 企业主体name
     */
    @ApiModelProperty("企业主体name")
    private String nickName;
    /**
     * 企业别称
     */
    @ApiModelProperty("企业别称")
    private String companyName;
    /**
     * 银行开户名
     */
    @ApiModelProperty("银行开户名")
    private String accountBankName;
    /**
     * 开户银行
     */
    @ApiModelProperty("开户银行")
    private String accountBank;
    /**
     * 银行账号
     */
    @ApiModelProperty("银行账号")
    private String bankAccount;
    /**
     * 注册省
     */
    @ApiModelProperty("注册省")
    private Long province;

    /**
     * 注册省
     */
    @ApiModelProperty("注册省名")
    private String provinceName;

    /**
     * 注册市
     */
    @ApiModelProperty("注册市")
    private Long city;

    /**
     * 注册市
     */
    @ApiModelProperty("注册市名称")
    private String cityName;
    /**
     * 是否默认 0 否 1 是
     */
    @ApiModelProperty("是否默认 0 否 1 是")
    private Integer defaulted;
    /**
     * 是否启用：0-禁用；1-启用
     */
    @ApiModelProperty("是否启用：0-禁用；1-启用")
    private Integer valid;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createByName;
    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    /**
     * 修改人
     */
    private Long updateBy;

    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    private String updateByName;
}
