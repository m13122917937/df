package com.ruoyi.system.facade.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.io.File;
import java.util.Date;
import java.util.List;
import com.ruoyi.system.domain.SysExcelTask;
import com.ruoyi.system.mapper.SysExcelTaskMapper;
import com.ruoyi.system.facade.ISysExcelTaskFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Excel导出任务Service业务层处理
 *
 * @author ruoyi
 */
@Slf4j
@Service
public class SysExcelTaskFacadeService extends ServiceImpl<SysExcelTaskMapper, SysExcelTask> implements ISysExcelTaskFacade {

    @Override
    public SysExcelTask selectSysExcelTaskByFileId(String fileId) {
        return baseMapper.selectSysExcelTaskByFileId(fileId);
    }

    @Override
    public List<SysExcelTask> selectSysExcelTaskList(SysExcelTask sysExcelTask) {
        return baseMapper.selectSysExcelTaskList(sysExcelTask);
    }

    @Override
    public int cleanExpiredTasks() {
        // 查询所有过期任务
        SysExcelTask query = new SysExcelTask();
        query.setExpireTime(new Date());
        List<SysExcelTask> expiredTasks = selectSysExcelTaskList(query);

        // 先删除物理文件
        for (SysExcelTask task : expiredTasks) {
            if (task.getLocalPath() != null) {
                File file = new File(task.getLocalPath());
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (!deleted) {
                        log.warn("删除过期Excel文件失败: {}", task.getLocalPath());
                    }
                }
            }
        }

        // 再删除数据库记录
        return baseMapper.deleteExpiredTasks();
    }
}
