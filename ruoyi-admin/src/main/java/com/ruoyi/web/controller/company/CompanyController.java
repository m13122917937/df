package com.ruoyi.web.controller.company;

import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.company.CompanyBizService;
import com.ruoyi.biz.company.MemberBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.validator.ValidatorUtils;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.mapper.company.CompanyConvert;
import com.ruoyi.mapper.company.MemberConvert;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.facade.IMemberFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.bo.MemberBO;
import com.ruoyi.user.model.consts.UserApiConsts;
import com.ruoyi.user.model.param.CompanyParam;
import com.ruoyi.user.model.param.MemberCompanyParam;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.user.model.query.MemberCompanyQuery;
import com.ruoyi.user.model.query.MemberQuery;
import com.ruoyi.web.form.company.CompanyAddForm;
import com.ruoyi.web.form.company.CompanyForm;
import com.ruoyi.web.form.company.CompanyUserForm;
import com.ruoyi.web.vo.UserVO;
import com.ruoyi.web.vo.company.CompanyVO;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController

@RequestMapping("/company")
public class CompanyController extends BaseController {

    @Autowired
    private ICompanyFacade companyService;

    @Autowired
    private IMemberFacade memberFacade;

    @Autowired
    private MemberBizService memberBizService;

    @Autowired
    CompanyBizService companyBizService;


    @PostMapping("/list")
    public TableDataInfo list(@RequestBody CompanyForm companyForm) {

        PageBO<CompanyBO> companyBOPageBO = companyService.listPage(CompanyConvert.INSTANCE.toCompanyParam(companyForm), startParamV2("create_time desc"));

        return getDataTable(CompanyConvert.INSTANCE.toVoListPage(companyBOPageBO.getData()), companyBOPageBO.getTotal());
    }

    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Validated CompanyAddForm companyForm) throws IOException {

        ValidatorUtils.validateEntity(companyForm, AddGroup.class);

        CompanyBO companyBO = companyService.queryOne(new CompanyQuery().setCompanyName(companyForm.getCompanyName()));
        Assert.isNull(companyBO, "企业已经存在，请重新添加");

        LoginUser loginUser = getLoginUser();
        CompanyParam companyParam = CompanyConvert.INSTANCE.toCompanyAddParam(companyForm);
        companyParam.setCreator(loginUser.getUsername()).setCreatorId(loginUser.getUserId());
        companyBO = companyService.add(companyParam);
        // erp创建对象
        companyBizService.createProvider(companyBO);

        return AjaxResult.success(CompanyConvert.INSTANCE.toVo(companyBO));
    }

    @PostMapping("/update")
    public AjaxResult update(@RequestBody @Validated CompanyAddForm companyForm) throws IOException {

        CompanyParam companyParam = CompanyConvert.INSTANCE.toCompanyAddParam(companyForm);
        companyService.update(companyParam, new CompanyQuery().setId(companyParam.getId()));
        // erp创建对象
        companyBizService.createProvider(CompanyConvert.INSTANCE.toParamToBO(companyParam));

        return AjaxResult.success();
    }

    @GetMapping("/user/list/{companyId}")
    public TableDataInfo userList(@PathVariable("companyId") Long companyId,
                                  @RequestParam(value = "phone", required = false) String phone,
                                  @RequestParam(value = "nickName", required = false)String nickName) {

        PageBO<MemberBO> memberBOPageBO = memberFacade.memberList(new MemberQuery().setNickName(nickName).setMobile(phone).setCompanyId(companyId), startParamV2());

        List<UserVO> userVoList = MemberConvert.INSTANCE.toVoList(memberBOPageBO.getData());
        return getDataTable(userVoList, memberBOPageBO.getTotal());
    }

    @PostMapping("/user/add")
    public AjaxResult memberAdd(@RequestBody @Validated CompanyUserForm companyUserForm) throws WxErrorException {

        ValidatorUtils.validateEntity(companyUserForm, AddGroup.class);

        memberBizService.checkCompany(companyUserForm.getCompanyId());
        return AjaxResult.success(AjaxResult.SUCCESS, memberBizService.channelCode(memberBizService.buildQrCodeParam(companyUserForm.getCompanyId(), UserApiConsts.INVITATION_SUB_QR_CODE,
                companyUserForm.getPhone(), companyUserForm.getNickName(), companyUserForm.getOwner(), null), UserApiConsts.INVITATION_SUB_QR_CODE));
    }

    @DeleteMapping("/{companyId}/{userId}")
    public AjaxResult memberDelete(@PathVariable("companyId") Long companyId, @PathVariable("userId") Long userId) throws WxErrorException {

        memberBizService.removeUserCompany(companyId, userId);

        return AjaxResult.success();
    }

    @PutMapping("/{companyId}/{userId}/{owner}")
    public AjaxResult update(@PathVariable("companyId") Long companyId, @PathVariable("userId") Long userId, @PathVariable("owner") Integer owner) {

        companyService.update(new MemberCompanyParam().setOwner(owner), new MemberCompanyQuery().setCompanyId(companyId).setUserId(userId));

        return AjaxResult.success();
    }

}
