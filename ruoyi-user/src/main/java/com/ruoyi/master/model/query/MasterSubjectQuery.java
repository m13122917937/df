package com.ruoyi.master.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 经营主体查询条件。
 */
@Data
@Accessors(chain = true)
public class MasterSubjectQuery {

    private Long id;

    private Long jkySubjectId;

    /**
     * 主体编码（精确）
     */
    private String subjectCode;

    /**
     * 主体名称（精确）
     */
    private String subjectName;

    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "subject_code")
    private String subjectCodeLike;

    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "subject_name")
    private String subjectNameLike;

    private Integer isDelete;
}
