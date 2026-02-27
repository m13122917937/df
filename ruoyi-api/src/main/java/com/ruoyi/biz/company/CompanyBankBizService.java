package com.ruoyi.biz.company;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.biz.common.IDictDistrictBizService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.mapper.user.CompanyBankConvert;
import com.ruoyi.system.model.bo.DictDistrictBO;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.user.domain.User;
import com.ruoyi.user.facade.ICompanyBankFacade;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.consts.CompanyBankConsts;
import com.ruoyi.user.model.param.CompanyBankParam;
import com.ruoyi.user.model.query.CompanyBankQuery;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.web.form.user.CompanyBankForm;
import com.ruoyi.web.vo.user.CompanyBankVO;
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
    ISysUserService sysUserService;

    @Autowired
    ICompanyFacade companyFacade;

    public List<CompanyBankVO> list(Long companyId) {

        List<CompanyBankVO> listVO = CompanyBankConvert.INSTANCE.toVOList(companyBankFacade.list(new CompanyBankQuery().setCompanyId(companyId), null));
        Map<Long, String> provinceMap = dictDistrictBizService.listProvince().stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
        for (CompanyBankVO companyBankVO : listVO) {
            companyBankVO.setProvinceName(provinceMap.get(companyBankVO.getProvince()));
            Map<Long, String> cityMap = dictDistrictBizService.listCity(companyBankVO.getProvince()).stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
            companyBankVO.setCityName(cityMap.get(companyBankVO.getCity()));

//            SysUser sysUser = sysUserService.selectUserById(companyBankVO.getCreateBy());
//            companyBankVO.setCreateByName(sysUser.getNickName());

//            sysUser = sysUserService.selectUserById(companyBankVO.getUpdateBy());
//            companyBankVO.setUpdateByName(sysUser.getNickName());
        }
        return listVO;

    }

    public void save(CompanyBankForm companyBankForm, Long userId) {
        // 检查当前用户是否为该企业的管理员
        User masterUser = companyFacade.companyMasterUser(companyBankForm.getCompanyId());
        if (masterUser == null || !Objects.equals(masterUser.getUserId(), userId)) {
            throw new ServiceException("只有企业管理员才能删除用户");
        }
        log.info("用户{}，添加银行卡，{}" , JacksonUtil.toJson(companyBankForm));

        DateTime date = DateUtil.date();
        CompanyBankParam param = CompanyBankConvert.INSTANCE.toParam(companyBankForm);
        param.setUpdateTime(date).setCreateTime(date).setCreateBy(userId).setUpdateBy(userId);

        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(param.getCompanyId()));
        param.setCompanyName(companyBO.getCompanyName()).setNickName(companyBO.getNickName());
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
