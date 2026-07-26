package com.ruoyi.web.convert.master;

import com.ruoyi.master.model.bo.MasterSubjectBO;
import com.ruoyi.master.model.query.MasterSubjectQuery;
import com.ruoyi.web.vo.master.MasterSubjectQueryRequest;
import com.ruoyi.web.vo.master.MasterSubjectVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 经营主体 Web 层对象转换器。
 */
@Mapper
public interface MasterSubjectWebConvert {

    MasterSubjectWebConvert INSTANCE = Mappers.getMapper(MasterSubjectWebConvert.class);

    /**
     * 转换查询请求。
     *
     * @param source Web 查询请求
     * @return 领域查询条件
     */
    MasterSubjectQuery toQuery(MasterSubjectQueryRequest source);

    /**
     * 批量转换经营主体响应。
     *
     * @param source 经营主体业务对象集合
     * @return Web 响应集合
     */
    List<MasterSubjectVO> toVOList(List<MasterSubjectBO> source);
}
