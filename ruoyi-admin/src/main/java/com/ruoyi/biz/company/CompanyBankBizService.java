package com.ruoyi.biz.company;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.biz.sys.IDictDistrictBizService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.mapper.company.CompanyBankConvert;
import com.ruoyi.system.model.bo.DictDistrictBO;
import com.ruoyi.system.facade.ISysUserFacade;
import com.ruoyi.user.domain.CompanyBank;
import com.ruoyi.user.facade.ICompanyBankFacade;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.bo.CompanyBankBO;
import com.ruoyi.user.model.consts.CompanyBankConsts;
import com.ruoyi.user.model.param.CompanyBankParam;
import com.ruoyi.user.model.query.CompanyBankQuery;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.web.form.company.CompanyBankForm;
import com.ruoyi.web.vo.company.CompanyBankVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CompanyBankBizService {

    @Autowired
    IDictDistrictBizService dictDistrictBizService;

    @Autowired
    ICompanyBankFacade companyBankFacade;

    @Autowired
    ISysUserFacade sysUserService;

    @Autowired
    ICompanyFacade companyFacade;

    public List<CompanyBankVO> list(Long companyId) {

        List<CompanyBankVO> listVO = CompanyBankConvert.INSTANCE.toVOList(companyBankFacade.list(new CompanyBankQuery().setCompanyId(companyId), null));
        Map<Long, String> provinceMap = dictDistrictBizService.listProvince().stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
        for (CompanyBankVO companyBankVO : listVO) {
            companyBankVO.setProvinceName(provinceMap.get(companyBankVO.getProvince()));
            Map<Long, String> cityMap = dictDistrictBizService.listCity(companyBankVO.getProvince()).stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
            companyBankVO.setCityName(cityMap.get(companyBankVO.getCity()));

            SysUser sysUser = sysUserService.selectUserById(companyBankVO.getCreateBy());
            companyBankVO.setCreateByName(Objects.nonNull(sysUser)?sysUser.getNickName(): null);

            sysUser = sysUserService.selectUserById(companyBankVO.getUpdateBy());
            companyBankVO.setUpdateByName(Objects.nonNull(sysUser)?sysUser.getNickName(): null);
        }
        return listVO;

    }

    public void save(CompanyBankForm companyBankForm, Long userId) {
        DateTime date = DateUtil.date();
        CompanyBankParam param = CompanyBankConvert.INSTANCE.toParam(companyBankForm).setCreateTime(date).setUpdateTime(date)
                .setCreateBy(userId).setUpdateBy(userId);
        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(param.getCompanyId()));
        param.setCompanyName(companyBO.getCompanyName()).setNickName(companyBO.getNickName());
        if (Objects.equals(companyBankForm.getDefaulted(), CompanyBankConsts.Defaulted.YES.getValue())) {
//            companyBankFacade.update(new CompanyBankParam().setDefaulted(CompanyBankConsts.Defaulted.NO.getValue()),
//                    new CompanyBankQuery().setCompanyId(param.getCompanyId()));
            Long count = companyBankFacade.count(new CompanyBankQuery().setCompanyId(param.getCompanyId()).setDefaulted(CompanyBankConsts.Defaulted.YES.getValue()));
            if (count > 0) {
                throw new ServiceException("该企业已经有默认付款账号，请先更改默认收款账号");
            }
        }
        companyBankFacade.save(param);

    }

    public void update(CompanyBankForm companyBankForm, Long userId) {
        CompanyBankParam param = CompanyBankConvert.INSTANCE.toParam(companyBankForm).setUpdateBy(userId).setUpdateTime(DateUtil.date());
        if (Objects.equals(companyBankForm.getDefaulted(), CompanyBankConsts.Defaulted.YES.getValue())) {
            companyBankFacade.update(new CompanyBankParam().setDefaulted(CompanyBankConsts.Defaulted.NO.getValue()),
                    new CompanyBankQuery().setCompanyId(param.getCompanyId()));
        }
        companyBankFacade.update(param, new CompanyBankQuery().setId(param.getId()));
    }
}
