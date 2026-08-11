package com.ruoyi.user.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.user.domain.Member;
import com.ruoyi.user.domain.MemberWechatIdentity;
import com.ruoyi.user.domain.MemberWechatIdentityConflict;
import com.ruoyi.user.mapper.MemberWechatIdentityConflictMapper;
import com.ruoyi.user.mapper.MemberWechatIdentityMapper;
import com.ruoyi.user.mapper.MemberMapper;
import com.ruoyi.user.model.bo.WechatIdentityBO;
import com.ruoyi.user.model.consts.MemberEnum;
import com.ruoyi.user.model.param.WechatIdentityParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 会员微信身份领域服务。
 */
@Service
@RequiredArgsConstructor
public class MemberWechatIdentityService {
    private static final String STATUS_NORMAL = "NORMAL";
    private static final String STATUS_PENDING_UNION = "PENDING_UNION";
    private static final String STATUS_CONFLICT = "CONFLICT";

    private final MemberWechatIdentityMapper identityMapper;
    private final MemberWechatIdentityConflictMapper conflictMapper;
    private final MemberMapper memberMapper;

    /**
     * 解析微信身份。
     *
     * @param param 微信身份参数
     * @return 身份结果
     */
    @Transactional(rollbackFor = Exception.class)
    public WechatIdentityBO resolve(final WechatIdentityParam param) {
        MemberWechatIdentity byOpenId = identityMapper.selectOne(new LambdaQueryWrapper<MemberWechatIdentity>()
                .eq(MemberWechatIdentity::getChannel, param.getChannel())
                .eq(MemberWechatIdentity::getAppId, param.getAppId())
                .eq(MemberWechatIdentity::getOpenId, param.getOpenId()));
        MemberWechatIdentity byUnionId = findByUnionId(param.getUnionId());
        if (byOpenId != null && byUnionId != null && !byOpenId.getMemberId().equals(byUnionId.getMemberId())) {
            return conflict(param.getUnionId(), byOpenId.getMemberId(), byUnionId.getMemberId());
        }
        if (byUnionId != null) {
            return bindOpenId(byOpenId, byUnionId, param);
        }
        if (byOpenId != null) {
            return updateUnionId(byOpenId, param);
        }
        return createIdentity(param);
    }

    /**
     * 查询会员渠道 OpenID。
     *
     * @param memberId 会员ID
     * @param channel 渠道
     * @param appId 微信AppID
     * @return OpenID
     */
    public String getOpenId(final Long memberId, final String channel, final String appId) {
        MemberWechatIdentity identity = identityMapper.selectOne(new LambdaQueryWrapper<MemberWechatIdentity>()
                .eq(MemberWechatIdentity::getMemberId, memberId).eq(MemberWechatIdentity::getChannel, channel)
                .eq(MemberWechatIdentity::getAppId, appId));
        return identity == null ? null : identity.getOpenId();
    }

    /**
     * 判断会员的小程序身份是否已完成 UnionID 归并且不存在冲突。
     *
     * @param memberId 会员 ID
     * @param channel 微信渠道
     * @param appId 微信 AppID
     * @return 是否允许购买
     */
    public boolean isPurchaseAllowed(final Long memberId, final String channel, final String appId) {
        MemberWechatIdentity identity = identityMapper.selectOne(new LambdaQueryWrapper<MemberWechatIdentity>()
                .eq(MemberWechatIdentity::getMemberId, memberId).eq(MemberWechatIdentity::getChannel, channel)
                .eq(MemberWechatIdentity::getAppId, appId));
        return identity != null && STATUS_NORMAL.equals(identity.getIdentityStatus()) && StrUtil.isNotBlank(identity.getUnionId());
    }

    /** 查询身份冲突人工审阅列表。 */
    public List<MemberWechatIdentityConflict> listConflicts(final String status) {
        return conflictMapper.selectListByStatus(status);
    }

    private MemberWechatIdentity findByUnionId(final String unionId) {
        if (StrUtil.isBlank(unionId)) {
            return null;
        }
        return identityMapper.selectOne(new LambdaQueryWrapper<MemberWechatIdentity>()
                .eq(MemberWechatIdentity::getUnionId, unionId));
    }

    private WechatIdentityBO bindOpenId(final MemberWechatIdentity byOpenId,
                                         final MemberWechatIdentity byUnionId,
                                         final WechatIdentityParam param) {
        if (byOpenId == null) {
            MemberWechatIdentity identity = new MemberWechatIdentity().setMemberId(byUnionId.getMemberId())
                    .setChannel(param.getChannel()).setAppId(param.getAppId()).setOpenId(param.getOpenId())
                    .setUnionId(param.getUnionId()).setIdentityStatus(STATUS_NORMAL)
                    .setCreateTime(DateUtil.date()).setUpdateTime(DateUtil.date());
            identityMapper.insert(identity);
        }
        return result(byUnionId.getMemberId(), STATUS_NORMAL, true);
    }

    private WechatIdentityBO updateUnionId(final MemberWechatIdentity identity, final WechatIdentityParam param) {
        if (StrUtil.isNotBlank(param.getUnionId()) && StrUtil.isBlank(identity.getUnionId())) {
            identity.setUnionId(param.getUnionId()).setIdentityStatus(STATUS_NORMAL).setUpdateTime(DateUtil.date());
            identityMapper.updateById(identity);
        }
        boolean purchaseAllowed = StrUtil.isNotBlank(identity.getUnionId()) || StrUtil.isNotBlank(param.getUnionId());
        return result(identity.getMemberId(), purchaseAllowed ? STATUS_NORMAL : STATUS_PENDING_UNION, purchaseAllowed);
    }

    private WechatIdentityBO createIdentity(final WechatIdentityParam param) {
        Member member = new Member().setOpenId(param.getOpenId()).setNickName("微信用户")
                .setDeleted(MemberEnum.UserDeleted.NORMAL.getValue()).setCreateTime(DateUtil.date());
        memberMapper.insert(member);
        boolean purchaseAllowed = StrUtil.isNotBlank(param.getUnionId());
        MemberWechatIdentity identity = new MemberWechatIdentity().setMemberId(member.getUserId())
                .setChannel(param.getChannel()).setAppId(param.getAppId()).setOpenId(param.getOpenId())
                .setUnionId(param.getUnionId()).setIdentityStatus(purchaseAllowed ? STATUS_NORMAL : STATUS_PENDING_UNION)
                .setCreateTime(DateUtil.date()).setUpdateTime(DateUtil.date());
        identityMapper.insert(identity);
        return result(member.getUserId(), identity.getIdentityStatus(), purchaseAllowed);
    }

    private WechatIdentityBO conflict(final String unionId, final Long sourceMemberId, final Long targetMemberId) {
        MemberWechatIdentityConflict existing = conflictMapper.selectOpenConflict(unionId, sourceMemberId, targetMemberId);
        if (existing == null) {
            MemberWechatIdentityConflict conflict = new MemberWechatIdentityConflict().setUnionId(unionId)
                    .setSourceMemberId(sourceMemberId).setTargetMemberId(targetMemberId).setConflictStatus(STATUS_CONFLICT)
                    .setCreateTime(DateUtil.date()).setUpdateTime(DateUtil.date());
            conflictMapper.insert(conflict);
        }
        return result(sourceMemberId, STATUS_CONFLICT, false);
    }

    private WechatIdentityBO result(final Long memberId, final String status, final boolean purchaseAllowed) {
        WechatIdentityBO result = new WechatIdentityBO();
        result.setMemberId(memberId);
        result.setIdentityStatus(status);
        result.setPurchaseAllowed(purchaseAllowed);
        return result;
    }
}
