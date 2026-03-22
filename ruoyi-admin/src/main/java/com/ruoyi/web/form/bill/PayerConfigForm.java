package com.ruoyi.web.form.bill;

import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 付款账号维护对象 f_payer
 *
 * @author ruoyi
 * @date 2025-11-07
 */
@Data
@Accessors(chain = true)
public class PayerConfigForm {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */

    @NotNull(message = "id不能为空", groups = {UpdateGroup.class})
    private Long id;

    /**
     * 平台
     */

    @NotNull(message = "平台不能为空", groups = {AddGroup.class})
    private String platform;
    /**
     * 关键字
     */

    @NotBlank(message = "店铺名称不能为空", groups = {AddGroup.class})
    private String keyWord;

    /**
     * 付款主体
     */

    @NotNull(message = "付款主体ID不能为空", groups = {AddGroup.class})
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

}
