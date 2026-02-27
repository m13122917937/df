package com.ruoyi.wangdian.param.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProviderParams {

    // 供应商编号 - 必须
    private String provider_no;

    // 供应商名称 - 必须
    private String provider_name;

    // 供应商分组 - 可选
    private String provider_group;

    // 最小采购量 - 必须
    private BigDecimal min_purchase_num;

    // 采购周期（天）- 必须
    private Integer purchase_cycle_days;

    // 到货周期（天）- 必须
    private Integer arrive_cycle_days;

    // 联系人 - 可选
    private String contact;

    // 座机 - 可选
    private String telno;

    // 移动电话 - 可选
    private String mobile;

    // 传真 - 可选
    private String fax;

    // 邮编 - 可选
    private String zip;

    // 邮箱 - 可选
    private String email;

    // QQ号码 - 可选
    private String qq;

    // 旺旺号 - 可选
    private String wangwang;

    // 地址 - 可选
    private String address;

    // 网址 - 可选
    private String website;

    // 最后采购日期 - 可选，默认值为当前时间或接口传入
    private Date last_purchase_time;

    // 停用状态：0否，1是 - 可选，默认0
    private Integer is_disabled;

    // 结算周期（天）- 可选，默认0
    private Integer charge_cycle_days;
}
