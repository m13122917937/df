package com.ruoyi.web.controller.user;

import com.github.pagehelper.Page;
import com.ruoyi.biz.company.UserBizService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.validator.ValidatorUtils;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.user.model.consts.UserApiConsts;
import com.ruoyi.user.model.consts.UserEnum;
import com.ruoyi.user.model.param.UserCompanyParam;
import com.ruoyi.user.model.query.UserCompanyQuery;
import com.ruoyi.web.form.user.CompanyUserForm;
import com.ruoyi.web.vo.user.LoginCompanyVO;
import com.ruoyi.web.vo.user.UserLoginVO;
import com.ruoyi.web.vo.user.UserVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.swing.table.TableColumnModel;
import java.util.Objects;

@Slf4j
@Api(tags = "用户登录")
@RestController
@RequestMapping("user")
public class UserController extends BaseController {

    @Autowired
    UserBizService userBizService;

    @Anonymous
    @ApiOperation("获取登录二维码")
    @GetMapping("/loginQrCode")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = UserLoginVO.class)
    })
    public AjaxResult loginQrCode() throws WxErrorException {
        return AjaxResult.success(userBizService.loginQrCode());
    }


    @Anonymous
    @ApiOperation("查询登录状态")
    @GetMapping("/loginStatus/{uuid}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = LoginCompanyVO.class)
    })
    public AjaxResult loginStatus(@PathVariable("uuid") String uuid) {
        LoginCompanyVO loginCompanyVO = userBizService.loginStatus(uuid);
        return AjaxResult.success(loginCompanyVO);
    }


    @Anonymous
    @ApiOperation("登录切换企业，获取token")
    @GetMapping("/login/{uuid}/{companyId}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = String.class, responseContainer = "String")
    })
    public AjaxResult login(@PathVariable("uuid") String uuid, @PathVariable("companyId") Long companyId) {
        String login = userBizService.login(uuid, companyId);
        return AjaxResult.success(AjaxResult.SUCCESS, login);
    }

    @ApiOperation("切换企业")
    @GetMapping("/login/{companyId}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = LoginCompanyVO.class)
    })
    public AjaxResult info(@PathVariable("companyId") Long companyId) {
        LoginUser loginUser = getLoginUser();
        String login = userBizService.login(companyId, loginUser);
        return AjaxResult.success(AjaxResult.SUCCESS, login);
    }


    @ApiOperation("获取当前人的登录信息")
    @GetMapping("/login/info")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = LoginCompanyVO.class)
    })
    public AjaxResult info() {
        LoginUser loginUser = getLoginUser();
        LoginCompanyVO loginCompanyVO = userBizService.loginInfo(loginUser);
        return AjaxResult.success(loginCompanyVO);
    }


    @Anonymous
    @ApiOperation("登录测试接口")
    @GetMapping("/login/test")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = String.class, responseContainer = "String")
    })
    public AjaxResult loginTest() {
        String login = userBizService.loginTest();
        return AjaxResult.success(login);
    }

    @ApiOperation("查询用户列表，通过用户姓名模糊查询")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = UserVO.class)
    })
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam("name") String name) {
        Long companyId = getDeptId();
        PageBO<UserVO> pageBO = userBizService.list(companyId, name, startParamV2());

        return getDataTable(pageBO.getData(), pageBO.getTotal());
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = String.class)
    })
    @ApiOperation("添加用户二维码")
    @PostMapping("/user/add")
    public AjaxResult userAdd(@ApiParam(value = "用户信息") @RequestBody CompanyUserForm companyUserForm) throws WxErrorException {
        ValidatorUtils.validateEntity(companyUserForm, AddGroup.class);
        userBizService.checkUserMaster(getUserId(), getDeptId());
        companyUserForm.setCompanyId(getDeptId());
        String bo = userBizService.channelCode(userBizService.buildQrCodeParam(companyUserForm.getCompanyId(), UserApiConsts.INVITATION_SUB_QR_CODE,
                companyUserForm.getPhone(), companyUserForm.getNickName(), UserEnum.UserOwner.PEOPLE.getValue(), null), UserApiConsts.INVITATION_SUB_QR_CODE);

        return AjaxResult.success(AjaxResult.SUCCESS, bo);
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{userId}")
    public AjaxResult userAdd(@PathVariable("userId") Long userId) throws WxErrorException {

        userBizService.removeUserCompany(getDeptId(), userId, getUserId());

        return AjaxResult.success();
    }

    @ApiOperation("修改用户账号类型")
    @PutMapping("/{userId}/{owner}")
    public AjaxResult update( @PathVariable("userId") Long userId, @PathVariable("owner") Integer owner) {

//        Long l = companyService.countUser(new UserCompanyQuery().setCompanyId(companyId).setOwner(owner));
//        if (l > 0 && Objects.equals(UserEnum.UserOwner.MASTER.getValue(), owner)) {
//            throw new ServiceException("企业已经存在主账号");
//        }
        userBizService.update(new UserCompanyParam().setOwner(owner), new UserCompanyQuery().setCompanyId(getDeptId()).setUserId(userId));

        return AjaxResult.success();
    }


}
