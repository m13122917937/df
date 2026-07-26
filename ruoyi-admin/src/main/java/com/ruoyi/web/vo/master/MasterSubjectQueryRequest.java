package com.ruoyi.web.vo.master;

import lombok.Data;

/**
 * 经营主体列表查询请求。
 */
@Data
public class MasterSubjectQueryRequest {

    private String subjectCodeLike;
    private String subjectNameLike;
    private Integer isDelete;
}
