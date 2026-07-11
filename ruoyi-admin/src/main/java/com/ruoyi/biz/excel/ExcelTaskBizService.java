package com.ruoyi.biz.excel;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.biz.order.OrderBizService;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.oss.cloud.CloudStorageService;
import com.ruoyi.system.domain.SysExcelTask;
import com.ruoyi.system.facade.ISysExcelTaskFacade;
import com.ruoyi.web.form.order.AllOrderForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Excel导出任务业务处理
 * 两步导出模式：先生成任务记录，异步生成文件到OSS，再下载
 *
 * @author ruoyi
 */
@Slf4j
@Component
public class ExcelTaskBizService {

    @Autowired
    private ISysExcelTaskFacade sysExcelTaskService;

    @Autowired
    private OrderBizService orderBizService;

    @Autowired
    private CloudStorageService cloudStorageService;

    /**
     * 创建导出任务（同步）
     *
     * @param userId 当前用户ID
     * @return 任务信息
     */
    public SysExcelTask createTask( Long userId) {
        // 生成文件ID和文件名
        String fileId = UUID.randomUUID().toString().replace("-", "");
        String timestamp = DateUtil.format(new Date(), "yyyyMMdd_HHmmss");
        String fileName = "全部订单_" + timestamp + ".xlsx";

        // 计算过期时间（默认24小时后过期）
        Date expireTime = DateUtil.offsetDay(new Date(), 1);

        // 构造本地临时文件路径
        String uploadDir = RuoYiConfig.getUploadPath();
        String excelDir = uploadDir + File.separator + "excel" + File.separator;
        File dir = new File(excelDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String localPath = excelDir + fileId + "_" + fileName;

        // 创建任务记录
        SysExcelTask task = new SysExcelTask();
        task.setFileId(fileId);
        task.setFileName(fileName);
        task.setLocalPath(localPath);
        task.setModuleName("allOrder");
        task.setStatus(0); // 生成中
        task.setExpireTime(expireTime);
        task.setCreateBy(userId);
        task.setCreateTime(DateUtil.date());
        task.setDownloaded(0); // 未下载
        sysExcelTaskService.save(task);

        return task;
    }

    /**
     * 异步生成Excel并上传到OSS（异步，由AsyncManager调度执行）
     *
     * @param task  任务信息
     * @param query 查询条件
     */
    public void generateAllOrderExcelAsync(SysExcelTask task, AllOrderForm query) {
        String localPath = task.getLocalPath();
        try {
            // 生成Excel文件到本地临时目录
            File excelFile = new File(localPath);
            orderBizService.exportExcelToFile(query, excelFile);

            // 上传到OSS
            try (FileInputStream fis = new FileInputStream(excelFile)) {
                String suffix = task.getFileName().substring(task.getFileName().lastIndexOf("."));
                String ossUrl = cloudStorageService.uploadSuffix(fis, suffix);
                task.setOssUrl(ossUrl);
                task.setFileSize(excelFile.length());
            }

            // 删除本地临时文件
            boolean deleted = excelFile.delete();
            if (!deleted) {
                log.warn("删除本地临时Excel文件失败: {}", localPath);
            }

            // 更新任务状态为成功 - 使用fileId更新避免主键为空问题
            task.setStatus(1);
            sysExcelTaskService.updateByFileId(task);
            log.info("Excel生成完成，fileId: {}, ossUrl: {}", task.getFileId(), task.getOssUrl());
        } catch (Exception e) {
            log.error("生成Excel失败，fileId: {}", task.getFileId(), e);
            // 更新任务状态为失败 - 使用fileId更新避免主键为空问题
            task.setStatus(2);
            task.setErrorMsg(e.getMessage());
            sysExcelTaskService.updateByFileId(task);
        }
    }

    /**
     * 获取当前用户最近的Excel导出任务列表
     *
     * @param userId 当前用户ID
     * @param moduleName 模块名称
     * @return 最近5条任务列表
     */
    public List<SysExcelTask> getRecentTasks(Long userId, String moduleName) {
        SysExcelTask query = new SysExcelTask();
        query.setCreateBy(userId);
        query.setModuleName(moduleName);
        List<SysExcelTask> list = sysExcelTaskService.selectSysExcelTaskList(query);
        // 只取最近5条
        if (list.size() > 5) {
            list = list.subList(0, 5);
        }
        return list;
    }

    /**
     * 清理过期任务
     *
     * @return 清理数量
     */
    public int cleanExpiredTasks() {
        return sysExcelTaskService.cleanExpiredTasks();
    }
}
