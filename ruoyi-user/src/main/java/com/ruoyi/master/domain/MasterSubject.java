package com.ruoyi.master.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 经营主体主数据，仅映射 m_subject。
 */
@Data
@TableName("m_subject")
public class MasterSubject {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long jkySubjectId;
    private String subjectCode;
    private String subjectName;
    private String subjectShortName;
    private String taxIdentifyNumber;

    /**
     * 默认银行卡ID，关联 m_subject_bank.id；本地维护，吉客云同步不覆盖。
     */
    private Long defaultPayerId;

    private Integer isDelete;
    private Long sourceCreatedTime;
    private Long sourceModifiedTime;
    private LocalDateTime lastSyncTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
