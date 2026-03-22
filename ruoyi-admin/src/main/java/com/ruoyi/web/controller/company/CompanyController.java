package com.ruoyi.web.controller.company;

import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.company.CompanyBizService;
import com.ruoyi.biz.company.UserBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.validator.ValidatorUtils;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import com.ruoyi.mapper.UserConvert;
import com.ruoyi.mapper.company.CompanyConvert;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.facade.IUserFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.bo.UserBO;
import com.ruoyi.user.model.consts.UserApiConsts;
import com.ruoyi.user.model.param.CompanyParam;
import com.ruoyi.user.model.param.UserCompanyParam;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.user.model.query.UserCompanyQuery;
import com.ruoyi.user.model.query.UserQuery;
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
    private IUserFacade userFacade;

    @Autowired
    private UserBizService userBizService;

    @Autowired
    CompanyBizService companyBizService;


    @PostMapping("/list")


    public TableDataInfo list(

        PageBO<CompanyBO> companyBOPageBO = companyService.listPage(CompanyConvert.INSTANCE.toCompanyParam(companyForm), startParamV2("create_time desc"));

        return getDataTable(CompanyConvert.INSTANCE.toVoListPage(companyBOPageBO.getData()), companyBOPageBO.getTotal());
    }



    @PostMapping("/add")
    public AjaxResult add(
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
    public AjaxResult update(

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

        PageBO<UserBO> userBOPageBO = userFacade.userList(new UserQuery().setNickName(nickName).setMobile(phone).setCompanyId(companyId), startParamV2());

        List<UserVO> userVoList = UserConvert.INSTANCE.toUserVoList(userBOPageBO.getData());
        return getDataTable(userVoList, userBOPageBO.getTotal());
    }


    @PostMapping("/user/add")
    public AjaxResult userAdd(
        ValidatorUtils.validateEntity(companyUserForm, AddGroup.class);

        userBizService.checkCompany(companyUserForm.getCompanyId());
        String bo = userBizService.channelCode(userBizService.buildQrCodeParam(companyUserForm.getCompanyId(), UserApiConsts.INVITATION_SUB_QR_CODE,
                companyUserForm.getPhone(), companyUserForm.getNickName(), companyUserForm.getOwner()), UserApiConsts.INVITATION_SUB_QR_CODE);

        return AjaxResult.success(AjaxResult.SUCCESS, bo);
    }



    @DeleteMapping("/{companyId}/{userId}")
    public AjaxResult userAdd(@PathVariable("companyId") Long companyId, @PathVariable("userId") Long userId) throws WxErrorException {

        userBizService.removeUserCompany(companyId, userId);

        return AjaxResult.success();
    }


    @PutMapping("/{companyId}/{userId}/{owner}")
    public AjaxResult update(@PathVariable("companyId") Long companyId, @PathVariable("userId") Long userId, @PathVariable("owner") Integer owner) {

//        Long l = companyService.countUser(new UserCompanyQuery().setCompanyId(companyId).setOwner(owner));
//        if (l > 0 && Objects.equals(UserEnum.UserOwner.MASTER.getValue(), owner)) {
//            throw new ServiceException("企业已经存在主账号");
//        }

        companyService.update(new UserCompanyParam().setOwner(owner), new UserCompanyQuery().setCompanyId(companyId).setUserId(userId));

        return AjaxResult.success();
    }


}
