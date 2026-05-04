package com.ruoyi.web.controller.user;

import com.ruoyi.biz.company.MemberBizService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.validator.ValidatorUtils;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.user.model.consts.UserApiConsts;
import com.ruoyi.user.model.consts.MemberEnum;
import com.ruoyi.user.model.param.MemberCompanyParam;
import com.ruoyi.user.model.query.MemberCompanyQuery;
import com.ruoyi.web.form.user.CompanyUserForm;
import com.ruoyi.web.vo.user.LoginCompanyVO;
import com.ruoyi.web.vo.user.UserVO;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j

@RestController
@RequestMapping("user")
public class MemberController extends BaseController {

    @Autowired
    MemberBizService memberBizService;

    @Anonymous
    @GetMapping("/loginQrCode")
    public AjaxResult loginQrCode() throws WxErrorException {
        return AjaxResult.success(memberBizService.loginQrCode());
    }


    @Anonymous
    @GetMapping("/loginStatus/{uuid}")
    public AjaxResult loginStatus(@PathVariable("uuid") String uuid) {
        LoginCompanyVO loginCompanyVO = memberBizService.loginStatus(uuid);
        return AjaxResult.success(loginCompanyVO);
    }


    @Anonymous
    @GetMapping("/login/{uuid}/{companyId}")
    public AjaxResult login(@PathVariable("uuid") String uuid, @PathVariable("companyId") Long companyId) {
        String login = memberBizService.login(uuid, companyId);
        return AjaxResult.success(AjaxResult.SUCCESS, login);
    }


    @GetMapping("/login/{companyId}")
    public AjaxResult info(@PathVariable("companyId") Long companyId) {
        LoginUser loginUser = getLoginUser();
        String login = memberBizService.login(companyId, loginUser);
        return AjaxResult.success(AjaxResult.SUCCESS, login);
    }



    @GetMapping("/login/info")
    public AjaxResult info() {
        LoginUser loginUser = getLoginUser();
        LoginCompanyVO loginCompanyVO = memberBizService.loginInfo(loginUser);
        return AjaxResult.success(loginCompanyVO);
    }


//    @Anonymous
//    @GetMapping("/login/test")
//    public AjaxResult loginTest() {
//        String login = memberBizService.loginTest();
//        return AjaxResult.success(login);
//    }




    @GetMapping("/list")
    public TableDataInfo list(@RequestParam("name") String name) {
        Long companyId = getDeptId();
        PageBO<UserVO> pageBO = memberBizService.list(companyId, name, startParamV2());

        return getDataTable(pageBO.getData(), pageBO.getTotal());
    }

    @PostMapping("/member/add")
    public AjaxResult memberAdd(@RequestBody @Validated CompanyUserForm companyUserForm) throws WxErrorException {
        ValidatorUtils.validateEntity(companyUserForm, AddGroup.class);
        memberBizService.checkUserMaster(getUserId(), getDeptId());
        companyUserForm.setCompanyId(getDeptId());
        String bo = memberBizService.channelCode(memberBizService.buildQrCodeParam(companyUserForm.getCompanyId(), UserApiConsts.INVITATION_SUB_QR_CODE,
                companyUserForm.getPhone(), companyUserForm.getNickName(), MemberEnum.UserOwner.PEOPLE.getValue(), null), UserApiConsts.INVITATION_SUB_QR_CODE);

        return AjaxResult.success(AjaxResult.SUCCESS, bo);
    }


    @DeleteMapping("/{userId}")
    public AjaxResult memberDelete(@PathVariable("userId") Long userId) throws WxErrorException {

        memberBizService.removeUserCompany(getDeptId(), userId, getUserId());

        return AjaxResult.success();
    }


    @PutMapping("/{userId}/{owner}")
    public AjaxResult update( @PathVariable("userId") Long userId, @PathVariable("owner") Integer owner) {

//        Long l = companyService.countUser(new UserCompanyQuery().setCompanyId(companyId).setOwner(owner));
//        if (l > 0 && Objects.equals(UserEnum.UserOwner.MASTER.getValue(), owner)) {
//            throw new ServiceException("企业已经存在主账号");
//        }
        memberBizService.update(new MemberCompanyParam().setOwner(owner), new MemberCompanyQuery().setCompanyId(getDeptId()).setUserId(userId));

        return AjaxResult.success();
    }


}
