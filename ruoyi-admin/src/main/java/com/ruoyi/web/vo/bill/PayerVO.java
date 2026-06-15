package com.ruoyi.web.vo.bill;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PayerVO {

    /**
     * $column.columnComment
     */

    private Long id;

    /**
     * 付款户名
     */

    private String nickName;

    /**
     * 编码
     */
    private String outCode;

    /**
     * 付款户名
     */


    private String payName;
    /**
     * 银行信息
     */


    private String bankName;
    /**
     * 银行卡号
     */

    private String payNo;

    /**
     * 账户余额
     */

    private BigDecimal balance;

    /**
     * 是否激活， 0 激活， 1弃用
     */

    private Integer actived;

    /**
     * 创建时间
     */


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 创建人
     */

    private Long createBy;


    private String createName;

    /**
     * 修改时间
     */


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * 修改人
     */
    private Long updateBy;



    private String updateName;
}
