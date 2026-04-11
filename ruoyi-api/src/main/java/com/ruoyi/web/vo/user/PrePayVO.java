package com.ruoyi.web.vo.user;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 预支付结果.
 *
 * @author zenghao
 * @date 2022/5/30
 */
@Data
@Accessors(chain = true)
public class PrePayVO implements Serializable {

    /**
     * 二维码链接
     */
    private Object data;

    /**
     * 交易单号
     */
    private String tradeNo;

}
