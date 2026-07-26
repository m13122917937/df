package com.ruoyi.master.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

import java.util.Date;

/**
 * 主体银行卡维护对象 m_subject_bank（原 f_payer），通过 out_code 关联 m_subject.subject_code。
 *
 * @author ruoyi
 * @date 2025-11-07
 */
@Data
@Accessors(chain = true)
@TableName("m_subject_bank")
public class MasterSubjectBank {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 编码（主体编码，对应 m_subject.subject_code）
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
    private Date createTime;
    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 修改人
     */
    private Long updateBy;


}
