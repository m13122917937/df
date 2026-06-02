package com.ruoyi.web.controller.system;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.biz.excel.ExcelTaskBizService;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.system.domain.SysExcelTask;
import com.ruoyi.system.facade.ISysExcelTaskFacade;
import com.ruoyi.web.form.order.AllOrderForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * Excel生成下载Controller
 * 实现先生成Excel文件存储在OSS，再下载的两步导出模式
 * 适用于大数据量导出，避免超时问题
 *
 * @author ruoyi
 */
@Slf4j
@RestController
@RequestMapping("/system/excel")
public class SysExcelTaskController extends BaseController {

    @Autowired
    private ISysExcelTaskFacade sysExcelTaskService;

    @Autowired
    private ExcelTaskBizService excelTaskBizService;

    /**
     * 第一步：异步生成Excel文件上传到OSS
     * POST /system/excel/generate
     * 返回任务信息，前端轮询查询任务状态
     */
    @PreAuthorize("@ss.hasPermi('system:excel:generate')")
    @PostMapping("/generate")
    public AjaxResult generateExcel(@RequestBody AllOrderForm query) {
        Long userId = getUserId();
        // 设置公司ID，数据权限过滤
        query.setCompanyId(getDeptId());
        SysExcelTask task = excelTaskBizService.createTask(userId);
        // 使用AsyncManager提交异步任务，确保真正异步执行
        AsyncManager.me().execute(new TimerTask() {
            @Override
            public void run() {
                excelTaskBizService.generateAllOrderExcelAsync(task, query);
            }
        });
        AjaxResult data = AjaxResult.success();
        data.put("fileId", task.getFileId());
        data.put("fileName", task.getFileName());
        data.put("status", task.getStatus());
        return data;
    }

    /**
     * 查询任务状态
     * GET /system/excel/status/{fileId}
     * 返回任务状态，0=生成中 1=生成成功 2=生成失败
     */
    @GetMapping("/status/{fileId}")
    public AjaxResult getTaskStatus(@PathVariable String fileId) {
        SysExcelTask task = sysExcelTaskService.selectSysExcelTaskByFileId(fileId);
        if (task == null) {
            return AjaxResult.error("任务不存在");
        }
        AjaxResult data = AjaxResult.success();
        data.put("fileId", task.getFileId());
        data.put("fileName", task.getFileName());
        data.put("status", task.getStatus());
        data.put("ossUrl", task.getOssUrl());
        data.put("errorMsg", task.getErrorMsg());
        return data;
    }

    /**
     * 第二步：下载已生成的Excel文件（302重定向到OSS直接下载）
     * GET /system/excel/download/{fileId}
     */
    @GetMapping("/download/{fileId}")
    public void downloadExcel(@PathVariable String fileId, HttpServletResponse response) {
        // 查询任务记录
        SysExcelTask task = sysExcelTaskService.selectSysExcelTaskByFileId(fileId);
        if (task == null) {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在或已过期");
            } catch (IOException e) {
                log.error("下载文件失败", e);
            }
            return;
        }

        // 检查是否过期
        Date now = new Date();
        if (task.getExpireTime() != null && now.after(task.getExpireTime())) {
            try {
                response.sendError(HttpServletResponse.SC_GONE, "文件已过期，请重新生成");
            } catch (IOException e) {
                log.error("文件已过期", e);
            }
            return;
        }

        // 更新下载标记
        if (task.getDownloaded() == 0) {
            task.setDownloaded(1);
            sysExcelTaskService.updateById(task);
        }

        // 直接重定向到OSS URL下载，无需中转
        try {
            response.sendRedirect(task.getOssUrl());
        } catch (IOException e) {
            log.error("重定向到OSS失败", e);
        }
    }

    /**
     * 获取当前用户最近的Excel导出任务列表
     * GET /system/excel/recent
     * 返回最近5条记录，按创建时间倒序
     */
    @PreAuthorize("@ss.hasPermi('system:excel:generate')")
    @GetMapping("/recent")
    public AjaxResult getRecentTasks() {
        Long userId = getUserId();
        List<SysExcelTask> list = excelTaskBizService.getRecentTasks(userId, "allOrder");
        return AjaxResult.success(list);
    }

    /**
     * 清理过期的Excel文件（定时任务调用）
     * 可以配置定时任务每天清理一次
     */
    public void cleanExpiredTasks() {
        int deleted = excelTaskBizService.cleanExpiredTasks();
        log.info("定时清理过期Excel任务完成，共删除 {} 条记录", deleted);
    }
}
