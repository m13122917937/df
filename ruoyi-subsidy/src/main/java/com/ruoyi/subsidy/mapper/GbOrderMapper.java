package com.ruoyi.subsidy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.subsidy.domain.GbOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 国补订单 Mapper。
 */
@Mapper
public interface GbOrderMapper extends BaseMapper<GbOrder> {

    /**
     * 按会员和订单号查询订单。
     *
     * @param memberId 会员ID
     * @param orderNo 订单号
     * @return 订单
     */
    GbOrder selectByMemberAndOrderNo(@Param("memberId") Long memberId, @Param("orderNo") String orderNo);

    /**
     * 仅将待支付订单更新为已支付。
     *
     * @param orderId 订单ID
     * @return 更新条数
     */
    int markPaidIfPending(@Param("orderId") Long orderId);

    /** 统计待发货订单数。 */
    Long countPendingShipment();

    /** 仅将已支付订单标记为退款申请中。 */
    int markRefundApplying(@Param("orderId") Long orderId);

    /** 条件更新订单退款状态。 */
    int updateRefundStatus(@Param("orderId") Long orderId, @Param("expected") String expected,
                           @Param("target") String target);

    /** 查询会员订单列表。 */
    List<GbOrder> selectByMemberId(@Param("memberId") Long memberId);

    /** 仅将已支付订单更新为已发货。 */
    int markShippedIfPaid(@Param("orderId") Long orderId);

    /** 按订单号查询订单。 */
    GbOrder selectByOrderNo(@Param("orderNo") String orderNo);

    /** 仅将已发货订单更新为已完成。 */
    int markCompletedIfShipped(@Param("orderId") Long orderId);

    /** 自动完成发货满七天的订单。 */
    int completeShippedBefore(@Param("completedBefore") java.util.Date completedBefore);

    /** 查询后台订单列表。 */
    List<GbOrder> selectAdminList(@Param("orderStatus") String orderStatus);

    /** 仅取消待支付订单。 */
    int cancelIfPending(@Param("orderId") Long orderId);
}
