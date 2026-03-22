package com.ruoyi.biz.bill;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.bill.constant.PayerConsts;
import com.ruoyi.bill.facade.IPayerConfigFacade;
import com.ruoyi.bill.facade.IPayerFacade;
import com.ruoyi.bill.model.bo.PayerBO;
import com.ruoyi.bill.model.query.PayerConfigQuery;
import com.ruoyi.bill.model.query.PayerQuery;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.mapper.bill.PayerConvert;
import com.ruoyi.system.facade.ISysUserFacade;
import com.ruoyi.web.form.bill.PayerForm;
import com.ruoyi.web.vo.bill.PayerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PayerBizService {

    @Autowired
    IPayerFacade payerFacade;

    @Autowired
    ISysUserFacade sysUserService;

    @Autowired
    IPayerConfigFacade payerConfigFacade;


    public PageBO<PayerVO> pageList(Integer actived, String payName, PageParamV2 pageParamV2) {

        PageBO<PayerBO> payerBOPageBO = payerFacade.listPage(new PayerQuery().setActived(actived).setPayNameLike(payName), pageParamV2);

        Map<Long, String> createNameMap = queryUserNameMap(payerBOPageBO.getData().stream().map(PayerBO::getCreateBy).collect(Collectors.toSet()));
        Map<Long, String> updateNameMap = queryUserNameMap(payerBOPageBO.getData().stream().map(PayerBO::getUpdateBy).collect(Collectors.toSet()));
        List<PayerVO> payerVOList = PayerConvert.INSTANCE.toVOList(payerBOPageBO.getData());
        for (PayerVO payerVO : payerVOList) {
            payerVO.setCreateName(createNameMap.get(payerVO.getCreateBy()));
            payerVO.setUpdateName(updateNameMap.get(payerVO.getUpdateBy()));
        }
        return new PageBO<>(payerVOList, payerBOPageBO.getTotal());
    }


    public Map<Long, String> queryUserNameMap(Set<Long> userIdSet) {
        if (CollectionUtil.isEmpty(userIdSet)) {
            return Collections.EMPTY_MAP;
        }
        List<SysUser> sysUserSet = sysUserService.selectUserByIds(userIdSet);

        return sysUserSet.stream().collect(Collectors.toMap(SysUser::getUserId, SysUser::getUserName));
    }

    public void update(PayerForm payerForm, LoginUser loginUser) {
        if(Objects.equals(payerForm.getActived(), PayerConsts.Activated.DEPRECATE.getCode())){
            if(payerConfigFacade.count(new PayerConfigQuery().setPayerId(payerForm.getId())) > 0){
                throw new ServiceException("该银行卡已经绑定店铺，请先解绑店铺");
            }
        }
        payerForm.setUpdateBy(loginUser.getUserId()).setUpdateTime(DateUtil.date());
        payerFacade.update(PayerConvert.INSTANCE.toParam(payerForm), new PayerQuery().setId(payerForm.getId()));


    }

    public void save(PayerForm payerForm, LoginUser loginUser) {

        payerForm.setCreateBy(loginUser.getUserId()).setCreateTime(DateUtil.date()).setId(null);
        payerFacade.save(PayerConvert.INSTANCE.toParam(payerForm));

    }

    public List<PayerVO> list(String payName) {
        List<PayerBO> list = payerFacade.list(new PayerQuery().setPayNameLike(payName), null);
        return PayerConvert.INSTANCE.toVOList(list);

    }
}
