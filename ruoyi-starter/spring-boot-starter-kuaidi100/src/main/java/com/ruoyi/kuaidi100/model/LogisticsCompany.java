package com.ruoyi.kuaidi100.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 物流公司匹配.
 *
 * @author
 * @date 2024-05-24 下午1:12
 */
@Data
public class LogisticsCompany implements Serializable {

    /** 单号的长度 */
    private Integer lengthPre;

    /** 快递公司对应的编码 */
    private String comCode;

    /** 快递公司名称 */
    private String name;

}
