package com.ruoyi.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.order.domain.HangingOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * 挂单Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-09
 */
@Mapper
public interface HangingOrderMapper extends BaseMapper<HangingOrder> {

    /**
     * 失效订单当前有效挂单。
     *
     * @param orderCode 订单编号
     * @param normalStatus 有效状态
     * @param failureStatus 失效状态
     * @param updateBy 更新人
     * @param updateTime 更新时间
     * @return 更新数量
     */
    int expireActiveByOrderCode(@Param("orderCode") String orderCode,
                                @Param("normalStatus") Integer normalStatus,
                                @Param("failureStatus") Integer failureStatus,
                                @Param("updateBy") Long updateBy,
                                @Param("updateTime") Date updateTime);
}
