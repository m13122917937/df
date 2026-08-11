package com.ruoyi.subsidy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.subsidy.domain.GbRefund;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 国补退款 Mapper。
 */
@Mapper
public interface GbRefundMapper extends BaseMapper<GbRefund> {
    /** 按退款单号查询退款单。 */
    GbRefund selectByRefundNo(@Param("refundNo") String refundNo);
    /** 条件更新退款状态。 */
    int updateStatus(@Param("refundId") Long refundId, @Param("expected") String expected,
                     @Param("target") String target, @Param("wechatRefundId") String wechatRefundId);
    /** 查询后台退款列表。 */
    List<GbRefund> selectAdminList(@Param("refundStatus") String refundStatus);
}
