package com.ruoyi.master.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 经营主体业务对象。
 */
@Data
public class MasterSubjectBO {

    private Long id;
    private Long jkySubjectId;
    private String subjectCode;
    private String subjectName;
    private String subjectShortName;
    private String taxIdentifyNumber;

    /**
     * 默认银行卡ID，关联 m_subject_bank.id。
     */
    private Long defaultPayerId;

    private Integer isDelete;
    private Long sourceCreatedTime;
    private Long sourceModifiedTime;
    private LocalDateTime lastSyncTime;
}
