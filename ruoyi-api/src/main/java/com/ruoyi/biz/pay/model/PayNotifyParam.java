package com.ruoyi.biz.pay.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 支付通知参数.
 *
 * @author zenghao
 * @date 2022/5/30
 */
@Data
@Accessors(chain = true)
public class PayNotifyParam implements Serializable {

    private String notifyData;

}
