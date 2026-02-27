
package com.ruoyi.kuaidi100.model.e;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 电子面单V2 寄件人/收件人信息模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EOrderAddressInfo {
    private String name; // 姓名
    private String mobile; // 手机号
    private String tel; // 座机号
    private String company; // 公司
    private String postCode; // 邮编
    private String province; // 省份
    private String city; // 城市
    private String area; // 区县
    private String address; // 详细地址
}
