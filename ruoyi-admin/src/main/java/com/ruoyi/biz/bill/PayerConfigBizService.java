package com.ruoyi.biz.bill;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.bill.facade.IPayerConfigFacade;
import com.ruoyi.bill.facade.IPayerFacade;
import com.ruoyi.bill.model.bo.PayerConfigBO;
import com.ruoyi.bill.model.query.PayerConfigQuery;
import com.ruoyi.bill.model.query.PayerQuery;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.mapper.bill.PayerConfigConvert;
import com.ruoyi.mapper.bill.PayerConvert;
import com.ruoyi.system.facade.ISysUserFacade;
import com.ruoyi.web.form.bill.PayerConfigForm;
import com.ruoyi.web.form.bill.PayerForm;
import com.ruoyi.web.vo.bill.PayerConfigVO;
import com.ruoyi.web.vo.bill.PayerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PayerConfigBizService {

    @Autowired
    IPayerConfigFacade payerConfigFacade;

    @Autowired
    ISysUserFacade sysUserService;

    @Autowired
    IPayerFacade payerFacade;


    public PageBO<PayerConfigVO> pageList(String keyword, PageParamV2 pageParamV2) {

        PageBO<PayerConfigBO> payerConfigBOPageBO = payerConfigFacade.listPage(new PayerConfigQuery().setKeyWordLike(keyword), pageParamV2);

        Map<Long, String> createNameMap = queryUserNameMap(payerConfigBOPageBO.getData().stream().map(PayerConfigBO::getCreateBy).collect(Collectors.toSet()));
        Map<Long, String> updateNameMap = queryUserNameMap(payerConfigBOPageBO.getData().stream().map(PayerConfigBO::getUpdateBy).collect(Collectors.toSet()));
        List<PayerConfigVO> payerVOList = PayerConfigConvert.INSTANCE.toVOList(payerConfigBOPageBO.getData());
        for (PayerConfigVO payerVO : payerVOList) {
            payerVO.setCreateByName(createNameMap.get(payerVO.getCreateBy()));
            payerVO.setUpdateByName(updateNameMap.get(payerVO.getUpdateBy()));
            payerVO.setPayerName(payerFacade.getOne(new PayerQuery().setId(payerVO.getPayerId())).getPayName());
        }
        return new PageBO<>(payerVOList, payerConfigBOPageBO.getTotal());
    }


    public Map<Long, String> queryUserNameMap(Set<Long> userIdSet) {
        if (CollectionUtil.isEmpty(userIdSet)) {
            return Collections.EMPTY_MAP;
        }
        List<SysUser> sysUserSet = sysUserService.selectUserByIds(userIdSet);

        return sysUserSet.stream().collect(Collectors.toMap(SysUser::getUserId, SysUser::getUserName));
    }

    public void update(PayerConfigForm payerConfigForm, LoginUser loginUser) {

        payerConfigForm.setUpdateBy(loginUser.getUserId()).setUpdateTime(DateUtil.date());
        payerConfigFacade.update(PayerConfigConvert.INSTANCE.toParam(payerConfigForm), new PayerConfigQuery().setId(payerConfigForm.getId()));

    }

    public void save(PayerConfigForm payerConfigForm, LoginUser loginUser) {

        payerConfigForm.setCreateBy(loginUser.getUserId()).setCreateTime(DateUtil.date()).setId(null);
        payerConfigFacade.save(PayerConfigConvert.INSTANCE.toParam(payerConfigForm));

    }
}
