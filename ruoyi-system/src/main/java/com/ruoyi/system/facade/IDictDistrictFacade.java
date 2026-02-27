package com.ruoyi.system.facade;

import java.util.List;

import com.ruoyi.system.model.bo.DictDistrictBO;
import com.ruoyi.system.model.param.DictDistrictParam;
import com.ruoyi.system.model.query.DictDistrictQuery;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2025-09-09
 */
public interface IDictDistrictFacade {

	public List<DictDistrictBO> list(DictDistrictQuery query);

	public DictDistrictBO getOne(DictDistrictQuery query);

	public boolean update(DictDistrictParam param,DictDistrictQuery query);

}
