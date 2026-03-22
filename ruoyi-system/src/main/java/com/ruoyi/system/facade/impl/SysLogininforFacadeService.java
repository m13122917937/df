package com.ruoyi.system.facade.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.manager.SysLogininforManager;
import com.ruoyi.system.facade.ISysLogininforFacade;

/**
 * 系统访问日志情况信息 服务层处理
 * 
 * @author ruoyi
 */
@Service
public class SysLogininforFacadeService implements ISysLogininforFacade
{

    @Autowired
    private SysLogininforManager sysLogininforManager;

    /**
     * 新增系统登录日志
     * 
     * @param logininfor 访问日志对象
     */
    @Override
    public void insertLogininfor(SysLogininfor logininfor)
    {
        sysLogininforManager.getBaseMapper().insertLogininfor(logininfor);
    }

    /**
     * 查询系统登录日志集合
     * 
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor)
    {
        return sysLogininforManager.getBaseMapper().selectLogininforList(logininfor);
    }

    /**
     * 批量删除系统登录日志
     * 
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    @Override
    public int deleteLogininforByIds(Long[] infoIds)
    {
        return sysLogininforManager.getBaseMapper().deleteLogininforByIds(infoIds);
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor()
    {
        sysLogininforManager.getBaseMapper().cleanLogininfor();
    }
}
