package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.SysExcelTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Excel导出任务Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface SysExcelTaskMapper extends BaseMapper<SysExcelTask> {

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
     * @return 结果
     */
    public int deleteExpiredTasks();
}
