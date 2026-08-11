package com.ruoyi.biz.subsidy;

import com.ruoyi.user.domain.MemberWechatIdentityConflict;
import com.ruoyi.user.facade.IWechatIdentityFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/** 国补微信身份冲突人工审阅编排。 */
@Component
@RequiredArgsConstructor
public class SubsidyIdentityConflictBizService {
    private final IWechatIdentityFacade identityFacade;
    /** 仅查询冲突，不自动处理或合并。 */
    public List<MemberWechatIdentityConflict> list(final String status) { return identityFacade.listConflicts(status); }
}
