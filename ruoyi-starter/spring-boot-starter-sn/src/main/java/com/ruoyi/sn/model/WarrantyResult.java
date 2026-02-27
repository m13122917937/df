package com.ruoyi.sn.model;

import cn.hutool.core.convert.Convert;
import lombok.Data;

@Data
public class WarrantyResult {
    protected String imei;
    protected String sn;
    private String description;
    protected String model;
    protected String activated;
    protected String activateDate; // 激活日期
    protected String coverage;     // 保修截止日期
    protected Integer daysLeft;    // 剩余天数（部分品牌有）
    protected String brand;
    protected String purchaseDate; // 购买/激活日期
    protected String warrantyStatus; // 保修状态：在保/过保
    protected Boolean exits; // 串码是否存在

    protected String status;


    public boolean isActivated() {
        return Convert.toBool(activated, "未激活".equals(activated));
    }


}