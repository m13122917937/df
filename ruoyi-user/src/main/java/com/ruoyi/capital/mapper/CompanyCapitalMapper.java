package com.ruoyi.capital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.capital.domain.CompanyCapital;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 企业押金Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-08
 */
 @Mapper
public interface CompanyCapitalMapper extends BaseMapper<CompanyCapital>{

    boolean updateAvailableAmount(@Param("id") Long id, @Param("amount")BigDecimal amount);

    boolean updateFrozenAmount(@Param("id") Long id, @Param("amount")BigDecimal amount);

}
