package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * Excel导出任务表 sys_excel_task
 * 用于记录先生成后下载的导出任务
 *
 * @author ruoyi
 */
@Data
public class SysExcelTask implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @TableId(type = IdType.AUTO)
    private Long taskId;

    /**
     * 文件ID（UUID）
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 本地临时文件路径（生成后删除）
     */
    private String localPath;

    /**
     * 文件存储OSS URL
     */
    private String ossUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 导出模块名称
     */
    private String moduleName;

    /**
     * 查询参数JSON
     */
    private String queryParams;

    /**
     * 任务状态：0-生成中 1-生成成功 2-生成失败
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建用户ID
     */
    private Long createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    /**
     * 是否已下载：0-未下载 1-已下载
     */
    private Integer downloaded;
}
