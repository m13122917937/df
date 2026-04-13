package com.ruoyi.bill.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 付款配置对象 f_payer_config
 *
 * @author ruoyi
 * @date 2025-11-07
 */
@Data
@Accessors(chain = true)
public class PayerConfigQuery {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;
    /**
     * 关键字
     */
    @QueryField(operator = DynamicCondition.Operator.IN, field = "key_word")
    private String keyWordLike;
    /**
     * 平台
     */
    private String platform;

    /**
     * 关键字
     */
    private String keyWord;
    /**
     * 关键字Id
     */
    private String token;
    /**
     * 付款主体
     */
    private Long payerId;
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


    /**
     * 过期时间
     */
    private Date expireTime;



    /**
     *  最后同步时间
     */
    private Long lastSyncTime;

}
