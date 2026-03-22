package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.SysExcelTask;

import java.util.List;

/**
 * Excel导出任务Service接口
 *
 * @author ruoyi
 */
public interface ISysExcelTaskFacade extends IService<SysExcelTask> {

    /**
     * 查询Excel导出任务 by fileId
     *
     * @param fileId 文件ID
     * @return Excel导出任务
     */
    public SysExcelTask selectSysExcelTaskByFileId(String fileId);

    /**
     * 查询Excel导出任务列表
     *
     * @param sysExcelTask Excel导出任务
     * @return Excel导出任务集合
     */
    public List<SysExcelTask> selectSysExcelTaskList(SysExcelTask sysExcelTask);

    /**
     * 清理过期任务
     *
     * @return 清理数量
     */
    public int cleanExpiredTasks();

    /**
     * 保存任务
     *
     * @param entity 任务实体
     * @return 是否成功
     */
    public boolean save(SysExcelTask entity);

    /**
     * 根据ID更新任务
     *
     * @param entity 任务实体
     * @return 是否成功
     */
    public boolean updateById(SysExcelTask entity);
}
