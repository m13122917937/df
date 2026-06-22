package com.ruoyi.web.controller.contract;

import com.ruoyi.biz.contract.ContractBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import com.ruoyi.web.form.contract.ContractForm;
import com.ruoyi.web.form.contract.ContractLaunchForm;
import com.ruoyi.web.form.contract.ContractQueryForm;
import com.ruoyi.web.vo.contract.ContractVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * 合同管理（后台）
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@RestController
@RequestMapping("/contract")
public class ContractController extends BaseController {

    @Autowired
    private ContractBizService contractBizService;

    /**
     * 分页查询合同列表
     *
     * @param queryForm 查询表单
     * @return 分页结果
     */
    @PostMapping("/page/list")
    public TableDataInfo pageList(@RequestBody ContractQueryForm queryForm) {
        PageBO<ContractVO> page = contractBizService.pageList(queryForm, startParamV2("create_time desc"));
        return getDataTable(page.getData(), page.getTotal());
    }

    /**
     * 查询合同详情
     *
     * @param id 合同主键
     * @return 详情
     */
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable("id") Long id) {
        return AjaxResult.success(contractBizService.detail(id));
    }

    /**
     * 新增草稿合同
     *
     * @param form 表单
     * @return 主键
     */
    @PostMapping("/save")
    public AjaxResult save(@Validated(value = AddGroup.class) @RequestBody ContractForm form) {
        LoginUser loginUser = getLoginUser();
        Long id = contractBizService.save(form, loginUser);
        return AjaxResult.success(id);
    }

    /**
     * 修改草稿合同
     *
     * @param form 表单
     * @return 结果
     */
    @PostMapping("/update")
    public AjaxResult update(@Validated(value = UpdateGroup.class) @RequestBody ContractForm form) {
        LoginUser loginUser = getLoginUser();
        contractBizService.update(form, loginUser);
        return AjaxResult.success();
    }

    /**
     * 删除草稿合同
     *
     * @param id 合同主键
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") Long id) {
        contractBizService.remove(id);
        return AjaxResult.success();
    }

    /**
     * 发起签署
     *
     * @param form 发起签署表单
     * @return 结果
     */
    @PostMapping("/launch")
    public AjaxResult launch(@Valid @RequestBody ContractLaunchForm form) {
        LoginUser loginUser = getLoginUser();
        contractBizService.launch(form, loginUser);
        return AjaxResult.success();
    }

    /**
     * 撤销签署
     *
     * @param id 合同主键
     * @return 结果
     */
    @PutMapping("/{id}/cancel")
    public AjaxResult cancel(@PathVariable("id") Long id) {
        LoginUser loginUser = getLoginUser();
        contractBizService.cancel(id, loginUser);
        return AjaxResult.success();
    }

    /**
     * 获取签署URL
     *
     * @param id         合同主键
     * @param signerType 签署人类型 our / vendor
     * @return 签署URL
     */
    @GetMapping("/{id}/sign-url")
    public AjaxResult signUrl(@PathVariable("id") Long id,
                              @RequestParam(value = "signerType", defaultValue = "our") String signerType) {
        return AjaxResult.success(contractBizService.signUrl(id, signerType));
    }

    /**
     * 手动同步合同状态
     *
     * @param id 合同主键
     * @return 结果
     */
    @PutMapping("/{id}/sync")
    public AjaxResult syncStatus(@PathVariable("id") Long id) {
        contractBizService.syncStatus(id);
        return AjaxResult.success();
    }
}
