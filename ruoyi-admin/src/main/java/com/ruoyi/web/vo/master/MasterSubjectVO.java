package com.ruoyi.web.vo.master;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 经营主体列表响应。
 */
@Data
public class MasterSubjectVO {

    private Long id;
    private Long jkySubjectId;
    private String subjectCode;
    private String subjectName;
    private String subjectShortName;
    private Integer isDelete;

    /**
     * 默认银行卡ID，关联 m_subject_bank.id。
     */
    private Long defaultPayerId;

    private LocalDateTime lastSyncTime;
}
